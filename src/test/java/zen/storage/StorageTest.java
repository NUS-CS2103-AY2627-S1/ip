package zen.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zen.ZenException;
import zen.task.Deadline;
import zen.task.Priority;
import zen.task.TaskList;
import zen.task.Todo;

/**
 * Tests persistence of task lists through {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    // AI-assisted
    @Test
    public void save_newNestedPath_createsFileAndWritesTaskRecords() throws ZenException, IOException {
        Path filePath = temporaryDirectory.resolve("data/tasks.txt");
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("buy milk", Priority.HIGH));
        taskList.addTask(new Deadline("submit report", LocalDateTime.of(2026, 10, 10, 10, 30), Priority.LOW));

        new Storage(filePath.toString()).save(taskList);

        assertTrue(Files.exists(filePath));
        assertEquals("T | 0 | buy milk | high\nD | 0 | submit report | 2026-10-10T10:30 | low",
                Files.readString(filePath));
    }

    // AI-assisted
    @Test
    public void save_existingFile_replacesPreviousContents() throws ZenException, IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList firstList = new TaskList();
        firstList.addTask(new Todo("old task"));
        TaskList replacementList = new TaskList();
        replacementList.addTask(new Todo("new task"));

        storage.save(firstList);
        storage.save(replacementList);

        assertEquals("T | 0 | new task | none", Files.readString(filePath));
    }

    // AI-assisted
    @Test
    public void load_missingFile_createsAndReturnsEmptyTaskList() throws ZenException, IOException {
        Path filePath = temporaryDirectory.resolve("data/tasks.txt");

        TaskList loadedTasks = new Storage(filePath.toString()).load();

        assertTrue(Files.exists(filePath));
        assertTrue(loadedTasks.isEmpty());
        assertEquals("", Files.readString(filePath));
    }

    // AI-assisted
    @Test
    public void load_legacyAndPriorityTaskRecords_restoresAllTaskRecords() throws ZenException, IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        String records = String.join("\n",
                "T | 1 | read book",
                "D | 0 | submit report | 2026-10-10T10:30 | high",
                "E | 1 | project meeting | 2026-10-10T09:00 to 2026-10-10T10:00 | medium");
        Files.writeString(filePath, records + "\n\n");

        TaskList loadedTasks = new Storage(filePath.toString()).load();

        assertEquals(3, loadedTasks.size());
        assertEquals(String.join("\n",
                "D | 0 | submit report | 2026-10-10T10:30 | high",
                "E | 1 | project meeting | 2026-10-10T09:00 to 2026-10-10T10:00 | medium",
                "T | 1 | read book | none"), loadedTasks.toStorageString());
    }

    // AI-assisted
    @Test
    public void load_unknownTaskType_throwsZenException() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "X | 0 | unsupported task");

        ZenException exception = assertThrows(ZenException.class, () ->
                new Storage(filePath.toString()).load());

        assertEquals("Unable to load tasks because line 1 is invalid. The file was not changed.",
                exception.getMessage());
    }

    @Test
    public void load_malformedPriority_throwsZenException() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "T | 0 | buy milk | urgent");

        ZenException exception = assertThrows(ZenException.class, () -> new Storage(filePath.toString()).load());

        assertEquals("Unable to load tasks because line 1 is invalid. The file was not changed.",
                exception.getMessage());
    }

    @Test
    public void load_invalidCompletionStatus_reportsItsLineAndPreservesFile() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        String records = String.join("\n", "T | 0 | first task | none", "", "T | 2 | buy milk | none");
        Files.writeString(filePath, records);

        ZenException exception = assertThrows(ZenException.class, () -> new Storage(filePath.toString()).load());

        assertEquals("Unable to load tasks because line 3 is invalid. The file was not changed.",
                exception.getMessage());
        assertEquals(records, Files.readString(filePath));
    }

    @Test
    public void load_multipleInvalidRecords_reportsAllInvalidLines() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        String records = String.join("\n", "T | 0 | valid task | none", "T | 2 | invalid status | none",
                "D | 0 | valid deadline | 2026-10-10T10:30 | none", "X | 0 | invalid type");
        Files.writeString(filePath, records);

        ZenException exception = assertThrows(ZenException.class, () -> new Storage(filePath.toString()).load());

        assertEquals("Unable to load tasks because lines 2, 4 are invalid. The file was not changed.",
                exception.getMessage());
    }

    // AI-assisted
    @Test
    public void load_recordsWithWrongFieldCounts_reportsAllInvalidLines() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        String records = String.join("\n", "T | 0", "D | 0 | report | 2026-10-10T10:30 | none | extra");
        Files.writeString(filePath, records);

        ZenException exception = assertThrows(ZenException.class, () -> new Storage(filePath.toString()).load());

        assertEquals("Unable to load tasks because lines 1, 2 are invalid. The file was not changed.",
                exception.getMessage());
        assertEquals(records, Files.readString(filePath));
    }

    // AI-assisted
    @Test
    public void load_deadlineWithInvalidDateTime_reportsInvalidLine() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "D | 0 | report | not-a-date | none");

        ZenException exception = assertThrows(ZenException.class, () -> new Storage(filePath.toString()).load());

        assertEquals("Unable to load tasks because line 1 is invalid. The file was not changed.",
                exception.getMessage());
    }

    // AI-assisted
    @Test
    public void load_eventWithMalformedTiming_reportsInvalidLine() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "E | 0 | meeting | 2026-10-10T09:00 until 2026-10-10T10:00 | none");

        ZenException exception = assertThrows(ZenException.class, () -> new Storage(filePath.toString()).load());

        assertEquals("Unable to load tasks because line 1 is invalid. The file was not changed.",
                exception.getMessage());
    }

    // AI-assisted
    @Test
    public void load_pathThatIsDirectory_throwsZenException() throws IOException {
        Path directoryPath = temporaryDirectory.resolve("task-directory");
        Files.createDirectory(directoryPath);

        ZenException exception = assertThrows(ZenException.class, () -> new Storage(directoryPath.toString()).load());

        assertEquals("Unable to load tasks from " + directoryPath + ".", exception.getMessage());
    }

    // AI-assisted
    @Test
    public void save_pathThatIsDirectory_throwsZenException() throws IOException {
        Path directoryPath = temporaryDirectory.resolve("task-directory");
        Files.createDirectory(directoryPath);

        ZenException exception = assertThrows(ZenException.class, ()
                -> new Storage(directoryPath.toString()).save(new TaskList()));

        assertEquals("Unable to save tasks to " + directoryPath, exception.getMessage());
    }
}
