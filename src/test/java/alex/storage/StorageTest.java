package alex.storage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;

import alex.task.Deadline;
import alex.task.Event;
import alex.task.Task;
import alex.task.Todo;
import alex.util.TaskDateTime;

/**
 * Tests task persistence behavior.
 */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void saveAndLoadTasks_variedTasks_preservesAllTaskData() throws StorageException {
        Storage storage = new Storage(tempDirectory.resolve("nested/data/alex.txt"));
        Task completedTodo = new Todo("阅读 book");
        completedTodo.markAsDone();
        List<Task> originalTasks = List.of(
                completedTodo,
                new Deadline("date only", LocalDate.of(2019, 12, 2)),
                new Deadline("timed", new TaskDateTime(
                        LocalDate.of(2019, 12, 2), LocalTime.of(18, 0))),
                new Event("same-day event", LocalDate.of(2019, 12, 2), LocalDate.of(2019, 12, 2)),
                new Event("mixed event",
                        new TaskDateTime(LocalDate.of(2019, 12, 2)),
                        new TaskDateTime(LocalDate.of(2019, 12, 3), LocalTime.of(9, 30))));

        storage.saveTasks(originalTasks);
        List<String> loadedTaskData = storage.loadTasks().stream()
                .map(Task::toDataString)
                .toList();

        assertEquals(originalTasks.stream().map(Task::toDataString).toList(), loadedTaskData);
    }

    @Test
    public void loadTasks_invalidTaskFields_reportsReason() {
        List<InvalidTaskData> invalidTasks = List.of(
                new InvalidTaskData("T | 2 | task", "completion status must be 0 or 1"),
                new InvalidTaskData("X | 0 | task", "unknown task type 'X'"),
                new InvalidTaskData("D | 0 | task", "expected 4 fields, but found 3"),
                new InvalidTaskData("T | 0 | ", "task details cannot be empty"));

        assertAll(invalidTasks.stream().<Executable>map(invalidTask -> () ->
                assertInvalidTaskData(invalidTask)));
    }

    @Test
    public void loadTasks_blankLinesBeforeInvalidTask_reportsPhysicalLineNumber()
            throws IOException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Files.writeString(dataFile,
                "\nT | 0 | valid task\n\nX | 0 | invalid task",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        StorageException exception = assertThrows(StorageException.class, storage::loadTasks);

        assertEquals("The data file is invalid at line 4: unknown task type 'X'",
                exception.getMessage());
    }

    @Test
    public void loadTasks_eventEndsBeforeStart_storageExceptionThrown() throws IOException {
        Path dataFile = tempDirectory.resolve("alex.txt");
        Files.writeString(dataFile,
                "E | 0 | meeting | 2019-12-02 1800 | 2019-12-02 1700",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        StorageException exception = assertThrows(StorageException.class, storage::loadTasks);

        assertEquals("The data file is invalid at line 1: "
                + "The event must end after it starts. We serve coffee, not temporal paradoxes.",
                exception.getMessage());
    }

    private void assertInvalidTaskData(InvalidTaskData invalidTask) throws IOException {
        Path dataFile = Files.createTempFile(tempDirectory, "invalid-", ".txt");
        Files.writeString(dataFile, invalidTask.line(), StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        StorageException exception = assertThrows(StorageException.class, storage::loadTasks);

        assertEquals("The data file is invalid at line 1: " + invalidTask.reason(),
                exception.getMessage());
    }

    private record InvalidTaskData(String line, String reason) {
    }
}
