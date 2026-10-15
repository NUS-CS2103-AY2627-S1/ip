package jeryl.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jeryl.task.Deadline;
import jeryl.task.Priority;
import jeryl.task.Task;
import jeryl.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("no-such-file.txt").toString());
        assertEquals(0, storage.load().size());
    }

    @Test
    public void saveThenLoad_mixOfTaskTypes_roundTripsExactly() {
        Storage storage = new Storage(tempDir.resolve("data/jeryl.txt").toString());

        ArrayList<Task> original = new ArrayList<>();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        original.add(todo);
        original.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T][X] read book", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", loaded.get(1).toString());
    }

    @Test
    public void save_missingParentFolder_createsItAutomatically() throws IOException {
        Path filePath = tempDir.resolve("nested/does/not/exist/jeryl.txt");
        Storage storage = new Storage(filePath.toString());

        storage.save(new ArrayList<>());

        assertTrue(Files.exists(filePath));
    }

    @Test
    public void saveThenLoad_taskWithPriority_roundTripsPriority() {
        Storage storage = new Storage(tempDir.resolve("data/jeryl.txt").toString());

        ArrayList<Task> original = new ArrayList<>();
        original.add(new Todo("read book", Priority.HIGH));

        storage.save(original);
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[H][T][ ] read book", loaded.get(0).toString());
    }

    @Test
    public void load_lineWithoutPriorityField_defaultsToNoPriority() throws IOException {
        Path filePath = tempDir.resolve("jeryl.txt");
        Files.writeString(filePath, "T | 0 | old save file task\n");

        Storage storage = new Storage(filePath.toString());
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] old save file task", loaded.get(0).toString());
    }

    @Test
    public void load_corruptedLineAmongValidOnes_skipsOnlyTheCorruptedLine() throws IOException {
        Path filePath = tempDir.resolve("jeryl.txt");
        Files.writeString(filePath, "T | 1 | ok task\nGARBAGE LINE\nD | 0 | missing by field\n");

        Storage storage = new Storage(filePath.toString());
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("[T][X] ok task", loaded.get(0).toString());
    }
}
