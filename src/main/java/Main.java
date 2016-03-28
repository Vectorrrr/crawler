import service.ControllerCorrectScheme;
import service.downloads.Crawling;
import service.producer.ClassProducer;
import service.producer.url.ProducerURL;

import java.io.File;


import static service.property.loader.CrawlerProperties.*;
import org.apache.log4j.Logger;
/**
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class Main {
    private static final String EXCEPTION_NOT_CORRECT_XML_SHEM = "Your xml doesn't correct";
    private static final Logger log = Logger.getLogger(Main.class);
    public static final String EXCEPTION_DOWNLOAD_PAGE = "Exception in download page %s";

    //todo write strings
    public static void main(String[] args) {
        if (!ControllerCorrectScheme.isCorrectXmlScheme(ClassLoader.getSystemResource(property("file.configuration.scheme")).getFile(),
                ClassLoader.getSystemResource(property("file.configuration.name")).getFile())) {
            log.error(EXCEPTION_NOT_CORRECT_XML_SHEM);
            throw new IllegalArgumentException(EXCEPTION_NOT_CORRECT_XML_SHEM);
        }

        ClassProducer classProducer = ClassProducer.initClassProducer(new File(ClassLoader.getSystemResource(property("file.configuration.name")).getFile()));

        try (Crawling crawling = (Crawling) classProducer.getInstance("Crawling")) {
            ProducerURL producerURL = (ProducerURL) classProducer.getInstance("urlProducer");
            if (crawling.crawling(producerURL.getURL())) {
                log.info("Crawling was successful");
            } else {
                log.info("Crawling was not successful");
            }
        } catch (Exception e) {
            log.error(String.format(EXCEPTION_DOWNLOAD_PAGE, e.getMessage()));
        }
    }
}



