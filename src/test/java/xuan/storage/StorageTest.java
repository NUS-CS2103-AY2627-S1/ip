package xuan.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import xuan.task.Deadline;
import xuan.task.Event;
import xuan.task.Task;
import xuan.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void loadTasks_fileDoesNotExist_returnsEmptyList()
            throws IOException {
        Storage storage = new Storage(
                tempDir.resolve("missing.txt").toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void saveAndLoadTasks_multipleTaskTypes_preservesTasks()
            throws IOException {
        Path filePath = tempDir.resolve("xuan.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline(
                "submit assignment",
                LocalDate.parse("2026-09-20")));
        tasks.add(new Event("meeting", "2pm", "4pm"));

        storage.saveTasks(tasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());

        assertTrue(loadedTasks.get(0) instanceof Todo);
        assertEquals("read book",
                loadedTasks.get(0).getDescription());

        assertTrue(loadedTasks.get(1) instanceof Deadline);
        Deadline deadline = (Deadline) loadedTasks.get(1);
        assertEquals("submit assignment", deadline.getDescription());
        assertEquals(LocalDate.parse("2026-09-20"), deadline.getBy());

        assertTrue(loadedTasks.get(2) instanceof Event);
        Event event = (Event) loadedTasks.get(2);
        assertEquals("meeting", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    public void saveAndLoadTasks_doneTask_preservesDoneStatus()
            throws IOException {
        Path filePath = tempDir.resolve("xuan.txt");
        Storage storage = new Storage(filePath.toString());

        Todo doneTask = new Todo("read book");
        doneTask.markAsDone();

        Todo undoneTask = new Todo("finish homework");

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(doneTask);
        tasks.add(undoneTask);

        storage.saveTasks(tasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertTrue(loadedTasks.get(0).isDone());
        assertFalse(loadedTasks.get(1).isDone());
    }
}
