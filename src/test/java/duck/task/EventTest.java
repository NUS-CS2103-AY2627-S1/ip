package duck.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests event display, persistence, status, and identity behavior.
 */
public class EventTest {

    @Test
    public void toString_paddedTimes_displaysTrimmedTimesAndCurrentStatus() {
        Event event = new Event("project meeting", " Mon 2pm ", " 4pm ");

        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());

        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void formatForStorage_specialCharacters_escapesAndTrimsFields() {
        Event event = new Event(
                "meet A | B \\ C", " Room | A\\1 ", " Room | B\\2 ");
        event.markAsDone();

        assertEquals("E | 1 | meet A \\| B \\\\ C | "
                + "Room \\| A\\\\1 | Room \\| B\\\\2", event.formatForStorage());
    }

    @Test
    public void hasSameDetails_sameDetailsIgnoringStatusAndTimePadding_returnsTrue() {
        Event completedEvent = new Event("meeting", " Mon 2pm ", " 4pm ");
        completedEvent.markAsDone();

        assertTrue(completedEvent.hasSameDetails(
                new Event("meeting", "Mon 2pm", "4pm")));
    }

    @Test
    public void hasSameDetails_differentDetailsOrType_returnsFalse() {
        Event event = new Event("meeting", "Mon 2pm", "4pm");

        assertFalse(event.hasSameDetails(new Event("workshop", "Mon 2pm", "4pm")));
        assertFalse(event.hasSameDetails(new Event("meeting", "Mon 3pm", "4pm")));
        assertFalse(event.hasSameDetails(new Event("meeting", "Mon 2pm", "5pm")));
        assertFalse(event.hasSameDetails(new Todo("meeting")));
        assertFalse(event.hasSameDetails(null));
    }
}
