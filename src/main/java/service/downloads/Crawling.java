package service.downloads;

import java.io.Closeable;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 *  Crawling method to perform some operations,
 *  and returns these operations were carried
 *  out correctly or not
 * @author Gladush Ivan
 * @since 24.03.16.
 */
public interface Crawling extends Callable<Void>,Closeable,AutoCloseable {
    boolean crawling(URL url);
}
