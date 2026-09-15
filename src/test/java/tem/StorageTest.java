package tem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests for {@link Storage}.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() throws TemException {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_directoryInsteadOfFile_throwsHelpfulException() {
        Storage storage = new Storage(tempDir.toString());

        TemException exception = assertThrows(TemException.class, storage::load);

        assertTrue(exception.getMessage().startsWith("Could not read saved tasks from "));
    }

    @Test
    public void save_directoryInsteadOfFile_throwsHelpfulException() {
        Storage storage = new Storage(tempDir.toString());

        TemException exception = assertThrows(TemException.class, () -> storage.save(List.of()));

        assertTrue(exception.getMessage().startsWith("Could not save tasks to "));
    }

    @Test
    public void saveAndLoad_roundTrip_preservesTasks() throws TemException {
        Path filePath = tempDir.resolve("tem.txt");
        Storage storage = new Storage(filePath.toString());

        Task todo = new Todo("read book");
        todo.markAsDone();
        Task deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        Task event = new Event("meeting", "Mon 2pm", "4pm");

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertTrue(loadedTasks.get(0) instanceof Todo);
        assertTrue(loadedTasks.get(0).isDone());
        assertEquals("read book", loadedTasks.get(0).getDescription());

        assertTrue(loadedTasks.get(1) instanceof Deadline);
        assertEquals(LocalDate.of(2019, 10, 15), ((Deadline) loadedTasks.get(1)).getBy());

        assertTrue(loadedTasks.get(2) instanceof Event);
        assertEquals("meeting", loadedTasks.get(2).getDescription());
    }

    @Test
    public void load_corruptLine_isSkipped() throws Exception {
        Path filePath = tempDir.resolve("tem.txt");
        Files.writeString(filePath, """
                T | 1 | read book
                not a valid line
                D | 0 | return book | 2019-10-15
                """);

        Storage storage = new Storage(filePath.toString());
        List<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertEquals("return book", loadedTasks.get(1).getDescription());
    }

    @Test
    public void saveAndLoad_specialCharactersInDescription_preservesText() throws TemException {
        Path filePath = tempDir.resolve("tem.txt");
        Storage storage = new Storage(filePath.toString());

        Task todo = new Todo("read | book");
        Task event = new Event("meet\\team", "Mon 2|pm", "4pm");

        storage.save(List.of(todo, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals("read | book", loadedTasks.get(0).getDescription());
        assertEquals("meet\\team", loadedTasks.get(1).getDescription());
        assertEquals("Mon 2|pm", ((Event) loadedTasks.get(1)).getFrom());
    }
}
