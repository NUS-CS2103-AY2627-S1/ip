package mrchatbot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mrchatbot.exception.MrChatbotException;
import mrchatbot.task.Deadline;
import mrchatbot.task.Event;
import mrchatbot.task.Recurrence;
import mrchatbot.task.RecurringTask;
import mrchatbot.task.Task;
import mrchatbot.task.TaskList;
import mrchatbot.task.Todo;

public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void saveAndLoadTasks_recurringSchedules_preservesStatusDatesAndEscaping() throws Exception {
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        RecurringTask daily = new RecurringTask("read | notes", Recurrence.DAY, LocalDate.of(2026, 9, 22));
        RecurringTask weekly = new RecurringTask("meeting", Recurrence.WEEK, LocalDate.of(2026, 9, 28));
        weekly.markAsDone();
        storage.saveTasks(new TaskList(daily, weekly));

        ArrayList<Task> loaded = storage.loadTasks();

        assertEquals(daily.toString(), loaded.get(0).toString());
        assertEquals(weekly.toString(), loaded.get(1).toString());
        assertEquals(Recurrence.WEEK, ((RecurringTask) loaded.get(1)).getRecurrence());
    }

    @Test
    public void loadTasks_invalidRecurringRecord_reportsLoadError() throws Exception {
        Path path = tempDir.resolve("tasks.txt");
        Storage storage = new Storage(path.toString());
        String[] records = {
            "R | 0 | meeting | MONTH | 2026-09-21",
            "R | 0 | meeting | WEEK | invalid",
            "R | 0 | meeting | WEEK | 2026-02-30",
            "R | 0 | meeting | WEEK",
            "R | 0 | meeting | WEEK | 2026-09-21 | extra",
            "R | 0 | | DAY | 2026-09-21",
            "R | 2 | meeting | WEEK | 2026-09-21"
        };
        for (String record : records) {
            Files.writeString(path, record);
            MrChatbotException exception = assertThrows(MrChatbotException.class, storage::loadTasks);
            assertEquals("Sorry, I could not load your tasks.", exception.getMessage());
        }
    }

    @Test
    public void saveTasks_destinationIsNonemptyDirectory_cleansUpTemporaryFile() throws Exception {
        Path destination = Files.createDirectory(tempDir.resolve("duke.txt"));
        Path existingFile = destination.resolve("keep.txt");
        Files.writeString(existingFile, "existing contents");
        Storage storage = new Storage(destination.toString());

        MrChatbotException exception = assertThrows(
                MrChatbotException.class, () -> storage.saveTasks(new TaskList(new Todo("read book"))));

        assertEquals("Sorry, I could not save your tasks.", exception.getMessage());
        assertEquals("existing contents", Files.readString(existingFile));
        try (Stream<Path> remainingFiles = Files.list(tempDir)) {
            assertEquals(1, remainingFiles.count());
        }
    }

    @Test
    public void saveTasks_taskWithSpecialCharacters_escapesSpecialCharacters() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList(new Todo("read | book \\ notes"));

        storage.saveTasks(tasks);

        assertEquals("T | 0 | read \\| book \\\\ notes" + System.lineSeparator(), Files.readString(filePath));
    }

    @Test
    public void saveTasks_nestedMissingDirectory_createsDirectoryAndFile() throws Exception {
        Path filePath = tempDir.resolve("data").resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());
        TaskList tasks = new TaskList(new Todo("read book"));

        storage.saveTasks(tasks);

        assertEquals("T | 0 | read book" + System.lineSeparator(), Files.readString(filePath));
    }

    @Test
    public void loadTasks_missingFile_emptyListReturned() throws MrChatbotException {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void loadTasks_validTodoDeadlineEvent_tasksRestored() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Files.writeString(filePath, "T | 1 | read book" + System.lineSeparator()
                + "D | 0 | return book | 2019-12-01" + System.lineSeparator()
                + "E | 0 | project meeting | 2019-12-01 | 2019-12-02" + System.lineSeparator());
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 1 2019)", tasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Dec 1 2019 to: Dec 2 2019)", tasks.get(2).toString());
    }

    @Test
    public void loadTasks_escapedSpecialCharacters_taskRestored() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Files.writeString(filePath, "T | 0 | read \\| book \\\\ notes" + System.lineSeparator());
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read | book \\ notes", tasks.get(0).toString());
    }

    @Test
    public void loadTasks_malformedTaskType_exceptionThrown() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Files.writeString(filePath, "X | 0 | read book" + System.lineSeparator());
        Storage storage = new Storage(filePath.toString());

        MrChatbotException exception = assertThrows(MrChatbotException.class, storage::loadTasks);
        assertEquals("Sorry, I could not load your tasks.", exception.getMessage());
    }

    @Test
    public void loadTasks_invalidEscapedCharacter_exceptionThrown() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Files.writeString(filePath, "T | 0 | read \\n book" + System.lineSeparator());
        Storage storage = new Storage(filePath.toString());

        MrChatbotException exception = assertThrows(MrChatbotException.class, storage::loadTasks);
        assertEquals("Sorry, I could not load your tasks.", exception.getMessage());
    }

    @Test
    public void saveAndLoadTasks_todoDeadlineEvent_tasksRoundTripped() throws Exception {
        Path filePath = tempDir.resolve("duke.txt");
        Storage storage = new Storage(filePath.toString());
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-12-01"));
        Event event = new Event("project meeting", LocalDate.parse("2019-12-01"), LocalDate.parse("2019-12-02"));
        deadline.markAsDone();
        TaskList tasks = new TaskList(todo, deadline, event);

        storage.saveTasks(tasks);
        ArrayList<Task> loadedTasks = storage.loadTasks();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[D][X] return book (by: Dec 1 2019)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Dec 1 2019 to: Dec 2 2019)", loadedTasks.get(2).toString());
    }
}
