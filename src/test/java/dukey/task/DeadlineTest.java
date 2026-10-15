package dukey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import dukey.exception.DukeyException;

public class DeadlineTest {

    @Test
    public void toString_newDeadline_returnsDeadlineTypeStatusAndFormattedDateTime() throws DukeyException {
        Deadline deadline = new Deadline("return book", "2099-12-06 1800");

        assertEquals("[D][ ] return book (by: Dec 06 2099, 6:00pm)", deadline.toString());
    }

    @Test
    public void toFileString_newDeadline_returnsStorageFormatWithInputDateTime() throws DukeyException {
        Deadline deadline = new Deadline("return book", "2099-12-06 1800");

        assertEquals("D | 0 | return book | 2099-12-06 1800 |", deadline.toFileString());
    }

    @Test
    public void toFileString_doneDeadline_returnsStorageFormatWithDoneStatus() throws DukeyException {
        Deadline deadline = new Deadline("return book", "2099-12-06 1800");
        deadline.markAsDone(LocalDateTime.of(2026, 9, 14, 14, 30));

        assertEquals("D | 1 | return book | 2099-12-06 1800 | 2026-09-14 1430", deadline.toFileString());
    }

    @Test
    public void occursOn_matchingDate_returnsTrue() throws DukeyException {
        Deadline deadline = new Deadline("return book", "2099-12-06 1800");

        assertTrue(deadline.occursOn(LocalDate.of(2099, 12, 6)));
    }

    @Test
    public void occursOn_differentDate_returnsFalse() throws DukeyException {
        Deadline deadline = new Deadline("return book", "2099-12-06 1800");

        assertFalse(deadline.occursOn(LocalDate.of(2099, 12, 7)));
    }

    @Test
    public void constructor_pastDateTime_throwsDukeyException() {
        DukeyException exception = assertThrows(DukeyException.class, () -> {
            new Deadline("return book", "2000-12-06 1800");
        });

        assertEquals("Deadline date/time cannot be in the past.", exception.getMessage());
    }
}
