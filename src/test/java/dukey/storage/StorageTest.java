package dukey.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dukey.exception.DukeyException;
import dukey.task.Deadline;
import dukey.task.Event;
import dukey.task.Task;
import dukey.task.TaskList;
import dukey.task.Todo;

public class StorageTest {

    @TempDir
    private Path tempDir;

    @Test
    public void load_validSavedTasks_returnsTaskList() throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "T | 1 | read book",
                "D | 0 | return book | 2099-12-06 1800",
                "E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600",
                ""));
        Storage storage = new Storage(dataFile.toString());

        TaskList tasks = storage.load();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book", tasks.get(1).toString());
        assertEquals("[D][ ] return book (by: Dec 06 2099, 6:00pm)", tasks.get(2).toString());
        assertEquals("[E][ ] project meeting (from: Aug 06 2099, 2:00pm to: Aug 06 2099, 4:00pm)",
                tasks.get(3).toString());
    }

    @Test
    public void load_savedTasksWithCompletionDates_returnsTaskListWithCompletionDates() throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "T | 1 | read book | 2026-09-14 1430",
                "D | 1 | return book | 2099-12-06 1800 | 2026-09-07 0900",
                "E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600 |",
                ""));
        Storage storage = new Storage(dataFile.toString());

        TaskList tasks = storage.load();

        assertEquals(3, tasks.size());
        assertEquals(2, tasks.countCompletedTasks());
        assertEquals(1, tasks.countPendingTasks());
        assertEquals(1, tasks.countCompletedInCalendarWeek(LocalDate.of(2026, 9, 14)));
        assertEquals(2, tasks.countCompletedInCalendarMonth(LocalDate.of(2026, 9, 30)));
    }

    @Test
    public void load_missingFile_throwsFileNotFoundException() {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());

        assertThrows(FileNotFoundException.class, storage::load);
    }

    @Test
    public void load_savedTaskWithMissingFields_throwsDukeyException() throws IOException {
        Path dataFile = writeDataFile("T | 1");
        Storage storage = new Storage(dataFile.toString());

        DukeyException exception = assertThrows(DukeyException.class, storage::load);

        assertEquals("Saved task is missing fields.", exception.getMessage());
    }

    @Test
    public void load_savedTaskWithUnknownType_throwsDukeyException() throws IOException {
        Path dataFile = writeDataFile("X | 0 | mystery task");
        Storage storage = new Storage(dataFile.toString());

        DukeyException exception = assertThrows(DukeyException.class, storage::load);

        assertEquals("Undefined task type.", exception.getMessage());
    }

    @Test
    public void load_savedTaskWithInvalidDoneStatus_throwsDukeyException() throws IOException {
        Path dataFile = writeDataFile("T | maybe | read book");
        Storage storage = new Storage(dataFile.toString());

        DukeyException exception = assertThrows(DukeyException.class, storage::load);

        assertEquals("Undefined done status.", exception.getMessage());
    }

    @Test
    public void load_savedDeadlineWithMissingDateTime_throwsDukeyException() throws IOException {
        Path dataFile = writeDataFile("D | 0 | return book");
        Storage storage = new Storage(dataFile.toString());

        DukeyException exception = assertThrows(DukeyException.class, storage::load);

        assertEquals("Deadline date/time is missing for this task.", exception.getMessage());
    }

    @Test
    public void load_savedTaskWithInvalidDateTime_throwsDukeyException() throws IOException {
        Path dataFile = writeDataFile("D | 0 | return book | not-a-date");
        Storage storage = new Storage(dataFile.toString());

        DukeyException exception = assertThrows(DukeyException.class, storage::load);

        assertEquals("Saved date/time must use format: yyyy-MM-dd HHmm", exception.getMessage());
    }

    @Test
    public void load_savedTaskWithInvalidCompletionDateTime_throwsDukeyException() throws IOException {
        Path dataFile = writeDataFile("T | 1 | read book | not-a-date");
        Storage storage = new Storage(dataFile.toString());

        DukeyException exception = assertThrows(DukeyException.class, storage::load);

        assertEquals("Saved date/time must use format: yyyy-MM-dd HHmm", exception.getMessage());
    }

    @Test
    public void load_undoneSavedTaskWithCompletionDateTime_throwsDukeyException() throws IOException {
        Path dataFile = writeDataFile("T | 0 | read book | 2026-09-14 1430");
        Storage storage = new Storage(dataFile.toString());

        DukeyException exception = assertThrows(DukeyException.class, storage::load);

        assertEquals("Undone saved tasks should not have a completion date/time.", exception.getMessage());
    }

    @Test
    public void save_taskList_writesTasksInStorageFormat() throws Exception {
        Path dataFile = tempDir.resolve("tasks.txt");
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        tasks.add(new Deadline("return book", "2099-12-06 1800"));
        tasks.add(new Event("project meeting", "2099-08-06 1400", "2099-08-06 1600"));
        Storage storage = new Storage(dataFile.toString());

        storage.save(tasks);

        assertEquals(String.join(System.lineSeparator(),
                "T | 1 | read book |",
                "D | 0 | return book | 2099-12-06 1800 |",
                "E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600 |",
                ""), Files.readString(dataFile));
    }

    @Test
    public void save_fileInMissingDirectory_createsDirectoryAndWritesTasks() throws Exception {
        Path dataFile = tempDir.resolve("data").resolve("tasks.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(dataFile.toString());

        storage.save(tasks);

        assertEquals("T | 0 | read book |" + System.lineSeparator(), Files.readString(dataFile));
    }

    @Test
    public void save_unknownTaskType_throwsDukeyException() {
        TaskList tasks = new TaskList();
        tasks.add(new Task("unknown task"));
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());

        DukeyException exception = assertThrows(DukeyException.class, () -> storage.save(tasks));

        assertEquals("Could not save an unknown task type.", exception.getMessage());
    }

    private Path writeDataFile(String content) throws IOException {
        Path dataFile = tempDir.resolve("tasks.txt");
        Files.writeString(dataFile, content);
        return dataFile;
    }
}
