package service.downloads;

import service.searcherLinks.SearcherLink;
import service.linksHolder.LinksHolder;

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
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class DownloaderWebPage implements Callable<LinksHolder> {
    private static final String EXCEPTION_URL_NAME = "You input incorrect url";
    private static final String EXCEPTION_DOWNLOAD_WEB_PAGE = "When I try download  web-page  I have exception";
    private static final String EXCEPTION_CREATE = "When I try created file, I have exception!";
    private static final String EXCEPTION_GET_FUTURE = "I could not save this page correctly";
    private static final String EXCEPTION_WRITE_ADDITIONAL_LINK = "When I saved the additional links error occurred";
    private static final String EXCEPTION_INVOKE_ALL = "When I try search additional page I have exception";
    private static final String PATH_TO_DEFAULT_DIR = "./Downloaded Sites/";
    private static final byte[] SEPARATOR = "\n".getBytes();

    /**
     * verifies the existence of the folder to store the default
     * if it does not create it
     */
    static {
        File defaultDirectory = new File(PATH_TO_DEFAULT_DIR);
        if (!defaultDirectory.exists()) {
            defaultDirectory.mkdirs();
        }
    }


    private static ExecutorService executor = null;

    /**
     * if it is the main, the all link, that finds on this page
     * we need add to add to the list for further processing
     */
    private URL url;
    private LinksHolder linksHolder;
    private int numberLink = 0;

    public DownloaderWebPage(String url, int countTread) {
        executor = Executors.newFixedThreadPool(countTread);
        this.linksHolder = new LinksHolder();
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(EXCEPTION_URL_NAME);
        }
    }

    public DownloaderWebPage(String url, int numberLink, LinksHolder linksHolder) {
        this(url, 10);
        this.linksHolder = linksHolder;
        this.numberLink = numberLink;
        System.out.println("Download the link number " + numberLink);
    }


    /**
     * each line of the method checks for links
     * each line is stored in the file that contains the original page
     */
    @Override
    public LinksHolder call() throws Exception {
        String line;
        File answerFile = createAnswerFile(PATH_TO_DEFAULT_DIR + "Page from link number" + numberLink + ".txt");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), "UTF-8"));
             FileOutputStream fileOutputStream = new FileOutputStream(answerFile)) {

            while ((line = reader.readLine()) != null) {
                fileOutputStream.write(line.getBytes());//save line in file
                fileOutputStream.write(SEPARATOR);      //move on next line

                List<String> links = SearcherLink.getLinks(line);

                if (numberLink == 0) {
                    linksHolder.addNewLinks(links);
                } else {
                    linksHolder.setAdditionalLinks(links);
                }
            }


        } catch (IOException e) {
            System.err.println(EXCEPTION_DOWNLOAD_WEB_PAGE);
        }
        //if is root page, download additional pages
        if (numberLink == 0) {
            downloadSubsidiariesPages();
            saveAdditionalLinks();
        }
        return linksHolder;
    }

    public static void stop() {
        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
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
    private void saveAdditionalLinks() {
        if (numberLink == 0) {
            try (FileOutputStream fileWriter = new FileOutputStream(PATH_TO_DEFAULT_DIR + "additionalLink.txt")) {
                for (String s : linksHolder.getAllAdditionalLinks()) {
                    fileWriter.write(s.getBytes());
                    fileWriter.write(SEPARATOR);
                }
            } catch (IOException e) {
                System.err.println(EXCEPTION_WRITE_ADDITIONAL_LINK);
            }
        }
    }

    private void downloadSubsidiariesPages() {
        System.out.println("#########COUNT ALL LINKS ##########" + linksHolder.countProcessingLink());
        List<DownloaderWebPage> tasks = new ArrayList<>();
        List<Future<LinksHolder>> results = new ArrayList<>();
        int numb = 0;
        while (linksHolder.countProcessingLink() > 0) {
            System.out.println("!!!!!!!!!!!! " + numb);
            String link = linksHolder.getNextLink();
            System.out.println("~~~~~~~~~~~      \t" + link);
            try {
                DownloaderWebPage dwp = new DownloaderWebPage(link, ++numb, linksHolder);
                tasks.add(dwp);
            } catch (IllegalArgumentException e) {
                System.err.println("I can't download the link " + link);
            }
        }
        try {
            results = executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            System.err.println(EXCEPTION_INVOKE_ALL);
        }
        for (Future<LinksHolder> future : results) {

            try {
                linksHolder = future.get(1L, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                System.err.println(EXCEPTION_GET_FUTURE);
            }


        }
        saveAdditionalLinks();

    }

    /**
     * method checks the existence of the file and in the absence creates it
     */
    private File createAnswerFile(String path) {
        File f = new File(path);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new IllegalArgumentException(EXCEPTION_CREATE);
            }
        }
        return f;
    }
}
