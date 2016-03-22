package service.property.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class loads the queries necessary
 * to manipulate data in a database
 * @author Gladush Ivan
 * @since 22.03.16.
 */
public class SqlQueryLoader {
    private static final String pathToPropertyFile = new File("src").getAbsolutePath() + new File("/main/resources/sql.query.properties").getPath();
    private static Properties properties = new Properties();

    static {
        try {
            properties.load(new FileInputStream(pathToPropertyFile));
        } catch (IOException e) {
            throw new IllegalStateException("I can't download sql properties", e);
        }
    }


    public static String selectIdRootPage() {
        return (String) properties.get("select.id.root.page");
    }

    public static String insertLink() {
        return (String) properties.get("insert.link.query");
    }

    public static String insertLinkInPage() {
        return (String) properties.get("insert.link.in.page");
    }

    public static String writeContent() {
        return (String) properties.get("insert.root.content");
    }

    public static String insertRootPage() {
        return (String) properties.get("insert.root.page");
    }

    public static String selectMaxIdLink() {
        return (String) properties.get("select.max.id.link");
    }
}

