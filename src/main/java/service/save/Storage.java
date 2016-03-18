package service.save;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Gladush Ivan
 * @since 18.03.16.
 */
public interface Storage<T> extends Closeable,AutoCloseable {
     void write(String s) throws IOException;
}
