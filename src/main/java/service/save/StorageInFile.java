package service.save;

import java.io.*;

/**
 * @author Gladush Ivan
 * @since 18.03.16.
 */
public class StorageInFile implements Storage<String> {
    private static final byte[] SEPARATOR = "\n".getBytes();
    private FileOutputStream fileOutputStream;

    public StorageInFile(String path) throws IOException {
        File f = new File(path);
        f.createNewFile();
        fileOutputStream = new FileOutputStream(f);
    }

    @Override
    public void write(String s) throws IOException {
        fileOutputStream.write(s.getBytes());
        fileOutputStream.write(SEPARATOR);
    }

    @Override
    public void close() throws IOException {
        fileOutputStream.close();
    }
}
