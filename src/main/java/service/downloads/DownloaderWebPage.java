package service.downloads;

import com.sun.istack.internal.Nullable;
import service.property.loader.SystemSettingsLoader;
import service.linksHolder.LinksHolder;
import service.save.Storage;
import service.searcherLinks.SearcherLink;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Class allow download page from web and also that class can do it
 * asynchronously
 * EXCEPTION_GET_FUTURE  This  error is caused in the two cases, or page reading
 * was not possible, either reading a page took more than 3 seconds
 *
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class DownloaderWebPage implements Callable<Void> {
    private static final ExecutorService executor = Executors.newFixedThreadPool(SystemSettingsLoader.getCountThread());

    /**
     * if it is the main, the all link, that finds on this page
     * we need add to add to the list for further processing
     */
    private URL url;
    private LinksHolder linksHolder;
    private int linkId = 0;
    private URL rootURL = null;
    private String pathToDefaultDirectory;
    private Storage storage;
    private String pageId;

    /**
     * Initializes dir for save web page;
     */
    public DownloaderWebPage(URL url, Storage storage) {
        this.rootURL = url;
        this.url = url;
        this.storage = storage;
        linksHolder = new LinksHolder();
        pathToDefaultDirectory = storage.createDir(url.getFile());
        pageId = storage.writePage(url.toString(), pathToDefaultDirectory + "File from link number" + linkId);
    }

    public DownloaderWebPage(URL url, int numberLink, LinksHolder linksHolder, String pathToDefaultDirectory, Storage storage) {
        this.url = url;
        this.linkId = numberLink;
        this.linksHolder = linksHolder;
        this.pathToDefaultDirectory = pathToDefaultDirectory;
        this.storage = storage;
        pageId = storage.writePage(url.toString(), this.pathToDefaultDirectory + "File from link number" + linkId);
        System.out.println("Download the link number " + numberLink + "URL " + url);
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
                List<String> links = SearcherLink.getLinks(line);
                if (linkId == 0) {
                    linksHolder.addLinkForSearch(links);
                } else {
                    linksHolder.addMetaLinks(links);
                }
            }
            if (linkId == 0) {//if is root page, download child pages
                asyncProcessChildPages();
                saveChildLinks();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }


        return null;
    }

    public void stop() {
        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
            storage.close();
        } catch (InterruptedException | IOException e) {
            System.err.println("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            System.out.println("shutdown finished");
        }
    }

    /**
     * Method save additional link
     */
    private void saveChildLinks() {
        storage.writeLinks(pageId, linksHolder.getMetaLinks());
    }

    private void asyncProcessChildPages()
            throws InterruptedException, ExecutionException, TimeoutException {
        for (Future<Void> future : executor.invokeAll(getTasks(), 10, TimeUnit.SECONDS)) {
            future.get(1, TimeUnit.MILLISECONDS);
        }
    }

    private List<DownloaderWebPage> getTasks() {
        List<DownloaderWebPage> tasks = new ArrayList<>();
        int numb = 0;
        URL tempURL;
        while (linksHolder.hasNext()) {
            String link = linksHolder.nextLink();
            if ((tempURL = createURL(link)) != null)
                tasks.add(downloaderForNextLink(tempURL, ++numb));
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
            e.printStackTrace();
            return null;
        }
    }

    private DownloaderWebPage downloaderForNextLink(URL url, int numberLink) {
        return new DownloaderWebPage(url, numberLink, linksHolder, pathToDefaultDirectory, storage);
    }

}
