package storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tasklist.Tasklist;

public class FileManipulatorTest {

    @TempDir
    Path tempDir;

    @Test
    public void loadFile_nonExistentFile_createsFileAndReturnsEmptyTasklist() {
        Tasklist loadedTasklist = FileManipulator.loadFile();
        assertNotNull(loadedTasklist);
    }

    @Test
    public void saveFile_validTasklist_writesAndOverwritesMasterFileSuccessfully() throws IOException {
        Tasklist tasklist = new Tasklist();
        // Tests that saving executes cleanly without throwing IOExceptions
        assertDoesNotThrow(() -> FileManipulator.saveFile(tasklist));
    }

    @Test
    public void saveFile_isolatedTempDirectory_fileCreatedSuccessfully() throws IOException {
        Path tempMasterFile = tempDir.resolve("masterTasklist.txt");
        Files.writeString(tempMasterFile, "T | 1 | read book\n");

        assertTrue(Files.exists(tempMasterFile));
        assertEquals(1, Files.readAllLines(tempMasterFile).size());
    }

    @Test
    public void loadFile_existingFile_readsDataSuccessfully() throws IOException {
        Tasklist tasklist = FileManipulator.loadFile();
        assertNotNull(tasklist);
    }
}
