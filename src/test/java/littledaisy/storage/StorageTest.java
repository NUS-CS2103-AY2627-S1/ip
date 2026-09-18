package littledaisy.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import littledaisy.exception.LittleDaisyException;
import littledaisy.task.Deadline;
import littledaisy.task.Event;
import littledaisy.task.Task;
import littledaisy.task.TaskList;
import littledaisy.task.Todo;

class StorageTest {
    @TempDir
    Path tempDirectory;

    @Test
    void load_missingDataFile_emptyListReturned() throws LittleDaisyException {
        Storage storage = new Storage(tempDirectory.resolve("data/tasks.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveThenLoad_mixedTaskList_allFieldsRestored() throws LittleDaisyException {
        Path file = tempDirectory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(file);
        TaskList original = new TaskList();
        Todo todo = new Todo("read \\ review");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 1));
        deadline.markAsDone();
        Event event = new Event("meeting", "2pm", "4pm");
        original.add(todo);
        original.add(deadline);
        original.add(event);

        storage.save(original);
        List<Task> loaded = storage.load();

        assertTrue(Files.exists(file));
        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertEquals("read \\ review", loaded.get(0).getDescription());
        assertFalse(loaded.get(0).isDone());
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertEquals("2026-09-01", ((Deadline) loaded.get(1)).getBy().toString());
        assertTrue(loaded.get(1).isDone());
        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("2pm", ((Event) loaded.get(2)).getFrom());
        assertEquals("4pm", ((Event) loaded.get(2)).getTo());
    }

    @Test
    void load_malformedRecord_exceptionThrown() throws IOException {
        Path file = tempDirectory.resolve("tasks.txt");
        Files.writeString(file, "D\t0\tmissing-date", StandardCharsets.UTF_8);
        Storage storage = new Storage(file);

        assertThrows(LittleDaisyException.class, storage::load);
    }

    @Test
    void load_blankRequiredField_exceptionThrown() throws IOException {
        Path file = tempDirectory.resolve("tasks.txt");
        Files.writeString(file, "E\t0\tmeeting\t \t4pm", StandardCharsets.UTF_8);
        Storage storage = new Storage(file);

        assertThrows(LittleDaisyException.class, storage::load);
    }
}
