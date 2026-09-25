package shan.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {
    private static final LocalDate EVENT_START_DATE = LocalDate.of(2019, 12, 3);
    private static final LocalDate EVENT_END_DATE = LocalDate.of(2019, 12, 5);

    @Test
    void constructor_endNotAfterStart_assertionThrown() {
        LocalDateTime startDate = EVENT_START_DATE.atTime(14, 0);

        assertThrows(
                AssertionError.class, () -> new Event("project meeting", startDate, startDate));
    }

    @Test
    void occursBetween_rangeOverlapsEventStart_returnsTrue() {
        Event event = createEvent();

        boolean result = event.occursBetween(
                EVENT_START_DATE.minusDays(1), EVENT_START_DATE);

        assertTrue(result);
    }

    @Test
    void occursBetween_rangeOverlapsEventEnd_returnsTrue() {
        Event event = createEvent();

        boolean result = event.occursBetween(
                EVENT_END_DATE, EVENT_END_DATE.plusDays(1));

        assertTrue(result);
    }

    @Test
    void occursBetween_rangeInsideEvent_returnsTrue() {
        Event event = createEvent();

        assertTrue(event.occursBetween(
                EVENT_START_DATE.plusDays(1), EVENT_START_DATE.plusDays(1)));
    }

    @Test
    void occursBetween_rangeOutsideEvent_returnsFalse() {
        Event event = createEvent();

        assertFalse(event.occursBetween(
                EVENT_START_DATE.minusDays(2), EVENT_START_DATE.minusDays(1)));
        assertFalse(event.occursBetween(
                EVENT_END_DATE.plusDays(1), EVENT_END_DATE.plusDays(2)));
    }

    private Event createEvent() {
        return new Event(
                "project meeting",
                EVENT_START_DATE.atTime(14, 0),
                EVENT_END_DATE.atTime(16, 0));
    }
}
