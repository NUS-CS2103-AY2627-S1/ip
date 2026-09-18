package chud.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chud.task.Deadline;
import chud.task.Event;
import chud.task.Task;
import chud.task.TaskDateTime;
import chud.task.TaskList;
import chud.task.Todo;

class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void load_fileDoesNotExist_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("nonexistent.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void save_thenLoad_roundTripsAllTaskTypesAndDoneStatus() {
        Storage storage = new Storage(tempDir.resolve("chud.txt").toString());
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-12-02"));
        Event event = new Event("project meeting",
                TaskDateTime.parse("2019-12-02 1400"), TaskDateTime.parse("2019-12-02 1600"));
        TaskList tasks = new TaskList();
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.save(tasks);
        ArrayList<Task> reloaded = storage.load();

        assertEquals(3, reloaded.size());
        assertEquals("[T][X] borrow book", reloaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", reloaded.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)",
                reloaded.get(2).toString());
    }

    @Test
    void save_missingParentDirectory_createsItAutomatically() {
        Path filePath = tempDir.resolve("nested/data/chud.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("borrow book"));

        storage.save(tasks);

        assertTrue(Files.exists(filePath));
    }

    @Test
    void save_filePathHasNoParentDirectory_doesNotThrow() throws Exception {
        // A relative path with no directory component (e.g. Path.of("chud.txt")) has a null
        // getParent() -- this must not crash trying to create a "parent" that doesn't exist.
        String fileName = "chud-storage-test-no-parent.txt";
        Storage storage = new Storage(fileName);
        TaskList tasks = new TaskList();
        tasks.add(new Todo("borrow book"));

        try {
            storage.save(tasks);

            assertTrue(Files.exists(Path.of(fileName)));
        } finally {
            Files.deleteIfExists(Path.of(fileName));
        }
    }

    @Test
    void load_corruptedLineAmongValidOnes_skipsOnlyTheCorruptedLine() throws Exception {
        Path filePath = tempDir.resolve("chud.txt");
        Files.writeString(filePath, String.join(System.lineSeparator(),
                "T | 0 | read book",
                "X | 0 | unrecognized tag",
                "D | 0 | no by field",
                "T | 1 | join sports club",
                ""));
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][X] join sports club", tasks.get(1).toString());
    }

    @Test
    void load_blankLinesInFile_skippedSilently() throws Exception {
        Path filePath = tempDir.resolve("chud.txt");
        Files.writeString(filePath, String.join(System.lineSeparator(),
                "T | 0 | read book",
                "",
                "   ",
                "T | 0 | return book",
                ""));
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
    }
}
