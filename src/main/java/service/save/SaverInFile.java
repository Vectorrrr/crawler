package service.save;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class save some text in default directory
 * When class load in project he check the default directory exist,
 * if default directory doest exist this class create default directory
 * @author Gladush Ivan
 * @since 16.03.16.
 */
public class SaverInFile {
    private static final String EXCEPTION_CREATE = "When I try created file, I have exception!";
    private static final String EXCEPTION_FOUND_FILE = "When I try found file I can't do it";
    private static final String EXCEPTION_SAVED_FILE = "When I try save web page in file I have exception";
    private static final String PATH_TO_SAVE_DIR = "./Downloaded Sites/";

    static {
        File f = new File(PATH_TO_SAVE_DIR);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static void saveInFile(String fileName, String... savedText) {
        File f = new File(PATH_TO_SAVE_DIR + fileName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(EXCEPTION_CREATE);
            }
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(f)) {
            for (String s : savedText) {
                fileOutputStream.write(s.getBytes());
                fileOutputStream.write("\n".getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException(EXCEPTION_FOUND_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(EXCEPTION_SAVED_FILE);
        }
    }
}
