package erina.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Event}, whose start and end are kept as the text typed.
 */
public class EventTest {
    @Test
    public void toString_showsStartAndEnd() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void toSaveString_doneEvent_includesStartAndEnd() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.markAsDone();
        assertEquals("E | 1 | project meeting | Mon 2pm | 4pm", event.toSaveString());
    }

    @Test
    public void isSameTask_differentEndTime_isFalse() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        Event other = new Event("project meeting", "Mon 2pm", "5pm");
        assertFalse(event.isSameTask(other));
    }
}
