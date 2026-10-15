package tblade.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tblade.exception.TBladeException;
import tblade.task.Deadline;
import tblade.task.Event;
import tblade.task.Task;
import tblade.task.Todo;

/**
 * Tests Storage's save/load round trip, including task completion status,
 * every task type, and its tolerance of corrupted lines in the data file.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() throws TBladeException {
        Storage storage = new Storage(tempDir.resolve("no-such-file.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_allTaskTypes_roundTripsExactly() throws TBladeException {
        Storage storage = new Storage(tempDir.resolve("duke.txt").toString());
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 3, 15));
        deadline.markAsDone();
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        List<Task> original = new ArrayList<>(List.of(todo, deadline, event));
        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[D][X] return book (by: Mar 15 2026)", loaded.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", loaded.get(2).toString());
    }

    @Test
    public void save_bareFilenameWithNoParentDirectory_doesNotThrow() throws TBladeException, IOException {
        String bareFileName = "storage-test-bare-file.txt";
        Storage storage = new Storage(bareFileName);
        try {
            storage.save(List.of(new Todo("read book")));

            assertEquals(1, storage.load().size());
        } finally {
            Files.deleteIfExists(Path.of(bareFileName));
        }
    }

    @Test
    public void load_corruptedLine_isSkippedButValidLinesRemain() throws TBladeException, IOException {
        Path dataFile = tempDir.resolve("duke.txt");
        Files.createDirectories(dataFile.getParent());
        Files.write(dataFile, List.of(
                "T | 0 | good todo",
                "D | 0 | broken deadline | not-a-date",
                "X | 0 | unknown task type"
        ));
        Storage storage = new Storage(dataFile.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("good todo", loaded.get(0).getDescription());
    }
}
