package chim.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void toFileFormat_notDone_correctFormat() {
        Event event = new Event("project meeting", "Aug 6th 2pm", "4pm");
        assertEquals("E | 0 | none | project meeting | Aug 6th 2pm | 4pm", event.toFileFormat());
    }

    @Test
    public void toFileFormat_markedDone_correctFormat() {
        Event event = new Event("project meeting", "Aug 6th 2pm", "4pm");
        event.markAsDone();
        assertEquals("E | 1 | none | project meeting | Aug 6th 2pm | 4pm", event.toFileFormat());
    }

    @Test
    public void toString_notDone_correctDisplayFormat() {
        Event event = new Event("project meeting", "Aug 6th 2pm", "4pm");
        assertEquals("[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)", event.toString());
    }

    @Test
    public void toString_markedDone_correctDisplayFormat() {
        Event event = new Event("project meeting", "Aug 6th 2pm", "4pm");
        event.markAsDone();
        assertEquals("[E][X] project meeting (from: Aug 6th 2pm to: 4pm)", event.toString());
    }

    @Test
    public void toFileFormat_fromAndToContainPipeLikeCharacters_preservedAsIs() {
        Event event = new Event("standup", "9am", "9:30am");
        assertEquals("E | 0 | none | standup | 9am | 9:30am", event.toFileFormat());
    }
}
