package yapbot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yapbot.task.Deadline;
import yapbot.task.Event;
import yapbot.task.Priority;
import yapbot.task.Task;
import yapbot.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDir;

    private Path saveFile;

    @BeforeEach
    public void redirectStorageToTempFile() {
        saveFile = tempDir.resolve("yapBot.txt");
        Storage.setTargetPathForTesting(saveFile);
    }

    @AfterEach
    public void restoreRealStoragePath() {
        Storage.resetTargetPathForTesting();
    }

    @Test
    public void saveAndLoad_mixedTaskTypesAndDoneStatus_roundTripsCorrectly() {
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();
        Task event = new Event("project meeting", "Mon 2pm", "4pm");
        Task[] tasksToSave = new Task[]{todo, deadline, event};

        Storage.save(tasksToSave, tasksToSave.length);

        Task[] loaded = new Task[10];
        int loadedCount = Storage.load(loaded);

        assertEquals(3, loadedCount);
        assertEquals("T | 0 | read book", loaded[0].toFileFormat());
        assertEquals("D | 1 | return book | 2019-10-15", loaded[1].toFileFormat());
        assertEquals("E | 0 | project meeting | Mon 2pm | 4pm", loaded[2].toFileFormat());
    }

    @Test
    public void saveAndLoad_tasksWithPriority_roundTripsPriorityAsLastField() {
        Task todo = new Todo("read book");
        todo.setPriority(Priority.HIGH);
        Task deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.setPriority(Priority.LOW);
        Task event = new Event("project meeting", "Mon 2pm", "4pm");
        event.setPriority(Priority.MEDIUM);
        Task[] tasksToSave = new Task[]{todo, deadline, event};

        Storage.save(tasksToSave, tasksToSave.length);

        Task[] loaded = new Task[10];
        int loadedCount = Storage.load(loaded);

        assertEquals(3, loadedCount);
        assertEquals("T | 0 | read book | HIGH", loaded[0].toFileFormat());
        assertEquals("D | 0 | return book | 2019-10-15 | LOW", loaded[1].toFileFormat());
        assertEquals("E | 0 | project meeting | Mon 2pm | 4pm | MEDIUM", loaded[2].toFileFormat());
    }

    @Test
    public void load_oldFormatLinesWithNoPriorityField_loadWithNoPriority() throws IOException {
        Files.writeString(saveFile, "T | 1 | read book\n"
                + "D | 0 | return book | 2019-06-06\n"
                + "E | 0 | project meeting | Aug 6th 2pm | 4pm\n");

        Task[] loaded = new Task[10];
        int loadedCount = Storage.load(loaded);

        assertEquals(3, loadedCount);
        assertEquals("T | 1 | read book", loaded[0].toFileFormat());
        assertEquals("D | 0 | return book | 2019-06-06", loaded[1].toFileFormat());
        assertEquals("E | 0 | project meeting | Aug 6th 2pm | 4pm", loaded[2].toFileFormat());
    }

    @Test
    public void load_lineWithInvalidPriority_lineIsSkipped() throws IOException {
        Files.writeString(saveFile, "T | 0 | read book | URGENT\nT | 0 | write code\n");

        Task[] loaded = new Task[10];
        int loadedCount = Storage.load(loaded);

        assertEquals(1, loadedCount);
        assertEquals("write code", loaded[0].getDescription());
    }

    @Test
    public void load_fileDoesNotExist_returnsZeroTasks() {
        int loadedCount = Storage.load(new Task[10]);

        assertEquals(0, loadedCount);
    }

    @Test
    public void load_blankLinesInFile_areSkipped() throws IOException {
        Files.writeString(saveFile, "\nT | 0 | read book\n\n   \n");

        Task[] loaded = new Task[10];
        int loadedCount = Storage.load(loaded);

        assertEquals(1, loadedCount);
        assertEquals("read book", loaded[0].getDescription());
    }

    @Test
    public void load_lineWithUnknownType_lineIsSkipped() throws IOException {
        Files.writeString(saveFile, "X | 0 | mystery task\nT | 0 | read book\n");

        Task[] loaded = new Task[10];
        int loadedCount = Storage.load(loaded);

        assertEquals(1, loadedCount);
        assertEquals("read book", loaded[0].getDescription());
    }

    @Test
    public void load_lineWithInvalidStatus_lineIsSkipped() throws IOException {
        Files.writeString(saveFile, "T | 2 | read book\nT | 0 | write code\n");

        Task[] loaded = new Task[10];
        int loadedCount = Storage.load(loaded);

        assertEquals(1, loadedCount);
        assertEquals("write code", loaded[0].getDescription());
    }

    @Test
    public void load_deadlineLineWithInvalidDate_lineIsSkipped() throws IOException {
        Files.writeString(saveFile, "D | 0 | return book | not-a-date\nT | 0 | read book\n");

        Task[] loaded = new Task[10];
        int loadedCount = Storage.load(loaded);

        assertEquals(1, loadedCount);
        assertEquals("read book", loaded[0].getDescription());
    }

    @Test
    public void load_moreLinesThanArrayCapacity_stopsAtCapacity() throws IOException {
        Files.writeString(saveFile, "T | 0 | one\nT | 0 | two\nT | 0 | three\n");

        Task[] loaded = new Task[2];
        int loadedCount = Storage.load(loaded);

        assertEquals(2, loadedCount);
    }

    @Test
    public void save_missingParentDirectory_directoryIsCreated() {
        Path nestedFile = tempDir.resolve("nested").resolve("yapBot.txt");
        Storage.setTargetPathForTesting(nestedFile);

        Storage.save(new Task[]{new Todo("read book")}, 1);

        assertTrue(Files.exists(nestedFile));
    }
}
