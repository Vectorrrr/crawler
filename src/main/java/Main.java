import configuration.Configuration;
import service.property.loader.SystemSettingsLoader;
import service.downloads.DownloaderWebPage;
import service.producer.URL.Producer;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class Main {



    public static void main(String[] args) throws Exception {
        String fileConfigurationName= SystemSettingsLoader.fileConfigurationName();
        JAXBContext jc=JAXBContext.newInstance(Configuration.class);
        Configuration configuration;
        String siteName;
        if(!isCorrectShem(fileConfigurationName)){
            System.out.println("Your file isn't correct");
            return;
        }
        try {
            Unmarshaller u = jc.createUnmarshaller();
            configuration = (Configuration) u.unmarshal(new File(fileConfigurationName));
            System.out.println(configuration);
            System.out.println(configuration.getNewStorage());
            Producer p=configuration.getProvider();
            siteName=p.getURL();

        } catch (JAXBException e) {
           throw new IllegalArgumentException(e);
        }
        DownloaderWebPage dwp=null;
        try {
            URL url = new URL(siteName);
            dwp  =  new DownloaderWebPage(url,configuration.getNewStorage());
            dwp.call();
        } catch (Exception e) {

            System.err.println("You input incorrect site"+e.getMessage());
        } finally {
            if (dwp != null) {
                dwp.stop();
            }

        }



    }
    private static boolean isCorrectShem(String fileName) {
        String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;

        String schemaName = SystemSettingsLoader.fileConfigurationScheme();
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
