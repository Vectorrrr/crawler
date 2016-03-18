package service;

import com.sun.javafx.scene.layout.region.Margins;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Gladush Ivan
 * @since 18.03.16.
 */
public class PropertiesLoader {
    private static final String pathToPropertyFile=new File("src").getAbsolutePath()+ new File("/main/resources/system.settings.properties").getPath();
    private static Properties properties=new Properties();

    static {
        try {
            properties.load(new FileInputStream(pathToPropertyFile));
        } catch (IOException e) {
            throw new IllegalStateException("I can't download properties",e);
        }
    }

    public static int getCountThread(){
        return Integer.parseInt(String.valueOf(properties.get("amount.thread.in.pull")));
    }
    public static String getDbDriver(){
        return (String) properties.get("db.driver.name");
    }
    public static String getDbPassword(){
        return (String)properties.get("db.user.password");
    }
    public static String getDbUserName(){
        return (String)properties.get("db.user.name");
    }
    public static String getDbUrlAddress(){
            return (String)properties.get("db.url.address");
    }
    public static String getDataBaseName(){ return (String)properties.get("database.name");}
}
