package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests date parsing, storage data, status, and display behavior of {@link Event}.
 */
class EventTest {
    @Test
    void constructor_fullDateTimes_parsesAndDisplaysEvent() {
        Event event = new Event("meeting", "2026-08-06 1400", "2026-08-06 1600");

        assertEquals("meeting", event.getDescription());
        assertFalse(event.isDone());
        assertEquals("E", event.getTypeCode());
        assertEquals("2026-08-06T14:00|2026-08-06T16:00", event.getExtra());
        assertEquals("[ ] meeting (from: 06 Aug 26, 1400hrs to: 06 Aug 26, 1600hrs)", event.toString());
        assertEquals("[E][ ] meeting (from: 06 Aug 26, 1400hrs to: 06 Aug 26, 1600hrs)",
                event.displayText());
    }

    @Test
    void constructor_timeOnlyEnd_usesStartDateForEnd() {
        Event event = new Event("review", "2026-08-06 1400", "4:30pm");

        assertEquals("2026-08-06T14:00|2026-08-06T16:30", event.getExtra());
        assertEquals("[E][ ] review (from: 06 Aug 26, 1400hrs to: 06 Aug 26, 1630hrs)",
                event.displayText());
    }

    @Test
    void constructor_localDateTimes_preservesStorageValues() {
        Event event = new Event("meeting", LocalDateTime.of(2026, 8, 6, 14, 5),
                LocalDateTime.of(2026, 8, 6, 16, 30));

        assertEquals("2026-08-06T14:05|2026-08-06T16:30", event.getExtra());
        assertEquals("[E][ ] meeting (from: 06 Aug 26, 1405hrs to: 06 Aug 26, 1630hrs)",
                event.displayText());
    }

    @Test
    void markAsDone_notDoneEvent_updatesStatusAndDisplay() {
        Event event = new Event("meeting", "2026-08-06 1400", "2026-08-06 1600");

        event.markAsDone();

        assertTrue(event.isDone());
        assertEquals("[E][X] meeting (from: 06 Aug 26, 1400hrs to: 06 Aug 26, 1600hrs)",
                event.displayText());
    }

    @Test
    void constructor_invalidStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", "not a date", "2026-08-06 1600"));
    }

    @Test
    void constructor_invalidEnd_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", "2026-08-06 1400", "not a time"));
    }

    @Test
    void constructor_endBeforeStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", "2026-08-06 1600", "2026-08-06 1400"));
    }

    @Test
    void constructor_endEqualToStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", "2026-08-06 1400", "2026-08-06 1400"));
    }

    @Test
    void constructorWithLocalDateTimes_endBeforeStart_throwsIllegalArgumentException() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 6, 16, 0);
        LocalDateTime end = LocalDateTime.of(2026, 8, 6, 14, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", start, end));
    }

    @Test
    void constructorWithLocalDateTimes_endEqualToStart_throwsIllegalArgumentException() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 6, 14, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", start, start));
    }
}
