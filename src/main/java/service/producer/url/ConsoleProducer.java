package service.producer.url;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class allow read line from console
 * @author Gladush Ivan
 * @since 21.03.16.
 */
public class ConsoleProducer implements Producer {
    private final Scanner sc = new Scanner(System.in);

    @Override
    public String getURL() {
        System.out.println("Input Url site ");
        return sc.nextLine();
    }

    @Override
    public void close() throws IOException {
        sc.close();
    }

}
