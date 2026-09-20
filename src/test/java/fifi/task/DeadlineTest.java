package fifi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the displayed and saved forms of deadline tasks.
 */
public class DeadlineTest {

    @Test
    public void toString_unmarkedDeadline_deadlineStringReturned() {
        Deadline deadline = new Deadline(false, "return book", LocalDate.of(2019, 12, 2));

        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    public void toString_markedDeadline_markedDeadlineStringReturned() {
        Deadline deadline = new Deadline(true, "return book", LocalDate.of(2019, 12, 2));

        assertEquals("[D][X] return book (by: Dec 02 2019)", deadline.toString());
    }

    @Test
    public void toFileString_unmarkedDeadline_deadlineStorageStringReturned() {
        Deadline deadline = new Deadline(false, "return book", LocalDate.of(2019, 12, 2),
                LocalDateTime.of(2019, 11, 30, 9, 0), null);

        assertEquals("D | 0 | return book | 2019-12-02 | 2019-11-30T09:00:00 | -", deadline.toFileString());
    }

    @Test
    public void toFileString_markedDeadline_markedDeadlineStorageStringReturned() {
        Deadline deadline = new Deadline(true, "return book", LocalDate.of(2019, 12, 2),
                LocalDateTime.of(2019, 11, 30, 9, 0), LocalDateTime.of(2019, 12, 1, 18, 0));

        assertEquals("D | 1 | return book | 2019-12-02 | 2019-11-30T09:00:00 | 2019-12-01T18:00:00",
                deadline.toFileString());
    }

    @Test
    public void occursOn_dueDateAndOtherDate_trueThenFalse() {
        Deadline deadline = new Deadline(false, "return book", LocalDate.of(2019, 12, 2));

        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 1)));
        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 3)));
    }
}
