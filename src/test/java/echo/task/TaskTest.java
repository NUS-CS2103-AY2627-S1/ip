package echo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import echo.EchoException;

/** Tests task model behaviors, status changes, string representations, and equality logic. */
public class TaskTest {

    @Test
    public void task_basicOperations_workCorrectly() {
        Task task = new Task("base task");

        assertEquals("base task", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] base task", task.toString());
        assertEquals("T | 0 | base task", task.toFileFormat());
        assertEquals(Optional.empty(), task.getDateTime());

        task.mark();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] base task", task.toString());
        assertEquals("T | 1 | base task", task.toFileFormat());

        task.unmark();
        assertFalse(task.isDone());
    }

    @Test
    public void task_isDuplicate_comparesCorrectly() {
        Task task1 = new Task("Task Name");
        Task task2 = new Task("task name");
        Task task3 = new Task("Different");
        TodoTask todo = new TodoTask("Task Name");

        assertTrue(task1.isDuplicate(task2));
        assertFalse(task1.isDuplicate(task3));
        assertFalse(task1.isDuplicate(null));
        assertFalse(task1.isDuplicate(todo));
    }

    @Test
    public void todoTask_formatAndDuplicate_behaveCorrectly() {
        TodoTask todo1 = new TodoTask("read book");
        TodoTask todo2 = new TodoTask("READ BOOK");
        TodoTask todo3 = new TodoTask("write paper");

        assertEquals("[To-Do][ ] read book", todo1.toString());
        assertEquals("T | 0 | read book", todo1.toFileFormat());
        assertTrue(todo1.isDuplicate(todo2));
        assertFalse(todo1.isDuplicate(todo3));
    }

    @Test
    public void deadlineTask_dateOnly_formatsCorrectly() {
        DeadlineTask deadline = new DeadlineTask("submit report", "15-10-2026");

        assertEquals("[Deadline][ ] submit report (by: Oct 15 2026)", deadline.toString());
        assertEquals("D | 0 | submit report | 15-10-2026", deadline.toFileFormat());
        assertEquals(Optional.of(LocalDateTime.of(2026, 10, 15, 0, 0)), deadline.getDateTime());
    }

    @Test
    public void deadlineTask_dateTime_formatsCorrectly() {
        DeadlineTask deadline = new DeadlineTask("submit report", "15-10-2026 23:59");

        assertEquals("[Deadline][ ] submit report (by: Oct 15 2026, 11:59pm)", deadline.toString());
        assertEquals("D | 0 | submit report | 15-10-2026 23:59", deadline.toFileFormat());
        assertEquals(Optional.of(LocalDateTime.of(2026, 10, 15, 23, 59)), deadline.getDateTime());
    }

    @Test
    public void deadlineTask_isDuplicate_differentiatesDatesAndTimes() {
        DeadlineTask d1 = new DeadlineTask("report", "15-10-2026 14:00");
        DeadlineTask d2 = new DeadlineTask("REPORT", "15-10-2026 14:00");
        DeadlineTask diffDate = new DeadlineTask("report", "16-10-2026 14:00");
        DeadlineTask diffTime = new DeadlineTask("report", "15-10-2026 16:00");
        DeadlineTask noTime = new DeadlineTask("report", "15-10-2026");
        TodoTask todo = new TodoTask("report");

        assertTrue(d1.isDuplicate(d2));
        assertFalse(d1.isDuplicate(diffDate));
        assertFalse(d1.isDuplicate(diffTime));
        assertFalse(d1.isDuplicate(noTime));
        assertFalse(d1.isDuplicate(todo));
    }

    @Test
    public void eventTask_dateOnly_formatsCorrectly() throws EchoException {
        EventTask event = new EventTask("camp", "10-10-2026", "12-10-2026");

        assertEquals("[Event][ ] camp (from: Oct 10 2026, to: Oct 12 2026)", event.toString());
        assertEquals("E | 0 | camp | 10-10-2026 | 12-10-2026", event.toFileFormat());
        assertEquals(Optional.of(LocalDateTime.of(2026, 10, 10, 0, 0)), event.getDateTime());
    }

    @Test
    public void eventTask_dateTime_formatsCorrectly() throws EchoException {
        EventTask event = new EventTask("meeting", "10-10-2026 10:00", "10-10-2026 12:00");

        assertEquals("[Event][ ] meeting (from: Oct 10 2026, 10:00am, to: Oct 10 2026, 12:00pm)",
                event.toString());
        assertEquals("E | 0 | meeting | 10-10-2026 10:00 | 10-10-2026 12:00", event.toFileFormat());
    }

    @Test
    public void eventTask_invalidChronology_throwsEchoException() {
        assertThrows(EchoException.class, () ->
                new EventTask("invalid", "15-10-2026", "10-10-2026"));
    }

    @Test
    public void eventTask_isDuplicate_differentiatesFields() throws EchoException {
        EventTask e1 = new EventTask("seminar", "10-10-2026 09:00", "10-10-2026 17:00");
        EventTask e2 = new EventTask("SEMINAR", "10-10-2026 09:00", "10-10-2026 17:00");
        EventTask diffStart = new EventTask("seminar", "11-10-2026 09:00", "11-10-2026 17:00");
        EventTask diffEnd = new EventTask("seminar", "10-10-2026 09:00", "10-10-2026 18:00");
        TodoTask todo = new TodoTask("seminar");

        assertTrue(e1.isDuplicate(e2));
        assertFalse(e1.isDuplicate(diffStart));
        assertFalse(e1.isDuplicate(diffEnd));
        assertFalse(e1.isDuplicate(todo));
    }

    @Test
    public void sortCriteria_enumConstants_exist() {
        assertEquals(SortCriteria.DATE, SortCriteria.valueOf("DATE"));
        assertEquals(SortCriteria.NAME, SortCriteria.valueOf("NAME"));
        assertEquals(2, SortCriteria.values().length);
    }
}
