package pulbot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pulbot.PulbotException;
import pulbot.task.Deadline;
import pulbot.task.Event;
import pulbot.task.TaskList;
import pulbot.task.Todo;

/** Tests saving and loading task data. */
public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() throws PulbotException {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveAndLoad_allTaskTypes_preservesTaskData() throws PulbotException {
        Path file = tempDir.resolve("nested/tasks.txt");
        Storage storage = new Storage(file.toString());
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        Event event = new Event("lecture", "3/12/2019 1400", "3/12/2019 1600");

        storage.save(new TaskList(List.of(todo, deadline, event)));
        TaskList loaded = storage.load();

        assertTrue(Files.exists(file));
        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertTrue(loaded.get(0).isDone());
        assertEquals("read book", loaded.get(0).getDescription());
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loaded.get(1));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), loadedDeadline.getBy());
        Event loadedEvent = assertInstanceOf(Event.class, loaded.get(2));
        assertEquals(LocalDateTime.of(2019, 12, 3, 14, 0), loadedEvent.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 16, 0), loadedEvent.getTo());
        assertFalse(loadedEvent.isDone());
    }

    @Test
    public void load_blankLines_ignoresBlankLines() throws IOException, PulbotException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "\nT\t0\tread book\n   \n");

        TaskList loaded = new Storage(file.toString()).load();

        assertEquals(1, loaded.size());
        assertEquals("read book", loaded.get(0).getDescription());
    }

    @Test
    public void load_existingDuplicateTasks_preservesBackwardCompatibleData()
            throws IOException, PulbotException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "T\t0\tread book\nT\t1\tREAD BOOK\n");

        TaskList loaded = new Storage(file.toString()).load();

        assertEquals(2, loaded.size());
        assertFalse(loaded.get(0).isDone());
        assertTrue(loaded.get(1).isDone());
    }

    @Test
    public void load_invalidData_throwsException() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        String[] invalidLines = {
            "X\t0\tunknown type\n",
            "T\t2\tinvalid status\n",
            "T\t0\t\n",
            "T\t0\n",
            "D\t0\tdeadline\t\n",
            "D\t0\tdeadline\tnot a date\n",
            "D\t0\tdeadline\tFeb 31 2019 6:00 PM\n",
            "E\t0\tevent\tDec 02 2019 2:00 PM\t\n",
            "E\t0\tevent\tDec 02 2019 4:00 PM\tDec 02 2019 2:00 PM\n"
        };

        for (String invalidLine : invalidLines) {
            Files.writeString(file, invalidLine);
            Storage storage = new Storage(file.toString());
            assertThrows(PulbotException.class, storage::load);
        }
    }

    @Test
    public void load_unreadablePath_throwsException() {
        Storage storage = new Storage(tempDir.toString());

        assertThrows(PulbotException.class, storage::load);
    }

    @Test
    public void save_directoryPath_throwsExceptionWithoutChangingTasks() {
        Todo task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = new Storage(tempDir.toString());

        assertThrows(PulbotException.class, () -> storage.save(tasks));
        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
    }

    @Test
    public void load_repairedFile_allowsSavingAgain() throws IOException, PulbotException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "invalid");
        Storage storage = new Storage(file.toString());
        assertThrows(PulbotException.class, storage::load);
        assertThrows(PulbotException.class, () -> storage.save(new TaskList()));

        Files.writeString(file, "T\t0\trecovered task\n");
        TaskList recovered = storage.load();
        recovered.add(new Todo("new task"));
        storage.save(recovered);

        assertEquals(2, storage.load().size());
    }
}
