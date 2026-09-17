package notmarth.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import notmarth.model.Contact;
import notmarth.model.Deadline;
import notmarth.model.Event;
import notmarth.model.ToDo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence of every supported task type and recovery from bad files. */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that saving and loading preserves task data, state, and escaped text. */
    @Test
    void saveAndLoad_validData_preservesTypesStateAndEscapedText() throws Exception {
        Path archive = temporaryDirectory.resolve("nested").resolve("battle-plan.txt");
        Storage storage = new Storage(archive.toString());
        ToDo todo = new ToDo("borrow | book\\bag\nsoon");
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        Event event = new Event("project meeting", LocalDateTime.of(2019, 10, 15, 14, 0),
                LocalDateTime.of(2019, 10, 15, 16, 0));
        Contact contact = new Contact("Mrs Tan", "81234567", "12 Engage Road | #04-05");
        deadline.markAsDone();

        storage.save(List.of(todo, deadline, event), List.of(contact));
        Storage.LoadResult result = storage.load(3);

        assertFalse(result.hasWarning());
        assertNull(result.getWarning());
        assertEquals(3, result.getTasks().size());
        assertEquals(todo.getDescription(), result.getTasks().get(0).getDescription());
        assertTrue(result.getTasks().get(1).isDone());
        assertEquals(deadline.getBy(), ((Deadline) result.getTasks().get(1)).getBy());
        assertEquals(event.getFrom(), ((Event) result.getTasks().get(2)).getFrom());
        assertEquals(event.getTo(), ((Event) result.getTasks().get(2)).getTo());
        assertEquals(contact.getName(), result.getContacts().get(0).getName());
        assertEquals(contact.getAddress(), result.getContacts().get(0).getAddress());
        assertTrue(Files.exists(archive));
    }

    /** Verifies that a missing archive starts as an empty plan without a warning. */
    @Test
    void load_missingArchive_returnsEmptyPlanWithoutWarning() {
        Storage.LoadResult result = new Storage(
                temporaryDirectory.resolve("missing.txt").toString()).load(10);

        assertTrue(result.getTasks().isEmpty());
        assertFalse(result.hasWarning());
        assertNull(result.getWarning());
    }

    /** Verifies that a corrupt archive produces a warning and no loaded tasks. */
    @Test
    void load_corruptArchive_returnsWarningAndNoTasks() throws Exception {
        Path archive = temporaryDirectory.resolve("corrupt.txt");
        Files.writeString(archive, "not a NotMarth archive\n");

        Storage.LoadResult result = new Storage(archive.toString()).load(10);

        assertTrue(result.getTasks().isEmpty());
        assertTrue(result.hasWarning());
        assertEquals(
                "The saved battle plan is corrupted. Repair or remove the file before starting "
                        + "NotMarth again.",
                result.getWarning());
    }

    /** Verifies that archives exceeding the configured capacity are rejected. */
    @Test
    void load_archiveBeyondTaskCapacity_returnsWarningAndNoTasks() throws Exception {
        Path archive = temporaryDirectory.resolve("too-many.txt");
        Files.writeString(archive, "# NotMarth battle plan v1\n"
                + "todo|open|one\n"
                + "todo|open|two\n");

        Storage.LoadResult result = new Storage(archive.toString()).load(1);

        assertTrue(result.hasWarning());
        assertTrue(result.getTasks().isEmpty());
    }

    /** Verifies that duplicate saved tasks are treated as corrupt archive data. */
    @Test
    void load_duplicateTaskRecords_returnsWarningAndNoTasks() throws Exception {
        Path archive = temporaryDirectory.resolve("duplicates.txt");
        Files.writeString(archive, "# NotMarth battle plan v1\n"
                + "todo|open|same\n"
                + "todo|done|same\n");

        Storage.LoadResult result = new Storage(archive.toString()).load(10);

        assertTrue(result.hasWarning());
        assertTrue(result.getTasks().isEmpty());
    }

    /** Verifies that malformed statuses, escapes, and field counts are rejected together. */
    @Test
    void load_malformedRecords_returnsWarningAndNoTasks() throws Exception {
        Path archive = temporaryDirectory.resolve("malformed.txt");
        Files.writeString(archive, "# NotMarth battle plan v1\n"
                + "todo|maybe|task\n");

        Storage.LoadResult result = new Storage(archive.toString()).load(10);

        assertTrue(result.hasWarning());
        assertTrue(result.getTasks().isEmpty());

        Files.writeString(archive, "# NotMarth battle plan v1\n"
                + "todo|open|bad\\qescape\n");
        assertTrue(new Storage(archive.toString()).load(10).hasWarning());
    }

    /** Verifies that invalid archive limits and non-file paths do not crash startup. */
    @Test
    void load_invalidLimitOrDirectory_returnsWarning() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());
        assertTrue(storage.load(0).hasWarning());

        Path directory = temporaryDirectory.resolve("archive-directory");
        Files.createDirectory(directory);
        assertTrue(new Storage(directory.toString()).load(10).hasWarning());
    }

    /** Verifies that an empty or null archive path is rejected at construction. */
    @Test
    void storageCreation_missingPath_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Storage(null));
        assertThrows(IllegalArgumentException.class, () -> new Storage(" "));
    }
}
