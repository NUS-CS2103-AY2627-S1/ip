package chatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests saving and loading task data with {@link Storage}.
 */
class StorageTest {

    @TempDir
    Path temporaryDirectory;

    /**
     * Loading a path that does not exist should return an empty task list.
     */
    @Test
    void load_missingFile_returnsEmptyTaskList() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());
        TaskList loadedTasks = storage.load();
        assertEquals(0, loadedTasks.size());
    }

    /**
     * Saving an empty task list should create an empty file.
     */
    @Test
    void save_emptyTaskList_createsEmptyFile() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.save(new TaskList());
        assertTrue(Files.exists(file));
        assertEquals("", Files.readString(file));
    }

    /**
     * Saving a todo should write its type, status, and description.
     */
    @Test
    void save_todo_writesTodoData() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        storage.save(tasks);
        assertEquals("T|[ ]|read book" + System.lineSeparator(), Files.readString(file));
    }

    /**
     * Saving a deadline should write its type, status, description, and date.
     */
    @Test
    void save_deadline_writesDeadlineData() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", "2026-09-01 1800"));
        storage.save(tasks);
        assertEquals("D|[ ]|submit report|2026-09-01 1800" + System.lineSeparator(),
            Files.readString(file));
    }

    /**
     * Saving an event should write its type, status, description, and dates.
     */
    @Test
    void save_event_writesEventData() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Event("team meeting", "2026-09-02 1000", "2026-09-02 1100"));
        storage.save(tasks);
        assertEquals("E|[ ]|team meeting|2026-09-02 1000|2026-09-02 1100" + System.lineSeparator(),
            Files.readString(file));
    }

    /**
     * Saving a completed task should persist its completed status.
     */
    @Test
    void save_completedTask_writesCompletedStatus() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markComplete();
        tasks.add(todo);
        storage.save(tasks);
        assertEquals("T|[X]|read book" + System.lineSeparator(), Files.readString(file));
    }

    /**
     * Loading saved data should recreate each supported task type and its details.
     */
    @Test
    void load_multipleTasks_recreatesTasksInOrder() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file,
            "T|[ ]|read book" + System.lineSeparator()
                + "D|[ ]|submit report|2026-09-01 1800" + System.lineSeparator()
                + "E|[ ]|team meeting|2026-09-02 1000|2026-09-02 1100" + System.lineSeparator());
        Storage storage = new Storage(file.toString());

        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        Todo todo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        Deadline deadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        Event event = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("read book", todo.getDescription());
        assertEquals("submit report", deadline.getDescription());
        assertEquals("2026-09-01T18:00", deadline.getDeadline().toString());
        assertEquals("team meeting", event.getDescription());
        assertEquals("2026-09-02T10:00", event.getStart().toString());
        assertEquals("2026-09-02T11:00", event.getEnd().toString());
    }

    /**
     * Loading completed data should restore the completed status.
     */
    @Test
    void load_completedTask_restoresCompletedStatus() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T|[X]|read book" + System.lineSeparator());
        Storage storage = new Storage(file.toString());
        TaskList loadedTasks = storage.load();
        assertTrue(loadedTasks.get(0).getCompletionStatus().equals("[X]"));
    }

    /**
     * Saving then loading should preserve task data and completion status.
     */
    @Test
    void saveThenLoad_tasksPreservesData() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        TaskList originalTasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markComplete();
        originalTasks.add(todo);
        originalTasks.add(new Deadline("submit report", "2026-09-01 1800"));
        storage.save(originalTasks);
        TaskList loadedTasks = storage.load();
        assertEquals(2, loadedTasks.size());
        assertEquals(originalTasks.get(0).toString(), loadedTasks.get(0).toString());
        assertEquals(originalTasks.get(1).toString(), loadedTasks.get(1).toString());
        assertFalse(loadedTasks.get(1).getCompletionStatus().equals("[X]"));
    }

    /**
     * Blank deadline and event date-time values should be rejected.
     */
    @Test
    void taskDateTime_nullOrBlankValue_throwsInvalidInputException() {
        assertThrows(InvalidInputException.class, () -> new Deadline("task", null));
        assertThrows(InvalidInputException.class, () -> new Deadline("task", "   "));
        assertThrows(InvalidInputException.class, () -> new Event("task", null, "2026-09-01 1800"));
        assertThrows(InvalidInputException.class, () -> new Event("task", "2026-09-01 1700", "   "));
        assertThrows(InvalidInputException.class, () -> new Event("task", "2026-09-02 1100", "2026-09-02 1000"));
    }

    /**
     * Malformed saved records should fail loudly instead of silently creating corrupted tasks.
     */
    @Test
    void load_malformedRecords_throwsInvalidInputException() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());

        assertThrows(InvalidInputException.class, () ->
            storage.convertLinesToTasks(Stream.of("X|[ ]|unknown"), new TaskList()));
        assertThrows(InvalidInputException.class, () ->
            storage.convertLinesToTasks(Stream.of("T|[?]|task"), new TaskList()));
        assertThrows(InvalidInputException.class, () ->
            storage.convertLinesToTasks(Stream.of("D|[ ]|missing date"), new TaskList()));
    }
}
