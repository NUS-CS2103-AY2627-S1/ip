package quu.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import quu.exception.InvalidFileContents;
import quu.exception.SaveFileException;
import quu.task.TaskList;
import quu.task.ToDo;

/**
 * Tests that {@link Storage} can persist tasks and reconstruct them from a file.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void writeFile_thenReadFile_roundTripsTask()
            throws IOException, InvalidFileContents, SaveFileException {
        Path file = tempDir.resolve("quu_test.txt");
        Storage storage = new Storage(file.toString());

        TaskList original = new TaskList();
        original.addTask(new ToDo("read book"));

        storage.writeFile(original);
        TaskList loaded = storage.readFile();

        assertEquals(1, loaded.getSize());
        assertTrue(loaded.getTaskAt(0).toString().contains("read book"));
    }

    @Test
    public void readFile_pathIsAFolder_reportsItCannotBeUsed() throws IOException {
        Path folder = Files.createDirectory(tempDir.resolve("Quu.txt"));
        Storage storage = new Storage(folder.toString());

        SaveFileException exception = assertThrows(SaveFileException.class, storage::readFile);
        assertTrue(exception.getMessage().contains("is a folder, not a file"));
    }

    @Test
    public void readFile_saveFileAbsent_reportsItIsMissingRatherThanUnusable() {
        Storage storage = new Storage(tempDir.resolve("not_created_yet.txt").toString());
        assertThrows(FileNotFoundException.class, storage::readFile);
    }

    @Test
    public void writeFile_parentPathIsAFile_reportsTheFailure() throws IOException {
        Path blockingFile = Files.createFile(tempDir.resolve("data"));
        Storage storage = new Storage(blockingFile.resolve("Quu.txt").toString());

        assertThrows(IOException.class, () -> storage.writeFile(new TaskList()));
    }

    @Test
    public void readFile_unknownTaskType_reportsTheLineAsCorrupted() throws IOException {
        Path file = tempDir.resolve("corrupt.txt");
        Files.writeString(file, "Z | 0 | mystery" + System.lineSeparator());

        assertThrows(InvalidFileContents.class, new Storage(file.toString())::readFile);
    }
}
