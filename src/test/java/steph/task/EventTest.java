package steph.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Event#clashesWith(Event)}.
 *
 * <p>The rules being pinned down: ranges are half-open ({@code [from, to)}),
 * so back-to-back events do not clash; and an end date-time that falls
 * exactly on midnight is treated as running through the end of that day
 * (see {@link Event#clashesWith(Event)}'s Javadoc), regardless of whether the
 * other boundary involved is timed or also date-only.
 */
public class EventTest {

    @Test
    public void clashesWith_noOverlap_returnsFalse() {
        Event morning = new Event("morning", at(2019, 10, 15, 9, 0), at(2019, 10, 15, 10, 0));
        Event afternoon = new Event("afternoon", at(2019, 10, 15, 14, 0), at(2019, 10, 15, 15, 0));

        assertFalse(morning.clashesWith(afternoon));
    }

    @Test
    public void clashesWith_exactSameRange_returnsTrue() {
        Event a = new Event("a", at(2019, 10, 15, 9, 0), at(2019, 10, 15, 10, 0));
        Event b = new Event("b", at(2019, 10, 15, 9, 0), at(2019, 10, 15, 10, 0));

        assertTrue(a.clashesWith(b));
    }

    @Test
    public void clashesWith_partialOverlap_returnsTrue() {
        Event a = new Event("a", at(2019, 10, 15, 9, 0), at(2019, 10, 15, 11, 0));
        Event b = new Event("b", at(2019, 10, 15, 10, 0), at(2019, 10, 15, 12, 0));

        assertTrue(a.clashesWith(b));
    }

    @Test
    public void clashesWith_backToBack_returnsFalse() {
        // a ends exactly when b starts; neither boundary is midnight.
        Event a = new Event("a", at(2019, 10, 15, 14, 0), at(2019, 10, 15, 16, 0));
        Event b = new Event("b", at(2019, 10, 15, 16, 0), at(2019, 10, 15, 18, 0));

        assertFalse(a.clashesWith(b));
    }

    @Test
    public void clashesWith_bothDateOnlySameDay_returnsTrue() {
        // A date-only "event on 2019-10-15" is from == to == midnight; without
        // expanding the end boundary this would be an empty, unclashable range.
        Event a = new Event("a", at(2019, 10, 15, 0, 0), at(2019, 10, 15, 0, 0));
        Event b = new Event("b", at(2019, 10, 15, 0, 0), at(2019, 10, 15, 0, 0));

        assertTrue(a.clashesWith(b));
    }

    @Test
    public void clashesWith_dateOnlyVsTimedSameDay_returnsTrue() {
        Event dateOnly = new Event("date-only", at(2019, 10, 15, 0, 0), at(2019, 10, 15, 0, 0));
        Event timed = new Event("timed", at(2019, 10, 15, 12, 0), at(2019, 10, 15, 13, 0));

        assertTrue(dateOnly.clashesWith(timed));
    }

    @Test
    public void clashesWith_dateOnlyVsTimedDifferentDay_returnsFalse() {
        Event dateOnly = new Event("date-only", at(2019, 10, 15, 0, 0), at(2019, 10, 15, 0, 0));
        Event timed = new Event("timed", at(2019, 10, 16, 12, 0), at(2019, 10, 16, 13, 0));

        assertFalse(dateOnly.clashesWith(timed));
    }

    @Test
    public void clashesWith_dateOnlyEndAfterTimedStart_returnsTrue() {
        // "/from 2019-10-15 0900 /to 2019-10-16" treats the date-only end as
        // running through all of Oct 16, so it clashes with something later
        // that same day.
        Event conference = new Event("conference", at(2019, 10, 15, 9, 0), at(2019, 10, 16, 0, 0));
        Event eveningOnThe16th = new Event("dinner", at(2019, 10, 16, 18, 0), at(2019, 10, 16, 19, 0));

        assertTrue(conference.clashesWith(eveningOnThe16th));
    }

    @Test
    public void clashesWith_isSymmetric_orderDoesNotMatter() {
        Event a = new Event("a", at(2019, 10, 15, 9, 0), at(2019, 10, 15, 11, 0));
        Event b = new Event("b", at(2019, 10, 15, 10, 0), at(2019, 10, 15, 12, 0));

        assertTrue(a.clashesWith(b));
        assertTrue(b.clashesWith(a));
    }

    private static LocalDateTime at(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute);
    }
}
