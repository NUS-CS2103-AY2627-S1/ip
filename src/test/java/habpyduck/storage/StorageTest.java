package habpyduck.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import habpyduck.HabpyDuckException;
import habpyduck.task.Deadline;
import habpyduck.task.Event;
import habpyduck.task.Task;
import habpyduck.task.Todo;

/**
 * Tests saving and loading tasks through the text file format.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void escapeFileField_specialCharacters_returnsEscapedText() {
        String original = "read | book\\notes\nline\rend";

        assertEquals("read \\| book\\\\notes\\nline\\rend", Storage.escapeFileField(original));
    }

    @Test
    public void loadTasks_missingFile_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("missing.txt").toString());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void saveTasks_mixedTasks_writesExpectedFileFormat() throws Exception {
        Path saveFile = tempDir.resolve("data").resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        Todo todo = new Todo("read | book");
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 8, 25, 18, 0));
        Event event = new Event("sync call", LocalDateTime.of(2026, 8, 25, 14, 0),
                LocalDateTime.of(2026, 8, 25, 16, 0));
        deadline.markAsDone();
        todo.addTag("#fun");
        todo.addTag("#school");
        event.addTag("#work");
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);

        storage.saveTasks(tasks);

        String expected = String.join(System.lineSeparator(),
                "T | 0 | read \\| book | #fun,#school",
                "D | 1 | return book | 2026-08-25T18:00",
                "E | 0 | sync call | 2026-08-25T14:00 | 2026-08-25T16:00 | #work");
        assertEquals(expected, Files.readString(saveFile).stripTrailing());
    }

    @Test
    public void loadTasks_validSavedTasks_returnsTaskObjectsWithStatusAndDetails() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 1 | read book | #fun,#school",
                "D | 0 | return book | 2026-08-25T18:00",
                "E | 1 | project meeting | 2026-08-25T14:00 | 2026-08-25T16:00 | #work"));
        Storage storage = new Storage(saveFile.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book #fun #school", tasks.get(0).toString());
        assertEquals("[D][ ] return book (by: 25/8/2026 1800)", tasks.get(1).toString());
        assertEquals("[E][X] project meeting (from: 25/8/2026 1400 to: 25/8/2026 1600) #work",
                tasks.get(2).toString());
    }

    @Test
    public void loadTasks_untaggedSavedTasks_returnsTaskObjectsWithoutTags() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 1 | read book",
                "D | 0 | return book | 2026-08-25T18:00",
                "E | 1 | project meeting | 2026-08-25T14:00 | 2026-08-25T16:00"));
        Storage storage = new Storage(saveFile.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[D][ ] return book (by: 25/8/2026 1800)", tasks.get(1).toString());
        assertEquals("[E][X] project meeting (from: 25/8/2026 1400 to: 25/8/2026 1600)",
                tasks.get(2).toString());
    }

    @Test
    public void loadTasks_savedDateWithoutTime_returnsDeadlineAtStartOfDay() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile, "D | 0 | return book | 2026-08-25");
        Storage storage = new Storage(saveFile.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("[D][ ] return book (by: 25/8/2026)", tasks.get(0).toString());
    }

    @Test
    public void loadTasks_savedEventDateTimes_returnsFormattedEvent() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 0 | read \\| book",
                "E | 0 | sync call | 2026-08-25T14:00 | 2026-08-25T16:00"));
        Storage storage = new Storage(saveFile.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read | book", tasks.get(0).toString());
        assertEquals("[E][ ] sync call (from: 25/8/2026 1400 to: 25/8/2026 1600)", tasks.get(1).toString());
    }

    @Test
    public void loadTasks_malformedLines_skipsInvalidTasksAndKeepsValidTasks() throws Exception {
        Path saveFile = tempDir.resolve("tasks.txt");
        Files.writeString(saveFile, String.join(System.lineSeparator(),
                "T | 1 | read book",
                "D | 2 | bad status | 2026-08-25T18:00",
                "X | 0 | bad type",
                "D | 0 | missing date",
                "D | 0 | invalid date | tomorrow",
                "E | 1 | project meeting | 2026-08-25T14:00 | 2026-08-25T16:00",
                "T | 0 | invalid tag | fun",
                "T | 0 |    "));
        Storage storage = new Storage(saveFile.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(2, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[E][X] project meeting (from: 25/8/2026 1400 to: 25/8/2026 1600)",
                tasks.get(1).toString());
    }

    @Test
    public void saveTasks_fileCannotBeWritten_exceptionThrown() throws Exception {
        Path directoryPath = tempDir.resolve("directory");
        Files.createDirectories(directoryPath);
        Storage storage = new Storage(directoryPath.toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        HabpyDuckException exception = assertThrows(
                HabpyDuckException.class, () -> storage.saveTasks(tasks));

        assertEquals("OH NO!!! I could not save your tasks to " + directoryPath + ".",
                exception.getMessage());
    }
}
