package baymax.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import baymax.task.Deadline;
import baymax.task.Event;
import baymax.task.TaskList;
import baymax.task.Todo;

/**
 * Tests task persistence without using the application's real data file.
 */
public class StorageTest {
    /** Temporary folder so persistence tests never touch real storage. */
    @TempDir
    public Path temporaryFolder;

    @Test
    public void load_initialBom_restoresTasksAndAllowsRepeatedSaves() throws IOException {
        Path filePath = temporaryFolder.resolve("bom.txt");
        Files.writeString(filePath, "\uFEFFT | 0 | valid task\n");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = storage.load();
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] valid task", tasks.get(0).toString());
        assertEquals("", storage.getLoadWarning());
        tasks.add(new Todo("new task"));
        storage.save(tasks);
        storage.save(tasks);
        assertEquals(2, new Storage(filePath.toString()).load().size());
        assertTrue(Files.readString(filePath).startsWith("T | 0 | valid task"));
    }

    @Test
    public void load_bomWithOnlyBlankLines_allowsSaving() throws IOException {
        Path filePath = temporaryFolder.resolve("blank-bom.txt");
        for (String content : new String[] {"\uFEFF", "\uFEFF\r\n \t\n"}) {
            Files.writeString(filePath, content);
            Storage storage = new Storage(filePath.toString());
            TaskList tasks = storage.load();
            assertEquals(0, tasks.size());
            assertEquals("", storage.getLoadWarning());
            tasks.add(new Todo("new task"));
            storage.save(tasks);
            assertEquals(1, new Storage(filePath.toString()).load().size());
        }
    }

    @Test
    public void load_misplacedOrRepeatedBom_preservesInvalidRecords() throws IOException {
        Path filePath = temporaryFolder.resolve("invalid-bom.txt");
        for (String content : new String[] {"\n\uFEFFT | 0 | task", "\uFEFF\uFEFFT | 0 | task",
            "\uFEFFinvalid record"}) {
            Files.writeString(filePath, content);
            Storage storage = new Storage(filePath.toString());
            TaskList tasks = storage.load();
            assertEquals(0, tasks.size());
            assertTrue(storage.getLoadWarning().contains("Skipped 1"));
            assertThrows(IOException.class, () -> storage.save(tasks));
            assertEquals(content, Files.readString(filePath));
        }
    }

    @Test
    public void load_bomInsideDescription_preservesText() throws IOException {
        Path filePath = temporaryFolder.resolve("description-bom.txt");
        String record = "T | 0 | caf\u00E9\uFEFF task";
        Files.writeString(filePath, "\uFEFF" + record);
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = storage.load();
        assertEquals(record, tasks.get(0).toStorageString());
        assertEquals("", storage.getLoadWarning());
        storage.save(tasks);
        assertEquals(record + System.lineSeparator(), Files.readString(filePath));
    }

    @Test
    public void load_unsupportedYears_skipsRecordsAndProtectsFile() throws IOException {
        Path filePath = temporaryFolder.resolve("years.txt");
        String original = String.join("\n", "D | 0 | zero | 0000-01-01",
                "D | 0 | negative | -0001-01-01", "D | 0 | large | +10000-01-01",
                "E | 0 | early | 0000-01-01 | 2026-01-01",
                "E | 0 | late | 2026-01-01 | +10000-01-01",
                "D | 0 | valid | 0001-01-01");
        Files.writeString(filePath, original);
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = storage.load();
        assertEquals(1, tasks.size());
        assertEquals("[D][ ] valid (by: Jan 01 0001)", tasks.get(0).toString());
        assertTrue(storage.getLoadWarning().contains("Skipped 5"));
        assertThrows(IOException.class, () -> storage.save(tasks));
        assertEquals(original, Files.readString(filePath));
    }

    @Test
    public void save_staleInstance_preservesOtherInstancesTasks() throws IOException {
        Path filePath = temporaryFolder.resolve("shared.txt");
        Files.writeString(filePath, "T | 0 | original\n");
        Storage first = new Storage(filePath.toString());
        Storage second = new Storage(filePath.toString());
        TaskList firstTasks = first.load();
        TaskList secondTasks = second.load();
        firstTasks.add(new Todo("first"));
        first.save(firstTasks);
        String saved = Files.readString(filePath);
        secondTasks.add(new Todo("second"));
        IOException exception = assertThrows(IOException.class, () -> second.save(secondTasks));
        assertTrue(exception.getMessage().contains("changed since it was loaded"));
        assertEquals(saved, Files.readString(filePath));
        firstTasks.add(new Todo("third"));
        first.save(firstTasks);
        assertEquals(3, first.load().size());
    }

    @Test
    public void save_fileCreatedAfterMissingLoad_rejectsOverwrite() throws IOException {
        Path filePath = temporaryFolder.resolve("new.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = storage.load();
        Files.writeString(filePath, "T | 0 | created elsewhere\n");
        assertThrows(IOException.class, () -> storage.save(tasks));
        assertEquals("T | 0 | created elsewhere\n", Files.readString(filePath));
    }

    @Test
    public void save_fileDeletedAfterLoad_rejectsStaleSave() throws IOException {
        Path filePath = temporaryFolder.resolve("deleted.txt");
        Files.writeString(filePath, "T | 0 | original\n");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = storage.load();
        Files.delete(filePath);
        assertThrows(IOException.class, () -> storage.save(tasks));
        assertTrue(Files.notExists(filePath));
    }

    @Test
    public void save_lockHeld_rejectsSaveAndAllowsRetry() throws IOException {
        Path filePath = temporaryFolder.resolve("locked.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = storage.load();
        tasks.add(new Todo("pending"));
        try (FileChannel channel = FileChannel.open(temporaryFolder.resolve("locked.txt.lock"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                FileLock lock = channel.lock()) {
            assertTrue(lock.isValid());
            assertThrows(IOException.class, () -> storage.save(tasks));
            assertTrue(Files.notExists(filePath));
        }
        storage.save(tasks);
        assertEquals(1, storage.load().size());
    }

    @Test
    public void load_emptyOrWhitespaceOnlyFile_allowsSavingWithoutWarning() throws IOException {
        Path filePath = temporaryFolder.resolve("Baymax.txt");
        for (String contents : new String[]{"", "\r\n", " \t\r\n\n  \n"}) {
            Files.writeString(filePath, contents);
            Storage storage = new Storage(filePath.toString());
            TaskList tasks = storage.load();

            assertEquals(0, tasks.size());
            assertEquals("", storage.getLoadWarning());
            tasks.add(new Todo("new task"));
            storage.save(tasks);
            assertEquals("new task", storage.load().get(0).getDescription());
        }
    }

    @Test
    public void load_blankLinesBetweenRecords_preservesTasksWithoutWarning() throws IOException {
        Path filePath = temporaryFolder.resolve("Baymax.txt");
        Files.writeString(filePath, "\nT | 0 | first\n \t\nT | 1 | second\n\n");
        Storage storage = new Storage(filePath.toString());

        TaskList tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][X] second", tasks.get(1).toString());
        assertEquals("", storage.getLoadWarning());
        storage.save(tasks);
    }

    @Test
    public void load_blankLinesWithMalformedRecord_stillProtectsOriginalFile() throws IOException {
        Path filePath = temporaryFolder.resolve("Baymax.txt");
        String contents = "\n \t\ninvalid record\n\n";
        Files.writeString(filePath, contents);
        Storage storage = new Storage(filePath.toString());

        TaskList tasks = storage.load();

        assertTrue(storage.getLoadWarning().contains("Skipped 1"));
        assertThrows(IOException.class, () -> storage.save(tasks));
        assertEquals(contents, Files.readString(filePath));
    }

    @Test
    public void load_corruptAndDuplicateRecords_warnsAndProtectsOriginalFile() throws IOException {
        Path filePath = temporaryFolder.resolve("Baymax.txt");
        String original = String.join(System.lineSeparator(), "T | 0 | valid", "T | 1 | valid",
                "E | 0 | backwards | 2024-01-02 | 2024-01-01",
                "E | 0 | equal | 2024-01-01 | 2024-01-01", "D | 0 | impossible | 2024-02-30");
        Files.writeString(filePath, original);
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = storage.load();
        assertEquals(1, tasks.size());
        assertTrue(storage.getLoadWarning().contains("Skipped 4"));
        assertThrows(IOException.class, () -> storage.save(tasks));
        assertEquals(original, Files.readString(filePath));
    }

    @Test
    public void load_directoryInsteadOfFile_warnsAndBlocksSaving() {
        Storage storage = new Storage(temporaryFolder.toString());
        assertEquals(0, storage.load().size());
        assertTrue(storage.getLoadWarning().contains("cannot read"));
        assertThrows(IOException.class, () -> storage.save(new TaskList()));
    }

    @Test
    public void save_parentIsFile_reportsFailureWithoutChangingParent() throws IOException {
        Path parent = temporaryFolder.resolve("parent");
        Files.writeString(parent, "original");
        Storage storage = new Storage(parent.resolve("Baymax.txt").toString());
        assertThrows(IOException.class, () -> storage.save(new TaskList()));
        assertEquals("original", Files.readString(parent));
    }

    @Test
    public void save_existingFile_replacesRecordsAndRoundTripsUnicode() throws IOException {
        Path filePath = temporaryFolder.resolve("Baymax.txt");
        Files.writeString(filePath, "T | 0 | old task");
        Storage storage = new Storage(filePath.toString());
        storage.load();
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read caf\u00e9 notes"));
        storage.save(tasks);
        assertEquals("read caf\u00e9 notes", storage.load().get(0).getDescription());
        try (var files = Files.list(temporaryFolder)) {
            assertEquals(2, files.count());
        }
    }


    /**
     * Verifies that saving tasks creates missing folders and writes storage records.
     */
    @Test
    public void save_tasksWithNestedFilePath_createsParentDirectoryAndWritesTasks()
            throws IOException {
        Path filePath = temporaryFolder.resolve("data").resolve("Baymax.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline(
                "submit report", LocalDate.of(2019, 12, 2));
        Event event = new Event(
                "team meeting",
                LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4));
        deadline.markAsDone();

        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);
        storage.save(tasks);

        assertTrue(Files.exists(filePath));
        assertEquals(String.join(System.lineSeparator(),
                        "T | 0 | read book",
                        "D | 1 | submit report | 2019-12-02",
                        "E | 0 | team meeting | 2019-12-02 | 2019-12-04")
                        + System.lineSeparator(),
                Files.readString(filePath));
    }

    /**
     * Verifies that loading from a missing data file returns an empty task list.
     */
    @Test
    public void load_missingFile_returnsEmptyTaskList() {
        Path filePath = temporaryFolder.resolve("missing").resolve("Baymax.txt");
        Storage storage = new Storage(filePath.toString());

        TaskList tasks = storage.load();

        assertEquals(0, tasks.size());
    }

    /**
     * Verifies that valid saved records are restored as the correct task types.
     */
    @Test
    public void load_validTaskRecords_returnsRestoredTasks() throws IOException {
        Path filePath = temporaryFolder.resolve("Baymax.txt");
        Files.writeString(filePath, String.join(System.lineSeparator(),
                "T | 0 | read book",
                "D | 1 | submit report | 2019-12-02",
                "E | 0 | team meeting | 2019-12-02 | 2019-12-04"));
        Storage storage = new Storage(filePath.toString());

        TaskList tasks = storage.load();

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[D][X] submit report (by: Dec 02 2019)",
                tasks.get(1).toString());
        assertEquals("[E][ ] team meeting (from: Dec 02 2019 to: Dec 04 2019)",
                tasks.get(2).toString());
    }

    /**
     * Verifies that malformed records are skipped while valid records still load.
     */
    @Test
    public void load_malformedTaskRecords_skipsInvalidRecords() throws IOException {
        Path filePath = temporaryFolder.resolve("Baymax.txt");
        Files.writeString(filePath, String.join(System.lineSeparator(),
                "T | 0 | valid todo",
                "T | 0",
                "T | 0 |   ",
                "T | maybe | invalid done flag",
                "X | 0 | unknown task type",
                "D | 0 | invalid deadline | 2019-99-99",
                "D | 0 | deadline with extra field | 2019-12-02 | extra",
                "E | 0 | invalid event | 2019-12-02 | not-a-date",
                "E | 0 | event with missing to | 2019-12-02"));
        Storage storage = new Storage(filePath.toString());

        TaskList tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] valid todo", tasks.get(0).toString());
    }
    @Test
    public void load_invalidUtf8_blocksSavingWithoutReplacingBytes() throws IOException {
        Path file = temporaryFolder.resolve("invalid-utf8.txt");
        byte[] original = new byte[]{(byte) 0xc3, (byte) 0x28};
        Files.write(file, original);
        Storage storage = new Storage(file.toString());
        assertEquals(0, storage.load().size());
        assertTrue(storage.isReadOnly());
        assertTrue(storage.getLoadWarning().contains("cannot read"));
        assertThrows(IOException.class, () -> storage.save(new TaskList()));
        assertArrayEquals(original, Files.readAllBytes(file));
    }

    @Test
    public void load_repairedFile_clearsWarningAndRefreshesSnapshot() throws IOException {
        Path file = temporaryFolder.resolve("repaired.txt");
        Files.writeString(file, "broken\n");
        Storage storage = new Storage(file.toString());
        storage.load();
        assertTrue(storage.isReadOnly());
        Files.writeString(file, "T | 1 | repaired\n");
        TaskList tasks = storage.load();
        assertFalse(storage.isReadOnly());
        assertEquals("", storage.getLoadWarning());
        assertEquals("[T][X] repaired", tasks.get(0).toString());
        tasks.remove(0);
        storage.save(tasks);
        assertEquals("", Files.readString(file));
    }

    @Test
    public void load_invalidFieldCountsAndEventRanges_skipsOnlyBadRecords() throws IOException {
        Path file = temporaryFolder.resolve("fields.txt");
        Files.writeString(file, String.join("\n",
                "T | 0 | extra | field",
                "D | 0 | missing date",
                "E | 0 | extra | 2024-01-01 | 2024-01-02 | field",
                "E | 0 | reversed | 2024-01-02 | 2024-01-01",
                "E | 0 | equal | 2024-01-01 | 2024-01-01",
                "T | 0 | embedded\u0000control",
                "T|1|valid"));
        Storage storage = new Storage(file.toString());
        TaskList tasks = storage.load();
        assertEquals(1, tasks.size());
        assertEquals("[T][X] valid", tasks.get(0).toString());
        assertTrue(storage.getLoadWarning().contains("Skipped 6 invalid"));
    }
}
