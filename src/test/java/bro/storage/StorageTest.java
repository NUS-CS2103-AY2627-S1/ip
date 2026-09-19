package bro.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bro.exception.BroException;
import bro.task.Deadline;
import bro.task.Event;
import bro.task.Task;
import bro.task.TaskList;
import bro.task.Todo;

/**
 * Unit tests for {@link Storage}.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void load_nonExistentFile_returnsEmptyTaskList() {
        Path nonExistentPath = tempDir.resolve("non_existent_tasks.txt");
        Storage storage = new Storage(nonExistentPath);

        TaskList taskList = storage.load();
        assertNotNull(taskList);
        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
    }

    @Test
    public void load_validFileWithMixedTasks_tasksLoadedCorrectly() throws IOException {
        Path filePath = tempDir.resolve("valid_tasks.txt");
        String content = "T | 0 | read book\n"
                + "T | 1 | return book\n"
                + "D | 0 | submit assignment | 2026-10-15 1800\n"
                + "E | 1 | project meeting | 2026-10-15 1400 | 2026-10-15 1600\n";
        Files.writeString(filePath, content);

        Storage storage = new Storage(filePath);
        TaskList taskList = storage.load();

        assertEquals(4, taskList.size());

        Task task1 = taskList.get(0);
        assertTrue(task1 instanceof Todo);
        assertEquals("read book", task1.getTask());
        assertFalse(task1.isDone());

        Task task2 = taskList.get(1);
        assertTrue(task2 instanceof Todo);
        assertEquals("return book", task2.getTask());
        assertTrue(task2.isDone());

        Task task3 = taskList.get(2);
        assertTrue(task3 instanceof Deadline);
        assertEquals("submit assignment", task3.getTask());
        assertFalse(task3.isDone());

        Task task4 = taskList.get(3);
        assertTrue(task4 instanceof Event);
        assertEquals("project meeting", task4.getTask());
        assertTrue(task4.isDone());
    }

    @Test
    public void load_fileWithBom_stripsBomAndLoads() throws IOException {
        Path filePath = tempDir.resolve("bom_tasks.txt");
        String content = "\uFEFFT | 0 | buy milk\n";
        Files.writeString(filePath, content);

        Storage storage = new Storage(filePath);
        TaskList taskList = storage.load();

        assertEquals(1, taskList.size());
        assertEquals("buy milk", taskList.get(0).getTask());
    }

    @Test
    public void load_corruptedLinesAndInvalidEntries_skipsGracefully() throws IOException {
        Path filePath = tempDir.resolve("corrupted_tasks.txt");
        String content = "\n"
                + "corrupted line\n"
                + "X | 0 | unknown type\n"
                + "D | 0 | incomplete deadline\n"
                + "E | 0 | incomplete event\n"
                + "T | 0 | valid todo\n";
        Files.writeString(filePath, content);

        Storage storage = new Storage(filePath);
        TaskList taskList = storage.load();

        assertEquals(1, taskList.size());
        assertEquals("valid todo", taskList.get(0).getTask());
    }

    @Test
    public void load_legacyEventFormat_parsedCorrectly() throws IOException {
        Path filePath = tempDir.resolve("legacy_event.txt");
        String content = "E | 0 | career fair | 2026-10-15 /to 2026-10-17\n";
        Files.writeString(filePath, content);

        Storage storage = new Storage(filePath);
        TaskList taskList = storage.load();

        assertEquals(1, taskList.size());
        assertTrue(taskList.get(0) instanceof Event);
        assertEquals("career fair", taskList.get(0).getTask());
    }

    @Test
    public void save_validTaskList_fileWrittenCorrectly() throws BroException, IOException {
        Path filePath = tempDir.resolve("saved_tasks.txt");
        Storage storage = new Storage(filePath);

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("buy groceries"));
        Deadline deadline = new Deadline("submit homework", "2026-10-15 2359");
        deadline.markDone();
        tasks.add(deadline);
        tasks.add(new Event("camp", "2026-10-15 0800", "2026-10-17 1800"));

        TaskList taskList = new TaskList(tasks);
        storage.save(taskList);

        assertTrue(Files.exists(filePath));
        Storage reader = new Storage(filePath);
        TaskList loadedList = reader.load();

        assertEquals(3, loadedList.size());
        assertEquals("buy groceries", loadedList.get(0).getTask());
        assertFalse(loadedList.get(0).isDone());
        assertEquals("submit homework", loadedList.get(1).getTask());
        assertTrue(loadedList.get(1).isDone());
        assertEquals("camp", loadedList.get(2).getTask());
        assertFalse(loadedList.get(2).isDone());
    }

    @Test
    public void save_nestedNonExistentDirectory_directoryCreatedAndSaved() {
        Path nestedPath = tempDir.resolve("nested").resolve("dir").resolve("tasks.txt");
        Storage storage = new Storage(nestedPath);

        TaskList taskList = new TaskList();
        taskList.add(new Todo("write code"));

        storage.save(taskList);
        assertTrue(Files.exists(nestedPath));
    }
}
