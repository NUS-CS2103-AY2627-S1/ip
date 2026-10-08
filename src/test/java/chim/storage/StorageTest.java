package chim.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chim.task.Deadline;
import chim.task.Task;
import chim.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("nonexistent.txt").toString());
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void saveThenLoad_mixedTasks_returnsSameTasks() {
        Storage storage = new Storage(tempDir.resolve("chim.txt").toString());

        ArrayList<Task> original = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", LocalDate.of(2019, 12, 1)));

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 01 2019)", loaded.get(1).toString());
    }

    @Test
    public void load_corruptedLine_skipsInvalidLineOnly() throws Exception {
        Path file = tempDir.resolve("chim.txt");
        Files.writeString(file,
                "T | 1 | none | read book\n"
                        + "this is not a valid line\n"
                        + "T | 0 | none | write essay\n");

        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[T][ ] write essay", tasks.get(1).toString());
    }

    @Test
    public void save_createsParentFolderIfMissing() {
        Path nestedFile = tempDir.resolve("newfolder").resolve("chim.txt");
        Storage storage = new Storage(nestedFile.toString());

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("test task"));
        storage.save(tasks);

        assertTrue(Files.exists(nestedFile));
    }

    @Test
    public void load_corruptedLine_reportsSkippedCount() throws Exception {
        Path file = tempDir.resolve("chim.txt");
        Files.writeString(file,
                "T | 1 | none | read book\n"
                        + "this is not a valid line\n"
                        + "also garbage\n"
                        + "T | 0 | none | write essay\n");

        Storage storage = new Storage(file.toString());
        storage.load();

        assertEquals(2, storage.getSkippedLineCount());
    }

    @Test
    public void load_noCorruptedLines_zeroSkippedCount() {
        Storage storage = new Storage(tempDir.resolve("nonexistent.txt").toString());
        storage.load();
        assertEquals(0, storage.getSkippedLineCount());
    }

    @Test
    public void load_blankLinesOnly_notCountedAsSkipped() throws Exception {
        Path file = tempDir.resolve("chim.txt");
        Files.writeString(file, "T | 1 | none | read book\n\n   \n");

        Storage storage = new Storage(file.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals(0, storage.getSkippedLineCount());
    }
}
