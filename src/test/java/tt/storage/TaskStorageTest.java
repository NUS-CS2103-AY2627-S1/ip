package tt.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tt.task.Deadline;
import tt.task.Event;
import tt.task.Task;
import tt.task.Todo;

class TaskStorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    void saveAndLoad_roundTripsAllTaskTypesAndCompletionState() {
        Path saveFile = temporaryDirectory.resolve("nested/tasks.txt");
        Task todo = new Todo("buy milk");
        Deadline deadline = new Deadline("submit project", LocalDate.of(2026, 8, 28));
        deadline.mark();
        Event event = new Event("team meeting", "Monday 2pm", "Monday 4pm");

        new TaskStorage(saveFile).save(List.of(todo, deadline, event));
        List<Task> loaded = new TaskStorage(saveFile).load();

        assertEquals(3, loaded.size());
        assertEquals("[T][ ] buy milk", loaded.get(0).toString());
        assertEquals("[D][X] submit project (by: Aug 28 2026)", loaded.get(1).toString());
        assertEquals("[E][ ] team meeting (from: Monday 2pm to Monday 4pm)", loaded.get(2).toString());
        assertTrue(loaded.get(1).isDone());
        assertEquals(LocalDate.of(2026, 8, 28), assertInstanceOf(Deadline.class, loaded.get(1)).getBy());
    }

    @Test
    void load_missingFile_returnsEmptyTaskList() {
        Path missingFile = temporaryDirectory.resolve("missing.txt");

        List<Task> loaded = new TaskStorage(missingFile).load();

        assertEquals(0, loaded.size());
    }

    @Test
    void load_malformedFile_throwsStorageExceptionWithLineNumber() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 0 | valid todo",
                "D | 1 | valid deadline | 2026-09-01",
                "E | 0 | valid event | 10am | 11am",
                "not a valid record"));

        StorageException exception = assertThrows(StorageException.class,
                () -> new TaskStorage(saveFile).load());

        assertTrue(exception.getMessage().contains("line 4"));
    }

    @Test
    void save_parentPathIsAFile_throwsStorageException() throws Exception {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "blocking file");
        TaskStorage storage = new TaskStorage(parentFile.resolve("tasks.txt"));

        StorageException exception = assertThrows(StorageException.class,
                () -> storage.save(List.of(new Todo("read book"))));

        assertTrue(exception.getMessage().contains("couldn't save"));
    }

    @Test
    void save_unsupportedTaskType_throwsIllegalArgumentException() {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));

        assertThrows(IllegalArgumentException.class, () -> storage.save(List.of(new Task("read book"))));
    }
}
