import service.ControllerCorrectScheme;
import service.downloads.Crawling;
import service.producer.ClassProducer;
import service.producer.url.ProducerURL;
 import static service.property.loader.PropertyLoader.*;

/**
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class Main {
    private static final String EXCEPTION_NOT_CORRECT_XML_SHEM = "Your xml doesn't correct";

    public static void main(String[] args) {
        if (!ControllerCorrectScheme.isCorrectXmlScheme(getProperty("file.configuration.scheme"), getProperty("file.configuration.name"))) {
            throw new IllegalArgumentException(EXCEPTION_NOT_CORRECT_XML_SHEM);
        }
        ClassProducer classProducer = new ClassProducer();
        try (Crawling crawling = (Crawling) classProducer.getInstance("Crawling")) {
            ProducerURL producerURL = (ProducerURL) classProducer.getInstance("urlProducer");
          if(crawling.crawling(producerURL.getURL())){
              System.out.println("Crawling was successful");
          }else{
              System.out.println("Crawling was not successful");
          }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
