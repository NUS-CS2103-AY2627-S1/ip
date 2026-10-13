package arrodes.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests event validation, accessors, formatting, and completion display. */
class EventTest {

    private static final LocalDateTime START = LocalDateTime.of(2025, 9, 1, 9, 0);
    private static final LocalDateTime END = LocalDateTime.of(2025, 9, 1, 17, 0);

    /** Verifies constructor nullStart throwsIllegalArgumentException. */
    @Test
    void constructor_nullStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("conference", null, END));
    }

    /** Verifies constructor nullEnd throwsIllegalArgumentException. */
    @Test
    void constructor_nullEnd_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("conference", START, null));
    }

    /** Verifies constructor endBeforeStart throwsIllegalArgumentException. */
    @Test
    void constructor_endBeforeStart_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("conference", END, START));
    }

    /** Returns the start supplied to a valid event. */
    @Test
    void getStartAt_validEvent_returnsStart() {
        Event event = new Event("conference", START, END);
        assertEquals(START, event.getStartAt());
    }

    /** Returns the end supplied to a valid event. */
    @Test
    void getEndAt_validEvent_returnsEnd() {
        Event event = new Event("conference", START, END);
        assertEquals(END, event.getEndAt());
    }

    /** Verifies the display format for a timed event. */
    @Test
    void toString_validEvent_hasCorrectFormat() {
        Event event = new Event("conference", START, END);
        String result = event.toString();
        assertTrue(result.startsWith("[E]"));
        assertTrue(result.contains("from:"));
        assertTrue(result.contains("to"));
        assertTrue(result.contains("Sep 01 2025 09:00"));
        assertTrue(result.contains("Sep 01 2025 17:00"));
    }

    /** Omits midnight times when endpoints were entered as dates only. */
    @Test
    void toString_dateOnlyEndpoints_omitsTime() {
        // When isTimeIncluded flags are false, time should not appear in output
        Event event = new Event("workshop",
                LocalDateTime.of(2025, 9, 1, 0, 0),
                LocalDateTime.of(2025, 9, 2, 0, 0),
                false, false);
        String result = event.toString();
        assertFalse(result.contains("00:00"));
        assertTrue(result.contains("Sep 01 2025"));
        assertTrue(result.contains("Sep 02 2025"));
    }

    /** Displays the completed status icon for a finished event. */
    @Test
    void toString_completedEvent_showsXStatusIcon() {
        Event event = new Event("conference", START, END);
        event.markAsDone();
        assertTrue(event.toString().contains("[X]"));
    }
}
