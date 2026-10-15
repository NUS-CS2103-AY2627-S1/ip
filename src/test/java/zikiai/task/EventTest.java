package zikiai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the display and storage representations of event tasks.
 */
class EventTest {

    @Test
    void constructor_blankStartTime_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Event("team meeting", " ", "4pm"));
    }

    @Test
    void constructor_blankEndTime_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Event("team meeting", "2pm", " "));
    }

    @Test
    void constructor_eventTimes_displayAndStorageRepresentationsReturned() {
        Event event = new Event("team meeting", "Mon 2pm", "4pm");

        assertEquals(
                "[E][ ] team meeting (from: Mon 2pm to: 4pm)", event.getDescription());
        assertEquals(
                "[E][ ] | team meeting | Mon 2pm | 4pm", event.toDataString());
    }

    @Test
    void markAsDone_incompleteEvent_doneStatusShownInBothRepresentations() {
        Event event = new Event("team meeting", "Mon 2pm", "4pm");

        event.markAsDone();

        assertEquals(
                "[E][X] team meeting (from: Mon 2pm to: 4pm)", event.getDescription());
        assertEquals(
                "[E][X] | team meeting | Mon 2pm | 4pm", event.toDataString());
    }
}
