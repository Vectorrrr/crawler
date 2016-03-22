package service.property.loader;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * class loads the settings for the connection,
 * as well as requests for the creation of databases
 * and tables
 * @author Gladush Ivan
 * @since 18.03.16.
 */
public class SystemSettingsLoader {
    private static final String EXCEPTION_DOWNLOAD_PROPERTI="I can't download properties";
    private static final String PATH_TO_PROPERTY_FILE = new File("src").getAbsolutePath() + new File("/main/resources/system.settings.properties").getPath();
    private static Properties properties = new Properties();

    static {
        try {
            properties.load(new FileInputStream(PATH_TO_PROPERTY_FILE));
        } catch (IOException e) {
            throw new IllegalStateException(EXCEPTION_DOWNLOAD_PROPERTI, e);
        }
    }

    public static int getCountThread() {
        return Integer.parseInt(String.valueOf(properties.get("amount.thread.in.pull")));
    }

    public static String getDbDriver() {
        return (String) properties.get("db.driver.name");
    }

    public static String getDbPassword() {
        return (String) properties.get("db.user.password");
    }

    public static String getDbUserName() {
        return (String) properties.get("db.user.name");
    }

    public static String getDbUrlAddress() {
        return (String) properties.get("db.url.address");
    }

    public static String getDataBaseName() {
        return (String) properties.get("database.name");
    }

    public static String fileConfigurationScheme() {
        return (String) properties.get("file.configuration.scheme");
    }

    public static String fileConfigurationName() {
        return (String) properties.get("file.configuration.name");
    }

    public static String[] getNeedTables() {
        return ((String) properties.get("need.tables")).split("-");
    }

    public static String getProperty(String propertyKey) {
        return (String) properties.get(propertyKey);
    }

    public static String getCreateDataBaseQuery() {
        return (String)properties.get("create.database.query");
    }

    public static String getShowDatabas() {
        return (String) properties.get("show.database.query");
    }

    public static String getStorageDirectory() {
        return (String)properties.get("storage.directory");
    }

    public static String getProducerDirectory() {
        return (String) properties.get("producer.directory");
    }
}
