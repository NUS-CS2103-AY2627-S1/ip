package luigibot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests date validation, formatting, and matching in {@link Deadline}.
 */
public class DeadlineTest {

    @Test
    public void constructor_invalidDate_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                new Deadline("return book", "2026-02-30 1800"));
    }

    @Test
    public void constructor_invalidTime_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                new Deadline("return book", "2026-08-27 2500"));
    }

    @Test
    public void toString_incompleteAndCompletedDeadline_correctDisplayReturned() {
        Deadline deadline = new Deadline("return book", "2026-08-27 1800");

        assertEquals("[D][ ] return book (by: Aug 27 2026, 6:00 PM)", deadline.toString());

        deadline.mark();

        assertEquals("[D][X] return book (by: Aug 27 2026, 6:00 PM)", deadline.toString());
    }

    @Test
    public void toFileString_incompleteAndCompletedDeadline_correctStorageFormatReturned() {
        Deadline deadline = new Deadline("return book", "2026-08-27 1800");

        assertEquals("D | 0 | return book | 2026-08-27 1800", deadline.toFileString());

        deadline.mark();

        assertEquals("D | 1 | return book | 2026-08-27 1800", deadline.toFileString());
    }

    @Test
    public void occursOn_dueDate_trueReturned() {
        Deadline deadline = new Deadline("return book", "2026-08-27 1800");

        assertTrue(deadline.occursOn(LocalDate.of(2026, 8, 27)));
    }

    @Test
    public void occursOn_otherDate_falseReturned() {
        Deadline deadline = new Deadline("return book", "2026-08-27 1800");

        assertFalse(deadline.occursOn(LocalDate.of(2026, 8, 28)));
    }
}
