package rene.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rene.exception.ReneException;
import rene.task.Deadline;
import rene.task.Event;
import rene.task.Task;
import rene.task.Todo;

/**
 * Covers storage behaviour around unusual but legal data and malformed data
 * that the primary storage tests do not exercise.
 */
class StorageEdgeCaseTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadTasks_blankAndWhitespaceOnlyLinesAreSkipped() throws IOException, ReneException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "\nT | 0 | real task\n   \n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        List<Task> tasks = storage.loadTasks().tasks();

        assertEquals(1, tasks.size());
        assertEquals("real task", tasks.get(0).getDescription());
    }

    @Test
    void loadTasks_unknownTaskTypeCharacter_reportsLineNumber() throws IOException, ReneException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | ok\nQ | 0 | mystery\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.loadTasks();

        assertEquals(1, result.tasks().size());
        assertEquals("I couldn't understand line 2 in " + dataFile + ".", result.warnings().get(0));
    }

    @Test
    void loadTasks_unknownStatusValue_reportsLineNumber() throws IOException, ReneException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 2 | wrong status\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.loadTasks();

        assertTrue(result.tasks().isEmpty());
        assertEquals(1, result.warnings().size());
    }

    @Test
    void loadTasks_deadlineWithUnparseableStoredDate_reportsLineNumber() throws IOException, ReneException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "D | 0 | report | not-a-date\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.loadTasks();

        assertTrue(result.tasks().isEmpty());
        assertEquals(1, result.warnings().size());
    }

    @Test
    void loadTasks_tooManyFields_reportsLineNumber() throws IOException, ReneException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | task | extra\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.loadTasks();

        assertTrue(result.tasks().isEmpty());
        assertEquals(1, result.warnings().size());
    }

    @Test
    void saveTasks_thenLoadTasks_descriptionContainingPipeIsPreserved() throws ReneException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        String trickyDescription = "fix bug | in module";
        storage.saveTasks(List.of(new Todo(trickyDescription)));

        List<Task> loaded = storage.loadTasks().tasks();

        assertEquals(1, loaded.size());
        assertEquals(trickyDescription, loaded.get(0).getDescription());
    }

    @Test
    void saveTasks_roundTripsAllTaskTypesAndStatuses() throws ReneException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("todo item");
        Deadline deadline = new Deadline("deadline item", java.time.LocalDate.of(2026, 12, 31));
        Event event = new Event("event item", "10am", "12pm");
        deadline.markAsDone();
        storage.saveTasks(List.of(todo, deadline, event));

        List<Task> loaded = storage.loadTasks().tasks();

        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertFalse(loaded.get(0).isDone());
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertTrue(loaded.get(1).isDone());
        assertInstanceOf(Event.class, loaded.get(2));
        assertFalse(loaded.get(2).isDone());
    }

    @Test
    void loadTasks_fileWithOnlyBlankLinesYieldsEmptyList() throws IOException, ReneException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "\n\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        assertTrue(storage.loadTasks().tasks().isEmpty());
    }
}
