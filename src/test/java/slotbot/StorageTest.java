package slotbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import slotbot.task.Deadline;
import slotbot.task.Event;
import slotbot.task.Task;
import slotbot.task.TaskList;
import slotbot.task.Todo;

public class StorageTest {

    @Test
    public void saveTasks_allTaskTypesAndStates_writesExpectedRecords(
            @TempDir Path tempDirectory) throws IOException {
        Path saveFilePath = tempDirectory.resolve("nested").resolve("tasks.txt");
        Todo todo = new Todo("read book");
        todo.markDone();
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 18));
        Event event = new Event(
                "meeting",
                LocalDateTime.of(2026, 9, 18, 10, 0),
                LocalDateTime.of(2026, 9, 18, 11, 30));
        Storage storage = new Storage(saveFilePath, new Ui());

        storage.saveTasks(new TaskList(List.of(todo, deadline, event)));

        assertEquals(List.of(
                "T | 1 | read book",
                "D | 0 | submit report | 2026-09-18",
                "E | 0 | meeting | 2026-09-18 10:00 | 2026-09-18 11:30"),
                Files.readAllLines(saveFilePath));
    }

    @Test
    public void saveTasks_baseTask_writesTodoRecord(@TempDir Path tempDirectory) throws IOException {
        Path saveFilePath = tempDirectory.resolve("tasks.txt");
        Storage storage = new Storage(saveFilePath, new Ui());

        storage.saveTasks(new TaskList(List.of(new Task("generic task"))));

        assertEquals(List.of("T | 0 | generic task"), Files.readAllLines(saveFilePath));
    }

    @Test
    public void saveTasks_unwritableLocation_reportsWarning(@TempDir Path tempDirectory)
            throws IOException {
        Path parentFile = tempDirectory.resolve("file");
        Files.writeString(parentFile, "not a directory");
        StringBuilder output = new StringBuilder();
        Storage storage = new Storage(parentFile.resolve("tasks.txt"), new Ui(output::append));

        storage.saveTasks(new TaskList(List.of(new Todo("read book"))));

        assertTrue(output.toString().contains("Warning: Unable to save tasks to disk."));
    }

    @Test
    public void loadTasks_missingSaveFile_returnsEmptyList(@TempDir Path tempDirectory) {
        Path saveFilePath = tempDirectory.resolve("data").resolve("slotbot.txt");
        Storage storage = new Storage(saveFilePath, new Ui());

        List<Task> tasks = storage.loadTasks();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void loadTasks_validTaskTypesAndStates_preservesOrderAndFields(
            @TempDir Path tempDirectory) throws IOException {
        Path saveFilePath = writeSaveFile(tempDirectory,
                "T | 1 | finish tutorial",
                "D | 0 | submit iP | 2026-09-01",
                "E | 1 | project meeting | 2026-09-02 14:00 | 2026-09-02 16:00");
        Storage storage = new Storage(saveFilePath, new Ui());

        List<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertEquals(Todo.class, tasks.get(0).getClass());
        assertEquals("finish tutorial", tasks.get(0).getDescription());
        assertTrue(tasks.get(0).isDone());

        assertEquals(Deadline.class, tasks.get(1).getClass());
        Deadline deadline = (Deadline) tasks.get(1);
        assertEquals("submit iP", deadline.getDescription());
        assertFalse(deadline.isDone());
        assertEquals(LocalDate.of(2026, 9, 1), deadline.getDate());

        assertEquals(Event.class, tasks.get(2).getClass());
        Event event = (Event) tasks.get(2);
        assertEquals("project meeting", event.getDescription());
        assertTrue(event.isDone());
        assertEquals(LocalDateTime.of(2026, 9, 2, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 9, 2, 16, 0), event.getTo());
    }

    @Test
    public void loadTasks_malformedRecordBetweenValidRecords_skipsMalformedRecord(
            @TempDir Path tempDirectory) throws IOException {
        Path saveFilePath = writeSaveFile(tempDirectory,
                "T | 0 | first task",
                "broken | record",
                "D | 1 | later task | 2026-09-03");
        Storage storage = new Storage(saveFilePath, new Ui());

        List<Task> tasks = storage.loadTasks();

        assertEquals(2, tasks.size());
        assertEquals("first task", tasks.get(0).getDescription());
        assertEquals("later task", tasks.get(1).getDescription());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void loadTasks_corruptedDeadlineDate_skipsDeadlineAndLoadsLaterRecord(
            @TempDir Path tempDirectory) throws IOException {
        Path saveFilePath = writeSaveFile(tempDirectory,
                "D | 0 | impossible deadline | 2019-02-30",
                "T | 0 | later task");
        Storage storage = new Storage(saveFilePath, new Ui());

        List<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("later task", tasks.get(0).getDescription());
    }

    @Test
    public void loadTasks_corruptedEventDateTime_skipsEventAndLoadsLaterRecord(
            @TempDir Path tempDirectory) throws IOException {
        Path saveFilePath = writeSaveFile(tempDirectory,
                "E | 0 | invalid event | 2019-10-15 25:00 | 2019-10-15 16:00",
                "T | 0 | later task");
        Storage storage = new Storage(saveFilePath, new Ui());

        List<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("later task", tasks.get(0).getDescription());
    }

    @Test
    public void loadTasks_eventWithReversedTimes_skipsEventAndLoadsLaterRecord(
            @TempDir Path tempDirectory) throws IOException {
        Path saveFilePath = writeSaveFile(tempDirectory,
                "E | 0 | invalid event | 2026-09-18 16:00 | 2026-09-18 14:00",
                "T | 0 | later task");
        Storage storage = new Storage(saveFilePath, new Ui());

        List<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("later task", tasks.get(0).getDescription());
    }

    @Test
    public void loadTasks_invalidStatusesTypesAndFields_skipsInvalidRecords(
            @TempDir Path tempDirectory) throws IOException {
        Path saveFilePath = writeSaveFile(tempDirectory,
                "T | 2 | invalid status",
                "X | 0 | unknown type",
                "T | 0",
                "D | 0 | empty date | ",
                "T | 0 | extra field | unexpected",
                "T | 0 | kept");
        StringBuilder output = new StringBuilder();
        Storage storage = new Storage(saveFilePath, new Ui(output::append));

        List<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("kept", tasks.get(0).getDescription());
        for (int lineNumber = 1; lineNumber <= 5; lineNumber++) {
            assertTrue(output.toString().contains("invalid task data on line " + lineNumber));
        }
    }

    @Test
    public void loadTasks_pathIsDirectory_reportsLoadError(@TempDir Path tempDirectory) {
        StringBuilder output = new StringBuilder();
        Storage storage = new Storage(tempDirectory, new Ui(output::append));

        List<Task> tasks = storage.loadTasks();

        assertTrue(tasks.isEmpty());
        assertTrue(output.toString().contains("Warning: Unable to load saved tasks."));
        assertTrue(output.toString().contains("Starting with an empty list."));
    }

    private Path writeSaveFile(Path tempDirectory, String... taskLines) throws IOException {
        Path saveFilePath = tempDirectory.resolve("data").resolve("slotbot.txt");
        Files.createDirectories(saveFilePath.getParent());
        Files.write(saveFilePath, List.of(taskLines));
        return saveFilePath;
    }
}
