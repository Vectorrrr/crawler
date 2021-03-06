package service.storage;

import java.io.*;
import java.net.URL;

/**
 * Class allows to save result in file
 * @author Gladush Ivan
 * @since 18.03.16.
 */
public class StorageInFile implements Storage {
    private static final String EXCEPTION_CREATE_FILE = "I can't create file ";
    private static final String EXCEPTION_WRITE_IN_FILE = "I can't write in file ";
    private static final String PATH_TO_DEFAULT_DIR = "./Downloaded Sites/";
    private static final String META_FILE_NAME = "meta";
    private static final String SEPARATOR = "\n";

    /**
     * when the class is loaded into the program,
     * it checks the availability of a directory by
     * default for saving files
     * */
    static {
        File defaultDirectory = new File(PATH_TO_DEFAULT_DIR);
        if (!defaultDirectory.exists()) {
            defaultDirectory.mkdirs();
        }
    }



    /**
     * Method creates a file which will save the loaded page
     * and return absolute path to this page
     */
    @Override
    public String writePage(URL url) {
        File f = getFile(url.getFile());
        return f.getAbsolutePath();
    }

    /**
     * method creates a special file which stores the shortcuts
     */
    @Override
    public void writeLinks(String rootPagePath, String[] value) {
        try (FileWriter fw = new FileWriter(getFile(rootPagePath + META_FILE_NAME))) {
            for (String s : value) {
                writeLine(s, fw);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method appends to the end of the file line
     * by setting a new record in a carriage beginning of the line
     */
    @Override
    public void writeContent(String siteFilePath, String content) {
        try (FileWriter fw = new FileWriter(siteFilePath, true)) {
            writeLine(content, fw);
        } catch (IOException e) {
            throw new IllegalStateException(EXCEPTION_WRITE_IN_FILE + e.getMessage());
        }
    }

    private void writeLine(String content, FileWriter fw)
            throws IOException {
        fw.write(content);
        fw.write(SEPARATOR);
    }


    private File getFile(String pageName) {
        pageName=pageName.replaceAll("/","");
        File f = new File(PATH_TO_DEFAULT_DIR + pageName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            throw new IllegalArgumentException(EXCEPTION_CREATE_FILE + e.getMessage());
        }
        return f;
    }


    @Override
    public void close() throws IOException {
    }
}
