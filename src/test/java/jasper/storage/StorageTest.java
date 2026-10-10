package jasper.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jasper.JasperException;
import jasper.task.TaskList;
import jasper.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_fileDoesNotExist_fileCreatedAndEmptyListReturned() throws JasperException {
        Path saveFile = tempDir.resolve("doesNotExist.txt");
        Storage storage = new Storage(tempDir.toString(), "doesNotExist.txt");
        TaskList tasks = storage.load();
        assertTrue(Files.exists(saveFile));
        assertEquals(0, tasks.getCount());
    }

    @Test
    void load_invalidLines_invalidLinesSkipped() throws Exception {
        Path saveFile = tempDir.resolve("corrupted.txt");
        List<String> lines = List.of("T | 0 | valid todo", "INVALID LINE", "T | 1 | another valid");
        Files.write(saveFile, lines);
        Storage storage = new Storage(tempDir.toString(), "corrupted.txt");
        TaskList tasks = storage.load();
        assertEquals(2, tasks.getCount());
    }

    @Test
    void save_validTaskList_fileWrittenTo() throws Exception {
        Storage storage = new Storage(tempDir.toString(), "saveTest.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("test save"));
        storage.save(tasks);

        Path expectedFile = tempDir.resolve("saveTest.txt");
        assertTrue(Files.exists(expectedFile));
        List<String> writtenLines = Files.readAllLines(expectedFile);
        assertEquals(1, writtenLines.size());
        assertEquals("T | 0 | test save", writtenLines.get(0));
    }
}
