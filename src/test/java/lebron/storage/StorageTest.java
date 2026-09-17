package lebron.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lebron.task.Deadline;
import lebron.task.Event;
import lebron.task.RecurrenceInterval;
import lebron.task.RecurringTask;
import lebron.task.TaskList;
import lebron.task.TaskListException;
import lebron.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("nested").resolve("LeBron.txt"));
    }

    @Test
    public void load_noSaveFileYet_returnsEmptyTaskList() throws IOException {
        Storage storage = newStorage();
        assertEquals(0, storage.load().size());
    }

    @Test
    public void saveThenLoad_mixedTaskTypesAndDoneStatuses_roundTripsCorrectly()
            throws IOException, TaskListException {
        Storage storage = newStorage();
        TaskList original = new TaskList(new ArrayList<>());
        Todo todo = new Todo("read book");
        todo.setStatus(true);
        original.addTask(todo);
        original.addTask(new Deadline("return book", LocalDateTime.of(2019, 10, 15, 18, 0)));
        original.addTask(new Event("project meeting",
                LocalDateTime.of(2019, 10, 16, 9, 0), LocalDateTime.of(2019, 10, 16, 11, 0)));

        storage.save(original);
        TaskList loaded = storage.load();

        assertEquals(original.toString(), loaded.toString());
    }

    @Test
    public void saveThenLoad_recurringTask_roundTripsCorrectly() throws IOException, TaskListException {
        Storage storage = newStorage();
        TaskList original = new TaskList(new ArrayList<>());
        original.addTask(new RecurringTask("project meeting",
                LocalDateTime.of(2019, 10, 15, 18, 0), RecurrenceInterval.WEEKLY));

        storage.save(original);
        TaskList loaded = storage.load();

        assertEquals(original.toString(), loaded.toString());
    }

    @Test
    public void save_parentDirectoryMissing_createsItAutomatically() throws IOException, TaskListException {
        Storage storage = newStorage();
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));

        storage.save(taskList);

        assertEquals(1, storage.load().size());
    }

    @Test
    public void load_lineWithUnknownTaskType_ioExceptionThrownWithCauseInMessage() throws IOException {
        Path filePath = tempDir.resolve("LeBron.txt");
        Files.writeString(filePath, "X | 0 | mystery task\n");
        Storage storage = new Storage(filePath);

        IOException thrown = assertThrows(IOException.class, storage::load);

        assertTrue(thrown.getCause() instanceof IllegalArgumentException);
    }

    @Test
    public void load_deadlineLineMissingDateField_ioExceptionThrown() throws IOException {
        Path filePath = tempDir.resolve("LeBron.txt");
        Files.writeString(filePath, "D | 0 | return book\n");
        Storage storage = new Storage(filePath);

        assertThrows(IOException.class, storage::load);
    }

    @Test
    public void load_lineWithMalformedDate_ioExceptionThrown() throws IOException {
        Path filePath = tempDir.resolve("LeBron.txt");
        Files.writeString(filePath, "D | 0 | return book | not-a-date\n");
        Storage storage = new Storage(filePath);

        assertThrows(IOException.class, storage::load);
    }

    @Test
    public void load_blankLinesInSaveFile_areSkipped() throws IOException {
        Path filePath = tempDir.resolve("LeBron.txt");
        Files.writeString(filePath, "T | 0 | read book\n\n   \nT | 1 | write essay\n");
        Storage storage = new Storage(filePath);

        TaskList loaded = storage.load();

        assertEquals(2, loaded.size());
    }
}
