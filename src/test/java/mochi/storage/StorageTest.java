package mochi.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mochi.exception.MochiException;
import mochi.task.Deadline;
import mochi.task.Event;
import mochi.task.Task;
import mochi.task.TaskList;
import mochi.task.Todo;

class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void load_missingFile_returnsEmptyList() throws MochiException {
        Storage storage = new Storage(tempDir.resolve("nested").resolve("tasks.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveThenLoad_allTaskDetailsAndStatuses_arePreserved() throws MochiException {
        Storage storage = new Storage(tempDir.resolve("nested").resolve("tasks.txt"));
        TaskList originalTasks = new TaskList();
        Todo todo = new Todo("borrow book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 30));
        Event event = new Event("meeting", "2pm", "4pm");
        deadline.markAsDone();
        originalTasks.add(todo);
        originalTasks.add(deadline);
        originalTasks.add(event);

        storage.save(originalTasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals(todo.toString(), loadedTasks.get(0).toString());
        assertEquals(deadline.toString(), loadedTasks.get(1).toString());
        assertEquals(event.toString(), loadedTasks.get(2).toString());
    }

    @Test
    void loadMalformedRecord_reportsFriendlyError() throws Exception {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | missing | unexpected\n", StandardCharsets.UTF_8);

        MochiException exception = org.junit.jupiter.api.Assertions.assertThrows(
                MochiException.class, () -> new Storage(file).load());

        assertEquals("Saved task data is invalid at line 1.", exception.getMessage());
    }

    @Test
    void saveFileWithoutParentDirectory_succeeds() throws Exception {
        Path file = tempDir.resolve("tasks.txt").getFileName();
        Storage storage = new Storage(file);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("portable save"));

        storage.save(tasks);

        assertEquals(1, storage.load().size());
        Files.deleteIfExists(file);
    }

    @Test
    void loadBlankTaskField_reportsFriendlyError() throws Exception {
        Path file = tempDir.resolve("blank.txt");
        Files.writeString(file, "T | 0 | \n", StandardCharsets.UTF_8);

        MochiException exception = org.junit.jupiter.api.Assertions.assertThrows(
                MochiException.class, () -> new Storage(file).load());

        assertEquals("Saved task data is invalid at line 1.", exception.getMessage());
    }
}
