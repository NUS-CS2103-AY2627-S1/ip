package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void saveThenLoad_roundTripsAllTypes() throws IOException {
        Storage storage = new Storage(tempDir.resolve("data/nova.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markDone();
        tasks.add(deadline);
        tasks.add(new Event("meeting", LocalDate.of(2026, 8, 28), LocalDate.of(2026, 8, 29)));
        storage.save(tasks);

        ArrayList<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertTrue(loaded.get(0) instanceof Todo);
        assertTrue(loaded.get(1).isDone());
        assertEquals("[D][X] return book (by: Oct 15 2019)", loaded.get(1).toString());
    }

    @Test
    public void appendToArchive_writesTasksInSaveFormat() throws IOException {
        Path dir = tempDir.resolve("data");
        Storage storage = new Storage(dir.resolve("nova.txt").toString(),
                dir.resolve("nova-archive.txt").toString());
        Todo done = new Todo("old task");
        done.markDone();
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(done);
        storage.appendToArchive(tasks);
        String content = Files.readString(dir.resolve("nova-archive.txt"));
        assertEquals("T | DONE | old task" + System.lineSeparator(), content);
    }

    @Test
    public void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("no-such-dir/nova.txt").toString());
        assertEquals(0, storage.load().size());
    }

    @Test
    public void load_corruptedLine_skipsIt() throws IOException {
        Path dir = tempDir.resolve("data");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("nova.txt"),
                "T | DONE | valid task\nD | NOT_DONE | x | Sunday\nGARBAGE\n");
        Storage storage = new Storage(dir.resolve("nova.txt").toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("valid task", loaded.get(0).getDescription());
    }

    @Test
    public void load_partlyCorruptedFile_reportsTheSkippedLinesOnce() throws IOException {
        Path dir = tempDir.resolve("data");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("nova.txt"), "T | NOT_DONE | keep me\nGARBAGE\nALSO GARBAGE\n");
        Storage storage = new Storage(dir.resolve("nova.txt").toString());
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertTrue(storage.consumeErrorMessage().contains("Skipped 2"));
        assertEquals("", storage.consumeErrorMessage());
    }

    @Test
    public void load_cleanFile_reportsNothing() throws IOException {
        Path dir = tempDir.resolve("data");
        Files.createDirectories(dir);
        Files.writeString(dir.resolve("nova.txt"), "T | NOT_DONE | keep me\n");
        Storage storage = new Storage(dir.resolve("nova.txt").toString());
        storage.load();
        assertEquals("", storage.consumeErrorMessage());
    }

    @Test
    public void save_folderCannotBeCreated_reportsError() throws IOException {
        Path blocker = tempDir.resolve("blocker");
        Files.writeString(blocker, "a file, not a folder");
        Storage storage = new Storage(blocker.resolve("nova.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        storage.save(tasks);
        assertFalse(storage.consumeErrorMessage().isEmpty());
    }

    @Test
    public void save_replacesThePreviousContent() throws IOException {
        Path dir = tempDir.resolve("data");
        Storage storage = new Storage(dir.resolve("nova.txt").toString());
        ArrayList<Task> first = new ArrayList<>();
        first.add(new Todo("first"));
        first.add(new Todo("second"));
        storage.save(first);
        ArrayList<Task> second = new ArrayList<>();
        second.add(new Todo("only one"));
        storage.save(second);
        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("only one", loaded.get(0).getDescription());
    }
}
