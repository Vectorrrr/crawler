package service.downloads;

import java.net.URL;
import java.util.concurrent.Callable;

/**
 * All class who implement  that interface
 * must allow search link in web
 * @author Gladush Ivan
 * @since 24.03.16.
 */

public interface Crawling extends Callable<Void>,Cloneable,AutoCloseable {
    void setUrl(URL url);

}
