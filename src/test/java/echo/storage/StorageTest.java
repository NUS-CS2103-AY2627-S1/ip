package echo.storage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import echo.exception.StorageException;
import echo.task.Deadline;
import echo.task.Event;
import echo.task.Task;
import echo.task.Todo;

// Class with Opencode contribution
/**
 * Tests {@link Storage} save/read round-trips using a temporary directory
 * so the real {@code data/echo.txt} is never touched.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    private Storage storageAt(String fileName) {
        return new Storage(tempDir.resolve(fileName));
    }

    private ArrayList<Task> sampleTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        Deadline doneDeadline = new Deadline("return book",
                LocalDateTime.of(2025, 1, 15, 18, 0));
        doneDeadline.markDone(LocalDateTime.of(2025, 1, 13, 12, 45));
        tasks.add(doneDeadline);
        tasks.add(new Event("project meeting",
                LocalDateTime.of(2025, 1, 15, 18, 30),
                LocalDateTime.of(2025, 1, 16, 20, 30)));
        return tasks;
    }

    @Test
    public void read_missingFile_returnsEmptyList() throws Exception {
        assertTrue(storageAt("absent.txt").read().isEmpty());
    }

    @Test
    public void saveThenRead_roundTripsAllTaskTypes() throws Exception {
        Storage storage = storageAt("echo.txt");
        ArrayList<Task> saved = sampleTasks();

        storage.save(saved);
        List<Task> loaded = storage.read();

        assertEquals(saved.size(), loaded.size());
        for (int i = 0; i < saved.size(); i++) {
            assertEquals(saved.get(i).toString(), loaded.get(i).toString());
            assertEquals(saved.get(i).toSaveFormat(), loaded.get(i).toSaveFormat());
        }
    }

    @Test
    public void save_createsMissingParentDirectories() throws Exception {
        Storage storage = new Storage(tempDir.resolve("nested/dir/echo.txt"));

        storage.save(sampleTasks());

        assertTrue(Files.exists(tempDir.resolve("nested/dir/echo.txt")));
    }

    @Test
    public void read_corruptLine_throwsStorageException() throws Exception {
        Path file = tempDir.resolve("corrupt.txt");
        Files.writeString(file, "T | invalid-date | fine line\nGARBAGE");

        assertThrows(StorageException.class, () -> new Storage(file).read());
    }

    @Test
    public void read_corruptSecondLine_errorIdentifiesLineNumber() throws Exception {
        Path file = tempDir.resolve("line-number.txt");
        Files.writeString(file, "T |  | valid\nD |  | invalid | not-a-date");

        StorageException exception = assertThrows(StorageException.class, () -> new Storage(file).read());

        assertTrue(exception.getMessage().contains("line 2"));
    }

    @Test
    public void read_duplicateTask_throwsStorageException() throws Exception {
        Path file = tempDir.resolve("duplicate.txt");
        Files.writeString(file, "T |  | read book\nT | 2025-01-13T12:45 | read book");

        StorageException exception = assertThrows(StorageException.class, () -> new Storage(file).read());

        assertTrue(exception.getMessage().contains("duplicates an earlier mission"));
    }

    @Test
    public void read_caseDifferentDescriptions_returnsBothTasks() throws Exception {
        Path file = tempDir.resolve("case-sensitive.txt");
        Files.writeString(file, "T |  | read book\nT |  | Read book");

        assertEquals(2, new Storage(file).read().size());
    }

    @Test
    public void saveThenRead_unicodeDescription_roundTrips() throws Exception {
        Storage storage = storageAt("unicode.txt");
        Todo task = new Todo("réviser 日本語");

        storage.save(List.of(task));

        assertEquals(task.toSaveFormat(), storage.read().get(0).toSaveFormat());
    }

    @Test
    public void save_parentIsRegularFile_throwsStorageException() throws Exception {
        Path parentFile = tempDir.resolve("not-a-directory");
        Files.writeString(parentFile, "unchanged");
        Storage storage = new Storage(parentFile.resolve("echo.txt"));

        assertThrows(StorageException.class, () -> storage.save(sampleTasks()));
        assertEquals("unchanged", Files.readString(parentFile));
    }

    @Test
    public void read_blankLines_skipped() throws Exception {
        Path file = tempDir.resolve("blanks.txt");
        Files.writeString(file, "\n\nT |  | read book\n   \n");

        List<Task> tasks = new Storage(file).read();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void read_legacyCompletionFlag_throwsStorageException() throws Exception {
        Path file = tempDir.resolve("legacy.txt");
        Files.writeString(file, "T | 0 | read book");

        assertThrows(StorageException.class, () -> new Storage(file).read());
    }
}
