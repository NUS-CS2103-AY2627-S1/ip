package clsl.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clsl.ClslException;
import clsl.task.Deadline;
import clsl.task.Event;
import clsl.task.Task;
import clsl.task.ToDo;

/** Tests saving and loading task data. */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadMissingFileReturnsEmptyList() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        assertEquals(List.of(), storage.load());
    }

    @Test
    void saveAndLoadPreservesTaskTypesStatusesAndDates() throws Exception {
        Path taskFile = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(taskFile.toString());
        ToDo todo = new ToDo("read book");
        Deadline deadline = new Deadline("submit report", "2026-09-02");
        Event event = new Event("conference", "2026-09-01", "2026-09-03");
        todo.markAsDone();
        event.markAsDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertTrue(Files.exists(taskFile));
        assertEquals(List.of(todo.toString(), deadline.toString(), event.toString()),
                loadedTasks.stream().map(Task::toString).toList());
        assertEquals(List.of(todo.getSaveString(), deadline.getSaveString(), event.getSaveString()),
                loadedTasks.stream().map(Task::getSaveString).toList());
    }

    @Test
    void saveEmptyTaskListCreatesAnEmptyFile() throws Exception {
        Path taskFile = temporaryDirectory.resolve("empty/tasks.txt");
        Storage storage = new Storage(taskFile.toString());

        storage.save(List.of());

        assertTrue(Files.exists(taskFile));
        assertEquals("", Files.readString(taskFile));
    }

    @Test
    void loadUnknownTaskTypeThrowsHelpfulException() throws Exception {
        Path taskFile = temporaryDirectory.resolve("unknown.txt");
        Files.writeString(taskFile, "X | 0 | unknown task\n");
        Storage storage = new Storage(taskFile.toString());

        ClslException exception = assertThrows(ClslException.class, storage::load);

        assertEquals("Unknown task type in file input", exception.getMessage());
    }

    @Test
    void loadIncompleteTasksWithoutStatusMarkerLeavesThemIncomplete() throws Exception {
        Path taskFile = temporaryDirectory.resolve("incomplete.txt");
        Files.writeString(taskFile, "T | 0 | read book\nD | 0 | submit report | 2026-09-02\n");
        Storage storage = new Storage(taskFile.toString());

        List<Task> loadedTasks = storage.load();

        assertFalse(loadedTasks.get(0).toString().contains("[X]"));
        assertFalse(loadedTasks.get(1).toString().contains("[X]"));
    }
}
