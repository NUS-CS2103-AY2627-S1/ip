package baemax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for {@link Storage}, which reads and writes the save file. */
public class StorageTest {

    @TempDir
    Path tempDir;

    private Storage storageAt(String fileName) {
        return new Storage(tempDir.resolve(fileName).toString());
    }

    @Test
    public void load_missingFile_returnsEmptyList() {
        assertEquals(0, storageAt("nothing-here.txt").load().size());
    }

    @Test
    public void load_missingDataFolder_returnsEmptyListInsteadOfThrowing() {
        Storage storage = new Storage(tempDir.resolve("nested/does-not-exist.txt").toString());
        assertEquals(0, storage.load().size());
    }

    @Test
    public void saveThenLoad_todoDeadlineEvent_roundTripsExactly() throws BaemaxException {
        Storage storage = storageAt("tasks.txt");
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", TaskDate.parse("2019-10-15"));
        Event event = new Event("camp", TaskDate.parse("2019-08-05 1800"), TaskDate.parse("2019-08-07"));

        storage.save(List.of(todo, deadline, event));
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] camp (from: Aug 05 2019, 6:00PM to: Aug 07 2019)", loaded.get(2).toString());
    }

    @Test
    public void save_createsMissingParentFolder() throws IOException {
        Storage storage = new Storage(tempDir.resolve("data/tasks.txt").toString());
        storage.save(List.of(new Todo("a")));
        assertTrue(Files.exists(tempDir.resolve("data/tasks.txt")));
    }

    @Test
    public void load_corruptedLine_isSkippedButGoodLinesSurvive() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "T | 1 | good todo",
                "this line is garbage",
                "X | 0 | unknown type",
                "D | 0 | missing due date",
                "T | 0 | another good todo"));

        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals(2, loaded.size());
        assertEquals("[T][X] good todo", loaded.get(0).toString());
        assertEquals("[T][ ] another good todo", loaded.get(1).toString());
    }

    @Test
    public void load_blankLines_areIgnoredWithoutError() throws IOException {
        Path file = tempDir.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | a\n\n   \nT | 0 | b\n");

        assertEquals(2, new Storage(file.toString()).load().size());
    }
}
