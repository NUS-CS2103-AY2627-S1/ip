package carl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 9, 30);
    private static final LocalDateTime END = LocalDateTime.of(2026, 10, 1, 11, 0);

    @Test
    public void taskStatus_values_returnExpectedDisplayAndStorageValues() {
        assertEquals("[ ]", TaskStatus.NOT_DONE.toString());
        assertEquals(0, TaskStatus.NOT_DONE.toInt());
        assertEquals("[X]", TaskStatus.DONE.toString());
        assertEquals(1, TaskStatus.DONE.toInt());
    }

    @Test
    public void taskType_prefixes_roundTripAndRejectUnknownValues() {
        assertEquals(TaskType.TODO, TaskType.parsePrefix("T"));
        assertEquals(TaskType.DEADLINE, TaskType.parsePrefix("D"));
        assertEquals(TaskType.EVENT, TaskType.parsePrefix("E"));
        assertEquals("T", TaskType.TODO.toString());
        assertThrows(IllegalArgumentException.class, () -> TaskType.parsePrefix("X"));
        assertThrows(IllegalArgumentException.class, () -> TaskType.parsePrefix(null));
    }

    @Test
    public void task_completionTransitions_returnWhetherStateChanged() {
        Task task = new Todo("buy milk");

        assertTrue(task.tryMarkAsDone());
        assertFalse(task.tryMarkAsDone());
        assertEquals("[T][X] buy milk", task.toString());
        assertTrue(task.tryUnmarkAsDone());
        assertFalse(task.tryUnmarkAsDone());
        assertEquals("[T][ ] buy milk", task.toString());
    }

    @Test
    public void taskFormats_allTypes_includeStatusNameAndDates() {
        Todo todo = new Todo("buy milk", TaskStatus.DONE);
        Deadline deadline = new Deadline("submit report", TaskStatus.NOT_DONE, START);
        Event event = new Event("workshop", TaskStatus.DONE, START, END);

        assertEquals("T | 1 | buy milk", todo.toSaveFormat());
        assertEquals("D | 0 | submit report | 2026-10-01 0930", deadline.toSaveFormat());
        assertEquals("E | 1 | workshop | 2026-10-01 0930 | 2026-10-01 1100",
                event.toSaveFormat());
        assertTrue(deadline.toString().contains("Oct 01 2026, 9:30 AM"));
        assertTrue(event.toString().contains("Oct 01 2026, 11:00 AM"));
    }

    @Test
    public void taskFactory_allTaskTypes_constructsCorrectSubclasses() {
        Task todo = Task.createFromData(new Task.TaskData(TaskType.TODO, TaskStatus.NOT_DONE,
                "todo", null, null));
        Task deadline = Task.createFromData(new Task.TaskData(TaskType.DEADLINE, TaskStatus.DONE,
                "deadline", START, null));
        Task event = Task.createFromData(new Task.TaskData(TaskType.EVENT, TaskStatus.NOT_DONE,
                "event", START, END));

        assertInstanceOf(Todo.class, todo);
        assertInstanceOf(Deadline.class, deadline);
        assertInstanceOf(Event.class, event);
    }

    @Test
    public void hasSameDetails_statusAndCaseDiffer_stillMatches() {
        Task first = new Deadline("Submit Report", TaskStatus.NOT_DONE, START);
        Task second = new Deadline("submit report", TaskStatus.DONE, START);

        assertTrue(first.hasSameDetails(second));
        assertFalse(first.hasSameDetails(new Deadline("submit report", END)));
        assertFalse(first.hasSameDetails(new Todo("submit report")));
        assertFalse(first.hasSameDetails(null));
    }

    @Test
    public void isDueOn_deadlineAndEvent_useExpectedDateBoundaries() {
        Deadline deadline = new Deadline("report", START);
        Event event = new Event("conference", START.minusDays(1), END.plusDays(1));
        Todo todo = new Todo("undated");

        assertTrue(deadline.isDueOn(LocalDate.of(2026, 10, 1)));
        assertFalse(deadline.isDueOn(LocalDate.of(2026, 10, 2)));
        assertTrue(event.isDueOn(LocalDate.of(2026, 9, 30)));
        assertTrue(event.isDueOn(LocalDate.of(2026, 10, 2)));
        assertFalse(event.isDueOn(LocalDate.of(2026, 10, 3)));
        assertFalse(todo.isDueOn(LocalDate.of(2026, 10, 1)));
    }

    @Test
    public void taskNameSearch_isCaseInsensitive() {
        assertTrue(new Todo("Submit Report").hasNameMatch("report"));
        assertTrue(new Todo("Submit Report").hasNameMatch("SUBMIT"));
        assertFalse(new Todo("Submit Report").hasNameMatch("slides"));
    }

    @Test
    public void item_toStringAndSearch_areCaseInsensitive() {
        Item item = new Item("Night Shift");

        assertEquals("Night Shift", item.toString());
        assertTrue(item.hasNameMatch("SHIFT"));
        assertFalse(item.hasNameMatch("day"));
    }

    @Test
    public void taskConstructors_nullRequiredValues_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Todo(null));
        assertThrows(NullPointerException.class, () ->
                new Task("task", null, TaskType.TODO));
        assertThrows(NullPointerException.class, () ->
                new Task("task", TaskStatus.NOT_DONE, null));
    }
}
