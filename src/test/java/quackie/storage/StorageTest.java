package quackie.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import quackie.task.Deadline;
import quackie.task.Event;
import quackie.task.TaskList;
import quackie.task.ToDo;

/** Tests persistence of task types, details, and completion status. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies that saved tasks can be loaded with their state intact. */
    @Test
    void savesAndLoadsAllTaskTypes() throws IOException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        Deadline deadline = new Deadline("submit report", "2019-10-15");
        deadline.markAsDone();
        tasks.add(deadline);
        tasks.add(new Event("meeting", "2pm", "3pm"));

        storage.save(tasks);

        TaskList loaded = new TaskList();
        storage.load(loaded);
        assertTrue(Files.exists(dataFile));
        assertEquals(3, loaded.size());
        assertEquals("[T][ ] read book", loaded.get(0).toString());
        assertEquals("[D][X] submit report (by: Oct 15 2019)", loaded.get(1).toString());
        assertEquals("[E][ ] meeting (from: 2pm to: 3pm)", loaded.get(2).toString());
    }

    /** Verifies that structured event times keep their stored format and friendly display after reloading. */
    @Test
    void roundTripsStructuredEventTimes() throws IOException {
        Path dataFile = temporaryDirectory.resolve("events.txt");
        // A record in the existing save format: type|status|Base64(description)|Base64(from)|Base64(to).
        String savedRecord = String.join("|", "E", "0", encode("project meeting"),
                encode("24/9/2026 1400"), encode("24/9/2026 1600"));
        Files.writeString(dataFile, savedRecord);
        Storage storage = new Storage(dataFile);
        TaskList loaded = new TaskList();

        storage.load(loaded);
        storage.save(loaded);
        TaskList reloaded = new TaskList();
        storage.load(reloaded);

        assertEquals(savedRecord, Files.readString(dataFile).strip());

        Event event = (Event) reloaded.get(0);
        assertEquals("24/9/2026 1400", event.getFrom());
        assertEquals("24/9/2026 1600", event.getTo());
        assertEquals("[E][ ] project meeting (from: Sep 24 2026, 2:00 PM to: Sep 24 2026, 4:00 PM)",
                event.toString());
        assertTrue(reloaded.contains(new Event("project meeting", "24/9/2026 1400", "24/9/2026 1600")));
        assertEquals(1, reloaded.find("meeting").size());
    }

    /** Verifies that loading a missing file leaves the list empty. */
    @Test
    void missingFileIsTreatedAsEmpty() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));
        TaskList tasks = new TaskList();

        storage.load(tasks);

        assertFalse(Files.exists(temporaryDirectory.resolve("missing.txt")));
        assertEquals(0, tasks.size());
    }

    /** Verifies that storage rejects a missing task list at its boundary. */
    @Test
    void rejectsNullTaskLists() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));

        assertThrows(AssertionError.class, () -> storage.save(null));
        assertThrows(AssertionError.class, () -> storage.load(null));
    }

    /** Verifies each malformed storage field is reported as corrupted data. */
    @Test
    void rejectsMalformedRecords() throws IOException {
        String[] malformedRecords = {
            "X|0|cmVhZCBib29r",
            "T|2|cmVhZCBib29r",
            "T|0",
            "T|0|not-base64!",
            "D|0|cmVwb3J0|MjAxOS0wMi0zMA=="
        };

        for (int i = 0; i < malformedRecords.length; i++) {
            Path dataFile = temporaryDirectory.resolve("malformed-" + i + ".txt");
            Files.writeString(dataFile, malformedRecords[i]);
            Storage storage = new Storage(dataFile);

            assertThrows(IOException.class, () -> storage.load(new TaskList()));
        }
    }

    /** Verifies that separators and non-ASCII text survive a storage round trip. */
    @Test
    void preservesSpecialCharacters() throws IOException {
        Path dataFile = temporaryDirectory.resolve("special.txt");
        Storage storage = new Storage(dataFile);
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read | review 🦆"));

        storage.save(tasks);
        TaskList loaded = new TaskList();
        storage.load(loaded);

        assertEquals("read | review 🦆", loaded.get(0).getDescription());
    }

    /**
     * Encodes text the same way the storage file does, so tests can write records in the saved format.
     *
     * @param value the text to encode
     * @return the Base64 representation of the text
     */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
