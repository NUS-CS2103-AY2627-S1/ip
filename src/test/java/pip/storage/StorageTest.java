package pip.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pip.task.Deadline;
import pip.task.Event;
import pip.task.Task;
import pip.task.TaskList;
import pip.task.Todo;

/**
 * Tests {@link Storage#load()} and {@link Storage#save(TaskList)}.
 *
 * <p>These two methods are tested together because their contract is a
 * round-trip: whatever save() writes, load() must be able to reconstruct
 * faithfully (including completion status and Base64-encoded fields).
 * Additional tests target load()'s defensive parsing of missing/corrupt
 * files, since that is the most bug-prone part of the class.
 */
class StorageTest {

    @TempDir
    Path tempDir;

    // ---------- load() on missing / empty file ----------

    @Test
    void load_fileDoesNotExist_returnsEmptyList() throws Exception {
        Storage storage = new Storage(tempDir.resolve("does_not_exist.txt").toString());
        List<Task> loaded = storage.load();
        assertTrue(loaded.isEmpty());
    }

    // ---------- save() then load(): round trip ----------

    @Test
    void saveThenLoad_mixedTaskTypes_preservesAllFieldsAndOrder() throws Exception {
        Storage storage = new Storage(tempDir.resolve("pip.txt").toString());

        TaskList original = new TaskList();
        original.add(new Todo("buy milk"));
        Deadline deadline = new Deadline("submit report", LocalDate.of(2024, 3, 15));
        deadline.markDone();
        original.add(deadline);
        original.add(new Event("team trip", LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 10)));

        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals(3, loaded.size());

        Task loadedTodo = loaded.get(0);
        assertTrue(loadedTodo instanceof Todo);
        assertEquals("buy milk", loadedTodo.getName());
        assertFalse(loadedTodo.isCompleted());

        Task loadedDeadline = loaded.get(1);
        assertTrue(loadedDeadline instanceof Deadline);
        assertEquals("submit report", loadedDeadline.getName());
        assertTrue(loadedDeadline.isCompleted());
        assertEquals(LocalDate.of(2024, 3, 15), ((Deadline) loadedDeadline).getDeadline());

        Task loadedEvent = loaded.get(2);
        assertTrue(loadedEvent instanceof Event);
        assertEquals("team trip", loadedEvent.getName());
        assertEquals(LocalDate.of(2024, 5, 1), ((Event) loadedEvent).getStart());
        assertEquals(LocalDate.of(2024, 5, 10), ((Event) loadedEvent).getEnd());
    }

    @Test
    void saveThenLoad_taskNameWithDelimiterCharacters_roundTripsCorrectly() throws Exception {
        // The storage format is pipe-delimited; names containing " | " must
        // still round-trip correctly because fields are Base64-encoded.
        Storage storage = new Storage(tempDir.resolve("pip.txt").toString());
        TaskList original = new TaskList();
        original.add(new Todo("buy milk | eggs | bread"));

        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("buy milk | eggs | bread", loaded.get(0).getName());
    }

    @Test
    void save_targetDirectoryMissing_createsParentDirectories() throws Exception {
        Path nestedPath = tempDir.resolve("nested/sub/pip.txt");
        Storage storage = new Storage(nestedPath.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("test"));

        storage.save(tasks);

        assertTrue(Files.exists(nestedPath));
    }

    // ---------- load() defensive parsing of malformed lines ----------

    @Test
    void load_malformedLines_areSkippedWithoutThrowing() throws Exception {
        Path file = tempDir.resolve("corrupt.txt");
        List<String> lines = List.of(
                "T | 0 | " + encode("valid todo"),         // valid
                "X | 0 | " + encode("unknown type"),        // unknown type char
                "T | 0",                                     // too few fields
                "T | 2 | " + encode("bad status"),           // invalid status flag
                "D | 0 | " + encode("bad date") + " | notabase64date", // bad date encoding
                "E | 0 | " + encode("evt") + " | " + encode("2024-01-01"), // too few fields for event
                "T | 1 | " + encode("second valid todo")     // valid, marked done
        );
        Files.write(file, lines, StandardCharsets.UTF_8);

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();

        // Only the two well-formed lines should survive.
        assertEquals(2, loaded.size());
        assertEquals("valid todo", loaded.get(0).getName());
        assertFalse(loaded.get(0).isCompleted());
        assertEquals("second valid todo", loaded.get(1).getName());
        assertTrue(loaded.get(1).isCompleted());
    }

    @Test
    void load_deadlineWithUnparseableDate_isSkipped() throws Exception {
        Path file = tempDir.resolve("bad_date.txt");
        Files.write(file, List.of("D | 0 | " + encode("report") + " | " + encode("not-a-date")));

        Storage storage = new Storage(file.toString());
        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
    }

    private static String encode(String text) {
        return java.util.Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }
}
