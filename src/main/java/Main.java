import service.downloads.Crawling;
import service.producer.ClassProducer;
import service.producer.url.ProducerURL;
import service.property.loader.PropertyLoader;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

/**
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class Main {
    private static final String EXCEPTION_NOT_CORRECT_XML_SHEM="Your xml doesn't correct";
    public static void main(String[] args) {
        if (!isCorrectShem("file.configuration.name")) {
            throw new IllegalArgumentException(EXCEPTION_NOT_CORRECT_XML_SHEM);
        }
        ClassProducer classProducer = new ClassProducer();
        try (Crawling crawling = (Crawling) classProducer.getInstance("Crawling")) {
            ProducerURL producerURL = (ProducerURL) classProducer.getInstance("urlProducer");
            crawling.setUrl(producerURL.getURL());
            crawling.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isCorrectShem(String fileName) {
        String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;

        String schemaName = PropertyLoader.getProperty("file.configuration.scheme");
        SchemaFactory factory = SchemaFactory.newInstance(language);
        File schemaLocation = new File(schemaName);

        try {
            Schema schema = factory.newSchema(schemaLocation);
            Validator validator = schema.newValidator();
            Source source = new StreamSource(fileName);
            validator.validate(source);

            System.out.println(fileName + " is valid.");
        } catch (IOException e) {
            System.err.print("validation " + fileName + " is not valid because "
                    + e.getMessage());
            return false;
        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
        return true;
    }

}
