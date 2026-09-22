package mono.testutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Resets the storage file used inside Gradle's isolated test working directory.
 */
public final class StorageTestSupport {
    private static final Path STORAGE_FILE = Path.of("data", "tasks.txt");

    private StorageTestSupport() {
    }

    /**
     * Deletes stored test tasks so each test starts with an empty task list.
     */
    public static void resetStorage() throws IOException {
        Files.deleteIfExists(STORAGE_FILE);
    }
}
