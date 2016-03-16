package service.downloads;

import service.searcherLinks.SearcherLink;
import service.linksHolder.LinksHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Class allow download page from web and also that class can do it
 * asynchronously
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class DownloaderWebPage implements Callable<String> {
    private static final String EXCEPTION_URL_NAME = "You input incorrect url";
    private static final String EXCEPTION_DOWNLOAD_WEB_PAGE = "When I try download  web-page  I have exception";
    private static final String EMPTY_STRING="";

    /**
     * if it is the main, the all link, that finds on this page
     * we need add to add to the list for further processing
     */
    private boolean isHead;
    private URL url;
    private LinksHolder linksHolder;

    public DownloaderWebPage(String s, boolean isHead, LinksHolder linksHolder) {
        this.isHead = isHead;
        this.linksHolder = linksHolder;
        try {
            url = new URL(s);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(EXCEPTION_URL_NAME);
        }
    }

    public String getWebPage() {
        StringBuilder sb = new StringBuilder();
        String line;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), "UTF-8"))) {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
                List<String> links = SearcherLink.gettLinks(line);

                if (isHead) {
                    linksHolder.addNewLinks(links);
                } else {
                    linksHolder.setAdditionalLinks(links);
                }
            }
        } catch (IOException e) {
            System.err.println(EXCEPTION_DOWNLOAD_WEB_PAGE);
            return EMPTY_STRING;
        }

        return sb.toString();
    }

    @Override
    public String call() throws Exception {
        return this.getWebPage();
    }
}
