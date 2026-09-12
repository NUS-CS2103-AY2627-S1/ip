package minweeder.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void toFileString_incompleteEvent_returnsCorrectFormat() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("E | 0 | project meeting | Mon 2pm | 4pm", event.toFileString());
    }

    @Test
    public void toFileString_completedEvent_returnsCorrectFormat() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.mark();

        assertEquals("E | 1 | project meeting | Mon 2pm | 4pm", event.toFileString());
    }

    @Test
    public void toString_incompleteEvent_returnsDisplayFormat() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void isOccurringOn_anyDate_returnsFalse() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertFalse(event.isOccurringOn(LocalDate.of(2024, 12, 2)));
    }

    @Test
    public void equals_sameFields_returnsTrue() {
        Event first = new Event("project meeting", "Mon 2pm", "4pm");
        Event second = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_differentTo_returnsFalse() {
        Event first = new Event("project meeting", "Mon 2pm", "4pm");
        Event second = new Event("project meeting", "Mon 2pm", "5pm");

        assertNotEquals(first, second);
    }

    @Test
    public void equals_differentTaskType_returnsFalse() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertNotEquals(event, new Todo("project meeting"));
    }
}
