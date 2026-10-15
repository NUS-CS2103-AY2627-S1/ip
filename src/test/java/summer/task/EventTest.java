package summer.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Event#occursOn}, which reports whether a date falls within the
 * event's start and end dates, inclusive of both endpoints.
 */
public class EventTest {

    private static final Event CAMP =
            new Event("camp", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 17), false);

    @Test
    public void occursOn_dateBeforeStart_false() {
        assertFalse(CAMP.occursOn(LocalDate.of(2019, 10, 14)));
    }

    @Test
    public void occursOn_startDate_true() {
        assertTrue(CAMP.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_dateWithinRange_true() {
        assertTrue(CAMP.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void occursOn_endDate_true() {
        assertTrue(CAMP.occursOn(LocalDate.of(2019, 10, 17)));
    }

    @Test
    public void occursOn_dateAfterEnd_false() {
        assertFalse(CAMP.occursOn(LocalDate.of(2019, 10, 18)));
    }

    @Test
    public void occursOn_singleDayEvent_matchesOnlyThatDay() {
        Event meeting =
                new Event("meeting", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1), false);
        assertTrue(meeting.occursOn(LocalDate.of(2020, 1, 1)));
        assertFalse(meeting.occursOn(LocalDate.of(2019, 12, 31)));
        assertFalse(meeting.occursOn(LocalDate.of(2020, 1, 2)));
    }
}
