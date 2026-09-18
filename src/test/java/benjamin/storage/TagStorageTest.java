package benjamin.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import benjamin.BenjaminException;
import benjamin.task.Task;
import benjamin.task.TaskDateTime;
import benjamin.task.TaskList;
import benjamin.task.Todo;

/**
 * Tests that tags survive a save and load, and that files written before
 * tagging existed still load.
 */
public class TagStorageTest {
    @TempDir
    Path root;

    private Storage storage() {
        return new Storage(root.toString(), "data", "benjamin.txt");
    }

    private void writeSaveFile(String... lines) throws IOException {
        Path file = root.resolve("data").resolve("benjamin.txt");
        Files.createDirectories(file.getParent());
        Files.write(file, List.of(lines));
    }

    private List<String> readSaveFile() throws IOException {
        return Files.readAllLines(root.resolve("data").resolve("benjamin.txt"));
    }

    @Test
    public void load_recordsWrittenBeforeTagging_stillLoad() throws BenjaminException, IOException {
        writeSaveFile(
                "T | 1 | read book",
                "D | 0 | return book | 2019-12-02 1800",
                "E | 0 | camp | 2019-08-06 1400 | 2019-08-08 1600");

        Storage storage = storage();
        ArrayList<Task> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertEquals(0, storage.getLoadWarnings().size());
        assertEquals(0, tasks.get(0).getTags().size());
    }

    @Test
    public void load_recordWithTags_attachesThem() throws BenjaminException, IOException {
        writeSaveFile("T | 0 | read book | fun urgent");

        ArrayList<Task> tasks = storage().load();

        assertEquals(List.of("fun", "urgent"), List.copyOf(tasks.get(0).getTags()));
    }

    @Test
    public void load_deadlineWithTags_readsDateAndTags() throws BenjaminException, IOException {
        writeSaveFile("D | 0 | essay | 2019-10-15 | school");

        ArrayList<Task> tasks = storage().load();

        assertEquals("[D][ ] essay (by: Oct 15 2019) #school", tasks.get(0).toString());
    }

    @Test
    public void load_eventWithTags_readsBothDatesAndTags() throws BenjaminException, IOException {
        writeSaveFile("E | 0 | camp | 2019-08-06 1400 | 2019-08-08 1600 | outdoors");

        ArrayList<Task> tasks = storage().load();

        assertTrue(tasks.get(0).hasTag("outdoors"));
    }

    @Test
    public void load_tooManyFields_lineSkipped() throws BenjaminException, IOException {
        writeSaveFile("T | 0 | read book | fun | extra");

        Storage storage = storage();

        assertEquals(0, storage.load().size());
        assertEquals(1, storage.getLoadWarnings().size());
    }

    @Test
    public void save_untaggedTasks_writeExactlyTheOldFormat() throws BenjaminException, IOException {
        TaskList tasks = TaskList.of(
                new Todo("read book"),
                new benjamin.task.Deadline("essay", TaskDateTime.parse("2019-10-15")));

        storage().save(tasks);

        assertEquals(List.of("T | 0 | read book", "D | 0 | essay | 2019-10-15"), readSaveFile());
    }

    @Test
    public void saveThenLoad_taggedTasks_roundTripUnchanged() throws BenjaminException {
        Todo todo = new Todo("read book");
        todo.addTag("fun");
        todo.addTag("urgent");
        TaskList original = TaskList.of(todo);

        Storage storage = storage();
        storage.save(original);
        TaskList reloaded = new TaskList(storage.load());

        assertEquals(original.toSaveFormat(), reloaded.toSaveFormat());
        assertEquals("[T][ ] read book #fun #urgent", reloaded.get(0).toString());
    }

    @Test
    public void saveThenLoad_tagOrder_isPreserved() throws BenjaminException {
        Todo todo = new Todo("read book");
        todo.addTag("zebra");
        todo.addTag("apple");
        Storage storage = storage();
        storage.save(TaskList.of(todo));

        ArrayList<Task> reloaded = storage.load();

        assertEquals(List.of("zebra", "apple"), List.copyOf(reloaded.get(0).getTags()));
    }
}
