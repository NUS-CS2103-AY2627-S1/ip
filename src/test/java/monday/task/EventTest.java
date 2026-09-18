package monday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for Event.
 */
class EventTest {
    @Test
    void constructor_withDatesAndTimes_createsIncompleteEvent() {
        LocalDate startDate = LocalDate.of(2026, 8, 30);
        LocalTime startTime = LocalTime.of(14, 30);
        LocalDate endDate = LocalDate.of(2026, 8, 31);
        LocalTime endTime = LocalTime.of(16, 0);
        Event event = new Event("team retreat", startDate, startTime, endDate, endTime);

        assertEquals("team retreat", event.getDescription());
        assertFalse(event.isDone());
        assertEquals(startDate, event.getStartDate());
        assertEquals(startTime, event.getStartTime());
        assertEquals(endDate, event.getEndDate());
        assertEquals(endTime, event.getEndTime());
        assertEquals("[E][ ] team retreat (from: 30 Aug 2026 1430 to: 31 Aug 2026 1600)", event.toString());
    }

    @Test
    void constructor_withCompletionStatus_createsEventWithGivenStatus() {
        Event event = new Event("team retreat", true, LocalDate.of(2026, 8, 30), LocalTime.of(14, 30),
                LocalDate.of(2026, 8, 31), LocalTime.of(16, 0));

        assertTrue(event.isDone());
        assertEquals("[E][X] team retreat (from: 30 Aug 2026 1430 to: 31 Aug 2026 1600)",
                event.toString());
    }

    @Test
    void constructor_withoutTimes_displaysOnlyDates() {
        Event event = new Event("team retreat", LocalDate.of(2026, 8, 30), null, LocalDate.of(2026, 8, 31), null);

        assertNull(event.getStartTime());
        assertNull(event.getEndTime());
        assertEquals("[E][ ] team retreat (from: 30 Aug 2026 to: 31 Aug 2026)", event.toString());
    }

    @Test
    void constructor_withNotes_storesAndDisplaysNotes() {
        Event event = new Event("team retreat", LocalDate.of(2026, 8, 30), LocalTime.of(14, 30),
                LocalDate.of(2026, 8, 31), LocalTime.of(16, 0), "Bring a jacket");

        assertEquals("Bring a jacket", event.getNotes());
        assertEquals("[E][ ] team retreat (from: 30 Aug 2026 1430 to: 31 Aug 2026 1600) Bring a jacket",
                event.toString());
    }

    @Test
    void constructor_withOnlyStartTime_displaysStartTimeOnly() {
        Event event = new Event("team retreat", LocalDate.of(2026, 8, 30), LocalTime.of(14, 30),
                LocalDate.of(2026, 8, 31), null);

        assertEquals("[E][ ] team retreat (from: 30 Aug 2026 1430 to: 31 Aug 2026)", event.toString());
    }

    @Test
    void markAsDone_marksEvent() {
        Event event = new Event("team retreat", LocalDate.of(2026, 8, 30), LocalTime.of(14, 30),
                LocalDate.of(2026, 8, 31), LocalTime.of(16, 0));

        event.markAsDone();

        assertTrue(event.isDone());
        assertEquals("[E][X] team retreat (from: 30 Aug 2026 1430 to: 31 Aug 2026 1600)", event.toString());
    }
}
