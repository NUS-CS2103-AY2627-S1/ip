package rocky.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests date-only and date-time event behavior. */
class EventTest {
    /** Verifies that a date-only event preserves date-only endpoints. */
    @Test
    void constructor_dateOnlyEvent_displaysStoresAndSortsByStartDate() {
        LocalDate from = LocalDate.of(2025, 3, 5);
        LocalDate to = LocalDate.of(2025, 3, 7);
        Event event = new Event("holiday", from, to);

        assertEquals("[E][ ] holiday (from: Mar 05 2025 to: Mar 07 2025)", event.toString());
        assertEquals("E | 0 | holiday | 2025-03-05 | 2025-03-07", event.toStorageString());
        assertEquals(from.atStartOfDay(), event.getSortDate().orElseThrow());
    }

    /** Verifies that a timed event preserves its start and end times. */
    @Test
    void constructor_timedEvent_displaysStoresAndSortsByStartDateTime() {
        LocalDateTime from = LocalDateTime.of(2025, 3, 5, 18, 30);
        LocalDateTime to = LocalDateTime.of(2025, 3, 5, 20, 0);
        Event event = new Event("meeting", from, to);

        assertEquals("[E][ ] meeting (from: Mar 05 2025, 6:30PM to: Mar 05 2025, 8:00PM)",
                event.toString());
        assertEquals("E | 0 | meeting | 2025-03-05T18:30 | 2025-03-05T20:00",
                event.toStorageString());
        assertEquals(from, event.getSortDate().orElseThrow());
    }

    /** Verifies that events cannot be constructed with an end before their start. */
    @Test
    void constructor_endBeforeStart_throwsIllegalArgumentException() {
        LocalDate startDate = LocalDate.of(2025, 3, 7);
        LocalDate endDate = LocalDate.of(2025, 3, 5);
        LocalDateTime startDateTime = LocalDateTime.of(2025, 3, 5, 20, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 3, 5, 18, 30);

        assertThrows(IllegalArgumentException.class,
                () -> new Event("holiday", startDate, endDate));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", startDateTime, endDateTime));
    }

    /** Verifies that completing an event updates display and storage status. */
    @Test
    void markAsDone_incompleteEvent_usesDoneStatus() {
        Event event = new Event("holiday", LocalDate.of(2025, 3, 5), LocalDate.of(2025, 3, 7));

        event.markAsDone();

        assertEquals("[E][X] holiday (from: Mar 05 2025 to: Mar 07 2025)", event.toString());
        assertEquals("E | 1 | holiday | 2025-03-05 | 2025-03-07", event.toStorageString());
    }
}
