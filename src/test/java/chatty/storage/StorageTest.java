package chatty.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

import chatty.exception.ChattyException;
import chatty.task.Deadline;
import chatty.task.Event;
import chatty.task.RecurrenceUnit;
import chatty.task.RecurringTask;
import chatty.task.Task;
import chatty.task.Todo;

/**
 * Tests saving and restoring tasks through Chatty's text-file format.
 */
public class StorageTest {
    /** Temporary directory used for isolated storage-file tests. */
    @TempDir
    private Path tempDirectory;

    /**
     * Verifies that loading a missing data file returns an empty list.
     *
     * @throws ChattyException if loading the missing file fails unexpectedly.
     */
    @Test
    public void loadTasks_missingFile_emptyListReturned() throws ChattyException {
        Storage storage = new Storage(tempDirectory.resolve("data/chatty.txt"));

        assertTrue(storage.loadTasks().isEmpty());
    }

    /**
     * Verifies that saving and loading preserves all supported task data and status.
     *
     * @throws ChattyException if task storage fails unexpectedly.
     * @throws IOException if reading the saved file fails unexpectedly.
     */
    @Test
    public void saveAndLoadTasks_multipleTaskTypes_allDataAndStatusPreserved()
            throws ChattyException, IOException {
        Path filePath = tempDirectory.resolve("data/chatty.txt");
        Storage storage = new Storage(filePath);

        storage.saveTasks(createTasksForRoundTrip());

        assertSavedTaskRecords(filePath);
        assertLoadedTaskDataAndStatus(storage.loadTasks());
    }

    /**
     * Creates all supported task types with both completed and incomplete statuses.
     *
     * @return the tasks used to verify the storage round trip.
     */
    private List<Task> createTasksForRoundTrip() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 10, 15));
        Event event = new Event("project meeting", "2pm", "4pm");
        event.markAsDone();
        RecurringTask recurringTask = new RecurringTask("team meeting",
                LocalDate.of(2026, 9, 14), 1, RecurrenceUnit.WEEK);

        return List.of(todo, deadline, event, recurringTask);
    }

    /**
     * Verifies the exact saved records, including task order, fields, and statuses.
     *
     * @param filePath the saved task file to inspect.
     * @throws IOException if reading the saved file fails unexpectedly.
     */
    private void assertSavedTaskRecords(Path filePath) throws IOException {
        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | submit report | 2026-10-15",
                "E | 1 | project meeting | 2pm | 4pm",
                "R | 0 | team meeting | 2026-09-14 | 1 | WEEK"),
                Files.readAllLines(filePath, StandardCharsets.UTF_8));
    }

    /**
     * Verifies every loaded task's type, data, and completion status.
     *
     * @param loadedTasks the tasks restored from the saved file.
     */
    private void assertLoadedTaskDataAndStatus(List<Task> loadedTasks) {
        Todo loadedTodo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        Deadline loadedDeadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        Event loadedEvent = assertInstanceOf(Event.class, loadedTasks.get(2));
        RecurringTask loadedRecurringTask =
                assertInstanceOf(RecurringTask.class, loadedTasks.get(3));

        assertEquals(4, loadedTasks.size());
        assertEquals("read book", loadedTodo.getDescription());
        assertTrue(loadedTodo.isDone());
        assertEquals("submit report", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2026, 10, 15), loadedDeadline.getDueDate());
        assertFalse(loadedDeadline.isDone());
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals("2pm", loadedEvent.getFrom());
        assertEquals("4pm", loadedEvent.getTo());
        assertTrue(loadedEvent.isDone());
        assertEquals("team meeting", loadedRecurringTask.getDescription());
        assertEquals(LocalDate.of(2026, 9, 14),
                loadedRecurringTask.getNextOccurrenceDate());
        assertEquals(1, loadedRecurringTask.getInterval());
        assertEquals(RecurrenceUnit.WEEK, loadedRecurringTask.getRecurrenceUnit());
        assertFalse(loadedRecurringTask.isDone());
    }

    /**
     * Verifies that saving replaces the complete file without leaving temporary files.
     *
     * @throws ChattyException if saving valid tasks fails unexpectedly.
     * @throws IOException if inspecting the storage directory fails unexpectedly.
     */
    @Test
    public void saveTasks_existingData_dataReplacedAndTemporaryFileRemoved()
            throws ChattyException, IOException {
        Path filePath = tempDirectory.resolve("data/chatty.txt");
        Storage storage = new Storage(filePath);
        storage.saveTasks(List.of(new Todo("old task")));

        storage.saveTasks(List.of(new Todo("new task")));

        assertEquals(List.of("T | 0 | new task"),
                Files.readAllLines(filePath, StandardCharsets.UTF_8));
        try (var files = Files.list(filePath.getParent())) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().endsWith(".tmp")));
        }
    }

    /**
     * Verifies that blank lines in a data file are ignored.
     *
     * @throws ChattyException if loading the valid data fails unexpectedly.
     * @throws IOException if creating the test data file fails unexpectedly.
     */
    @Test
    public void loadTasks_blankLines_blankLinesIgnored() throws ChattyException, IOException {
        Path filePath = tempDirectory.resolve("chatty.txt");
        Files.write(filePath, List.of("", "   ", "T | 0 | read book"), StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(1, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
    }

    /**
     * Verifies that a completed recurring-task record is rejected.
     *
     * @throws IOException if creating the malformed test data file fails unexpectedly.
     */
    @Test
    public void loadTasks_completedRecurringTask_exceptionThrown() throws IOException {
        Path filePath = tempDirectory.resolve("chatty.txt");
        Files.writeString(filePath,
                "R | 1 | team meeting | 2026-09-14 | 1 | WEEK",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        assertThrows(ChattyException.class, storage::loadTasks);
    }

    /**
     * Verifies malformed type, status, field count, and recurrence fields are rejected.
     *
     * @throws IOException if creating malformed test data fails unexpectedly.
     */
    @Test
    public void loadTasks_variousMalformedRecords_exceptionThrown() throws IOException {
        List<String> malformedRecords = List.of(
                "X | 0 | unknown type",
                "T | 2 | invalid status",
                "T | 0 | description | extra",
                "D | 0 | deadline | ",
                "E | 0 | event | start | ",
                "R | 0 | recurring | 2026-09-21 | zero | WEEK",
                "R | 0 | recurring | 2026-09-21 | -1 | WEEK",
                "R | 0 | recurring | 2026-09-21 | 1 | FORTNIGHT");

        for (int i = 0; i < malformedRecords.size(); i++) {
            Path filePath = tempDirectory.resolve("malformed-" + i + ".txt");
            Files.writeString(filePath, malformedRecords.get(i), StandardCharsets.UTF_8);
            Storage storage = new Storage(filePath);

            assertThrows(ChattyException.class, storage::loadTasks);
        }
    }

    /**
     * Verifies that malformed records identify their one-based line number.
     *
     * @throws IOException if creating the malformed test data file fails unexpectedly.
     */
    @Test
    public void loadTasks_malformedRecord_exceptionIdentifiesLine() throws IOException {
        Path filePath = tempDirectory.resolve("chatty.txt");
        Files.write(filePath, List.of(
                "T | 0 | read book",
                "D | 0 | submit report | 2026-02-30"), StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        ChattyException exception = assertThrows(ChattyException.class, storage::loadTasks);

        assertEquals("OOPS!!! The data file is corrupted at line 2.", exception.getMessage());
    }
}
