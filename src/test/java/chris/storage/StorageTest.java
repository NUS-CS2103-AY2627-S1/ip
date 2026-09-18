package chris.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chris.exception.ChrisException;
import chris.task.Deadline;
import chris.task.Event;
import chris.task.Task;
import chris.task.Todo;

/**
 * Tests loading and saving tasks through file storage.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies that loading from a missing file produces an empty task list.
     *
     * @throws ChrisException If loading unexpectedly fails.
     */
    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws ChrisException {
        Storage storage = new Storage(this.temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    /**
     * Verifies that saved tasks and their completion states can be restored.
     *
     * @throws ChrisException If saving or loading unexpectedly fails.
     */
    @Test
    public void saveAndLoadTasks_validTasks_restoresTasks() throws ChrisException {
        Storage storage = new Storage(this.temporaryDirectory.resolve("data").resolve("chris.txt"));
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));

        Event event = new Event("project meeting", "Monday 2pm", "Monday 4pm");

        storage.saveTasks(List.of(todo, deadline, event));
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 2 2019)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Monday 2pm to: Monday 4pm)",
                loadedTasks.get(2).toString());
    }

    /**
     * Verifies that blank lines in an otherwise valid data file are ignored.
     *
     * @throws IOException If the test data cannot be prepared.
     * @throws ChrisException If the valid stored task is unexpectedly rejected.
     */
    @Test
    public void loadTasks_fileContainingBlankLines_ignoresBlankLines()
            throws IOException, ChrisException {
        Path filePath = this.temporaryDirectory.resolve("chris.txt");
        Files.writeString(filePath, System.lineSeparator() + "T | 0 | read book"
                + System.lineSeparator() + System.lineSeparator());

        ArrayList<Task> tasks = new Storage(filePath).loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    /**
     * Verifies that an unknown task type is reported as corrupted data.
     *
     * @throws IOException If the test data cannot be prepared.
     */
    @Test
    public void loadTasks_unknownTaskType_throwsChrisException() throws IOException {
        Path filePath = this.temporaryDirectory.resolve("chris.txt");
        Files.writeString(filePath, "X | 0 | mystery task");

        ChrisException exception = assertThrows(
                ChrisException.class, () -> new Storage(filePath).loadTasks());

        assertEquals("The saved data is invalid on line 1.", exception.getMessage());
    }

    /**
     * Verifies that an invalid stored completion flag is rejected.
     *
     * @throws IOException If the test data cannot be prepared.
     */
    @Test
    public void loadTasks_invalidCompletionFlag_throwsChrisException() throws IOException {
        Path filePath = this.temporaryDirectory.resolve("chris.txt");
        Files.writeString(filePath, "T | 2 | read book");

        assertThrows(ChrisException.class, () -> new Storage(filePath).loadTasks());
    }

    /**
     * Verifies that an invalid stored deadline date is rejected.
     *
     * @throws IOException If the test data cannot be prepared.
     */
    @Test
    public void loadTasks_invalidDeadlineDate_throwsChrisException() throws IOException {
        Path filePath = this.temporaryDirectory.resolve("chris.txt");
        Files.writeString(filePath, "D | 0 | return book | 2019-02-30");

        assertThrows(ChrisException.class, () -> new Storage(filePath).loadTasks());
    }
}
