package leo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void newTask_isNotDoneByDefault() {
        Task task = new Todo("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatus());
    }

    @Test
    public void markAsDone_thenMarkAsNotDone_flipsStatusBothWays() {
        Task task = new Todo("read book");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatus());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatus());
    }

    @Test
    public void toString_todo_matchesExpectedFormat() {
        Task task = new Todo("read book");
        assertEquals("[T][ ] read book", task.toString());

        task.markAsDone();
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void toFileString_todo_matchesExpectedFormat() {
        Task task = new Todo("read book");
        assertEquals("T | 0 | read book", task.toFileString());

        task.markAsDone();
        assertEquals("T | 1 | read book", task.toFileString());
    }

    @Test
    public void deadline_toString_includesFormattedByDate() {
        Task task = new Deadline("return book", "2019-12-01 1800");
        assertEquals("[D][ ] return book (by: Dec 01 2019, 6:00PM)", task.toString());
    }

    @Test
    public void deadline_toFileString_roundTripsInputFormat() {
        Task task = new Deadline("return book", "2019-12-01 1800");
        assertEquals("D | 0 | return book | 2019-12-01 1800", task.toFileString());
    }

    @Test
    public void event_toString_includesFormattedFromAndTo() {
        Task task = new Event("project meeting", "2019-12-02 1400", "2019-12-02 1600");
        assertEquals(
                "[E][ ] project meeting (from: Dec 02 2019, 2:00PM to: Dec 02 2019, 4:00PM)",
                task.toString()
        );
    }

    @Test
    public void event_toFileString_roundTripsInputFormat() {
        Task task = new Event("project meeting", "2019-12-02 1400", "2019-12-02 1600");
        assertEquals(
                "E | 0 | project meeting | 2019-12-02 1400 | 2019-12-02 1600",
                task.toFileString()
        );
    }

    @Test
    public void deadline_invalidDateFormat_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class,
                () -> new Deadline("return book", "not-a-date"));
    }

    @Test
    public void event_invalidDateFormat_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class,
                () -> new Event("meeting", "not-a-date", "2019-12-02 1600"));
    }

    // A-MoreErrorHandling: isSameTaskAs backs TaskList's duplicate-task
    // warning, so it is tested directly here for every field it needs
    // to distinguish.
    @Test
    public void isSameTaskAs_sameTypeAndDescription_isTrueRegardlessOfDoneStatus() {
        Task a = new Todo("read book");
        Task b = new Todo("read book");
        b.markAsDone();

        assertTrue(a.isSameTaskAs(b));
        assertTrue(b.isSameTaskAs(a));
    }

    @Test
    public void isSameTaskAs_differentDescription_isFalse() {
        Task a = new Todo("read book");
        Task b = new Todo("return book");

        assertFalse(a.isSameTaskAs(b));
    }

    @Test
    public void isSameTaskAs_sameDescriptionDifferentType_isFalse() {
        Task todo = new Todo("clean room");
        Task deadline = new Deadline("clean room", "2019-12-01 1800");

        assertFalse(todo.isSameTaskAs(deadline));
    }

    @Test
    public void isSameTaskAs_deadlinesWithDifferentDueDates_isFalse() {
        Task a = new Deadline("return book", "2019-12-01 1800");
        Task b = new Deadline("return book", "2019-12-02 1800");

        assertFalse(a.isSameTaskAs(b));
    }

    @Test
    public void isSameTaskAs_null_isFalse() {
        Task a = new Todo("read book");

        assertFalse(a.isSameTaskAs(null));
    }
}
