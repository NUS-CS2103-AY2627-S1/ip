package tblade.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests Event's start/end storage, its toString formatting, and equals/hashCode.
 */
public class EventTest {
    @Test
    public void gettersReturnTheStoredStartAndEnd() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }

    @Test
    public void toString_notDone_includesFromAndTo() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void toString_done_showsXIcon() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.markAsDone();

        assertEquals("[E][X] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void equals_sameDescriptionFromTo_returnsTrue() {
        assertEquals(new Event("project meeting", "Mon 2pm", "4pm"),
                new Event("project meeting", "Mon 2pm", "4pm"));
    }

    @Test
    public void equals_differentFrom_returnsFalse() {
        assertNotEquals(new Event("project meeting", "Mon 2pm", "4pm"),
                new Event("project meeting", "Tue 2pm", "4pm"));
    }

    @Test
    public void equals_differentTo_returnsFalse() {
        assertNotEquals(new Event("project meeting", "Mon 2pm", "4pm"),
                new Event("project meeting", "Mon 2pm", "5pm"));
    }
}
