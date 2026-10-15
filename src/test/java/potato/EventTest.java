package potato.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for testing the behavior of the {@link Event} class.
 */
public class EventTest {

    /**
     * Tests that compact 24-hour date-time inputs are formatted into readable dates and times.
     */
    @Test
    public void toStringCompactDateTimesFormattedCorrectly() {
        Event event = new Event("project meeting", "2026-06-15 1400", "2026-06-15 1600");
        assertEquals("[E][ ] project meeting (from: Jun 15 2026, 14:00 to: Jun 15 2026, 16:00)",
                event.toString());
    }

    /**
     * Tests that colon-separated 24-hour date-time inputs are formatted into readable dates and times.
     */
    @Test
    public void toStringColonDateTimesFormattedCorrectly() {
        Event event = new Event("project meeting", "2026-06-15 14:00", "2026-06-15 16:00");
        assertEquals("[E][ ] project meeting (from: Jun 15 2026, 14:00 to: Jun 15 2026, 16:00)",
                event.toString());
    }
}
