package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Verifies event time fields and display formatting. */
class EventsTest {
    /** An event preserves its start and end values in its human-readable representation. */
    @Test
    void constructor_eventTimes_formatsAllFields() {
        Events event = new Events("2pm", "4pm", "project meeting");

        assertEquals("2pm", event.getStartTime());
        assertEquals("4pm", event.getEndTime());
        assertEquals("[E] [ ] project meeting(from: 2pm to: 4pm)", event.toString());
    }

    /** An event inherits task completion behavior. */
    @Test
    void setDone_completedEvent_updatesDisplayMarker() {
        Events event = new Events("2pm", "4pm", "project meeting");

        event.setDone(true);

        assertEquals("[E] [X] project meeting(from: 2pm to: 4pm)", event.toString());
    }
}
