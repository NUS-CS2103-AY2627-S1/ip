package ramly.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ramly.exception.StorageException;
import ramly.model.Task;
import ramly.model.TaskList;
import ramly.model.Todo;

/** Tests storage configuration and collaborator assumptions. */
public class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void constructor_nullFilePath_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Storage(null));
    }

    @Test
    public void constructor_blankFilePath_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Storage("  "));
    }

    @Test
    public void save_nullTaskList_throwsAssertionError() {
        Storage storage = new Storage("unused.txt");

        assertThrows(AssertionError.class, () -> storage.save(null));
    }

    @Test
    public void load_validAndUnknownRecords_returnsValidTasksAndCreatesBackup() throws IOException {
        Path storageFile = temporaryDirectory.resolve("ramly.txt");
        Files.writeString(storageFile, String.join(System.lineSeparator(),
                "T | 1 | read a book",
                "D | 0 | submit report | 2019-10-15T00:00:00",
                "E | 0 | team meeting | 10am | 11am",
                "X | 0 | unknown task"));

        Storage storage = new Storage(storageFile.toString());
        ArrayList<Task> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertEquals("[•][✓] read a book", tasks.get(0).toString());
        assertEquals("[⏳][ ] submit report (by: Oct 15 2019)", tasks.get(1).toString());
        assertEquals("[◆][ ] team meeting (from: 10am to: 11am)", tasks.get(2).toString());
        assertEquals(1, storage.getWarnings().size());
        try (Stream<Path> files = Files.list(temporaryDirectory)) {
            Path backup = files.filter(path -> path.getFileName().toString().endsWith(".bak"))
                    .findFirst()
                    .orElseThrow();
            assertEquals(Files.readString(storageFile), Files.readString(backup));
        }
    }

    @Test
    public void load_missingFile_createsEmptyFile() {
        Path storageFile = temporaryDirectory.resolve("nested").resolve("ramly.txt");

        ArrayList<Task> tasks = new Storage(storageFile.toString()).load();

        assertEquals(0, tasks.size());
        assertTrue(Files.isRegularFile(storageFile));
    }

    @Test
    public void load_directoryPath_throwsStorageException() {
        assertThrows(StorageException.class, () -> new Storage(temporaryDirectory.toString()).load());
    }

    @Test
    public void load_parentPathIsFile_throwsStorageException() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.createFile(parentFile);

        assertThrows(StorageException.class,
                () -> new Storage(parentFile.resolve("ramly.txt").toString()).load());
    }

    @Test
    public void load_invalidUtf8_throwsStorageException() throws IOException {
        Path storageFile = temporaryDirectory.resolve("ramly.txt");
        Files.write(storageFile, new byte[] {(byte) 0xC3, (byte) 0x28});

        assertThrows(StorageException.class, () -> new Storage(storageFile.toString()).load());
    }

    @Test
    public void load_malformedFieldsDatesStatusesAndDuplicates_skipsInvalidLines() throws IOException {
        Path storageFile = temporaryDirectory.resolve("ramly.txt");
        Files.writeString(storageFile, String.join(System.lineSeparator(),
                "T | 0 | keep me",
                "T | 2 | invalid status",
                "D | 0 | invalid date | 2026-02-30T00:00:00",
                "E | 0 | reverse | 2026-01-01T10:00:00 | 2026-01-01T09:00:00",
                "T | 0 | KEEP   ME",
                "T | 0 | too | many"));
        Storage storage = new Storage(storageFile.toString());

        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[•][ ] keep me", tasks.get(0).toString());
        assertEquals(1, storage.getWarnings().size());
    }

    @Test
    public void save_unicodeTask_writesUtf8Record() throws IOException {
        Path storageFile = temporaryDirectory.resolve("ramly.txt");
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(new Todo("购买牛奶 🥛"));

        new Storage(storageFile.toString()).save(tasks);

        assertEquals("T | 0 | 购买牛奶 🥛" + System.lineSeparator(), Files.readString(storageFile));
    }
}
