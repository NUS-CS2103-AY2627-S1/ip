package bott.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Deadline}'s two textual representations: {@code toString}
 * (display format "MMM dd yyyy") and {@code toFileFormat} (save format,
 * plain ISO "yyyy-MM-dd" via {@link LocalDate#toString()}).
 */
public class DeadlineTest {

    @Test
    public void toString_notDone_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_done_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void toString_singleDigitDay_padsWithZero() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 8, 6));
        assertEquals("[D][ ] return book (by: Aug 06 2019)", deadline.toString());
    }

    @Test
    public void toString_december_correctMonthAbbreviation() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 25));
        assertEquals("[D][ ] return book (by: Dec 25 2019)", deadline.toString());
    }

    @Test
    public void toFileFormat_notDone_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toFileFormat());
    }

    @Test
    public void toFileFormat_done_correctFormat() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 10, 15));
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-10-15", deadline.toFileFormat());
    }
}
