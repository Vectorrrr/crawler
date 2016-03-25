package service;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

/**
 * Class checks some XML
 * file to correct for any scheme
 * @author Gladush Ivan
 * @since 25.03.16.
 */
public class ControllerCorrectScheme {

    /**
     * Method checks correct XML scheme by xsd scheme
     * */
    public static boolean isCorrectXmlScheme(String pathToScheme, String pathToFile) {
        String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;

        SchemaFactory factory = SchemaFactory.newInstance(language);
        File schemaLocation = new File(pathToScheme);

        try {
            Schema schema = factory.newSchema(schemaLocation);
            Validator validator = schema.newValidator();
            Source source = new StreamSource(pathToFile);
            validator.validate(source);

            System.out.println(pathToFile + " is valid.");
        } catch (IOException | org.xml.sax.SAXException e) {
            System.err.print("validation " + pathToFile + " is not valid because "
                    + e.getMessage());
            return false;
        }
        return true;
    }
}
