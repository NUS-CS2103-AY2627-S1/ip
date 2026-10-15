package noms.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Event#occursOn(LocalDate)}, which decides whether an event
 * spans a given date. The range check is inclusive of both endpoints, a
 * boundary condition that is easy to get wrong (off-by-one at the start or
 * end), so each boundary is covered explicitly.
 */
public class EventTest {

    private final Event event = new Event("conference",
            LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 8));

    @Test
    public void occursOn_startDate_returnsTrue() {
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 6)));
    }

    @Test
    public void occursOn_endDate_returnsTrue() {
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 8)));
    }

    @Test
    public void occursOn_dateInsideRange_returnsTrue() {
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 7)));
    }

    @Test
    public void occursOn_dayBeforeStart_returnsFalse() {
        assertFalse(event.occursOn(LocalDate.of(2019, 8, 5)));
    }

    @Test
    public void occursOn_dayAfterEnd_returnsFalse() {
        assertFalse(event.occursOn(LocalDate.of(2019, 8, 9)));
    }

    @Test
    public void constructor_sameStartAndEndDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("standup",
                        LocalDate.of(2019, 8, 6), LocalDate.of(2019, 8, 6)));
    }

    @Test
    public void rescheduleFrom_newStart_preservesDuration() {
        event.rescheduleFrom(LocalDate.of(2026, 9, 20));

        assertEquals("[E][ ] conference (from: Sep 20 2026 to: Sep 22 2026)",
                event.toString());
    }

    @Test
    public void reschedule_newDates_replacesBothDates() {
        event.reschedule(LocalDate.of(2026, 9, 20), LocalDate.of(2026, 10, 1));

        assertEquals("[E][ ] conference (from: Sep 20 2026 to: Oct 01 2026)",
                event.toString());
    }

    @Test
    public void constructor_invalidDateRange_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("conference",
                        LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 1)));
    }

    @Test
    public void reschedule_invalidDateRange_throwsAndPreservesDates() {
        assertThrows(IllegalArgumentException.class,
                () -> event.reschedule(
                        LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 5)));
        assertEquals("[E][ ] conference (from: Aug 06 2019 to: Aug 08 2019)",
                event.toString());
    }
}
