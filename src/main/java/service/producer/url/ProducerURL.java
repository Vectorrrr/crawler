package service.producer.url;

import java.io.Closeable;
import java.net.URL;

/**
 * @author Gladush Ivan
 * @since 21.03.16.
 */
//todo think about generics
public interface ProducerURL extends AutoCloseable,Closeable{
    URL getURL();
}
