package jeff.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jeff.task.Deadline;
import jeff.task.Event;
import jeff.task.Task;
import jeff.task.Todo;

public class StorageAdditionalTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void saveAndLoadTasks_completionStates_restoresEveryField()
            throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());
        ArrayList<Task> tasks = new ArrayList<>();

        Todo todo = new Todo("read  Java book");
        Deadline deadline = new Deadline(
                "submit report", LocalDate.of(2026, 9, 30));
        Event event = new Event(
                "team meeting", "Monday afternoon", "Tuesday morning");
        deadline.markAsDone();

        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);
        storage.saveTasks(tasks);

        ArrayList<Task> loadedTasks = storage.loadTasks();
        Todo loadedTodo = (Todo) loadedTasks.get(0);
        Deadline loadedDeadline = (Deadline) loadedTasks.get(1);
        Event loadedEvent = (Event) loadedTasks.get(2);

        assertEquals(3, loadedTasks.size());
        assertEquals("read  Java book", loadedTodo.getDescription());
        assertFalse(loadedTodo.isDone());
        assertEquals("submit report", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 30), loadedDeadline.getDueDate());
        assertTrue(loadedDeadline.isDone());
        assertEquals("team meeting", loadedEvent.getDescription());
        assertEquals("Monday afternoon", loadedEvent.getStartTime());
        assertEquals("Tuesday morning", loadedEvent.getEndTime());
        assertFalse(loadedEvent.isDone());
    }

    @Test
    public void saveTasks_fewerTasks_overwritesPreviousContents()
            throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());
        ArrayList<Task> originalTasks = new ArrayList<>();
        originalTasks.add(new Todo("first"));
        originalTasks.add(new Todo("second"));
        storage.saveTasks(originalTasks);

        ArrayList<Task> replacementTasks = new ArrayList<>();
        replacementTasks.add(new Todo("replacement"));
        storage.saveTasks(replacementTasks);

        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("replacement", loadedTasks.get(0).getDescription());
    }

    @Test
    public void saveTasks_emptyList_createsEmptyFile() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());

        storage.saveTasks(new ArrayList<>());

        assertTrue(Files.exists(filePath));
        assertEquals("", Files.readString(filePath));
        assertTrue(storage.loadTasks().isEmpty());
    }
}
