import service.downloads.DownloaderWebPage;
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
            new DownloaderWebPage(siteName, 100).call();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("When I try download page I have exception");
        }


        DownloaderWebPage.stop();
        sc.close();

    }
}
