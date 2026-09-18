package lune.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

/**
 * Tests every method Deadline itself defines or overrides:
 * {@link Deadline#occursOn(LocalDate)}, {@link Deadline#toString()}, and
 * {@link Deadline#toSaveFormat()}.
 */
public class DeadlineTest {

    private static final LocalDateTime BY = LocalDateTime.of(2019, 6, 6, 0, 0);

    // Mirrors Task's private display formatter, built independently so
    // these tests don't depend on the JVM's default locale for AM/PM casing.
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    // --- occursOn ---

    @Test
    public void occursOn_dateMatchesExactly_trueReturned() {
        Deadline deadline = new Deadline("return book", BY);
        assertTrue(deadline.occursOn(LocalDate.of(2019, 6, 6)));
    }

    @Test
    public void occursOn_dateBefore_falseReturned() {
        Deadline deadline = new Deadline("return book", BY);
        assertFalse(deadline.occursOn(LocalDate.of(2019, 6, 5)));
    }

    @Test
    public void occursOn_dateAfter_falseReturned() {
        Deadline deadline = new Deadline("return book", BY);
        assertFalse(deadline.occursOn(LocalDate.of(2019, 6, 7)));
    }

    @Test
    public void occursOn_deadlineWithTimeComponent_timeOfDayIgnored() {
        Deadline deadline = new Deadline("pay rent", LocalDateTime.of(2019, 6, 6, 18, 0));
        assertTrue(deadline.occursOn(LocalDate.of(2019, 6, 6)));
    }

    // --- toString ---

    @Test
    public void toString_notDoneDeadlineWithMidnightTime_dateOnlyDisplayed() {
        Deadline deadline = new Deadline("return book", BY);
        assertEquals("[D][ ] return book (by: " + BY.format(DATE_FORMAT) + ")", deadline.toString());
    }

    @Test
    public void toString_doneDeadline_statusIconXShown() {
        Deadline deadline = new Deadline("return book", BY);
        deadline.markAsDone();
        assertEquals("[D][X] return book (by: " + BY.format(DATE_FORMAT) + ")", deadline.toString());
    }

    @Test
    public void toString_deadlineWithTimeComponent_dateAndTimeDisplayed() {
        LocalDateTime by = LocalDateTime.of(2019, 12, 2, 18, 0);
        Deadline deadline = new Deadline("pay rent", by);
        assertEquals("[D][ ] pay rent (by: " + by.format(DATE_TIME_FORMAT) + ")", deadline.toString());
    }

    // --- toSaveFormat ---

    @Test
    public void toSaveFormat_notDoneDeadline_pipeSeparatedLineWithZeroFlag() {
        Deadline deadline = new Deadline("return book", BY);
        assertEquals("D | 0 | return book | " + BY, deadline.toSaveFormat());
    }

    @Test
    public void toSaveFormat_doneDeadline_pipeSeparatedLineWithOneFlag() {
        Deadline deadline = new Deadline("return book", BY);
        deadline.markAsDone();
        assertEquals("D | 1 | return book | " + BY, deadline.toSaveFormat());
    }
}
