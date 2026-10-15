package jiji.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jiji.exception.JijiStorageException;
import jiji.task.Deadline;
import jiji.task.Event;
import jiji.task.Task;
import jiji.task.TaskList;
import jiji.task.Todo;

/**
 * Unit tests for {@link Storage}.
 */
public class StorageTest {

    @TempDir
    public Path tempDir;

    @Test
    public void load_nonExistentFile_returnsEmptyList() throws JijiStorageException {
        Path file = tempDir.resolve("missing.txt");
        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void load_createsParentDirectories() throws JijiStorageException {
        Path nestedFile = tempDir.resolve("sub").resolve("dir").resolve("jiji.txt");
        Storage storage = new Storage(nestedFile.toString());
        List<Task> loaded = storage.load();
        assertTrue(loaded.isEmpty());
        assertTrue(Files.exists(nestedFile.getParent()));
    }

    @Test
    public void saveAndLoad_mixedTasks_preservesTasksAndStatus() throws JijiStorageException {
        Path file = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());

        TaskList taskList = new TaskList();
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", "2026-08-30");
        deadline.markAsDone();
        Task event = new Event("camp", "2026-08-29", "2026-08-31");
        taskList.add(todo);
        taskList.add(deadline);
        taskList.add(event);

        storage.save(taskList);
        assertTrue(Files.exists(file));

        List<Task> loaded = storage.load();
        assertEquals(3, loaded.size());

        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[D][X] return book (by: Aug 30 2026)", loaded.get(1).toString());
        assertTrue(loaded.get(1).isDone());
        assertEquals("[E][ ] camp (from: Aug 29 2026 to: Aug 31 2026)", loaded.get(2).toString());
    }

    @Test
    public void load_corruptedAndMalformedLines_skippedGracefully() throws IOException, JijiStorageException {
        Path file = tempDir.resolve("corrupt.txt");
        List<String> content = List.of(
                "T | 0 | valid todo",
                "",
                "   ",
                "INVALID LINE WITHOUT DELIMITERS",
                "X | 1 | unknown type",
                "D | 0",
                "D | 0 | incomplete deadline",
                "E | 0 | incomplete event | only from",
                "T | 1 | valid completed todo"
        );
        Files.write(file, content);

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][ ] valid todo", loaded.get(0).toString());
        assertEquals("[T][X] valid completed todo", loaded.get(1).toString());
        assertTrue(loaded.get(1).isDone());
    }

    @Test
    public void save_overwritesExistingFile() throws JijiStorageException {
        Path file = tempDir.resolve("overwrite.txt");
        Storage storage = new Storage(file.toString());

        TaskList list1 = new TaskList();
        list1.add(new Todo("initial task"));
        storage.save(list1);
        assertEquals(1, storage.load().size());

        TaskList list2 = new TaskList();
        list2.add(new Todo("new task 1"));
        list2.add(new Todo("new task 2"));
        storage.save(list2);

        List<Task> loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("[T][ ] new task 1", loaded.get(0).toString());
        assertEquals("[T][ ] new task 2", loaded.get(1).toString());
    }

    @Test
    public void constructor_nullOrBlankPath_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Storage(null));
        assertThrows(AssertionError.class, () -> new Storage("   "));
    }

    @Test
    public void save_nullTaskList_throwsAssertionError() {
        Storage storage = new Storage(tempDir.resolve("test.txt").toString());
        assertThrows(AssertionError.class, () -> storage.save(null));
    }
}
