package service.property.loader;
import java.io.*;
import java.util.Properties;

/**
 * class loads the settings for the connection,
 * as well as requests for the creation of databases
 * and tables
 * @author Gladush Ivan
 * @since 18.03.16.
 */
public class PropertyLoader {
    private static final String EXCEPTION_DOWNLOAD_PROPERTIES = "I can't download properties";
    private static final String EXCEPTION_PROPETIE_DONT_FOUND = "This property not exist";
    private static Properties properties = new Properties();

    static {
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("system.settings.properties"));
        } catch (IOException e) {
            throw new IllegalStateException(EXCEPTION_DOWNLOAD_PROPERTIES, e);
        }
    }


    public static String getProperty(String propertyKey) {
        String property = (String) properties.get(propertyKey);
        if (property == null) {
            throw new IllegalStateException(EXCEPTION_PROPETIE_DONT_FOUND);
        }
        return property;
    }

    public static String[] getNeedTables() {
        return (getProperty("need.tables")).split("-");
    }





}
