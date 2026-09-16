package lebronjames.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import lebronjames.LebronJamesException;

/**
 * Tests {@link Deadline}, which shows and stores the single date a task is due.
 */
public class DeadlineTest {
    /**
     * Builds a deadline due at the given date or time.
     *
     * @param by Date, optionally with a time, in any accepted format.
     * @return Deadline under test.
     * @throws LebronJamesException If the date cannot be parsed.
     */
    private static Deadline deadlineDue(String by) throws LebronJamesException {
        return new Deadline("return book", TaskDateTime.parse(by));
    }

    @Test
    public void toString_dateOnly_noTimeShown() throws LebronJamesException {
        assertEquals("[D][ ] return book (by: Jun 06 2019)", deadlineDue("2019-06-06").toString());
    }

    @Test
    public void toString_dateAndTime_timeShown() throws LebronJamesException {
        assertEquals("[D][ ] return book (by: Jun 06 2019, 6:00pm)",
                deadlineDue("2019-06-06 1800").toString());
    }

    @Test
    public void toString_done_statusShown() throws LebronJamesException {
        Deadline deadline = deadlineDue("2019-06-06");

        deadline.markAsDone();

        assertEquals("[D][X] return book (by: Jun 06 2019)", deadline.toString());
    }

    @Test
    public void toFileFormat_dateOnly_fourFields() throws LebronJamesException {
        assertEquals("D | 0 | return book | 2019-06-06", deadlineDue("2019-06-06").toFileFormat());
    }

    @Test
    public void toFileFormat_dateTypedWithSlashes_writtenInCanonicalForm() throws LebronJamesException {
        // Whatever the user typed, the file always holds yyyy-MM-dd.
        assertEquals("D | 0 | return book | 2019-06-06", deadlineDue("6/6/2019").toFileFormat());
    }

    @Test
    public void toFileFormat_dateAndTime_timeIncluded() throws LebronJamesException {
        assertEquals("D | 1 | return book | 2019-06-06 1800", markedDone(deadlineDue("2019-06-06 1800")));
    }

    /**
     * Marks a deadline as done and returns its save-file form.
     *
     * @param deadline Deadline to mark.
     * @return Save-file representation after marking.
     */
    private static String markedDone(Deadline deadline) {
        deadline.markAsDone();
        return deadline.toFileFormat();
    }

    @Test
    public void isOn_sameDate_true() throws LebronJamesException {
        assertTrue(deadlineDue("2019-06-06").isOn(LocalDate.of(2019, 6, 6)));
    }

    @Test
    public void isOn_differentDate_false() throws LebronJamesException {
        Deadline deadline = deadlineDue("2019-06-06");

        assertFalse(deadline.isOn(LocalDate.of(2019, 6, 5)));
        assertFalse(deadline.isOn(LocalDate.of(2019, 6, 7)));
    }

    @Test
    public void isOn_sameDateDifferentTime_true() throws LebronJamesException {
        // The time of day must not affect which day the deadline falls on.
        assertTrue(deadlineDue("2019-06-06 2359").isOn(LocalDate.of(2019, 6, 6)));
        assertTrue(deadlineDue("2019-06-06 0000").isOn(LocalDate.of(2019, 6, 6)));
    }

    @Test
    public void equals_sameDescriptionAndDate_equalWithMatchingHashCode() throws LebronJamesException {
        assertEquals(deadlineDue("2019-06-06"), deadlineDue("2019-06-06"));
        assertEquals(deadlineDue("2019-06-06").hashCode(), deadlineDue("2019-06-06").hashCode());
    }

    @Test
    public void equals_sameDescriptionDifferentDate_notEqual() throws LebronJamesException {
        // A repeating chore is a real case: same words, genuinely two tasks.
        assertNotEquals(deadlineDue("2019-06-06"), deadlineDue("2019-06-13"));
    }
}
