package peter.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Deadline}.
 *
 * <p>The interesting behavior is that {@link Deadline#occursOn(LocalDate)}
 * compares dates only, so a deadline at any time of day still matches its own
 * date. {@code toDataString} is covered because it defines the save format.
 */
public class DeadlineTest {

    /** A deadline due at 18:00, used where the time of day matters. */
    private static Deadline eveningDeadline() {
        return new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
    }

    // =====================================================================
    // occursOn(LocalDate)
    // =====================================================================

    @Test
    public void occursOn_dueDate_trueReturned() {
        assertTrue(eveningDeadline().occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_dayBeforeDueDate_falseReturned() {
        assertFalse(eveningDeadline().occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_dayAfterDueDate_falseReturned() {
        // A deadline matches only its own date; it is not treated as ongoing.
        assertFalse(eveningDeadline().occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_dueDateWithLateTime_timeIgnored() {
        // 23:59 is still the same date, so the time must not affect matching.
        Deadline lateDeadline = new Deadline("submit", LocalDateTime.of(2019, 12, 2, 23, 59));
        assertTrue(lateDeadline.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(lateDeadline.occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_dueAtMidnight_timeIgnored() {
        // The opposite boundary of the same day.
        Deadline midnightDeadline = new Deadline("submit", LocalDateTime.of(2019, 12, 2, 0, 0));
        assertTrue(midnightDeadline.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(midnightDeadline.occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_sameDayDifferentMonth_falseReturned() {
        // Guards against comparing only part of the date.
        assertFalse(eveningDeadline().occursOn(LocalDate.of(2019, 11, 2)));
    }

    @Test
    public void occursOn_sameDateDifferentYear_falseReturned() {
        assertFalse(eveningDeadline().occursOn(LocalDate.of(2020, 12, 2)));
    }

    // =====================================================================
    // hasKeyword(String)
    // =====================================================================

    @Test
    public void hasKeyword_keywordInDescription_trueReturned() {
        assertTrue(eveningDeadline().hasKeyword("book"));
    }

    @Test
    public void hasKeyword_keywordOnlyInDueDate_falseReturned() {
        // Only the description is searched. A deadline due in 2019 must not
        // be found by searching for "2019", or every dated task would match.
        assertFalse(eveningDeadline().hasKeyword("2019"));
        assertFalse(eveningDeadline().hasKeyword("Dec"));
    }

    // =====================================================================
    // toDataString()
    // =====================================================================

    @Test
    public void toDataString_notDone_zeroStatusWritten() {
        assertEquals("D | 0 | return book | 2019-12-02T18:00",
                eveningDeadline().toDataString());
    }

    @Test
    public void toDataString_done_oneStatusWritten() {
        Deadline deadline = eveningDeadline();
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-12-02T18:00", deadline.toDataString());
    }

    @Test
    public void toDataString_midnightDueDate_isoTimeWritten() {
        assertEquals("D | 0 | return book | 2019-12-02T00:00",
                new Deadline("return book", LocalDateTime.of(2019, 12, 2, 0, 0))
                        .toDataString());
    }

    // =====================================================================
    // getScheduleDetails() and type icon
    // =====================================================================

    @Test
    public void getScheduleDetails_dueDateWithTime_timeShown() {
        assertEquals(" (by: Dec 2 2019, 6:00 PM)", eveningDeadline().getScheduleDetails());
    }

    @Test
    public void getScheduleDetails_dueAtMidnight_dateOnlyShown() {
        // A deadline entered as a bare date should not display "12:00 AM".
        assertEquals(" (by: Dec 2 2019)",
                new Deadline("return book", LocalDateTime.of(2019, 12, 2, 0, 0))
                        .getScheduleDetails());
    }

    @Test
    public void getTaskTypeIcon_deadline_dIconReturned() {
        assertEquals("D", eveningDeadline().getTaskTypeIcon());
    }
}
