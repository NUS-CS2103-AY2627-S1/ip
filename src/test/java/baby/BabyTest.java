package baby;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class BabyTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void getResponse_storageWriteFails_returnsErrorAndRestoresTaskList() {
        String originalPath = System.getProperty("baby.filePath");
        System.setProperty("baby.filePath", tempDirectory.toString());

        try {
            Baby baby = new Baby();

            String addResponse = baby.getResponse("todo read book");
            String listResponse = baby.getResponse("list");

            assertTrue(addResponse.contains("couldn't save your tasks"));
            assertTrue(listResponse.contains("here are the tasks in your list:\n"));
            assertFalse(listResponse.contains("read book"));
        } finally {
            if (originalPath == null) {
                System.clearProperty("baby.filePath");
            } else {
                System.setProperty("baby.filePath", originalPath);
            }
        }
    }
}
