package verity.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests whether events occur on specified dates.
 */
class EventTest {
    private static final LocalDate START_DATE = LocalDate.of(2026, 8, 10);
    private static final LocalDate END_DATE = LocalDate.of(2026, 8, 12);

    private final Event event = new Event(
            "project meeting",
            START_DATE,
            END_DATE
    );

    @Test
    void getStatus_incompleteEvent_includesEventDetails() {
        assertEquals(
                "[E][ ] project meeting (from: Aug 10 2026 to: Aug 12 2026)",
                event.getStatus()
        );
    }

    @Test
    void getStatus_completedEvent_showsCompletedStatus() {
        event.markAsDone();

        assertEquals(
                "[E][X] project meeting (from: Aug 10 2026 to: Aug 12 2026)",
                event.getStatus()
        );
    }

    @Test
    void serialize_incompleteEvent_returnsStorageFormat() {
        assertEquals(
                "E\t0\tproject meeting\t2026-08-10\t2026-08-12\t\t",
                event.serialize()
        );
    }

    @Test
    void serialize_completedEvent_returnsCompletedStorageFormat() {
        event.markAsDone();

        assertEquals(
                "E\t1\tproject meeting\t2026-08-10\t2026-08-12\t\t",
                event.serialize()
        );
    }

    @Test
    void occursOn_dateBeforeStart_returnsFalse() {
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 9)));
    }

    @Test
    void occursOn_startDate_returnsTrue() {
        assertTrue(event.occursOn(START_DATE));
    }

    @Test
    void occursOn_dateInsideRange_returnsTrue() {
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 11)));
    }

    @Test
    void occursOn_endDate_returnsTrue() {
        assertTrue(event.occursOn(END_DATE));
    }

    @Test
    void occursOn_dateAfterEnd_returnsFalse() {
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 13)));
    }

    @Test
    void occursOn_singleDayEvent_returnsTrueOnThatDay() {
        LocalDate eventDate = LocalDate.of(2026, 9, 1);
        Event singleDayEvent = new Event(
                "single-day event",
                eventDate,
                eventDate
        );

        assertTrue(singleDayEvent.occursOn(eventDate));
    }

    @Test
    void constructor_reversedDates_throwsIllegalArgumentException() {
        LocalDate startDate = LocalDate.of(2026, 8, 12);
        LocalDate endDate = LocalDate.of(2026, 8, 10);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Event(
                        "project meeting", startDate, endDate));

        assertEquals(
                "The event end date cannot be before the start date.",
                exception.getMessage());
    }

    @Test
    void constructor_nullDates_throwAssertionError() {
        assertThrows(
                AssertionError.class,
                () -> new Event("meeting", null, END_DATE));
        assertThrows(
                AssertionError.class,
                () -> new Event("meeting", START_DATE, null));
    }
}
