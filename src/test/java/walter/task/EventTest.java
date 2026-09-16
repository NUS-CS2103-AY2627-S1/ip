package walter.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests both supported Event display formats.
 */
public class EventTest {
    @Test
    public void toString_atEvent_atFormattingIncluded() {
        Event event = new Event("meeting", "3pm");

        assertTrue(event.isAtFormat());
        assertEquals("3pm", event.getAt());
        assertNull(event.getFrom());
        assertNull(event.getTo());
        assertEquals("[E][ ] meeting (at: 3pm)", event.toString());
    }

    @Test
    public void toString_fromToEvent_rangeFormattingIncluded() {
        Event event = new Event("workshop", "2pm", "4pm");

        assertFalse(event.isAtFormat());
        assertNull(event.getAt());
        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
        assertEquals("[E][ ] workshop (from: 2pm to: 4pm)", event.toString());
    }
}
