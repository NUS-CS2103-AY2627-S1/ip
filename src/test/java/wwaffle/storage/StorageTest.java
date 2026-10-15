package wwaffle.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import wwaffle.task.Deadline;
import wwaffle.task.Event;
import wwaffle.task.Task;
import wwaffle.task.Todo;

class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void load_malformedRecords_areRejected() throws IOException {
        Path file = temporaryDirectory.resolve("bad.txt");
        for (String record : new String[]{"T | 0", "T | 2 | task", "T | 0 | ", "T | 0 | task | extra",
            "D | 0 | task | 2026-02-30", "E | 0 | task |  | end", "Z | 0 | task"}) {
            Files.writeString(file, record);
            assertThrows(IOException.class, () -> new Storage(file.toString()).load(), record);
        }
    }

    @Test
    void load_missingFile_returnsEmptyTaskList() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveThenLoad_mixedTasks_preservesTaskData() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/tasks.txt").toString());
        ArrayList<Task> originalTasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        originalTasks.add(todo);
        originalTasks.add(new Deadline("submit report", "2026-08-31"));
        originalTasks.add(new Event("lecture", "10am", "12pm"));

        storage.save(originalTasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertTrue(loadedTasks.get(0).isDone());
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertEquals("2026-08-31", ((Deadline) loadedTasks.get(1)).getDueDate().toString());
        assertEquals("10am", ((Event) loadedTasks.get(2)).getStart());
        assertEquals("12pm", ((Event) loadedTasks.get(2)).getEnd());
    }
}
