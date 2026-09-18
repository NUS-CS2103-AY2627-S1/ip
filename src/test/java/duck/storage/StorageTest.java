package duck.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duck.DuckException;
import duck.task.Deadline;
import duck.task.Event;
import duck.task.Task;
import duck.task.Todo;

/**
 * Tests task persistence and malformed-file handling provided by {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyList() throws DuckException {
        Storage storage = new Storage(this.temporaryDirectory.resolve("missing.txt").toString());

        assertEquals(List.of(), storage.load());
    }

    @Test
    public void save_emptyList_createsEmptyFile() throws DuckException, IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of());

        assertTrue(Files.exists(dataFile));
        assertEquals(List.of(), Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void save_nestedPath_createsParentDirectories() throws DuckException {
        Path dataFile = this.temporaryDirectory.resolve("nested/data/duck.txt");
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of(new Todo("task")));

        assertTrue(Files.exists(dataFile));
    }

    @Test
    public void save_existingFile_replacesOldContents() throws DuckException, IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        Files.writeString(dataFile, "old data", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of(new Todo("new task")));

        assertEquals(List.of("T | 0 | new task"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void saveAndLoad_allTaskTypesAndSpecialCharacters_roundTripsWithoutDataLoss()
            throws DuckException {
        Storage storage = new Storage(this.temporaryDirectory.resolve("duck.txt").toString());
        Task todo = new Todo("read A | B \\ C");
        todo.markAsDone();
        Task deadline = new Deadline("return book", LocalDate.of(2026, 8, 30));
        Task event = new Event("project | meeting", "Room C:\\1", "Room C:\\2");
        List<Task> originalTasks = List.of(todo, deadline, event);

        storage.save(originalTasks);
        List<Task> loadedTasks = storage.load();

        assertEquals(formatTasksForStorage(originalTasks), formatTasksForStorage(loadedTasks));
        assertTrue(loadedTasks.get(0).isDone());
        assertFalse(loadedTasks.get(1).isDone());
        assertFalse(loadedTasks.get(2).isDone());
    }

    @Test
    public void load_blankLines_ignoresBlankRecords() throws IOException, DuckException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        Files.writeString(dataFile, "\nT | 0 | first\n   \nT | 1 | second\n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile.toString());

        List<Task> tasks = storage.load();

        assertEquals(List.of("T | 0 | first", "T | 1 | second"), formatTasksForStorage(tasks));
    }

    @Test
    public void load_invalidSecondRecord_reportsLineNumber() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        Files.writeString(dataFile,
                "T | 0 | valid\nD | 2 | invalid status | 2026-08-30\n",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile.toString());

        DuckException exception = assertThrows(DuckException.class, storage::load);

        assertEquals("Unable to load tasks from line 2: the status must be 0 or 1.",
                exception.getMessage());
    }

    @Test
    public void save_afterLoadFailure_preservesExistingFile() throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("duck.txt");
        String originalContents = "T | 0 | valid task\ninvalid record\n";
        Files.writeString(dataFile, originalContents, StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile.toString());
        assertThrows(DuckException.class, storage::load);

        DuckException exception = assertThrows(DuckException.class, () ->
                storage.save(List.of(new Todo("replacement task"))));

        assertEquals("Tasks cannot be changed because Duck could not load "
                + "the saved task file. Repair or move " + dataFile
                + ", then restart Duck.", exception.getMessage());
        assertEquals(originalContents, Files.readString(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    public void load_malformedRecords_throwsRelevantError() throws IOException {
        assertLoadError("T | 0", "the record has too few fields.");
        assertLoadError("X | 0 | unknown", "the task type is not recognized.");
        assertLoadError("T | 0 | description | extra",
                "the task type has the wrong number of fields.");
        assertLoadError("T | 0 |   ", "the todo description cannot be empty.");
        assertLoadError("D | 0 | deadline | 2026-02-30",
                "the deadline date must be a valid yyyy-MM-dd date.");
        assertLoadError("E | 0 | event | start |   ",
                "the event end time cannot be empty.");
    }

    @Test
    public void save_parentPathIsFile_throwsSaveError() throws IOException {
        Path parentFile = this.temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "blocking file", StandardCharsets.UTF_8);
        Path dataFile = parentFile.resolve("duck.txt");
        Storage storage = new Storage(dataFile.toString());

        DuckException exception = assertThrows(DuckException.class, () ->
                storage.save(List.of(new Todo("task"))));

        assertEquals("Unable to save tasks to " + dataFile + ".", exception.getMessage());
    }

    /** Formats tasks as their stable persisted representations for comparison. */
    private List<String> formatTasksForStorage(List<Task> tasks) {
        return tasks.stream()
                .map(Task::formatForStorage)
                .toList();
    }

    /** Writes one malformed record and verifies the line-specific loading error. */
    private void assertLoadError(String record, String expectedDetail) throws IOException {
        Path dataFile = this.temporaryDirectory.resolve("malformed.txt");
        Files.writeString(dataFile, record + "\n", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile.toString());

        DuckException exception = assertThrows(DuckException.class, storage::load);
        assertEquals("Unable to load tasks from line 1: " + expectedDetail,
                exception.getMessage());
    }
}
