package tasque.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    public void fromStoredValues_validStartLegacyEnd_preservesBothOriginalValues() {
        Event event = Event.fromStoredValues("meeting", "2026-08-26", "after lunch");

        assertEquals("[E][ ] meeting (from: 2026-08-26 to: after lunch)", event.toString());
        assertEquals("E2 | 0 | bWVldGluZw== | 2026-08-26 | after lunch", event.toStorageString());
        assertFalse(event.hasReversedDates());
    }

    @Test
    public void hasSameDetails_legacyEndpoints_comparesEachValue() {
        Event event = Event.fromStoredValues("meeting", "Monday 2pm", "Monday 4pm");

        assertTrue(event.hasSameDetails(Event.fromStoredValues("meeting", "Monday 2pm", "Monday 4pm")));
        assertFalse(event.hasSameDetails(Event.fromStoredValues("meeting", "Monday 3pm", "Monday 4pm")));
        assertFalse(event.hasSameDetails(Event.fromStoredValues("meeting", "Monday 2pm", "Monday 5pm")));
    }

    @Test
    public void hasSameDetails_differentCompletionStates_preservesDetailIdentity() {
        Event incomplete = Event.fromStoredValues("meeting", "Monday 2pm", "Monday 4pm");
        Event completed = Event.fromStoredValues("meeting", "Monday 2pm", "Monday 4pm");
        completed.markAsDone();

        assertTrue(incomplete.hasSameDetails(completed));
        assertTrue(completed.hasSameDetails(incomplete));
    }
}
