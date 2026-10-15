package surveyprogram.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the date-range behavior of {@link Event}. */
class EventTest {
    private static final Event MULTI_DAY_EVENT = new Event(
            "conference",
            LocalDateTime.of(2026, 8, 24, 14, 0),
            LocalDateTime.of(2026, 8, 26, 10, 0));

    @Test
    void inRange_dateBeforeEvent_returnsFalse() {
        assertFalse(MULTI_DAY_EVENT.inRange(LocalDate.of(2026, 8, 23)));
    }

    @Test
    void inRange_startAndEndDates_returnsTrue() {
        assertTrue(MULTI_DAY_EVENT.inRange(LocalDate.of(2026, 8, 24)));
        assertTrue(MULTI_DAY_EVENT.inRange(LocalDate.of(2026, 8, 26)));
    }

    @Test
    void inRange_dateBetweenStartAndEnd_returnsTrue() {
        assertTrue(MULTI_DAY_EVENT.inRange(LocalDate.of(2026, 8, 25)));
    }

    @Test
    void inRange_dateAfterEvent_returnsFalse() {
        assertFalse(MULTI_DAY_EVENT.inRange(LocalDate.of(2026, 8, 27)));
    }

    @Test
    void accessors_returnConstructionDateTimes() {
        assertEquals(LocalDateTime.of(2026, 8, 24, 14, 0), MULTI_DAY_EVENT.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 26, 10, 0), MULTI_DAY_EVENT.getTo());
    }

    @Test
    void toString_includesTypeStateDescriptionAndRange() {
        assertEquals("[E][ ] conference (from: Aug 24 2026, 2:00 PM to: Aug 26 2026, 10:00 AM)",
                MULTI_DAY_EVENT.toString());
    }
}
