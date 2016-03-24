package service.producer.url;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * This class allow read line from console
 * @author Gladush Ivan
 * @since 21.03.16.
 */
public class ConsoleProducer implements ProducerURL {
    private final Scanner sc = new Scanner(System.in);

    @Override
    public URL getURL() {
        System.out.println("Input Url site ");
        String s=sc.nextLine();
        try{
            return new URL(s);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("You input incorrect url becouse "+ e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        sc.close();
    }

}
