package kibo.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Manages disposable storage fixtures only inside Gradle's dedicated test working directory.
 */
public abstract class StorageTestSupport {
    protected static final Path DATA_DIRECTORY = Path.of("data");
    protected static final Path SAVE_PATH = DATA_DIRECTORY.resolve("duke.txt");
    protected static final Path TEMPORARY_PATH = DATA_DIRECTORY.resolve("duke.txt.tmp");
    private boolean isWorkspaceVerified;

    @BeforeEach
    void prepareStorage() throws IOException {
        String workspace = System.getProperty("kibo.test.workspace");
        assertNotNull(workspace, "Run storage tests through Gradle to isolate test data");
        assertEquals(Path.of(workspace).toAbsolutePath().normalize(), Path.of("").toAbsolutePath().normalize());
        isWorkspaceVerified = true;
        clearStorage();
    }

    @AfterEach
    void cleanStorage() throws IOException {
        if (isWorkspaceVerified) {
            clearStorage();
        }
    }

    /**
     * Writes a complete save-file fixture.
     *
     * @param contents saved task records.
     * @throws IOException if the fixture cannot be written.
     */
    protected void writeFixture(String contents) throws IOException {
        Files.createDirectories(DATA_DIRECTORY);
        Files.writeString(SAVE_PATH, contents);
    }

    /**
     * Causes save attempts to fail consistently without relying on operating-system permissions.
     *
     * @throws IOException if the blocking directory cannot be created.
     */
    protected void blockSaving() throws IOException {
        Files.createDirectories(TEMPORARY_PATH);
    }

    private void clearStorage() throws IOException {
        if (Files.isDirectory(DATA_DIRECTORY)) {
            Files.deleteIfExists(TEMPORARY_PATH);
            Files.deleteIfExists(SAVE_PATH);
        }
        Files.deleteIfExists(DATA_DIRECTORY);
    }
}
