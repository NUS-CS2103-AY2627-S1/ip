package choo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Checks the shared and type-specific behavior of the task hierarchy.
 */
public class TaskTest {
    @Test
    void toString_eachTaskSubtype_includesTypeAndDetails() {
        Task todo = new Todo("borrow book");
        Deadline deadline = new Deadline("return book", "2019-12-02");
        Deadline timedDeadline = new Deadline("submit report", "2019-12-02 1800");
        Deadline midnightDeadline = new Deadline("start day", "2019-12-02 0000");
        Deadline leapDayDeadline = new Deadline("leap day", "2020-02-29 2359");
        Task event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("[T][ ] borrow book", todo.toString());
        assertEquals("[D][ ] return book (by: Dec 2 2019)", deadline.toString());
        assertEquals("[D][ ] submit report (by: Dec 2 2019, 6:00PM)",
                timedDeadline.toString());
        assertEquals("[D][ ] start day (by: Dec 2 2019, 12:00AM)",
                midnightDeadline.toString());
        assertEquals("[D][ ] leap day (by: Feb 29 2020, 11:59PM)",
                leapDayDeadline.toString());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getDueDateTime());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    void markAndUnmark_updatesCompletionState() {
        Task task = new Todo("borrow book");
        assertFalse(task.isDone());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] borrow book", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    void deadline_invalidDates_throwDateTimeParseException() {
        String[] invalidDates = {
            "2019-12-02 2400",
            "2019-12-02 2360",
            "2019-1-02",
            "2019-02-29",
            "+12345-01-01",
            "-0001-01-01"
        };
        for (String invalidDate : invalidDates) {
            assertThrows(DateTimeParseException.class, () -> new Deadline("invalid", invalidDate), invalidDate);
        }
    }
}
