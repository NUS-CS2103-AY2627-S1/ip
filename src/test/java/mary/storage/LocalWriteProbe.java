package mary.storage;

import java.util.List;

import mary.Launcher;
import mary.exception.MaryException;
import mary.task.Todo;

/**
 * Exercises production write policies in a child process with a real working directory.
 */
public class LocalWriteProbe {
    /**
     * Runs a save or cache setup without allowing a test-only containment bypass.
     */
    public static void main(String[] args) {
        try {
            if (args[0].equals("cache")) {
                Launcher.configureLocalCaches();
                System.out.println("CACHE: " + System.getProperty("javafx.cachedir"));
                System.out.println("TEMP: " + System.getProperty("java.io.tmpdir"));
            } else {
                new Storage(args[1]).save(List.of(new Todo("local only")));
                System.out.println("SAVED");
            }
        } catch (MaryException exception) {
            System.out.println("REJECTED: " + exception.getMessage());
        }
    }
}
