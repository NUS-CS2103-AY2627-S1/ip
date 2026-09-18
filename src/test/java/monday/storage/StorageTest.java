package monday.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import monday.exception.MondayException;
import monday.task.Deadline;
import monday.task.Event;
import monday.task.Task;
import monday.task.TaskList;
import monday.task.Todo;

/**
 * Tests for Storage.
 */
class StorageTest {
    private static final Path DATA_FILE = Path.of("data", "monday.txt");

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(DATA_FILE);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(DATA_FILE);
    }

    @Test
    void loadTask_whenSaveFileDoesNotExist_returnsEmptyList() {
        ArrayList<Task> loadedTasks = Storage.loadTask();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void saveTask_withEmptyList_writesEmptySaveFile() throws IOException, MondayException {
        Storage.saveTask(new TaskList());

        assertTrue(Files.exists(DATA_FILE));
        assertTrue(Files.readAllLines(DATA_FILE).isEmpty());
    }

    @Test
    void saveTask_withTasks_writesExpectedFileFormat() throws IOException, MondayException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk", "Use the voucher"));
        tasks.add(new Deadline("submit report", true, LocalDate.of(2026, 8, 30), LocalTime.of(14, 30),
                "Attach the appendix"));
        tasks.add(new Deadline("renew passport", false, LocalDate.of(2026, 9, 1), null));
        tasks.add(new Event("team retreat", true, LocalDate.of(2026, 9, 10), LocalTime.of(9, 0),
                LocalDate.of(2026, 9, 11), null, "Bring a jacket"));

        Storage.saveTask(tasks);

        assertEquals(List.of(
                "T | 0 | buy milk | Use the voucher",
                "D | 1 | submit report | 2026-08-30 | 1430 | Attach the appendix",
                "D | 0 | renew passport | 2026-09-01 | ",
                "E | 1 | team retreat | 2026-09-10 | 0900 | 2026-09-11 |  | Bring a jacket"
        ), Files.readAllLines(DATA_FILE));
    }

    @Test
    void loadTask_withSavedTaskTypes_recreatesTasksWithCorrectDetails() throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        Files.write(DATA_FILE, List.of(
                "T | 0 | buy milk | Use the voucher",
                "",
                "D | 1 | submit report | 2026-08-30 | 1430",
                "D | 0 | renew passport | 2026-09-01 | ",
                "E | 1 | team retreat | 2026-09-10 | 0900 | 2026-09-11 | ",
                "E | 0 | workshop | 2026-10-01 |  | 2026-10-01 | 1800 | Prepare slides"
        ));

        ArrayList<Task> loadedTasks = Storage.loadTask();

        assertEquals(5, loadedTasks.size());

        Todo todo = assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertEquals("buy milk", todo.getDescription());
        assertEquals("Use the voucher", todo.getNotes());
        assertFalse(todo.isDone());

        Deadline deadlineWithTime = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals("submit report", deadlineWithTime.getDescription());
        assertEquals("", deadlineWithTime.getNotes());
        assertTrue(deadlineWithTime.isDone());
        assertEquals(LocalDate.of(2026, 8, 30), deadlineWithTime.getDeadlineDate());
        assertEquals(LocalTime.of(14, 30), deadlineWithTime.getDeadlineTime());

        Deadline deadlineWithoutTime = assertInstanceOf(Deadline.class, loadedTasks.get(2));
        assertEquals("renew passport", deadlineWithoutTime.getDescription());
        assertFalse(deadlineWithoutTime.isDone());
        assertEquals(LocalDate.of(2026, 9, 1), deadlineWithoutTime.getDeadlineDate());
        assertNull(deadlineWithoutTime.getDeadlineTime());

        Event eventWithEndTimeOmitted = assertInstanceOf(Event.class, loadedTasks.get(3));
        assertEquals("team retreat", eventWithEndTimeOmitted.getDescription());
        assertTrue(eventWithEndTimeOmitted.isDone());
        assertEquals(LocalDate.of(2026, 9, 10), eventWithEndTimeOmitted.getStartDate());
        assertEquals(LocalTime.of(9, 0), eventWithEndTimeOmitted.getStartTime());
        assertEquals(LocalDate.of(2026, 9, 11), eventWithEndTimeOmitted.getEndDate());
        assertNull(eventWithEndTimeOmitted.getEndTime());

        Event eventWithStartTimeOmitted = assertInstanceOf(Event.class, loadedTasks.get(4));
        assertEquals("workshop", eventWithStartTimeOmitted.getDescription());
        assertEquals("Prepare slides", eventWithStartTimeOmitted.getNotes());
        assertFalse(eventWithStartTimeOmitted.isDone());
        assertEquals(LocalDate.of(2026, 10, 1), eventWithStartTimeOmitted.getStartDate());
        assertNull(eventWithStartTimeOmitted.getStartTime());
        assertEquals(LocalDate.of(2026, 10, 1), eventWithStartTimeOmitted.getEndDate());
        assertEquals(LocalTime.of(18, 0), eventWithStartTimeOmitted.getEndTime());
    }

    @Test
    void loadTask_withInvalidSavedRows_skipsInvalidRows() throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        Files.write(DATA_FILE, List.of(
                "T | 0 | buy milk",
                "D | 0 | incomplete deadline",
                "T | 2 | invalid status",
                "E | 0 | invalid event | 2026-09-11 | 0900 | 2026-09-10 | 1000",
                "X | 0 | unknown task"
        ));

        ArrayList<Task> loadedTasks = Storage.loadTask();

        assertEquals(1, loadedTasks.size());
        assertEquals("buy milk", loadedTasks.get(0).getDescription());
    }
}
