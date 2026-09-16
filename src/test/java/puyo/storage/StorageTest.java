package puyo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import puyo.PuyoException;
import puyo.task.Deadline;
import puyo.task.Event;
import puyo.task.Task;
import puyo.task.TaskList;
import puyo.task.ToDo;

/**
 * Tests for {@code Storage}.
 */
public class StorageTest {

    @Test
    public void load_fileDoesNotExist_returnsEmptyList(@TempDir Path tempDir) {
        Path filePath = tempDir.resolve("non_existent.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void saveAndLoad_validTasks_persistsDataCorrectly(@TempDir Path tempDir) throws PuyoException {
        Path filePath = tempDir.resolve("data/tasks.txt");
        Storage storage = new Storage(filePath.toString());

        TaskList taskList = new TaskList();
        ToDo todo = new ToDo("buy groceries");
        todo.markDone();

        LocalDateTime now = LocalDateTime.of(2026, 8, 28, 18, 0);
        Deadline deadline = new Deadline("submit assignment", now);
        Event event = new Event("meeting", now, now.plusHours(2));

        taskList.add(todo);
        taskList.add(deadline);
        taskList.add(event);

        storage.save(taskList);

        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("buy groceries", loadedTasks.get(0).getName());
        assertTrue(loadedTasks.get(0).getDone());

        assertEquals("submit assignment", loadedTasks.get(1).getName());
        assertFalse(loadedTasks.get(1).getDone());

        assertEquals("meeting", loadedTasks.get(2).getName());
    }

    @Test
    public void load_corruptedLinesInFile_skipsCorruptedLines(@TempDir Path tempDir) throws IOException {
        Path filePath = tempDir.resolve("corrupted.txt");

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write("T | 1 | valid todo\n");
            writer.write("INVALID LINE FORMAT\n");
            writer.write("D | 0 | invalid deadline format without date\n");
            writer.write("E | 0 | meeting | 2026-08-28 1400\n");
            writer.write("T | 0 | another valid todo\n");
        }

        Storage storage = new Storage(filePath.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("valid todo", tasks.get(0).getName());
        assertEquals("another valid todo", tasks.get(1).getName());
    }
}
