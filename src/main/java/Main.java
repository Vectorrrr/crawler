import service.ControllerCorrectScheme;
import service.downloads.Crawling;
import service.producer.ClassProducer;
import service.producer.url.ProducerURL;

import java.io.File;

import static service.property.loader.CrawlerProperties.*;

/**
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class Main {
    private static final String EXCEPTION_NOT_CORRECT_XML_SHEM = "Your xml doesn't correct";
    //todo write strings
    public static void main(String[] args) {
        if (!ControllerCorrectScheme.isCorrectXmlScheme(ClassLoader.getSystemResource(property("file.configuration.scheme")).getFile(),
                                ClassLoader.getSystemResource(property("file.configuration.name")).getFile())) {
            throw new IllegalArgumentException(EXCEPTION_NOT_CORRECT_XML_SHEM);
        }
        ClassProducer classProducer = ClassProducer.initClassProducer(new File(ClassLoader.getSystemResource(property("file.configuration.name")).getFile()));
        try (Crawling crawling = (Crawling) classProducer.getInstance("Crawling")) {
            ProducerURL producerURL = (ProducerURL) classProducer.getInstance("urlProducer");
            if (crawling.crawling(producerURL.getURL())) {
                System.out.println("Crawling was successful");
            } else {
                System.out.println("Crawling was not successful");
            }
        } catch (Exception e) {
            e.printStackTrace();


        }
    }
}



