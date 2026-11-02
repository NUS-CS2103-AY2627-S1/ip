package megia.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {
    private static final Event MULTI_DAY_EVENT = new Event(
            "conference",
            LocalDateTime.of(2024, 6, 10, 9, 0),
            LocalDateTime.of(2024, 6, 12, 17, 0));

    @Test
    void occursOn_startDate_returnsTrue() {
        assertTrue(MULTI_DAY_EVENT.occursOn(LocalDate.of(2024, 6, 10)));
    }

    @Test
    void occursOn_endDate_returnsTrue() {
        assertTrue(MULTI_DAY_EVENT.occursOn(LocalDate.of(2024, 6, 12)));
    }

    @Test
    void occursOn_dateBetweenEndpoints_returnsTrue() {
        assertTrue(MULTI_DAY_EVENT.occursOn(LocalDate.of(2024, 6, 11)));
    }

    @Test
    void occursOn_dateBeforeStart_returnsFalse() {
        assertFalse(MULTI_DAY_EVENT.occursOn(LocalDate.of(2024, 6, 9)));
    }

    @Test
    void occursOn_dateAfterEnd_returnsFalse() {
        assertFalse(MULTI_DAY_EVENT.occursOn(LocalDate.of(2024, 6, 13)));
    }

    @Test
    void occursOn_sameDayEventDate_returnsTrue() {
        Event sameDayEvent = new Event(
                "meeting",
                LocalDateTime.of(2024, 6, 10, 14, 0),
                LocalDateTime.of(2024, 6, 10, 16, 0));

        assertTrue(sameDayEvent.occursOn(LocalDate.of(2024, 6, 10)));
    }

    @Test
    void occursOn_sameDayEventAdjacentDate_returnsFalse() {
        Event sameDayEvent = new Event(
                "meeting",
                LocalDateTime.of(2024, 6, 10, 14, 0),
                LocalDateTime.of(2024, 6, 10, 16, 0));

        assertFalse(sameDayEvent.occursOn(LocalDate.of(2024, 6, 9)));
        assertFalse(sameDayEvent.occursOn(LocalDate.of(2024, 6, 11)));
    }
}
