package glennon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests event validation, defining details, and inclusive date filtering.
 */
class EventTest {
    @Test
    void constructor_nullStartOrEnd_throwsHelpfulException() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 1, 9, 0);

        IllegalArgumentException missingStart = assertThrows(
                IllegalArgumentException.class, () -> new Event("meeting", null, dateTime));
        IllegalArgumentException missingEnd = assertThrows(
                IllegalArgumentException.class, () -> new Event("meeting", dateTime, null));
        IllegalArgumentException missingBoth = assertThrows(
                IllegalArgumentException.class, () -> new Event("meeting", null, null));

        assertEquals("Event start and end date-times must not be null.", missingStart.getMessage());
        assertEquals(missingStart.getMessage(), missingEnd.getMessage());
        assertEquals(missingStart.getMessage(), missingBoth.getMessage());
    }

    @Test
    void constructor_endAtOrBeforeStart_throwsHelpfulException() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);
        LocalDateTime earlierEnd = startDateTime.minusMinutes(1);

        IllegalArgumentException equalTimes = assertThrows(
                IllegalArgumentException.class, () -> new Event("meeting", startDateTime, startDateTime));
        IllegalArgumentException reversedTimes = assertThrows(
                IllegalArgumentException.class, () -> new Event("meeting", startDateTime, earlierEnd));

        assertEquals("Event end must be after its start.", equalTimes.getMessage());
        assertEquals(equalTimes.getMessage(), reversedTimes.getMessage());
    }

    @Test
    void constructor_invalidDescription_rejectsInheritedValidationFailures() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);
        LocalDateTime endDateTime = startDateTime.plusHours(1);

        assertThrows(IllegalArgumentException.class, () -> new Event(null, startDateTime, endDateTime));
        assertThrows(IllegalArgumentException.class, () -> new Event(" \t ", startDateTime, endDateTime));
        assertThrows(IllegalArgumentException.class, () -> new Event("a\nb", startDateTime, endDateTime));
    }

    @Test
    void constructor_singleDayAllDayEvent_acceptsAndDisplaysFullDay() {
        LocalDate date = LocalDate.of(2026, 9, 1);
        Event event = new Event("holiday", date.atStartOfDay(), date.atTime(LocalTime.MAX));

        assertEquals("[E][ ] holiday (all day: Sep 1 2026 to: Sep 1 2026)", event.toString());
        assertTrue(event.occursOn(date));
    }

    @Test
    void hasSameDetails_matchingDescriptionAndBoundaries_ignoresCompletion() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);
        LocalDateTime endDateTime = startDateTime.plusHours(1);
        Event first = new Event("meeting", startDateTime, endDateTime);
        Event second = new Event(" meeting ", startDateTime, endDateTime);
        second.markAsDone();

        assertTrue(first.hasSameDetails(second));
        assertTrue(second.hasSameDetails(first));
    }

    @Test
    void hasSameDetails_differentDescriptionOrBoundary_returnsFalse() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);
        LocalDateTime endDateTime = startDateTime.plusHours(1);
        Event event = new Event("meeting", startDateTime, endDateTime);

        assertFalse(event.hasSameDetails(new Event("Meeting", startDateTime, endDateTime)));
        assertFalse(event.hasSameDetails(new Event("meeting", startDateTime.plusMinutes(1), endDateTime)));
        assertFalse(event.hasSameDetails(new Event("meeting", startDateTime, endDateTime.plusMinutes(1))));
    }

    @Test
    void hasSameDetails_nullOrOtherTaskType_returnsFalse() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);
        Event event = new Event("meeting", startDateTime, startDateTime.plusHours(1));

        assertFalse(event.hasSameDetails(null));
        assertFalse(event.hasSameDetails(new Todo("meeting")));
        assertFalse(event.hasSameDetails(new Deadline("meeting", startDateTime)));
    }

    @Test
    void occursOn_multiDayEvent_includesStartMiddleAndEndDates() {
        Event event = new Event(
                "conference",
                LocalDateTime.of(2026, 8, 28, 23, 30),
                LocalDateTime.of(2026, 8, 30, 0, 15));

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 28)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 29)));
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 30)));
    }

    @Test
    void occursOn_datesOutsideEvent_returnsFalse() {
        Event event = new Event(
                "conference",
                LocalDateTime.of(2026, 8, 28, 23, 30),
                LocalDateTime.of(2026, 8, 30, 0, 15));

        assertFalse(event.occursOn(LocalDate.of(2026, 8, 27)));
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 31)));
    }

    @Test
    void occursOn_singleDayEvent_matchesOnlyThatDate() {
        Event event = new Event(
                "lecture",
                LocalDateTime.of(2026, 8, 29, 9, 0),
                LocalDateTime.of(2026, 8, 29, 10, 0));

        assertTrue(event.occursOn(LocalDate.of(2026, 8, 29)));
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 28)));
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 30)));
    }

    @Test
    void constructor_oneNanosecondInterval_preservesBothBoundaries() {
        LocalDateTime startDateTime = LocalDateTime.of(2028, 2, 29, 23, 59, 59, 999999999);
        LocalDateTime endDateTime = startDateTime.plusNanos(1);
        Event event = new Event(" midnight transition ", startDateTime, endDateTime);

        assertEquals(startDateTime, event.getStartDateTime());
        assertEquals(endDateTime, event.getEndDateTime());
        assertEquals("midnight transition", event.getDescription());
        assertFalse(event.isDone());
        assertTrue(event.occursOn(LocalDate.of(2028, 2, 29)));
        assertTrue(event.occursOn(LocalDate.of(2028, 3, 1)));
    }

    @Test
    void toString_allDayMultiDayEvent_usesEnglishDatesAndCompletionMarker() {
        Locale originalLocale = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.CHINESE);
            Event event = new Event("holiday", LocalDate.of(2026, 12, 31).atStartOfDay(),
                    LocalDate.of(2027, 1, 2).atTime(LocalTime.MAX));
            event.markAsDone();

            assertEquals("[E][X] holiday (all day: Dec 31 2026 to: Jan 2 2027)", event.toString());
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, originalLocale);
        }
    }

    @Test
    void toString_onlyOneAllDayBoundary_keepsTimedDisplay() {
        LocalDate date = LocalDate.of(2026, 9, 1);
        Event startsAtMidnight = new Event("morning", date.atStartOfDay(), date.atTime(12, 0));
        Event endsAtDayEnd = new Event("evening", date.atTime(12, 0), date.atTime(LocalTime.MAX));

        assertEquals("[E][ ] morning (from: Sep 1 2026, 12:00 AM to: Sep 1 2026, 12:00 PM)",
                startsAtMidnight.toString());
        assertEquals("[E][ ] evening (from: Sep 1 2026, 12:00 PM to: Sep 1 2026, 11:59 PM)",
                endsAtDayEnd.toString());
    }

    @Test
    void hasSameDetails_subclassWithIdenticalDetails_isDistinctConcreteType() {
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);
        LocalDateTime endDateTime = startDateTime.plusHours(1);
        Event event = new Event("meeting", startDateTime, endDateTime);
        Event specializedEvent = new SpecializedEvent("meeting", startDateTime, endDateTime);

        assertFalse(event.hasSameDetails(specializedEvent));
        assertFalse(specializedEvent.hasSameDetails(event));
    }

    /**
     * Supplies an event subtype to verify exact concrete-type comparisons.
     */
    private static final class SpecializedEvent extends Event {
        private SpecializedEvent(String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
            super(description, startDateTime, endDateTime);
        }
    }
}
