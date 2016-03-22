package service.save;

import java.io.Closeable;


/**
 * The interface allows you to save a link to the page,
 * the contents of a particular table, and the resulting links
 * @author Gladush Ivan
 * @since 18.03.16.
 */
public interface Storage extends Closeable,AutoCloseable {
     /**
      * retains specific reference table returns a reference
      * to the table in the preservation system
      */
     String writePage(String siteURL, String pageName);

     /**
      * writes a reference to a particular
      * database table obtained
      */
     void writeLinks(String rootPage, String[] value);

     /**
      * writes the contents of the page
      */
     void writeContent(String siteURL, String content);

     /**
      * creates a storage location
      */
     String createDir(String path);
}
