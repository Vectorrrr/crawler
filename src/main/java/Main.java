import service.downloads.DownloaderWebPage;
import service.linksHolder.LinksHolder;
import java.util.Scanner;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

/**
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class Main {
    public static void main(String[] args) {
        LinksHolder lh = new LinksHolder();
        Scanner sc = new Scanner(System.in);

        System.out.println("Input url site");
        String siteName = sc.nextLine();
        try {
            new DownloaderWebPage(siteName, 100).call();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("When I try download page I have exception");
        }


        DownloaderWebPage.stop();
        sc.close();

    }
}
