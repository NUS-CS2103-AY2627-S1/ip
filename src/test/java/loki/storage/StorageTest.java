package loki.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import loki.exception.LokiExceptions;
import loki.model.TaskList;

/** Tests stored task parsing through the public storage API. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws LokiExceptions {
        assertTrue(new Storage(temporaryDirectory.resolve("missing.txt").toString()).load().isEmpty());
    }

    @Test
    void load_allTaskTypes_preservesStatusAndRoundTrips() throws IOException, LokiExceptions {
        Storage storage = storeRecords("T | 1 | Read book\n\nD | 0 | Return book | 2019-06-06T00:00:00\n"
                + "E | 1 | Meeting | 2019-08-06T14:00:00 -> 2019-08-06T16:00:00\n");

        TaskList tasks = storage.load();

        assertEquals(3, tasks.size());
        assertEquals("T | 1 | Read book", tasks.get(1).saveString());
        assertEquals("D | 0 | Return book | 2019-06-06T00:00:00", tasks.get(2).saveString());
        assertEquals("E | 1 | Meeting | 2019-08-06T14:00:00 -> 2019-08-06T16:00:00", tasks.get(3).saveString());
        storage.save(tasks);
        TaskList reloaded = storage.load();
        for (int index = 1; index <= tasks.size(); index++) {
            assertEquals(tasks.get(index).saveString(), reloaded.get(index).saveString());
        }
    }

    @Test
    void load_wrongFieldCounts_rejectsEveryTaskType() throws IOException {
        String[] records = {"T | 0", "T | 0 | Title | extra", "D | 0 | Title", "E | 0 | Title",
            "D | 0 | Title | 2019-06-06T00:00:00 | extra", "E | 0 | Title | schedule | extra"};
        for (String record : records) {
            assertInvalidRecord(record, "Invalid task format");
        }
    }

    @Test
    void load_invalidSharedFields_preservesErrorMessages() throws IOException {
        assertInvalidRecord("T | 2 | Title", "Invalid task status");
        assertInvalidRecord("T | 0 | ", "Task title cannot be empty");
        assertInvalidRecord("X | 0 | Title", "Unknown Task type; Is your file corrupted?");
    }

    @Test
    void load_invalidDeadline_preservesErrorMessages() throws IOException {
        assertInvalidRecord("D | 0 | Title | ", "Deadline cannot be empty");
        assertInvalidRecord("D | 0 | Title | tomorrow", "Invalid deadline date/time");
    }

    @Test
    void load_malformedEventSchedule_preservesErrorMessages() throws IOException {
        String[] schedules = {"", "2019-08-06T14:00:00", "-> 2019-08-06T16:00:00",
            "2019-08-06T14:00:00 ->"};
        for (String schedule : schedules) {
            assertInvalidRecord("E | 0 | Title | " + schedule, "Invalid event time format");
        }
    }

    @Test
    void load_invalidEventDates_preservesErrorMessages() throws IOException {
        assertInvalidRecord("E | 0 | Title | tomorrow -> later", "Invalid event date/time");
        assertInvalidRecord("E | 0 | Title | 2019-08-06T16:00:00 -> 2019-08-06T14:00:00",
                "Invalid event date/time");
    }

    /**
     * Writes records to an isolated file for a storage test.
     *
     * @param records the file contents.
     * @return storage backed by the test file.
     * @throws IOException if the test file cannot be written.
     */
    private Storage storeRecords(String records) throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, records);
        return new Storage(taskFile.toString());
    }

    /**
     * Checks that a corrupt record reports the expected storage error.
     *
     * @param record the invalid record.
     * @param expectedMessage the expected error message.
     * @throws IOException if the test file cannot be written.
     */
    private void assertInvalidRecord(String record, String expectedMessage) throws IOException {
        Storage storage = storeRecords(record);
        LokiExceptions exception = assertThrows(LokiExceptions.class, storage::load, record);
        assertTrue(exception.getMessage().endsWith("\n" + expectedMessage), exception.getMessage());
    }
}
