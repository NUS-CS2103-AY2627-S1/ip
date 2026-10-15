package jassabot.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jassabot.task.Deadline;
import jassabot.task.Event;
import jassabot.task.Task;
import jassabot.task.Todo;

/**
 * Tests durable task storage, recovery from malformed data, and write failures.
 */
public class StorageTest {
    private final Path testDirectory = Path.of(
            "build", "test-data", "storage-" + UUID.randomUUID());

    @BeforeEach
    public void setUp() throws IOException {
        Files.createDirectories(testDirectory);
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (!Files.exists(testDirectory)) {
            return;
        }
        List<Path> paths;
        try (Stream<Path> pathStream = Files.walk(testDirectory)) {
            paths = pathStream.sorted(Comparator.reverseOrder()).toList();
        }
        for (Path path : paths) {
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void constructor_absolutePath_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Storage(testDirectory.toAbsolutePath().resolve("tasks.txt")));
    }

    @Test
    public void loadTasks_missingFile_returnsEmptyResult() {
        Storage storage = new Storage(testDirectory.resolve("missing.txt"));

        Storage.LoadResult result = storage.loadTasks();

        assertAll(() ->
                assertTrue(result.getTasks().isEmpty()), () ->
                assertTrue(result.getWarnings().isEmpty())
        );
    }

    @Test
    public void saveAndLoad_allTaskTypes_roundTripsExactly()
            throws IOException, StorageException {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("compare A | B \\ C");
        todo.markAsDone();
        List<Task> originalTasks = List.of(
                todo,
                new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)),
                new Event("meeting",
                        LocalDateTime.of(2019, 12, 2, 14, 0),
                        LocalDateTime.of(2019, 12, 2, 16, 0)));

        storage.saveTasks(originalTasks);
        Storage.LoadResult result = storage.loadTasks();

        List<String> expectedLines = List.of(
                "T | 1 | compare A \\| B \\\\ C",
                "D | 0 | return book | 2019-12-02T18:00",
                "E | 0 | meeting | 2019-12-02T14:00 | 2019-12-02T16:00");
        assertAll(() ->
                assertEquals(expectedLines,
                        Files.readAllLines(dataFile, StandardCharsets.UTF_8)), () ->
                assertEquals(expectedLines,
                        result.getTasks().stream().map(Task::toDataString).toList()), () ->
                assertTrue(result.getWarnings().isEmpty())
        );
    }

    @Test
    public void saveTasks_existingFile_replacesOldContents()
            throws IOException, StorageException {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(List.of(new Todo("old task")));

        storage.saveTasks(List.of(new Todo("new task")));

        assertEquals(List.of("T | 0 | new task"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void loadTasks_malformedLines_loadsValidTasksAndReportsWarnings()
            throws IOException {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of(
                "",
                "T | 1 | valid todo",
                "X | 0 | mystery task",
                "T | 0 | too | many",
                "T | 2 | invalid status",
                "T | 0 | ",
                "D | 0 | blank due | ",
                "D | 0 | bad due | Sunday",
                "E | 0 | bad start | 2pm | 2019-12-02T16:00",
                "E | 0 | bad end | 2019-12-02T14:00 | 4pm",
                "T | 0 | legacy \\q"), StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        Storage.LoadResult result = storage.loadTasks();

        assertAll(() ->
                assertEquals(2, result.getTasks().size()), () ->
                assertEquals("T | 1 | valid todo",
                        result.getTasks().get(0).toDataString()), () ->
                assertEquals("legacy \\q",
                        result.getTasks().get(1).getDescription()), () ->
                assertEquals(List.of(
                        "Skipped data line 3: unknown task type 'X'.",
                        "Skipped data line 4: task type 'T' expects 3 fields but found 4.",
                        "Skipped data line 5: status must be 0 or 1.",
                        "Skipped data line 6: task description cannot be empty.",
                        "Skipped data line 7: deadline date and time cannot be empty.",
                        "Skipped data line 8: deadline date and time must use "
                                + "yyyy-MM-dd'T'HH:mm format.",
                        "Skipped data line 9: event start date and time must use "
                                + "yyyy-MM-dd'T'HH:mm format.",
                        "Skipped data line 10: event end date and time must use "
                                + "yyyy-MM-dd'T'HH:mm format."),
                        result.getWarnings())
        );
    }

    @Test
    public void loadTasks_storagePathIsDirectory_returnsWarning() throws IOException {
        Path dataPath = testDirectory.resolve("tasks.txt");
        Files.createDirectory(dataPath);
        Storage storage = new Storage(dataPath);

        Storage.LoadResult result = storage.loadTasks();

        assertAll(() ->
                assertTrue(result.getTasks().isEmpty()), () ->
                assertEquals(List.of(
                        "The task data path is not a regular file. "
                                + "Starting with an empty task list."),
                        result.getWarnings())
        );
    }

    @Test
    public void saveTasks_parentPathIsFile_throwsStorageException() throws IOException {
        Path parentFile = testDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "content", StandardCharsets.UTF_8);
        Storage storage = new Storage(parentFile.resolve("tasks.txt"));

        StorageException exception = assertThrows(StorageException.class, () ->
                storage.saveTasks(List.of(new Todo("read book"))));

        assertAll(() ->
                assertEquals("The task data file could not be written.",
                        exception.getMessage()), () ->
                assertNotNull(exception.getCause())
        );
    }

    @Test
    public void saveTasks_nestedMissingDirectories_createsParentsAndClearsSavedTasks()
            throws IOException, StorageException {
        Path dataFile = testDirectory.resolve("nested").resolve("directory").resolve("tasks.txt");
        Storage storage = new Storage(dataFile);
        storage.saveTasks(List.of(new Todo("saved")));
        assertEquals(List.of("T | 0 | saved"), Files.readAllLines(dataFile));
        storage.saveTasks(List.of());
        assertEquals(0, Files.size(dataFile));
        assertTrue(storage.loadTasks().getTasks().isEmpty());
        assertTrue(storage.loadTasks().getWarnings().isEmpty());
    }

    @Test
    public void saveTasks_filenameWithoutParent_roundTripsAndCleansUp() throws IOException, StorageException {
        Path dataFile = Path.of("storage-test-" + UUID.randomUUID() + ".txt");
        try {
            Storage storage = new Storage(dataFile);
            storage.saveTasks(List.of(new Todo("relative filename")));
            assertEquals(List.of("T | 0 | relative filename"), Files.readAllLines(dataFile));
            assertEquals("relative filename", storage.loadTasks().getTasks().get(0).getDescription());
            assertTrue(storage.loadTasks().getWarnings().isEmpty());
        } finally {
            Files.deleteIfExists(dataFile);
        }
    }

    @Test
    public void saveTasks_nonemptyDirectory_preservesContentsAndRemovesTemporaryFile() throws IOException {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Files.createDirectory(dataFile);
        Path blocker = dataFile.resolve("blocker");
        Files.writeString(blocker, "retained");
        Storage storage = new Storage(dataFile);
        StorageException exception = assertThrows(StorageException.class, () ->
                storage.saveTasks(List.of(new Todo("rejected"))));
        assertEquals("The task data file could not be written.", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("retained", Files.readString(blocker));
        try (Stream<Path> paths = Files.list(testDirectory)) {
            assertEquals(List.of(dataFile), paths.toList());
        }
    }

    @Test
    public void loadTasks_invalidUtf8_reportsReadFailureWithoutChangingBytes() throws IOException {
        Path dataFile = testDirectory.resolve("tasks.txt");
        byte[] invalidBytes = {(byte) 0xc3, (byte) 0x28};
        Files.write(dataFile, invalidBytes);
        Storage.LoadResult result = new Storage(dataFile).loadTasks();
        assertTrue(result.getTasks().isEmpty());
        assertEquals(List.of("The task data file could not be read. Starting with an empty task list."),
                result.getWarnings());
        assertArrayEquals(invalidBytes, Files.readAllBytes(dataFile));
    }

    @Test
    public void loadTasks_trailingBackslashAndUnknownEscapes_preservesLegacyText() throws IOException {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of("T | 0 | trailing\\", "T | 0 | legacy\\n\\t\\q"));
        Storage.LoadResult result = new Storage(dataFile).loadTasks();
        assertEquals(List.of("trailing\\", "legacy\\n\\t\\q"),
                result.getTasks().stream().map(Task::getDescription).toList());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void saveAndLoad_unicodeAndRepeatedEscapes_preservesEveryTaskTypeAndStatus()
            throws StorageException {
        String description = "\u4e2d\u6587 \ud83c\udf31 | \\| \\\\ ||";
        LocalDateTime date = LocalDateTime.of(2024, 2, 29, 0, 0);
        List<Task> tasks = List.of(new Todo(description), new Deadline(description, date),
                new Event(description, date, date.plusDays(1)));
        tasks.forEach(Task::markAsDone);
        Storage storage = new Storage(testDirectory.resolve("tasks.txt"));
        storage.saveTasks(tasks);
        Storage.LoadResult result = storage.loadTasks();
        assertEquals(tasks.stream().map(Task::toString).toList(),
                result.getTasks().stream().map(Task::toString).toList());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    public void loadTasks_missingFieldsAndBlankEventDates_reportsExactLineNumbers() throws IOException {
        Path dataFile = testDirectory.resolve("tasks.txt");
        Files.write(dataFile, List.of(" \t", "T", "D | 0 | date", "E | 0 | event | 2024-02-29T00:00",
                "E | 0 | event |  | 2024-03-01T00:00", "E | 0 | event | 2024-02-29T00:00 | ",
                "T |  | empty status", "T | 0 | recovered"));
        Storage.LoadResult result = new Storage(dataFile).loadTasks();
        assertEquals(List.of(
                "Skipped data line 2: task type 'T' expects 3 fields but found 1.",
                "Skipped data line 3: task type 'D' expects 4 fields but found 3.",
                "Skipped data line 4: task type 'E' expects 5 fields but found 4.",
                "Skipped data line 5: event start date and time cannot be empty.",
                "Skipped data line 6: event end date and time cannot be empty.",
                "Skipped data line 7: status must be 0 or 1."), result.getWarnings());
        assertEquals(1, result.getTasks().size());
        assertEquals("recovered", result.getTasks().get(0).getDescription());
        assertThrows(UnsupportedOperationException.class, () -> result.getWarnings().add("extra"));
    }
}
