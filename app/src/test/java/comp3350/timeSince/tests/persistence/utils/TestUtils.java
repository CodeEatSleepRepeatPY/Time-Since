package comp3350.timeSince.tests.persistence.utils;

import java.io.File;

import comp3350.timeSince.application.Main;
import com.google.common.io.Files;

import java.io.IOException;


/**
 * @author Taken (and modified slightly) from cook ebook 2 and team rocket 15
 */
public class TestUtils {

    private static final File DB_SRC = new File("src/main/assets/db/TS.script");

    public static File copyDB() throws IOException {
        final File target = File.createTempFile("temp-db", ".script");
        Files.copy(DB_SRC, target);
        Main.setDBPathName(target.getAbsolutePath().replace(".script", ""));
        return target;
    }

}
