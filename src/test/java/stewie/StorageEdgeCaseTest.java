package stewie;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import stewie.model.Deadline;
import stewie.model.Event;
import stewie.model.Task;
import stewie.model.ToDo;
import stewie.storage.Storage;
import stewie.storage.StorageException;

/**
 * Verifies storage format boundaries, Unicode portability, reload recovery, and external file changes.
 */
public class StorageEdgeCaseTest {
    @TempDir
    private Path directory;

    /**
     * Verifies a missing or empty file loads cleanly and missing parent folders are created on save.
     */
    @Test
    public void loadAndSave_handlesEmptyStorage() throws IOException {
        Path file = directory.resolve("nested/data/stewie.txt");
        Storage storage = new Storage(file);
        assertTrue(storage.loadFromDisk().isEmpty());
        assertEquals("", storage.getLoadWarning());
        assertFalse(Files.exists(file));
        storage.saveToDisk(new ArrayList<>());
        assertEquals("", Files.readString(file));
        assertTrue(storage.loadFromDisk().isEmpty());
        assertEquals("", storage.getLoadWarning());
        storage.saveToDisk(new ArrayList<>(List.of(new ToDo("first"))));
        assertEquals("T | 0 | first", Files.readString(file));
        storage.saveToDisk(new ArrayList<>());
        assertTrue(storage.loadFromDisk().isEmpty());
    }

    /**
     * Verifies exact serialization and round trips for every type and completion state in Burmese locale.
     */
    @Test
    public void saveTasks_preservesUnicodeAndEveryStatus() throws IOException {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("my-MM"));
            Path file = directory.resolve("စာအုပ်.txt");
            Storage storage = new Storage(file);
            storage.loadFromDisk();
            ArrayList<Task> tasks = new ArrayList<>(List.of(new ToDo("စာအုပ် ဖတ်ရန်"), new ToDo("done"),
                    new Deadline("report", "2026-01-01"), new Deadline("submitted", "2026-01-02"),
                    new Event("trip", "2026-01-01", "2026-01-02"),
                    new Event("returned", "2026-01-02", "2026-01-03")));
            for (int index : new int[] {1, 3, 5}) {
                tasks.get(index).markAsDone();
            }
            storage.saveToDisk(tasks);
            assertEquals(String.join("\n", "T | 0 | စာအုပ် ဖတ်ရန်", "T | 1 | done",
                    "D | 0 | report | 01 Jan 2026", "D | 1 | submitted | 02 Jan 2026",
                    "E | 0 | trip | 01 Jan 2026 | 02 Jan 2026", "E | 1 | returned | 02 Jan 2026 | 03 Jan 2026"),
                    Files.readString(file));
            ArrayList<Task> restored = new Storage(file).loadFromDisk();
            assertEquals(tasks.size(), restored.size());
            for (int index = 0; index < tasks.size(); index++) {
                assertTrue(tasks.get(index).hasSameDetails(restored.get(index)));
                assertEquals(tasks.get(index).isDone(), restored.get(index).isDone());
            }
        } finally {
            Locale.setDefault(original);
        }
    }

    /**
     * Verifies Windows and Unix line endings with or without a final newline load identically.
     */
    @Test
    public void loadTasks_acceptsPlatformLineEndings() throws IOException {
        Path file = directory.resolve("stewie.txt");
        for (String separator : new String[] {"\n", "\r\n", "\r"}) {
            for (String ending : new String[] {"", separator}) {
                Files.writeString(file, "T|0|first" + separator + "T | 1 | last" + ending);
                Storage storage = new Storage(file);
                ArrayList<Task> tasks = storage.loadFromDisk();
                assertEquals(2, tasks.size());
                assertEquals("[T] [ ] first", tasks.get(0).toString());
                assertEquals("[T] [X] last", tasks.get(1).toString());
                assertEquals("", storage.getLoadWarning());
            }
        }
    }

    /**
     * Verifies all malformed shapes report their line numbers while later valid records survive.
     */
    @Test
    public void loadTasks_reportsEveryMalformedShape() throws IOException {
        String[] invalid = {"", "T", "T | 0", "T | 2 | bad", "T | | bad", "X | 0 | bad",
            "T | 0 | extra | field", "D | 0 | missing", "D | 0 | extra | 2026-01-01 | field",
            "E | 0 | missing | 2026-01-01", "E | 0 | extra | 2026-01-01 | 2026-01-02 | field",
            "T | 0 |", "D | 0 | missing |", "E | 0 | missing | | 2026-01-02"};
        Path file = directory.resolve("stewie.txt");
        for (String record : invalid) {
            String content = "T | 0 | first\n" + record + "\nT | 1 | last\n";
            Files.writeString(file, content);
            Storage storage = new Storage(file);
            ArrayList<Task> tasks = storage.loadFromDisk();
            assertEquals(2, tasks.size(), record);
            assertEquals("[T] [X] last", tasks.get(1).toString());
            assertEquals("Storage warning: Invalid or duplicate records at lines 2. "
                    + "Valid tasks are available read-only. Back up and repair the task file, then restart.",
                    storage.getLoadWarning());
            assertThrows(StorageException.class, () -> storage.saveToDisk(new ArrayList<>()));
            assertEquals(content, Files.readString(file));
        }
    }

    /**
     * Verifies reloading after repair clears read-only state and the old warning.
     */
    @Test
    public void loadTasks_recoversAfterRepair() throws IOException {
        Path file = directory.resolve("stewie.txt");
        Files.writeString(file, "broken");
        Storage storage = new Storage(file);
        storage.loadFromDisk();
        assertFalse(storage.getLoadWarning().isEmpty());
        Files.writeString(file, "T | 1 | repaired");
        ArrayList<Task> tasks = storage.loadFromDisk();
        assertEquals("", storage.getLoadWarning());
        assertEquals("[T] [X] repaired", tasks.getFirst().toString());
        tasks.add(new ToDo("new"));
        storage.saveToDisk(tasks);
        assertEquals(2, new Storage(file).loadFromDisk().size());
    }

    /**
     * Verifies an externally created or deleted file cannot be silently overwritten.
     */
    @Test
    public void saveTasks_detectsExternalCreationAndDeletion() throws IOException {
        Path file = directory.resolve("stewie.txt");
        Storage storage = new Storage(file);
        storage.loadFromDisk();
        Files.writeString(file, "T | 0 | external");
        assertEquals("The task file changed outside Stewie. Restart before making changes.",
                assertThrows(StorageException.class, () -> storage.saveToDisk(new ArrayList<>())).getMessage());
        assertEquals("T | 0 | external", Files.readString(file));
        storage.loadFromDisk();
        Files.delete(file);
        assertThrows(StorageException.class, () -> storage.saveToDisk(new ArrayList<>()));
        assertFalse(Files.exists(file));
    }

    /**
     * Verifies a symlink substituted after loading cannot redirect a subsequent save.
     */
    @Test
    public void saveTasks_protectsAgainstSubstitutedSymbolicLink() throws IOException {
        assumeTrue(Files.getFileStore(directory).supportsFileAttributeView("posix"),
                "Symbolic-link checks require a POSIX test environment");
        Path file = directory.resolve("stewie.txt");
        Path target = directory.resolve("other.txt");
        Files.writeString(file, "T | 0 | original");
        Files.writeString(target, "T | 0 | original");
        Storage storage = new Storage(file);
        storage.loadFromDisk();
        Files.delete(file);
        Files.createSymbolicLink(file, target);
        assertEquals("The task file changed outside Stewie. Restart before making changes.",
                assertThrows(StorageException.class, () -> storage.saveToDisk(new ArrayList<>())).getMessage());
        assertTrue(Files.isSymbolicLink(file));
        assertEquals("T | 0 | original", Files.readString(target));
    }

    /**
     * Verifies replacing the file with a directory yields a safe read or save error.
     */
    @Test
    public void storage_rejectsDirectoryAsTaskFile() throws IOException {
        Path file = directory.resolve("stewie.txt");
        Storage storage = new Storage(file);
        storage.loadFromDisk();
        Files.createDirectory(file);
        assertTrue(assertThrows(StorageException.class, () -> storage.saveToDisk(new ArrayList<>()))
                .getMessage().startsWith("Unable to save tasks. No changes were applied."));
        assertTrue(storage.loadFromDisk().isEmpty());
        assertTrue(storage.getLoadWarning().startsWith("Storage warning: Unable to read the task file."));
        assertThrows(StorageException.class, () -> storage.saveToDisk(new ArrayList<>()));
        assertTrue(Files.isDirectory(file));
        try (var files = Files.list(file)) {
            assertEquals(0, files.count());
        }
    }

    /**
     * Verifies duplicate dated records are compared by normalized dates and ignore completion status.
     */
    @Test
    public void loadTasks_rejectsDuplicateDatedRecords() throws IOException {
        Path file = directory.resolve("stewie.txt");
        byte[] content = String.join("\n", "D | 0 | report | 2026-01-01", "D | 1 | REPORT | 1/1/2026",
                "E | 0 | trip | 2026-01-01 | 2026-01-02", "E | 1 | TRIP | 1/1/2026 | 2/1/2026")
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
        Files.write(file, content);
        Storage storage = new Storage(file);
        assertEquals(2, storage.loadFromDisk().size());
        assertTrue(storage.getLoadWarning().contains("lines 2, 4."));
        assertThrows(StorageException.class, () -> storage.saveToDisk(new ArrayList<>()));
        assertArrayEquals(content, Files.readAllBytes(file));
    }
}
