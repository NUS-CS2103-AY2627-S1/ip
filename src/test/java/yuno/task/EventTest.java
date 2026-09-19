package yuno.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class EventTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 26, 14, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 8, 28, 16, 30);

    @Test
    void constructor_endBeforeStart_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("meeting", false, END, START));
    }

    @Test
    void constructor_endEqualsStart_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("meeting", false, START, START));
    }

    @Test
    void isRelevantFor_dateBeforeEvent_returnsFalse() {
        Event event = new Event("project meeting", false, START, END);

        assertFalse(event.isRelevantFor(LocalDate.of(2026, 8, 25)));
    }

    @Test
    void isRelevantFor_startAndEndDates_returnsTrue() {
        Event event = new Event("project meeting", false, START, END);

        assertTrue(event.isRelevantFor(LocalDate.of(2026, 8, 26)));
        assertTrue(event.isRelevantFor(LocalDate.of(2026, 8, 28)));
    }

    @Test
    void isRelevantFor_dateAfterEvent_returnsFalse() {
        Event event = new Event("project meeting", false, START, END);

        assertFalse(event.isRelevantFor(LocalDate.of(2026, 8, 29)));
    }

    @Test
    void toStorageString_descriptionContainsDelimiter_preservesDescriptionAndDateTimes() {
        Event event = new Event("compare A | B", true, START, END);

        assertEquals(
                "E | X | compare A | B | Aug 26 2026, 02:00 PM | Aug 28 2026, 04:30 PM",
                event.toStorageString());
    }

    @Test
    void toString_event_returnsDisplayFormat() {
        Event event = new Event("project meeting", false, START, END);

        assertEquals(
                "[E][ ] project meeting (from: Aug 26 2026, 02:00 PM to: Aug 28 2026, 04:30 PM)",
                event.toString());
    }
}
