package wwaffle.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests event display formatting.
 */
public class EventTest {
    @Test
    void toString_eventDuration_usesCompactTimeRange() {
        Event event = new Event("team meeting", "2pm", "3pm");

        assertEquals("[E][ ] team meeting (2pm - 3pm)", event.toString());
    }
}
