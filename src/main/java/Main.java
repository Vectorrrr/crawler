import service.downloads.DownloaderWebPage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Input url site");
        String siteName = sc.nextLine();
        try {
            URL url = new URL(siteName);
            new DownloaderWebPage(url).call();

        } catch (Exception e) {
            System.err.println("You input incorrect site");
            return;
        } finally {
            DownloaderWebPage.stop();
            sc.close();
        }

    }

}

