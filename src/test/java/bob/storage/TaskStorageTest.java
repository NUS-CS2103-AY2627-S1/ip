package bob.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bob.exception.BobException;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.TaskList;
import bob.task.ToDo;

/**
 * Tests the behavior of {@link TaskStorage}.
 */
public class TaskStorageTest {

    @TempDir
    private Path tempDir;

    @Test
    public void load_fileDoesNotExist_returnsEmptyTaskList() throws BobException {
        Path testFile = tempDir.resolve("non_existent_tasks.txt");
        TaskStorage storage = new TaskStorage(testFile);

        TaskList loadedTasks = storage.load();
        assertTrue(loadedTasks.isEmpty());
        assertEquals(0, loadedTasks.size());
    }

    @Test
    public void saveAndLoad_allTaskTypesWithDoneStatus_preservesTasks() throws BobException {
        Path testFile = tempDir.resolve("tasks.txt");
        TaskStorage storage = new TaskStorage(testFile);

        TaskList tasks = new TaskList();

        ToDo todo = new ToDo("buy milk");
        ToDo doneTodo = new ToDo("read book");
        doneTodo.mark();

        LocalDateTime deadlineTime = LocalDateTime.of(2026, 11, 11, 18, 45);
        Deadline deadline = new Deadline("submit assignment", deadlineTime);
        Deadline doneDeadline = new Deadline("return book", deadlineTime);
        doneDeadline.mark();

        LocalDateTime eventStart = LocalDateTime.of(2026, 9, 1, 9, 0);
        LocalDateTime eventEnd = LocalDateTime.of(2026, 9, 1, 17, 0);
        Event event = new Event("orientation", eventStart, eventEnd);
        Event doneEvent = new Event("conference", eventStart, eventEnd);
        doneEvent.mark();

        tasks.add(todo);
        tasks.add(doneTodo);
        tasks.add(deadline);
        tasks.add(doneDeadline);
        tasks.add(event);
        tasks.add(doneEvent);

        storage.save(tasks);

        TaskStorage loadingStorage = new TaskStorage(testFile);
        TaskList loadedTasks = loadingStorage.load();

        assertEquals(tasks.size(), loadedTasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i).export(), loadedTasks.get(i).export());
            assertEquals(tasks.get(i).toString(), loadedTasks.get(i).toString());
        }
    }

    @Test
    void saveAndLoad_descriptionContainingPipe_preservesDescription() throws IOException, BobException {
        Path testFile = tempDir.resolve("pipe_description.txt");
        TaskStorage storage = new TaskStorage(testFile);
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("compare option A | option B"));

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertEquals("compare option A | option B", loadedTasks.get(0).getName());
        assertEquals("T\u00010\u0001compare option A | option B", Files.readString(testFile).trim());
    }

    @Test
    public void load_emptyFileAndBlankLines_returnsEmptyTaskList() throws IOException, BobException {
        Path testFile = tempDir.resolve("empty_tasks.txt");
        Files.write(testFile, List.of("", "   ", ""));

        TaskStorage storage = new TaskStorage(testFile);
        TaskList loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    public void load_corruptedTaskType_removesInvalidLine() throws IOException, BobException {
        Path testFile = tempDir.resolve("corrupted_type.txt");
        Files.write(testFile, List.of("X | 0 | invalid task type"));

        TaskStorage storage = new TaskStorage(testFile);
        TaskList loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(Files.readAllLines(testFile).isEmpty());
    }

    @Test
    public void load_invalidTodoBeforeValidTodo_loadsValidTodoAndRemovesInvalidLine()
            throws IOException, BobException {
        Path testFile = tempDir.resolve("insufficient_fields.txt");
        Files.write(testFile, List.of("T\u00010", "T\u00011\u0001testing"));

        TaskStorage storage = new TaskStorage(testFile);
        TaskList loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("T\u00011\u0001testing", loadedTasks.get(0).export());
        assertEquals(1, storage.getInvalidTaskCount());
        assertEquals(List.of("T\u00011\u0001testing"), Files.readAllLines(testFile));
    }

    @Test
    public void load_corruptedDeadlineFieldCount_removesInvalidLine() throws IOException, BobException {
        Path testFile = tempDir.resolve("corrupted_deadline_count.txt");
        Files.write(testFile, List.of("D\u00010\u0001submit paper"));

        TaskStorage storage = new TaskStorage(testFile);
        TaskList loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(Files.readAllLines(testFile).isEmpty());
    }

    @Test
    public void load_corruptedDeadlineDateFormat_removesInvalidLine() throws IOException, BobException {
        Path testFile = tempDir.resolve("corrupted_deadline.txt");
        Files.write(testFile, List.of("D\u00010\u0001submit paper\u0001invalid-date"));

        TaskStorage storage = new TaskStorage(testFile);
        TaskList loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(Files.readAllLines(testFile).isEmpty());
    }

    @Test
    public void load_corruptedEventFieldCount_removesInvalidLine() throws IOException, BobException {
        Path testFile = tempDir.resolve("corrupted_event.txt");
        Files.write(testFile, List.of("E\u00010\u0001workshop\u00012026-09-01T09:00"));

        TaskStorage storage = new TaskStorage(testFile);
        TaskList loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(Files.readAllLines(testFile).isEmpty());
    }

    @Test
    public void load_corruptedEventDateFormat_removesInvalidLine() throws IOException, BobException {
        Path testFile = tempDir.resolve("corrupted_event_date.txt");
        Files.write(testFile, List.of("E\u00010\u0001workshop\u0001invalid-from\u0001invalid-to"));

        TaskStorage storage = new TaskStorage(testFile);
        TaskList loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(Files.readAllLines(testFile).isEmpty());
    }

    @Test
    public void save_nestedDirectory_createsParentDirectoriesAndSaves() throws BobException {
        Path nestedFile = tempDir.resolve("subdir").resolve("nested_tasks.txt");
        TaskStorage storage = new TaskStorage(nestedFile);

        TaskList tasks = new TaskList();
        tasks.add(new ToDo("nested task"));

        storage.save(tasks);

        assertTrue(Files.exists(nestedFile));
        TaskList loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals(tasks.get(0).export(), loaded.get(0).export());
    }
}
