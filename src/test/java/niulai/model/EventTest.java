package niulai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests event date-range matching and validation. */
class EventTest {
    /** Verifies that an event matches every date in its inclusive range. */
    @Test
    void occursOn_dateRange_matchesInclusiveStartAndEnd() {
        Event event = new Event("conference", "2026-08-30", "2026-09-02");

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 30)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 1)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 2)));
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 29)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 3)));
        assertFalse(event.occursOn(null));
    }

    /** Verifies that date-time endpoints are compared using their calendar dates. */
    @Test
    void occursOn_dateTimeRange_matchesCalendarDateRange() {
        Event event = new Event("meeting", "2026-08-31 0900", "2026-08-31 1700");

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 31)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 1)));
    }

    /** Verifies that unsupported endpoint values do not produce false date matches. */
    @Test
    void occursOn_freeFormEndpoints_returnsFalse() {
        Event event = new Event("meeting", "tomorrow morning", "tomorrow afternoon");

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 31)));
    }

    /** Verifies that missing event endpoints are rejected. */
    @Test
    void event_blankEndpoint_exceptionThrown() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "", "10am"));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", null, "10am"));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "9am", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "9am", null));
    }

    /** Verifies that an event with equal comparable endpoints is rejected. */
    @Test
    void event_equalComparableEndpoints_exceptionThrown() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "2026-09-10 1000", "2026-09-10 1000"));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "10am", "10am"));
    }

    /** Verifies that an event ending before its comparable start is rejected. */
    @Test
    void event_reversedComparableEndpoints_exceptionThrown() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "2026-09-11", "2026-09-10"));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("meeting", "11am", "10am"));
    }

    /** Verifies that free-form event endpoints remain accepted and unchanged. */
    @Test
    void event_freeFormEndpoints_preserved() {
        Event event = new Event("meeting", "after lunch", "before dinner");

        assertTrue(event.toString().contains("from: after lunch to: before dinner"));
    }

    /** Verifies that event fields are normalized and escaped in storage. */
    @Test
    void eventRepresentation_whitespaceAndStorageCharacters_normalizedAndEscaped() {
        Event event = new Event(" team | sync ", " after \\ lunch ", " before | dinner ");

        assertEquals("[E][ ] team | sync (from: after \\ lunch to: before | dinner)",
                event.toString());
        assertEquals("E | 0 | team \\| sync | after \\\\ lunch | before \\| dinner",
                event.toStorageString());
    }

    /** Verifies that one unrecognized endpoint prevents an accidental calendar match. */
    @Test
    void occursOn_partlyFreeFormEndpoints_returnsFalse() {
        Event freeFormEnd = new Event("meeting", "2026-09-10", "someday later");
        Event freeFormStart = new Event("meeting", "someday", "2026-09-10");

        assertFalse(freeFormEnd.occursOn(LocalDate.of(2026, 9, 10)));
        assertFalse(freeFormStart.occursOn(LocalDate.of(2026, 9, 10)));
    }
}
