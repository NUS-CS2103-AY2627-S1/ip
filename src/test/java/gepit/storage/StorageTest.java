package gepit.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gepit.GepitException;
import gepit.TaskList;
import gepit.task.Deadline;
import gepit.task.Event;
import gepit.task.Task;
import gepit.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void saveAndLoad_mixedTasks_preservesTasks()
            throws GepitException {
        Path filePath = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());

        TaskList originalTasks = new TaskList();

        Todo todo = new Todo("read book");

        Deadline deadline = new Deadline(
                "submit report",
                LocalDate.of(2026, 9, 10));
        deadline.markDone();

        Event event = new Event(
                "holiday",
                LocalDate.of(2026, 9, 20),
                LocalDate.of(2026, 9, 22));

        originalTasks.add(todo);
        originalTasks.add(deadline);
        originalTasks.add(event);

        storage.save(originalTasks);

        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());

        assertEquals(
                "read book",
                loadedTasks.get(0).getDescription());

        assertEquals(
                "submit report",
                loadedTasks.get(1).getDescription());

        assertEquals(
                "holiday",
                loadedTasks.get(2).getDescription());

        // Verify completion status survives saving/loading.
        assertEquals(
                deadline.toDataString(),
                loadedTasks.get(1).toDataString());
    }

    @Test
    public void load_missingFile_returnsEmptyList()
            throws GepitException {
        Path filePath = tempDir.resolve("does-not-exist.txt");
        Storage storage = new Storage(filePath.toString());

        List<Task> loadedTasks = storage.load();

        assertEquals(0, loadedTasks.size());
    }

    @Test
    public void load_corruptedDate_throwsException()
            throws IOException {
        Path filePath = tempDir.resolve("tasks.txt");

        Files.writeString(
                filePath,
                "D | 0 | submit report | definitely-not-a-date");

        Storage storage = new Storage(filePath.toString());

        assertThrows(
                GepitException.class,
                storage::load);
    }

    @Test
    public void load_missingFields_throwsException()
            throws IOException {
        Path filePath = tempDir.resolve("tasks.txt");

        Files.writeString(
                filePath,
                "E | 0 | meeting");

        Storage storage = new Storage(filePath.toString());

        assertThrows(
                GepitException.class,
                storage::load);
    }

    @Test
    public void load_eventWithSameDates_throwsException()
            throws IOException {
        Path filePath = tempDir.resolve("tasks.txt");
        Files.writeString(
                filePath,
                "E | 0 | meeting | 2026-09-10 | 2026-09-10");

        Storage storage = new Storage(filePath.toString());

        assertThrows(GepitException.class, storage::load);
    }

    @Test
    public void save_missingDirectory_createsDirectory()
            throws GepitException {
        Path filePath = tempDir
                .resolve("data")
                .resolve("tasks.txt");

        Storage storage = new Storage(filePath.toString());

        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        storage.save(tasks);

        assertTrue(Files.exists(filePath));
    }
}
