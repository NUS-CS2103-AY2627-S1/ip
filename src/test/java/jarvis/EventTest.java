package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests event-specific task behavior. */
class EventTest {
    @Test
    void getFromAndGetTo_event_returnOriginalTimes() {
        Event event = new Event("team meeting", "Mon 2pm", "Mon 4pm");

        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("Mon 4pm", event.getTo());
    }

    @Test
    void toString_incompleteEvent_includesTypeStatusDescriptionAndTimes() {
        Event event = new Event("team meeting", "Mon 2pm", "Mon 4pm");

        assertEquals("[E][ ] team meeting (from: Mon 2pm to: Mon 4pm)", event.toString());
    }

    @Test
    void toString_completedEvent_includesDoneStatus() {
        Event event = new Event("team meeting", "Mon 2pm", "Mon 4pm");
        event.markAsDone();

        assertEquals("[E][X] team meeting (from: Mon 2pm to: Mon 4pm)", event.toString());
    }
}
