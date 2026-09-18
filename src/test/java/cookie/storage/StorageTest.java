package cookie.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cookie.task.DateTimeValue;
import cookie.task.Deadline;
import cookie.task.Event;
import cookie.task.TaskList;
import cookie.task.Todo;

/** Tests persistence, including round trips and malformed-record handling. */
public class StorageTest {
    private Path temporaryDirectory;

    @BeforeEach
    public void createTemporaryDirectory() throws IOException {
        temporaryDirectory = Files.createTempDirectory(Path.of("build"), "storage-test-");
    }

    @AfterEach
    public void deleteTemporaryDirectory() throws IOException {
        List<Path> paths;
        try (var stream = Files.walk(temporaryDirectory)) {
            paths = stream.sorted(Comparator.reverseOrder()).toList();
        }
        for (Path path : paths) {
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void load_missingFile_returnsEmptyTaskList() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        Storage.LoadResult result = storage.load();

        assertEquals(0, result.tasks().size());
        assertTrue(result.malformedLineNumbers().isEmpty());
    }

    @Test
    public void saveAndLoad_mixedTasks_preservesTypesDetailsAndStatuses() throws IOException {
        Path file = temporaryDirectory.resolve("nested").resolve("cookie.txt");
        Storage storage = new Storage(file.toString());
        DateTimeValue deadlineTime = new DateTimeValue(LocalDate.of(2026, 8, 27),
                LocalTime.of(17, 5));
        DateTimeValue eventStart = new DateTimeValue(null, LocalTime.of(9, 0));
        DateTimeValue eventEnd = new DateTimeValue(null, LocalTime.of(10, 30));
        TaskList original = new TaskList(
                new Todo("buy milk"),
                new Deadline("submit report", deadlineTime),
                new Event("team meeting", eventStart, eventEnd));
        original.mark(1);

        storage.save(original);

        assertTrue(Files.exists(file));
        assertEquals(List.of(
                "T | Not Done | buy milk",
                "D | Done | submit report | 2026-08-27T17:05",
                "E | Not Done | team meeting | 09:00 to 10:30"),
                Files.readAllLines(file));

        Storage.LoadResult loadResult = storage.load();
        TaskList loaded = loadResult.tasks();
        assertEquals(3, loaded.size());
        assertTrue(loadResult.malformedLineNumbers().isEmpty());
        assertTrue(loaded.get(0) instanceof Todo);
        assertTrue(loaded.get(1) instanceof Deadline);
        assertTrue(loaded.get(2) instanceof Event);
        assertFalse(loaded.get(0).isDone());
        assertTrue(loaded.get(1).isDone());
        assertEquals("submit report", loaded.get(1).getDescription());
        assertEquals(deadlineTime.toDisplayString(), ((Deadline) loaded.get(1)).getBy()
                .toDisplayString());
        assertEquals(eventStart.toDisplayString(), ((Event) loaded.get(2)).getStart()
                .toDisplayString());
        assertEquals(eventEnd.toDisplayString(), ((Event) loaded.get(2)).getEnd()
                .toDisplayString());
    }

    @Test
    public void load_malformedRecords_ignoresBadLinesAndKeepsValidRecords() throws IOException {
        Path file = temporaryDirectory.resolve("cookie.txt");
        Files.write(file, List.of(
                "",
                "T | Done | valid todo",
                "not a saved task",
                "T | Maybe | invalid status",
                "D | Not Done | missing date |",
                "D | Not Done | bad date | 2026-02-30",
                "E | Not Done | valid event | 2026-08-27T09:00 to 2026-08-27T10:00",
                "E | Not Done | missing end | 09:00 to",
                "T | Not Done | valid todo",
                "E | Not Done | backwards event | 11:00 to 10:00",
                " "));

        Storage.LoadResult loadResult = new Storage(file.toString()).load();
        TaskList loaded = loadResult.tasks();

        assertEquals(2, loaded.size());
        assertEquals(List.of(3, 4, 5, 6, 8, 9, 10), loadResult.malformedLineNumbers());
        assertEquals("valid todo", loaded.get(0).getDescription());
        assertTrue(loaded.get(0).isDone());
        assertEquals("valid event", loaded.get(1).getDescription());
    }

    @Test
    public void load_additionalMalformedRecordShapes_reportsEveryInvalidLine() throws IOException {
        Path file = temporaryDirectory.resolve("cookie.txt");
        Files.write(file, List.of(
                "X | Not Done | unknown type",
                "T | Not Done | extra field | unexpected",
                "T | Not Done | ",
                "T | Not Done | invalid\u0001description",
                "D | Not Done | | 2026-08-27",
                "D | Not Done | extra field | 2026-08-27 | unexpected",
                "E | Not Done | | 09:00 to 10:00",
                "E | Not Done | mixed values | 2026-08-27 to 10:00",
                "T | not done | valid task"));

        Storage.LoadResult result = new Storage(file.toString()).load();

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8), result.malformedLineNumbers());
        assertEquals(1, result.tasks().size());
        assertEquals("valid task", result.tasks().get(0).getDescription());
    }

    @Test
    public void save_targetIsNonEmptyDirectory_removesTemporaryFileAndPreservesDirectory()
            throws IOException {
        Path targetDirectory = temporaryDirectory.resolve("target");
        Files.createDirectories(targetDirectory);
        Path existingFile = targetDirectory.resolve("existing.txt");
        Files.writeString(existingFile, "existing data");
        Storage storage = new Storage(targetDirectory.toString());

        assertThrows(IOException.class, () -> storage.save(new TaskList(new Todo("buy milk"))));

        assertEquals("existing data", Files.readString(existingFile));
        try (var files = Files.list(temporaryDirectory)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().startsWith(".cookie-")));
        }
    }

    @Test
    public void loadResult_mutableSourceList_keepsImmutableSnapshot() {
        List<Integer> mutableLineNumbers = new ArrayList<>(List.of(2));
        Storage.LoadResult result = new Storage.LoadResult(new TaskList(), mutableLineNumbers);

        mutableLineNumbers.add(5);

        assertEquals(List.of(2), result.malformedLineNumbers());
        assertThrows(UnsupportedOperationException.class, () -> result.malformedLineNumbers().add(7));
    }

    @Test
    public void load_pathIsDirectory_throwsIoException() throws IOException {
        Storage storage = new Storage(temporaryDirectory.toString());

        try {
            storage.load();
        } catch (IOException exception) {
            assertFalse(exception.getMessage().isBlank());
            return;
        }
        throw new AssertionError("Loading a directory as a task file should fail");
    }
}
