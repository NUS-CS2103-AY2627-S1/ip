package caitlyn;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence round trips and validation of the task file format. */
class TaskStorageTest {
    @TempDir
    private Path temporaryDirectory;

    private Path taskFile;
    private TaskStorage storage;

    @BeforeEach
    void setUpStorage() {
        taskFile = temporaryDirectory.resolve("data").resolve("duke.txt");
        storage = new TaskStorage(taskFile);
    }

    @Test
    void saveAndLoad_roundTripsTaskTypesStatusesAndEscapedFields() throws IOException {
        Todo todo = new Todo("review | notes\\backup");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", "2025-03-14 0926");
        Event event = new Event("project meeting", "2025-03-15", "2025-03-15 1600");
        List<Task> tasks = List.of(todo, deadline, event);

        storage.save(tasks);
        List<String> savedLines = Files.readAllLines(taskFile, StandardCharsets.UTF_8);
        List<Task> loadedTasks = storage.load();

        assertEquals(List.of(
                "T | 1 | review \\| notes\\\\backup",
                "D | 0 | return book | 2025-03-14T09:26",
                "E | 0 | project meeting | 2025-03-15 | 2025-03-15T16:00"), savedLines);
        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("review | notes\\backup", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(0).isDone());
        assertEquals(LocalDateTime.of(2025, 3, 14, 9, 26), ((Deadline) loadedTasks.get(1)).getBy());
        assertEquals(LocalDateTime.of(2025, 3, 15, 0, 0), ((Event) loadedTasks.get(2)).getFrom());
        assertEquals(LocalDateTime.of(2025, 3, 15, 16, 0), ((Event) loadedTasks.get(2)).getTo());
    }

    @Test
    void load_returnsEmptyListWhenTaskFileDoesNotExist() throws IOException {
        Files.deleteIfExists(taskFile);

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void save_rejectsNullListsAndNullTasks() {
        assertThrows(IllegalArgumentException.class, () -> storage.save(null));
        List<Task> tasksWithNull = new ArrayList<>();
        tasksWithNull.add(new Todo("valid"));
        tasksWithNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> storage.save(tasksWithNull));
    }

    @Test
    void load_rejectsMalformedDataAndReportsItsLineNumber() throws IOException {
        writeTaskFile(List.of(
                "T | 0 | valid task",
                "T | 2 | invalid status"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, storage::load);

        assertTrue(exception.getMessage().contains("line 2"));
        assertTrue(exception.getMessage().contains("status must be 0 or 1"));
    }

    @Test
    void load_rejectsUnknownTypesWrongFieldCountsAndIncompleteEscapes() throws IOException {
        assertLoadFailsWith("X | 0 | unknown", "Unknown saved task type");
        assertLoadFailsWith("T | 0", "type, status, and description");
        assertLoadFailsWith("T | 0 | too many | fields", "wrong number of fields");
        assertLoadFailsWith("T | 0 | incomplete\\", "incomplete escape sequence");
    }

    @Test
    void saveAndLoad_controlCharacters_preservesDescription() throws IOException {
        String description = "first\nsecond\rthird | path\\backup";

        storage.save(List.of(new Todo(description)));

        assertEquals(description, storage.load().getFirst().getDescription());
    }

    @Test
    void load_blankLinesAndUnknownEscapes_preservesTaskData() throws IOException {
        writeTaskFile(List.of("", "   ", "T | 0 | path\\unknown"));

        List<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals("path\\unknown", tasks.getFirst().getDescription());
    }

    @Test
    void save_invalidTaskList_preservesExistingFile() throws IOException {
        storage.save(List.of(new Todo("keep this")));
        List<Task> invalidTasks = new ArrayList<>();
        invalidTasks.add(new Todo("replacement"));
        invalidTasks.add(null);

        assertThrows(IllegalArgumentException.class, () -> storage.save(invalidTasks));

        assertEquals("keep this", storage.load().getFirst().getDescription());
    }

    @Test
    void save_replacementFails_removesTemporaryFile() throws IOException {
        Files.createDirectories(taskFile);
        Path existingFile = taskFile.resolve("keep.txt");
        Files.writeString(existingFile, "keep this");

        assertThrows(IOException.class, () -> storage.save(List.of(new Todo("replacement"))));

        assertEquals("keep this", Files.readString(existingFile));
        try (var savedPaths = Files.list(taskFile.getParent())) {
            assertEquals(List.of(taskFile), savedPaths.toList());
        }
    }

    @Test
    void save_separateStorageInstances_keepsFilesIndependent() throws IOException {
        TaskStorage otherStorage = new TaskStorage(temporaryDirectory.resolve("other.txt"));

        storage.save(List.of(new Todo("first file")));
        otherStorage.save(List.of(new Todo("second file")));

        assertEquals("first file", storage.load().getFirst().getDescription());
        assertEquals("second file", otherStorage.load().getFirst().getDescription());
    }

    @Test
    void saveAndLoad_withinTasks_preservesPrecisionStatusesAndEscaping() throws IOException {
        List<Task> tasks = new ArrayList<>();
        for (String from : List.of("2027-01-15", "2027-01-15T09:00")) {
            for (String to : List.of("2027-01-25", "2027-01-25T17:00")) {
                WithinTask task = new WithinTask("collect | \\backup /from notes", from, to);
                tasks.add(task);
                WithinTask doneTask = new WithinTask("collect | \\backup /from notes", from, to);
                doneTask.markAsDone();
                tasks.add(doneTask);
            }
        }
        storage.save(tasks);
        List<Task> loadedTasks = storage.load();
        assertEquals(tasks.stream().map(Task::toStorageString).toList(),
                loadedTasks.stream().map(Task::toStorageString).toList());
        assertEquals(tasks.stream().map(Task::toString).toList(), loadedTasks.stream().map(Task::toString).toList());
        for (Task task : loadedTasks) {
            assertInstanceOf(WithinTask.class, task);
        }
    }

    @Test
    void load_invalidWithinRecords_rejectsEachInvalidField() throws IOException {
        assertLoadFailsWith("W | 0 | task | 2027-01-15", "wrong number of fields");
        assertLoadFailsWith("W | 0 | task | 2027-01-15 | 2027-01-25 | extra", "wrong number of fields");
        assertLoadFailsWith("W | 2 | task | 2027-01-15 | 2027-01-25", "status must be 0 or 1");
        assertLoadFailsWith("W | 0 |  | 2027-01-15 | 2027-01-25", "description cannot be empty");
        assertLoadFailsWith("W | 0 | task | 2027-02-30 | 2027-03-01", "valid start date");
        assertLoadFailsWith("W | 0 | task | 2027-01-15 | tomorrow", "valid end date");
        assertLoadFailsWith("W | 0 | task | 2027-01-15T09:00:01 | 2027-01-25", "start time must use minute precision");
        assertLoadFailsWith("W | 0 | task | 2027-01-15 | 2027-01-25T09:00:00.001",
                "end time must use minute precision");
        assertLoadFailsWith("W | 0 | task | 2027-01-26 | 2027-01-25", "must not be after its end");
    }

    @Test
    void load_mixedLegacyAndWithinRecords_retainsLegacyValidation() throws IOException {
        writeTaskFile(List.of("T | 1 | keep", "D | 0 | precise | 2027-01-15T09:00:01",
                "E | 0 | precise | 2027-01-15T09:00:30 | 2027-01-15T09:00:45.123456789",
                "E | 1 | equal | 2027-01-15T09:00 | 2027-01-15T09:00",
                "W | 0 | new task | 15/1/2027 0900 | 25/1/2027"));
        List<Task> tasks = storage.load();
        assertEquals(5, tasks.size());
        storage.save(tasks);
        assertEquals(List.of("T | 1 | keep", "D | 0 | precise | 2027-01-15T09:00:01",
                "E | 0 | precise | 2027-01-15T09:00:30 | 2027-01-15T09:00:45.123456789",
                "E | 1 | equal | 2027-01-15T09:00 | 2027-01-15T09:00",
                "W | 0 | new task | 2027-01-15T09:00 | 2027-01-25"), Files.readAllLines(taskFile));
        assertEquals("[D][ ] precise (by: Jan 15 2027 9:00:01 AM)", tasks.get(1).toString());
        assertEquals("[E][ ] precise (from: Jan 15 2027 9:00:30 AM to: Jan 15 2027 9:00:45.123456789 AM)",
                tasks.get(2).toString());
    }

    @Test
    void load_invalidLegacyDatesAndFieldCounts_reportsCauseAndPhysicalLine() throws IOException {
        for (String line : List.of("D | 0 | task", "D | 0 | task | 2027-01-15 | extra",
                "E | 0 | task | 2027-01-15", "E | 0 | task | 2027-01-15 | 2027-01-25 | extra",
                "D | 0 | task | tomorrow", "E | 0 | task | tomorrow | 2027-01-25",
                "E | 0 | task | 2027-01-15 | tomorrow", "D | 0 | task |")) {
            writeTaskFile(List.of("", "T | 1 | keep", "  ", line));
            byte[] original = Files.readAllBytes(taskFile);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, storage::load, line);
            assertTrue(exception.getMessage().startsWith("Invalid saved task on line 4:"), line);
            assertInstanceOf(IllegalArgumentException.class, exception.getCause(), line);
            assertArrayEquals(original, Files.readAllBytes(taskFile), line);
        }
    }

    @Test
    void save_emptyList_replacesExistingDataAndLeavesNoTemporaryFiles() throws IOException {
        storage.save(List.of(new Todo("remove this")));
        storage.save(List.of());
        assertEquals("", Files.readString(taskFile));
        assertTrue(storage.load().isEmpty());
        try (var files = Files.list(taskFile.getParent())) {
            assertEquals(List.of(taskFile), files.toList());
        }
    }

    @Test
    void saveAndLoad_unicodeAndEscapeCombinations_preservesEveryTaskType() throws IOException {
        String description = "阅读 📚 café | \\n literal \\r \\| \\\\ end";
        List<Task> tasks = List.of(new Todo(description), new Deadline(description, "2027-01-15"),
                new Event(description, "2027-01-15", "2027-01-25"),
                new WithinTask(description, "2027-01-15", "2027-01-25"));
        tasks.forEach(Task::markAsDone);
        storage.save(tasks);
        assertEquals(tasks.stream().map(Task::toString).toList(),
                storage.load().stream().map(Task::toString).toList());
        assertEquals(tasks.stream().map(Task::toStorageString).toList(),
                Files.readAllLines(taskFile, StandardCharsets.UTF_8));
    }

    @Test
    void load_windowsAndUnixLineEndings_readsSameTasks() throws IOException {
        for (String newline : List.of("\n", "\r\n", "\r")) {
            Files.createDirectories(taskFile.getParent());
            Files.writeString(taskFile, "T | 1 | 阅读" + newline + "T | 0 | café" + newline);
            assertEquals(List.of("[T][X] 阅读", "[T][ ] café"),
                    storage.load().stream().map(Task::toString).toList());
        }
    }

    /** Writes a temporary task file and verifies that loading reports the expected problem. */
    private void assertLoadFailsWith(String line, String expectedMessage) throws IOException {
        writeTaskFile(List.of(line));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, storage::load);

        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    /** Writes task-file lines using the same UTF-8 encoding as the application. */
    private void writeTaskFile(List<String> lines) throws IOException {
        Files.createDirectories(taskFile.getParent());
        Files.write(taskFile, lines, StandardCharsets.UTF_8);
    }
}
