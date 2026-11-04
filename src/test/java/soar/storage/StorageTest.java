package soar.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import soar.exception.StorageException;
import soar.task.Deadline;
import soar.task.Event;
import soar.task.Task;
import soar.task.ToDo;

/**
 * Tests durable task round-trips and rejection of malformed storage records.
 */
public class StorageTest {
    private Path testDirectory;

    /** Creates an isolated relative directory because application storage forbids absolute paths. */
    @BeforeEach
    public void createTestDirectory() throws IOException {
        Path testRoot = Path.of("build", "junit-storage");
        Files.createDirectories(testRoot);
        testDirectory = Files.createTempDirectory(testRoot, "case-");
    }

    /** Removes the generated test data after each test. */
    @AfterEach
    public void deleteTestDirectory() throws IOException {
        if (testDirectory == null || !Files.exists(testDirectory)) {
            return;
        }
        try (var paths = Files.walk(testDirectory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    /** Verifies constructor safeguards around the configured data-file path. */
    @Test
    public void constructor_invalidPath_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Storage(null));
        assertThrows(IllegalArgumentException.class, () ->
                new Storage(testDirectory.resolve("tasks.txt").toAbsolutePath()));
    }

    /** Verifies a first run without a data file starts with an empty task list. */
    @Test
    public void load_missingFile_returnsEmptyList() throws Exception {
        assertTrue(new Storage(testDirectory.resolve("missing.txt")).load().isEmpty());
    }

    /** Verifies every task type, completion state, and escaped field survives a round-trip. */
    @Test
    public void saveAndLoad_representativeTasks_preservesTaskData() throws Exception {
        ToDo todo = new ToDo("pipe | slash \\ newline\ncarriage\rreturn");
        Deadline datedDeadline = new Deadline("date only", LocalDate.of(2019, 10, 15));
        Deadline timedDeadline = new Deadline(
                "date and time", LocalDateTime.of(2019, 10, 15, 18, 0));
        Event event = new Event("meeting", "room | one", "room \\ two");
        timedDeadline.markAsDone();
        List<Task> original = List.of(todo, datedDeadline, timedDeadline, event);
        Storage storage = new Storage(testDirectory.resolve("nested").resolve("tasks.txt"));

        storage.save(original);
        List<Task> loaded = storage.load();

        assertEquals(original.stream().map(Task::toString).toList(),
                loaded.stream().map(Task::toString).toList());
        assertInstanceOf(ToDo.class, loaded.get(0));
        assertFalse(((Deadline) loaded.get(1)).hasTime());
        assertTrue(((Deadline) loaded.get(2)).hasTime());
        assertTrue(loaded.get(2).isDone());
        assertInstanceOf(Event.class, loaded.get(3));
    }

    /** Verifies every structural character is escaped in the documented order. */
    @Test
    public void escapeField_structuralCharacters_returnsSafeField() {
        assertEquals("pipe \\| slash \\\\ newline\\ncarriage\\rreturn",
                Storage.escapeField("pipe | slash \\ newline\ncarriage\rreturn"));
    }

    /** Verifies malformed records report both their line and the validation problem. */
    @Test
    public void load_malformedRecords_throwsStorageExceptionWithContext() throws Exception {
        Path dataFile = testDirectory.resolve("malformed.txt");

        assertInvalid(dataFile, List.of(""), "line 1", "blank records");
        assertInvalid(dataFile, List.of("X | 0 | task"), "unknown task type");
        assertInvalid(dataFile, List.of("T | 2 | task"), "completion status");
        assertInvalid(dataFile, List.of("D | 0 | task"), "requires 4 fields");
        assertInvalid(dataFile, List.of("D | 0 | task | 2019-02-29"), "deadline date");
        assertInvalid(dataFile, List.of("T | 0 |  "), "must not be empty");
        assertInvalid(dataFile, List.of("T | 0 | bad\\qescape"), "unsupported escape");
        assertInvalid(dataFile, List.of("T | 0 | valid", "E | 0 | incomplete"), "line 2");
    }

    /** Verifies failed replacement does not leave a temporary save file behind. */
    @Test
    public void save_targetIsDirectory_throwsIoExceptionAndCleansTemporaryFile() throws Exception {
        Path dataFile = testDirectory.resolve("target-directory");
        Files.createDirectories(dataFile);

        assertThrows(IOException.class, () ->
                new Storage(dataFile).save(List.of(new ToDo("cannot save"))));

        try (var paths = Files.list(testDirectory)) {
            assertFalse(paths.anyMatch(path -> path.getFileName().toString()
                    .startsWith("target-directory")
                    && path.getFileName().toString().endsWith(".tmp")));
        }
    }

    /** Writes one malformed fixture and verifies all expected message fragments. */
    private void assertInvalid(Path dataFile, List<String> lines, String... expectedMessages)
            throws Exception {
        Files.write(dataFile, lines);
        StorageException exception = assertThrows(StorageException.class, () ->
                new Storage(dataFile).load());
        for (String expectedMessage : expectedMessages) {
            assertTrue(exception.getMessage().contains(expectedMessage), exception.getMessage());
        }
    }
}
