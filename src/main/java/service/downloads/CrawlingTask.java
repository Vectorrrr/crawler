package service.downloads;

import com.sun.istack.internal.Nullable;

import service.property.loader.CrawlerProperties;
import service.links.holder.LinksHolder;
import service.storage.Storage;
import service.link.processor.LinkProcessor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.apache.log4j.Logger;


/**
 * Class allow download page from web and also that class can do it
 * asynchronously
 *
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class CrawlingTask implements  Crawling {
    private static final ExecutorService executor = Executors.newFixedThreadPool(
            Integer.valueOf(CrawlerProperties.property("amount.thread.in.pull")));

    private static final Logger log = Logger.getLogger(CrawlingTask.class);
    public static final String EXCEPTION_CREATE_URL = "Create url from string %s exception %s";
    public static final String EXCEPTION_SHUTDOWN_EXECUTOR = "ExecutorService interrupted exception %s";
    public static final String DOWNLOAD_THE_LINK_MESSAGE = "Download the link number %d URL %s";
    public static final String EXCEPTION_IN_CRAWLING = "In %s exception %s";
    private URL url;
    private LinksHolder linksHolder;
    private int linkId;
    private URL rootURL = null;
    private Storage storage;
    private String pageId;

    /**
     * The default constructor is needed in order
     * to be able to create an instance of an object
     */
    public CrawlingTask() {
    }

    private CrawlingTask(URL url, Storage storage, int numberLink, LinksHolder linksHolder) {
        this.url = url;
        this.linkId = numberLink;
        this.linksHolder = linksHolder;
        this.storage = storage;
        this.pageId = storage.writePage(url);
        log.info(String.format(DOWNLOAD_THE_LINK_MESSAGE, numberLink, url));
    }

    public boolean crawling(URL url) {
        try {
            Future<Void> future = executor.submit(new CrawlingTask(url, this.storage, 0, new LinksHolder()));
            future.get();
            return true;
        } catch (Exception e) {
            log.error(String.format(EXCEPTION_IN_CRAWLING, url, e.getMessage()));
            return false;
        }
    }

    /**
     * each line of the method checks for links
     * each line is stored in the file that contains the original page
     */
    @Override
    public Void call() throws Exception {
        String line;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), "UTF-8"))) {
            while ((line = reader.readLine()) != null) {
                storage.writeContent(pageId, line);
                List<String> links = LinkProcessor.getLinksForText(line);
                if (linkId == 0) {
                    linksHolder.addLinkForSearch(links);
                } else {
                    linksHolder.addMetaLinks(links);
                }
            }
            if (linkId == 0) {//if is root page, download child pages
                asyncProcessChildPages();
            }
        } finally {
            saveChildLinks();
        }


        return null;
    }

    @Override
    public void close() {
        try {
            log.info("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
            storage.close();
        } catch (InterruptedException | IOException e) {
            log.warn(String.format(EXCEPTION_SHUTDOWN_EXECUTOR, e.getMessage()));
        } finally {
            if (!executor.isTerminated()) {
                log.warn("When interrupted ExecutorService, he had several active threads");
            }
            log.info("shutdown finished");
        }
    }

    /**
     * Method save additional link
     */
    private void saveChildLinks() {
        if (linkId == 0) {
            storage.writeLinks(pageId, linksHolder.getMetaLinks());
        }
    }

    private void asyncProcessChildPages()
            throws InterruptedException, ExecutionException, TimeoutException {
        for (Future<Void> future : executor.invokeAll(getTasks(), 10, TimeUnit.SECONDS)) {
            future.get(1, TimeUnit.MILLISECONDS);
        }
    }

    private List<CrawlingTask> getTasks() {
        List<CrawlingTask> tasks = new ArrayList<>();
        int numb = 0;
        URL tempURL;
        while (linksHolder.hasNext()) {
            String link = linksHolder.nextLink();
            if ((tempURL = createURL(link)) != null)
                tasks.add(crawlingForNextLink(tempURL, ++numb));
        }
        return tasks;
    }

    /**
     * if the URL has been created successfully,
     * it returns new URL otherwise null
     */
    @Nullable
    private URL createURL(String link) {
        try {
            if (link.startsWith("/")) {
                return new URL(rootURL, link);
            }
            return new URL(link);
        } catch (MalformedURLException e) {
            log.error(String.format(EXCEPTION_CREATE_URL, link, e.getMessage()));
            return null;
        }
    }

    private CrawlingTask crawlingForNextLink(URL url, int numberLink) {
        return new CrawlingTask(url, storage, numberLink, linksHolder);
    }
}
