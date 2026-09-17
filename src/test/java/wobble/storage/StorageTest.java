package wobble.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.TaskList;
import wobble.tasks.Todo;

/** Tests storage creation, persistence, and restoration of task state. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void constructor_nullPath_rejectsInvalidPath() {
        assertThrows(IllegalArgumentException.class, () -> new Storage(null));
    }

    @Test
    void constructor_rootPath_rejectsDirectoryPath() {
        Path rootPath = temporaryDirectory.getRoot();

        assertThrows(IllegalArgumentException.class, () -> new Storage(rootPath));
    }

    @Test
    void load_missingFile_returnsEmptyTaskList() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/wobble.txt"));
        assertEquals(0, storage.load().size());
    }

    @Test
    void load_blankLines_ignoresEmptyRecords() throws Exception {
        Path file = temporaryDirectory.resolve("wobble.txt");
        Files.writeString(file, System.lineSeparator() + "  " + System.lineSeparator());

        assertEquals(0, new Storage(file).load().size());
    }

    @Test
    void load_directoryPath_throwsIOException() {
        assertThrows(IOException.class, () -> new Storage(temporaryDirectory).load());
    }

    @Test
    void save_nullTaskList_rejectsInvalidInput() {
        Storage storage = new Storage(temporaryDirectory.resolve("wobble.txt"));

        assertThrows(IllegalArgumentException.class, () -> storage.save(null));
    }

    @Test
    void save_emptyTaskList_createsEmptyFile() throws Exception {
        Path file = temporaryDirectory.resolve("nested/wobble.txt");

        new Storage(file).save(new TaskList());

        assertEquals("", Files.readString(file));
    }

    @Test
    void saveAndLoad_allTaskTypesPreservesTasksAndStatus() throws Exception {
        Path file = temporaryDirectory.resolve("nested/wobble.txt");
        Storage storage = new Storage(file);
        TaskList original = new TaskList();
        original.add(new Todo("read | book"));
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 27, 18, 0));
        deadline.markAsDone();
        original.add(deadline);
        original.add(new Event("meeting", LocalDateTime.of(2099, 8, 27, 14, 0),
                LocalDateTime.of(2099, 8, 27, 16, 0)));

        storage.save(original);
        TaskList loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("[T][ ] read | book", loaded.get(1).toString());
        assertEquals("[D][X] submit report (by: Aug 27 2026 6:00 pm)", loaded.get(2).toString());
        assertEquals("[E][ ] meeting (from: Aug 27 2099 2:00 pm to: Aug 27 2099 4:00 pm)", loaded.get(3).toString());
        assertFalse(Files.readString(file).isBlank());
    }

    @Test
    void load_corruptedAndDuplicateRecords_skipsInvalidEntries() throws Exception {
        Path file = temporaryDirectory.resolve("wobble.txt");
        String description = Base64.getEncoder().encodeToString(
                "read book".getBytes(StandardCharsets.UTF_8));
        String validRecord = "TODO|0|" + description;
        String invalidEvent = "EVENT|0|" + description + "|"
                + Base64.getEncoder().encodeToString("2026-08-27T14:00".getBytes(StandardCharsets.UTF_8))
                + "|"
                + Base64.getEncoder().encodeToString("2026-08-27T14:00".getBytes(StandardCharsets.UTF_8));
        Files.writeString(file, validRecord + System.lineSeparator()
                + validRecord + System.lineSeparator()
                + invalidEvent + System.lineSeparator()
                + "NOT_A_TASK|0|invalid" + System.lineSeparator());

        TaskList loaded = new Storage(file).load();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(1).toString());
    }

    @Test
    void load_malformedFieldsAndDates_skipsInvalidEntries() throws Exception {
        Path file = temporaryDirectory.resolve("wobble.txt");
        String description = Base64.getEncoder().encodeToString(
                "read book".getBytes(StandardCharsets.UTF_8));
        String validRecord = "TODO|0|" + description;
        String invalidStatus = "TODO|2|" + description;
        String invalidFieldCount = "TODO|0|" + description + "|extra";
        String invalidBase64 = "TODO|0|not-base64";
        String invalidDate = "DEADLINE|0|" + description + "|"
                + Base64.getEncoder().encodeToString("2027-02-30T18:00".getBytes(StandardCharsets.UTF_8));

        Files.writeString(file, validRecord + System.lineSeparator()
                + invalidStatus + System.lineSeparator()
                + invalidFieldCount + System.lineSeparator()
                + invalidBase64 + System.lineSeparator()
                + invalidDate + System.lineSeparator());

        TaskList loaded = new Storage(file).load();

        assertEquals(1, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(1).toString());
    }
}
