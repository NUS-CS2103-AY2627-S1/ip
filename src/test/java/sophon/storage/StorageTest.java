package sophon.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sophon.exception.SophonException;
import sophon.model.Deadline;
import sophon.model.Event;
import sophon.model.TaskList;
import sophon.model.Todo;

public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void loadTasks_missingFile_returnsEmptyList() throws IOException, SophonException {
        Storage storage = new Storage(temporaryDirectory.toString(), "missing.txt");

        assertEquals(0, storage.loadTasks().size());
    }

    @Test
    public void saveAndLoadTasks_allTaskTypes_preservesTasks() throws IOException, SophonException {
        Storage storage = new Storage(temporaryDirectory.toString(), "nested", "tasks.txt");
        TaskList original = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", LocalDate.parse("2019-10-15")));
        original.add(new Event("meeting", LocalDate.parse("2019-10-15"), LocalDate.parse("2019-10-16")));

        storage.saveTasks(original);
        TaskList loaded = storage.loadTasks();

        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", loaded.get(2).toString());
    }

    @Test
    public void saveTasks_fileWithoutParent_savesSuccessfully() throws IOException {
        Path saveFile = Path.of("sophon-storage-test-" + System.nanoTime() + ".txt");
        try {
            Storage storage = new Storage(saveFile.toString());
            TaskList tasks = new TaskList();
            tasks.add(new Todo("read book"));

            storage.saveTasks(tasks);

            assertEquals("T | 0 | read book", Files.readString(saveFile).trim());
        } finally {
            Files.deleteIfExists(saveFile);
        }
    }

    @Test
    public void loadTasks_blankLines_ignoresBlankLines() throws IOException, SophonException {
        Storage storage = storageWithLines("", "T | 0 | read book", "   ");

        assertEquals(1, storage.loadTasks().size());
    }

    @Test
    public void loadTasks_malformedLines_throwsSpecificExceptions() throws IOException {
        assertLoadFailure("broken", "The save file contains an incomplete task.");
        assertLoadFailure("T | 2 | read book", "The save file contains an invalid task status.");
        assertLoadFailure("T | 0 |  ", "The save file contains an empty task description.");
        assertLoadFailure("X | 0 | read book", "The save file contains an unknown task type.");
        assertLoadFailure("T | 0 | read book | extra", "The save file contains an invalid todo.");
        assertLoadFailure("D | 0 | return book", "The save file contains an invalid deadline.");
        assertLoadFailure("D | 0 | return book | bad-date", "Please enter the date in yyyy-MM-dd format.");
        assertLoadFailure("E | 0 | meeting | 2019-10-15", "The save file contains an invalid event.");
        assertLoadFailure("E | 0 | meeting | bad-date | 2019-10-16",
                "Please enter the date in yyyy-MM-dd format.");
    }

    private Storage storageWithLines(String... lines) throws IOException {
        Path file = temporaryDirectory.resolve("tasks-" + System.nanoTime() + ".txt");
        Files.write(file, java.util.List.of(lines), StandardCharsets.UTF_8);
        return new Storage(file.toString());
    }

    private void assertLoadFailure(String line, String expectedMessage) throws IOException {
        Storage storage = storageWithLines(line);
        SophonException exception = assertThrows(SophonException.class, storage::loadTasks);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
