package ace.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void toString_incompleteTask_formatsWithFromAndTo() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void toString_completedTask_formatsWithCheckedBox() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.completeTask();
        assertEquals("[E][X] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void loadFormat_matchesToString() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        assertEquals(event.toString(), event.loadFormat());
    }
}
