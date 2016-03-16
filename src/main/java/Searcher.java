import service.downloads.DownloaderWebPage;
import service.linksHolder.LinksHolder;
import service.save.SaverInFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * While class work he check a new link for search
 * if he find new link he run new thread for this search
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class Searcher extends Thread {
    private static final String EXCEPTION_INTERRUPTED_FUTURE = "When I want get future value I have Interrupted exception";
    private static final String EXCEPTION_EXECUTION_FUTURE = "When I want get future value I have Execution exception";
    private boolean running = true;
    private LinksHolder linksHolder;

    public Searcher(LinksHolder linksHolder) {
        this.linksHolder = linksHolder;
    }

    @Override
    public void run() {
        ExecutorService executionServer = Executors.newFixedThreadPool(10);
        List<Future<String>> tasks = new ArrayList<>();
        List<String> links = new ArrayList<>();

        while (running || linksHolder.countProcessingLink() != 0) {
            if (linksHolder.countProcessingLink() > 0) {
                String link = linksHolder.getNextLink();
                link = link.substring(6, link.length() - 1);
                System.out.println("Start processing link: " + link);
                if(!link.startsWith("http://")){
                    link="http://"+link;
                }

                try {
                    DownloaderWebPage dwp = new DownloaderWebPage(link, false, linksHolder);
                    tasks.add(executionServer.submit(dwp));
                } catch (IllegalArgumentException e) {
                    System.err.println("Not successful start link by name " + link);

                    continue;
                }
                links.add(link);

            }
        }
        saveAnswers(tasks, links);
        saveAdditionalLinks();
        executionServer.shutdown();
        executionServer.shutdownNow();
        System.out.println("Stop searcher");
    }

    private void saveAdditionalLinks() {
        SaverInFile.saveInFile("Add links.txt",linksHolder.getAllAdditionalLinks());
    }

    /**
     * Method take all tasks and save the result work this task
     * in file which is located in default directory
     * the file name creat by link+ number this link in sequence
     * */
    private void saveAnswers(List<Future<String>> tasks, List<String> links) {
        for (int i = 0; i < links.size(); ++i) {
            System.out.println(links.size()+"  "+i);
            String text;
            try {
                text = tasks.get(i).get();
            } catch (InterruptedException e) {
                System.err.println(EXCEPTION_INTERRUPTED_FUTURE);
               // e.printStackTrace();
                continue;
            } catch (ExecutionException e) {
                System.err.println(EXCEPTION_EXECUTION_FUTURE);
              //  e.printStackTrace();
                continue;
            }
            if (text.length() > 0) {
                SaverInFile.saveInFile("Page by link number " + i, text);
            }
        }

    }

    public void stopRunning() {
        this.running = false;
    }
}
