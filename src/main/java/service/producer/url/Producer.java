package service.producer.url;

import java.io.Closeable;

/**
 * @author Gladush Ivan
 * @since 21.03.16.
 */
public interface Producer extends AutoCloseable,Closeable{
    String getURL();
}
