package milo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EventTest {
    @Test
    void toString_newEvent_showsFromAndTo() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    void toString_doneEvent_showsXInBracket() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    void toFileFormat_newEvent_matchesExpectedFormat() {
        Event event = new Event("project meeting", "Aug 6th 2pm", "4pm");
        assertEquals("E | 0 | project meeting | Aug 6th 2pm | 4pm", event.toFileFormat());
    }
}
