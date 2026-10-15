package larry.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests date matching for events.
 */
class EventTest {
    @Test
    void constructor_endNotAfterStart_exceptionThrown() {
        LocalDateTime startTime = LocalDateTime.of(2026, 9, 6, 10, 0);

        assertThrows(IllegalArgumentException.class, () ->
                new Event("backwards", startTime, startTime.minusHours(1)));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("instant", startTime, startTime));
    }

    @Test
    void occursOn_multiDayEvent_startMiddleAndEndDatesTrue() {
        Event event = new Event("conference",
                LocalDateTime.of(2026, 9, 5, 9, 0),
                LocalDateTime.of(2026, 9, 7, 17, 0));

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 5)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 6)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 7)));
    }

    @Test
    void occursOn_dateOutsideEventRange_false() {
        Event event = new Event("conference",
                LocalDateTime.of(2026, 9, 5, 9, 0),
                LocalDateTime.of(2026, 9, 7, 17, 0));

        assertFalse(event.occursOn(LocalDate.of(2026, 9, 4)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 8)));
    }

    @Test
    void occursOn_singleDayEvent_onlyEventDateTrue() {
        Event event = new Event("meeting",
                LocalDateTime.of(2026, 9, 6, 9, 0),
                LocalDateTime.of(2026, 9, 6, 10, 0));

        assertFalse(event.occursOn(LocalDate.of(2026, 9, 5)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 6)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 7)));
    }

    @Test
    void occursOn_legacyStartOrEndDate_false() {
        TaskDateTime parsedDateTime = new TaskDateTime(LocalDateTime.of(2026, 9, 6, 9, 0));
        TaskDateTime legacyDateTime = TaskDateTime.fromStorageString("Sunday morning");
        Event legacyStartEvent = new Event("meeting", legacyDateTime, parsedDateTime);
        Event legacyEndEvent = new Event("meeting", parsedDateTime, legacyDateTime);

        assertFalse(legacyStartEvent.occursOn(LocalDate.of(2026, 9, 6)));
        assertFalse(legacyEndEvent.occursOn(LocalDate.of(2026, 9, 6)));
    }

    @Test
    void occursOn_nullDate_nullPointerExceptionThrown() {
        Event event = new Event("meeting",
                LocalDateTime.of(2026, 9, 6, 9, 0),
                LocalDateTime.of(2026, 9, 6, 10, 0));

        assertThrows(NullPointerException.class, () -> event.occursOn(null));
    }

    @Test
    void updateEndpoints_validValues_onlyRequestedEndpointChanged() {
        Event event = new Event("meeting",
                LocalDateTime.of(2026, 9, 6, 9, 0),
                LocalDateTime.of(2026, 9, 6, 10, 0));

        event.updateStartDateTime("6-9-2026 0830");
        assertEquals(LocalDateTime.of(2026, 9, 6, 8, 30),
                event.getStartDateTime().getValue().orElseThrow());
        assertEquals(LocalDateTime.of(2026, 9, 6, 10, 0),
                event.getEndDateTime().getValue().orElseThrow());

        event.updateEndDateTime("6-9-2026 1100");
        assertEquals(LocalDateTime.of(2026, 9, 6, 8, 30),
                event.getStartDateTime().getValue().orElseThrow());
        assertEquals(LocalDateTime.of(2026, 9, 6, 11, 0),
                event.getEndDateTime().getValue().orElseThrow());
    }

    @Test
    void updateEndpoints_invalidOrder_eventUnchanged() {
        LocalDateTime originalStart = LocalDateTime.of(2026, 9, 6, 9, 0);
        LocalDateTime originalEnd = LocalDateTime.of(2026, 9, 6, 10, 0);
        Event event = new Event("meeting", originalStart, originalEnd);

        assertThrows(IllegalArgumentException.class, () ->
                event.updateStartDateTime("6-9-2026 1000"));
        assertEquals(originalStart, event.getStartDateTime().getValue().orElseThrow());
        assertEquals(originalEnd, event.getEndDateTime().getValue().orElseThrow());

        assertThrows(IllegalArgumentException.class, () ->
                event.updateEndDateTime("6-9-2026 0859"));
        assertEquals(originalStart, event.getStartDateTime().getValue().orElseThrow());
        assertEquals(originalEnd, event.getEndDateTime().getValue().orElseThrow());
    }

    @Test
    void updateEndpoints_legacyValues_gradualRepairAllowedAndThenOrderingEnforced() {
        Event legacyEndEvent = new Event("meeting",
                new TaskDateTime(LocalDateTime.of(2026, 9, 6, 9, 0)),
                TaskDateTime.fromStorageString("later that morning"));
        legacyEndEvent.updateStartDateTime("6-9-2026 0930");
        legacyEndEvent.updateEndDateTime("6-9-2026 1000");
        assertEquals(LocalDateTime.of(2026, 9, 6, 10, 0),
                legacyEndEvent.getEndDateTime().getValue().orElseThrow());

        Event legacyStartEvent = new Event("meeting",
                TaskDateTime.fromStorageString("early that morning"),
                new TaskDateTime(LocalDateTime.of(2026, 9, 6, 10, 0)));
        legacyStartEvent.updateEndDateTime("6-9-2026 1030");
        assertThrows(IllegalArgumentException.class, () ->
                legacyStartEvent.updateStartDateTime("6-9-2026 1030"));
        assertTrue(legacyStartEvent.getStartDateTime().getValue().isEmpty());
    }
}
