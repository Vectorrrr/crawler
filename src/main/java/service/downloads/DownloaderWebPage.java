package service.downloads;

import com.sun.istack.internal.Nullable;
import service.PropertiesLoader;
import service.linksHolder.LinksHolder;
import service.save.StorageInFile;
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
    private static final String EXCEPTION_DOWNLOAD_WEB_PAGE = "Don't correct download web page from link ";
    private static final String EXCEPTION_WRITE_ADDITIONAL_LINK = "When I saved the additional links error occurred";
    private static final String PATH_TO_DEFAULT_DIR = "./Downloaded Sites/";
    private static final byte[] SEPARATOR = "\n".getBytes();



    private static ExecutorService executor;


    /**
     * verifies the existence of the folder to store the default
     * if it does not create it
     */
    //todo may be throw new unchecked exception and stop program because if we don't create directory we can't save answer
    //todo or we must not create a folder and simply accept the way and let the folder created by the user
    static {
        File defaultDirectory = new File(PATH_TO_DEFAULT_DIR);
        if (!defaultDirectory.exists()) {
            defaultDirectory.mkdirs();
        }
        executor=Executors.newFixedThreadPool(PropertiesLoader.getCountThread());
    }


    /**
     * if it is the main, the all link, that finds on this page
     * we need add to add to the list for further processing
     */
    private URL url;
    private LinksHolder linksHolder;
    private int linkId = 0;
    private URL rootURL=null;
    private String pathToDefaultDirectory;

    /**
     * Initializes dir for save web page;
     * */
    public DownloaderWebPage(URL url) {
        this.rootURL=url;
        this.url = url;
        linksHolder=new LinksHolder();
        File f=new File(PATH_TO_DEFAULT_DIR+url.getFile());
        f.mkdirs();
        pathToDefaultDirectory =f.getAbsolutePath()+"/";

    }

    public DownloaderWebPage(URL url, int numberLink, LinksHolder linksHolder,String pathToDefualtDirrectory) {
        this.url=url;
        this.linkId = numberLink;
        this.linksHolder=linksHolder;
        this.pathToDefaultDirectory =pathToDefualtDirrectory;
        System.out.println("Download the link number " + numberLink+"URL "+url);
    }

    /**
     * each line of the method checks for links
     * each line is stored in the file that contains the original page
     */
    @Override
    public Void call() throws Exception {
        String line;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), "UTF-8"));
             Storage<?> saver=createSaver()) {
            while ((line = reader.readLine()) != null) {
                saver.write(line);
                List<String> links = SearcherLink.getLinks(line);
                if (linkId == 0) {
                    linksHolder.addLinkForSearch(links);
                } else {
                    linksHolder.addMetaLinks(links);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(EXCEPTION_DOWNLOAD_WEB_PAGE+url.toString());
        }

        //if is root page, download child pages
        if (linkId == 0) {
            asyncProcessChildPages();
            saveChildLinks();
        }
        return null;
    }

    public static void stop() {
        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        } finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }

           executor.shutdownNow();

            System.out.println("shutdown finished");
        }
    }

    /**
     * Method save additional link
     */
    private void saveChildLinks() {
        if (linkId == 0) {
            try (FileOutputStream fileWriter = new FileOutputStream(pathToDefaultDirectory + "additionalLink.txt")) {
                for (String s : linksHolder.getMetaLinks()) {
                    fileWriter.write(s.getBytes());
                    fileWriter.write(SEPARATOR);
                }
            } catch (IOException e) {
                System.err.println(EXCEPTION_WRITE_ADDITIONAL_LINK);
            }
        }
    }

    private void asyncProcessChildPages() {
        try {
            for (Future<Void> future : executor.invokeAll(getTasks())) {
                future.get(1L, TimeUnit.SECONDS);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private List<DownloaderWebPage> getTasks() {
        List<DownloaderWebPage> tasks = new ArrayList<>();
        int numb = 0;
        URL tempURL;
        while (linksHolder.hasNext()) {
            String link = linksHolder.nextLink();
            if ((tempURL =createURL(link))!=null)
                tasks.add(downloaderForNextLink(tempURL, ++numb));
        }
        return tasks;
    }

    /**
     * if the URL has been created successfully,
     * it returns new URL otherwise null
     * */
    @Nullable
    private URL createURL(String link) {
        try {
            if (link.startsWith("/")) {
                return new URL(rootURL, link);
            }
            return new URL(link);
        }catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private DownloaderWebPage downloaderForNextLink(URL url,int numberLink) {
        return new DownloaderWebPage(url, numberLink, linksHolder, pathToDefaultDirectory);
    }

    private Storage<?> createSaver() throws IOException {
            return new StorageInFile(pathToDefaultDirectory +"File from link number"+linkId);
    }
}
