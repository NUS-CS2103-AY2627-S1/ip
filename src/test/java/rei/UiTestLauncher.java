package rei;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Starts Rei with a clean data file so UI test cases remain independent. */
public class UiTestLauncher {
    public static void main(String[] args) throws IOException {
        Path testDataFile = Path.of("data", "ui-test.txt");
        Files.deleteIfExists(testDataFile);
        System.setProperty("rei.data.file", testDataFile.toString());
        Rei.main(args);
    }
}
