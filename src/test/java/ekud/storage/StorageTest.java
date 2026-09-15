package ekud.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ekud.EkudException;
import ekud.task.Deadline;
import ekud.task.Event;
import ekud.task.Task;
import ekud.task.TaskDateTime;
import ekud.task.TaskList;
import ekud.task.Todo;

/**
 * Tests {@link Storage} save/load round-trips and skipping of unusable lines.
 */
public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() throws EkudException {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_restoresTasksAndDoneFlag() throws EkudException {
        Path file = tempDir.resolve("nested").resolve("ekud.txt");
        Storage storage = new Storage(file.toString());

        TaskList original = new TaskList();
        Todo doneTodo = new Todo("read book");
        doneTodo.markAsDone();
        original.add(doneTodo);
        original.add(new Deadline("return book", TaskDateTime.parse("2019-12-02")));
        original.add(new Event("meeting",
                TaskDateTime.parse("2019-12-02 1400"),
                TaskDateTime.parse("2019-12-02 1600")));
        storage.save(original);

        List<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)",
                loaded.get(2).toString());
        assertTrue(Files.exists(file.getParent()));
    }

    @Test
    public void load_skipsBlankAndCorruptLines() throws Exception {
        Path file = tempDir.resolve("messy.txt");
        Files.write(file, List.of(
                "",
                "not-a-valid-line",
                "0",
                "0 list",
                "0 todo",
                "0 todo keep me",
                "2 todo bad flag"
        ), StandardCharsets.UTF_8);

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("[T][ ] keep me", loaded.get(0).toString());
    }

    @Test
    public void load_pathIsDirectory_throwsEkudException() throws Exception {
        Path directory = tempDir.resolve("not-a-file");
        Files.createDirectory(directory);
        Storage storage = new Storage(directory.toString());
        EkudException exception = assertThrows(EkudException.class, storage::load);
        assertTrue(exception.getMessage().contains("Could not read saved tasks"));
    }

    @Test
    public void save_parentPathIsFile_throwsEkudException() throws Exception {
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "not a directory");
        Storage storage = new Storage(blocker.resolve("ekud.txt").toString());
        EkudException exception = assertThrows(EkudException.class, () -> storage.save(new TaskList()));
        assertTrue(exception.getMessage().contains("Could not save tasks"));
    }

    @Test
    public void defaultPath_pointsAtDataFolder() {
        assertEquals("data/ekud.txt", Storage.DEFAULT_PATH);
    }
}
