package ben;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for {@link Storage}'s load/save round-tripping, run against a scratch directory. */
class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    void load_fileDoesNotExist_returnsEmptyList() throws BenException {
        Storage storage = new Storage(tempDir.resolve("no-such-file.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void save_missingParentFolder_createsItAutomatically() throws BenException {
        Storage storage = new Storage(tempDir.resolve("nested/dir/tasks.txt").toString());
        TaskList tasks = new TaskList();
        storage.save(tasks);
        assertTrue(Files.exists(tempDir.resolve("nested/dir/tasks.txt")));
    }

    @Test
    void saveAndLoad_roundTripsTaskList() throws BenException {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Task deadline = new Deadline("submit report", "2019-12-02 0930");
        deadline.markAsDone();
        tasks.add(deadline);

        storage.save(tasks);
        List<Task> reloaded = storage.load();

        assertEquals(2, reloaded.size());
        assertEquals("[T][ ] read book", reloaded.get(0).toString());
        assertEquals(deadline.toString(), reloaded.get(1).toString());
    }

    @Test
    void load_corruptedLineAmongValidOnes_skipsOnlyTheCorruptedLine() throws IOException, BenException {
        Path file = tempDir.resolve("tasks.txt");
        Files.write(file, List.of("T | 0 | read book", "not a valid line", "T | 1 | buy milk"));

        Storage storage = new Storage(file.toString());
        List<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][X] buy milk", tasks.get(1).toString());
    }

    @Test
    void load_blankLines_areIgnored() throws IOException, BenException {
        Path file = tempDir.resolve("tasks.txt");
        Files.write(file, List.of("T | 0 | read book", "", "   "));

        assertEquals(1, new Storage(file.toString()).load().size());
    }
}
