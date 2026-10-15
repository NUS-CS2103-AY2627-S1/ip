package min.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import min.task.Deadline;
import min.task.Event;
import min.task.Task;
import min.task.Todo;

class TaskStorageTest {
    private Path dataFile;
    private TaskStorage storage;

    @BeforeEach
    void setUp(@TempDir Path tempDirectory) {
        dataFile = tempDirectory.resolve("data/min.txt");
        storage = new TaskStorage(dataFile);
    }

    @Test
    void load_missingDataFile_returnsEmptyList() throws IOException {
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_mixedTasks_preservesTaskDataAndCompletionStatus() throws IOException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 28));
        Event event = new Event("meeting", "2pm", "4pm");
        todo.markAsDone();
        event.markAsDone();

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | submit report | 2026-08-28",
                "E | 1 | meeting | 2pm | 4pm"),
                loadedTasks.stream().map(Task::toFileString).toList());
    }

    @Test
    void save_existingData_replacesPreviousContents() throws IOException {
        storage.save(List.of(new Todo("old task"), new Todo("another old task")));

        storage.save(List.of(new Todo("replacement task")));

        List<Task> loadedTasks = storage.load();
        assertEquals(1, loadedTasks.size());
        assertEquals("T | 0 | replacement task", loadedTasks.get(0).toFileString());
    }

    @Test
    void load_unknownTaskType_throwsIllegalArgumentException() throws IOException {
        writeDataFile("X | 0 | unknown task");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Unknown task type.", exception.getMessage());
    }

    @Test
    void load_truncatedTodo_throwsIllegalArgumentException() throws IOException {
        assertDamagedRecord("T | 0");
    }

    @Test
    void load_truncatedDeadline_throwsIllegalArgumentException() throws IOException {
        assertDamagedRecord("D | 0 | submit report");
    }

    @Test
    void load_truncatedEvent_throwsIllegalArgumentException() throws IOException {
        assertDamagedRecord("E | 0 | meeting | 2pm");
    }

    @Test
    void load_recordWithExtraField_throwsIllegalArgumentException() throws IOException {
        assertDamagedRecord("T | 0 | read book | unexpected");
    }

    @Test
    void load_invalidStatus_throwsIllegalArgumentException() throws IOException {
        writeDataFile("T | 2 | read book");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Invalid task status.", exception.getMessage());
    }

    @Test
    void load_blankDescription_throwsIllegalArgumentException() throws IOException {
        writeDataFile("T | 0 | ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Task description cannot be blank.", exception.getMessage());
    }

    @Test
    void load_blankEventStartTime_throwsIllegalArgumentException() throws IOException {
        writeDataFile("E | 0 | meeting |  | 4pm");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Event times cannot be blank.", exception.getMessage());
    }

    @Test
    void load_blankEventEndTime_throwsIllegalArgumentException() throws IOException {
        writeDataFile("E | 0 | meeting | 2pm | ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Event times cannot be blank.", exception.getMessage());
    }

    @Test
    void load_invalidDeadlineDate_throwsDateTimeParseException() throws IOException {
        writeDataFile("D | 0 | submit report | 2026-02-30");

        assertThrows(DateTimeParseException.class, storage::load);
    }

    @Test
    void load_blankLines_ignoresThemAndPreservesTaskOrder() throws IOException {
        writeDataFile("T | 0 | read book\n\n  \t \nD | 1 | submit report | 2026-08-28");

        List<Task> loadedTasks = storage.load();

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 1 | submit report | 2026-08-28"),
                loadedTasks.stream().map(Task::toFileString).toList());
    }

    /** Verifies that loading the given record reports structural damage. */
    private void assertDamagedRecord(String record) throws IOException {
        writeDataFile(record);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, storage::load);

        assertEquals("Damaged task record.", exception.getMessage());
    }

    private void writeDataFile(String contents) throws IOException {
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, contents);
    }
}
