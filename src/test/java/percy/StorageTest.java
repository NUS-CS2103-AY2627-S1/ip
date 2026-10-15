package percy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests {@link Storage} against real (temporary) files: saving and loading
 * should round-trip every task type, and a missing or corrupted file should
 * degrade gracefully rather than crash Percy.
 */
public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_missingFile_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("percy.txt").toString());
        assertEquals(0, storage.load().size());
    }

    @Test
    public void saveThenLoad_roundTripsEveryTaskType() {
        Storage storage = new Storage(tempDir.resolve("percy.txt").toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.setDone(true);
        tasks.add(deadline);
        tasks.add(new Event("project", LocalDate.of(2019, 10, 10), LocalDate.of(2019, 10, 12)));

        storage.save(tasks);
        ArrayList<Task> reloaded = storage.load();

        assertEquals(3, reloaded.size());
        assertEquals("T | 0 | read book", reloaded.get(0).toFileFormat());
        assertEquals("D | 1 | return book | 2019-10-15", reloaded.get(1).toFileFormat());
        assertEquals("E | 0 | project | 2019-10-10 | 2019-10-12", reloaded.get(2).toFileFormat());
    }

    @Test
    public void save_createsTheParentFolderIfMissing() {
        Path nested = tempDir.resolve("nested").resolve("dir").resolve("percy.txt");
        Storage storage = new Storage(nested.toString());

        storage.save(new TaskList());

        assertTrue(Files.exists(nested));
    }

    @Test
    public void load_corruptedOrUnrecognisedLines_areSkippedNotThrown() throws IOException {
        Path file = tempDir.resolve("percy.txt");
        Files.write(file, List.of(
                "T | 0 | read book",
                "not a valid line at all",
                "D | 0 | missing the date field",
                "Z | 0 | unknown type letter",
                "T | 0 | buy milk"));
        Storage storage = new Storage(file.toString());

        ArrayList<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("read book", tasks.get(0).getRawDescription());
        assertEquals("buy milk", tasks.get(1).getRawDescription());
    }

    @Test
    public void saveThenLoad_emptyList_producesAnEmptyFile() throws IOException {
        Path file = tempDir.resolve("percy.txt");
        Storage storage = new Storage(file.toString());

        storage.save(new TaskList());

        assertEquals("", Files.readString(file));
        assertEquals(0, storage.load().size());
    }
}
