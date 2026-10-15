package bot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bot.task.Deadline;
import bot.task.Event;
import bot.task.TaskList;
import bot.task.Todo;

/**
 * Tests loading, saving, and validation of the task storage format.
 */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadTasks_missingFile_returnsEmptyTaskList() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toFile());

        TaskList tasks = storage.loadTasks();

        assertEquals(0, tasks.size());
    }

    @Test
    void loadTasks_validData_restoresAllTaskTypesAndStatuses() throws IOException {
        File dataFile = writeDataFile("T | 1 | read book\n"
                + "D | 0 | submit report | 2019-12-02\n"
                + "E | 1 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00\n");

        TaskList tasks = new Storage(dataFile).loadTasks();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book", tasks.getTask(0).toString());
        assertEquals("[D][ ] submit report (by: Dec 02 2019)", tasks.getTask(1).toString());
        assertEquals("[E][X] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)",
                tasks.getTask(2).toString());
    }

    @Test
    void loadTasks_invalidStatus_reportsLineNumberAndReason() throws IOException {
        File dataFile = writeDataFile("T | 0 | valid task\nT | X | invalid task\n");

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(dataFile).loadTasks());

        assertEquals("invalid data on line 2 (status must be 0 or 1)", exception.getMessage());
    }

    @Test
    void loadTasks_unknownTaskType_reportsLineNumberAndReason() throws IOException {
        File dataFile = writeDataFile("N | 0 | unknown task\n");

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(dataFile).loadTasks());

        assertEquals("invalid data on line 1 (unknown task type 'N')", exception.getMessage());
    }

    @Test
    void loadTasks_incompleteEvent_reportsRequiredFieldCount() throws IOException {
        File dataFile = writeDataFile("E | 0 | meeting | 2019-12-02T14:00\n");

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(dataFile).loadTasks());

        assertEquals("invalid data on line 1 (event must contain 5 fields)", exception.getMessage());
    }

    @Test
    void loadTasks_invalidDeadlineDate_reportsExpectedFormat() throws IOException {
        File dataFile = writeDataFile("D | 0 | submit report | 02-12-2019\n");

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(dataFile).loadTasks());

        assertEquals("invalid data on line 1 (deadline date must use yyyy-MM-dd)", exception.getMessage());
    }

    @Test
    void loadTasks_eventEndBeforeStart_reportsInvalidRange() throws IOException {
        File dataFile = writeDataFile(
                "E | 0 | meeting | 2019-12-02T16:00 | 2019-12-02T14:00\n");

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(dataFile).loadTasks());

        assertEquals("invalid data on line 1 (event end must be after its start)",
                exception.getMessage());
    }

    @Test
    void loadTasks_duplicateTask_reportsLineNumberAndReason() throws IOException {
        File dataFile = writeDataFile("T | 1 | Read Book\nT | 0 | read book\n");

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(dataFile).loadTasks());

        assertEquals("invalid data on line 2 (duplicates an earlier task)", exception.getMessage());
    }

    @Test
    void loadTasks_dataPathIsDirectory_reportsReason() throws IOException {
        Path dataDirectory = temporaryDirectory.resolve("bot.txt");
        Files.createDirectory(dataDirectory);

        IOException exception = assertThrows(IOException.class, () ->
                new Storage(dataDirectory.toFile()).loadTasks());

        assertEquals("data path is not a file: " + dataDirectory, exception.getMessage());
    }

    @Test
    void saveTasks_missingParentDirectory_createsDirectoryAndWritesTasks() throws IOException {
        File dataFile = temporaryDirectory.resolve("data/tasks.txt").toFile();
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read book");
        todo.mark();
        TaskList tasks = new TaskList();
        tasks.add(todo,
                new Deadline("submit report", LocalDate.of(2019, 12, 2)),
                new Event("project meeting",
                        LocalDateTime.of(2019, 12, 2, 14, 0),
                        LocalDateTime.of(2019, 12, 2, 16, 0)));

        storage.saveTasks(tasks);

        assertTrue(dataFile.isFile());
        String savedData = Files.readString(dataFile.toPath());
        assertTrue(savedData.contains("T | 1 | read book"));
        assertTrue(savedData.contains("D | 0 | submit report | 2019-12-02"));
        assertTrue(savedData.contains(
                "E | 0 | project meeting | 2019-12-02T14:00 | 2019-12-02T16:00"));
        assertFalse(savedData.isBlank());
    }

    /**
     * Writes test data to the temporary directory.
     *
     * @param content storage text to write.
     * @return created data file
     * @throws IOException if the test data cannot be written
     */
    private File writeDataFile(String content) throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, content);
        return dataFile.toFile();
    }
}
