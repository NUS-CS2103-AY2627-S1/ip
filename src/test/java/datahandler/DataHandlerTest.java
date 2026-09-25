package datahandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for the {@link DataHandler} class.
 */
class DataHandlerTest {
    /** Temporary directory supplied by JUnit for safe file-system testing. */
    @TempDir
    Path tempDir;

    @Test
    void constructor_missingFolderAndFile_createsBoth() throws Exception {
        Path dataFolderPath = tempDir.resolve("data");
        Path dataFilePath = dataFolderPath.resolve("friedberg.txt");

        new DataHandler(dataFolderPath, dataFilePath);

        assertTrue(Files.exists(dataFolderPath));
        assertTrue(Files.isDirectory(dataFolderPath));
        assertTrue(Files.exists(dataFilePath));
    }

    @Test
    void write_dataWithSurroundingWhitespace_returnsCorrectData() throws Exception {
        Path dataFolderPath = tempDir.resolve("data");
        Path dataFilePath = dataFolderPath.resolve("friedberg.txt");
        DataHandler dataHandler = new DataHandler(dataFolderPath, dataFilePath);

        dataHandler.write("\n  just some data  \n");

        assertEquals("just some data", dataHandler.read());
    }

    @Test
    void read_fileWithSurroundingWhitespace_returnsCorrectData() throws Exception {
        Path dataFolderPath = tempDir.resolve("data");
        Path dataFilePath = dataFolderPath.resolve("friedberg.txt");
        DataHandler dataHandler = new DataHandler(dataFolderPath, dataFilePath);
        Files.writeString(dataFilePath, "\n  just some data  \n");

        assertEquals("just some data", dataHandler.read());
    }
}
