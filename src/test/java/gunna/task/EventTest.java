package gunna.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * JUnit test class for testing the Event class.
 * Tests cover creation, string representations, file format, and time period handling.
 */
public class EventTest {

    private Event event;

    @BeforeEach
    public void setUp() {
        event = new Event("Team meeting", "2pm", "4pm");
    }

    @Test
    public void constructor_newEvent_isNotDone() {
        assertFalse(event.isDone(), "New event should not be marked as done");
    }

    @Test
    public void constructor_newEvent_hasCorrectDescription() {
        assertEquals("Team meeting", event.getDescription(),
                "Event description should match the constructor argument");
    }

    @Test
    public void getFrom_newEvent_returnsCorrectFrom() {
        assertEquals("2pm", event.getFrom(),
                "getFrom should return the from time");
    }

    @Test
    public void getTo_newEvent_returnsCorrectTo() {
        assertEquals("4pm", event.getTo(),
                "getTo should return the to time");
    }

    @Test
    public void toString_notDoneEvent_correctFormat() {
        assertEquals("[E][ ] Team meeting (from: 2pm to: 4pm)", event.toString(),
                "String representation should show [E] prefix with from and to times");
    }

    @Test
    public void toString_doneEvent_correctFormat() {
        event.markAsDone();
        assertEquals("[E][X] Team meeting (from: 2pm to: 4pm)", event.toString(),
                "String representation should show [E] prefix and [X] for done event");
    }

    @Test
    public void toFileFormat_notDoneEvent_correctFormat() {
        assertEquals("E | 0 | Team meeting | 2pm | 4pm", event.toFileFormat(),
                "File format should use 'E' as task type with from and to times");
    }

    @Test
    public void toFileFormat_doneEvent_correctFormat() {
        event.markAsDone();
        assertEquals("E | 1 | Team meeting | 2pm | 4pm", event.toFileFormat(),
                "File format should have 1 as status indicator for done event");
    }

    @Test
    public void constructor_emptyDescription_handlesCorrectly() {
        Event emptyEvent = new Event("", "9am", "10am");
        assertEquals("", emptyEvent.getDescription(),
                "Event should handle empty description");
        assertEquals("[E][ ]  (from: 9am to: 10am)", emptyEvent.toString(),
                "toString should handle empty description");
    }

    @Test
    public void constructor_emptyFromTime_handlesCorrectly() {
        Event emptyFromEvent = new Event("Meeting", "", "5pm");
        assertEquals("", emptyFromEvent.getFrom(),
                "Event should handle empty from time");
        assertEquals("E | 0 | Meeting |  | 5pm", emptyFromEvent.toFileFormat(),
                "File format should handle empty from time");
    }

    @Test
    public void constructor_emptyToTime_handlesCorrectly() {
        Event emptyToEvent = new Event("Meeting", "3pm", "");
        assertEquals("", emptyToEvent.getTo(),
                "Event should handle empty to time");
        assertEquals("E | 0 | Meeting | 3pm | ", emptyToEvent.toFileFormat(),
                "File format should handle empty to time");
    }

    @Test
    public void constructor_dateTimeFormat_handlesCorrectly() {
        Event dateTimeEvent = new Event("Conference", "2024-06-01 9am", "2024-06-01 5pm");
        assertEquals("2024-06-01 9am", dateTimeEvent.getFrom(),
                "Event should preserve datetime format in from");
        assertEquals("2024-06-01 5pm", dateTimeEvent.getTo(),
                "Event should preserve datetime format in to");
    }

    @Test
    public void constructor_naturalLanguageTime_handlesCorrectly() {
        Event naturalEvent = new Event("Lunch", "noon", "1 o'clock");
        assertEquals("noon", naturalEvent.getFrom(),
                "Event should handle natural language time in from");
        assertEquals("1 o'clock", naturalEvent.getTo(),
                "Event should handle natural language time in to");
    }

    @Test
    public void constructor_descriptionWithPipeCharacter_handlesCorrectly() {
        Event pipeEvent = new Event("Task | with | pipes", "1pm", "2pm");
        assertTrue(pipeEvent.toFileFormat().contains("Task | with | pipes"),
                "Event should preserve pipe characters in description");
    }

    @Test
    public void constructor_timeWithPipeCharacter_handlesCorrectly() {
        Event pipeTimeEvent = new Event("Meeting", "1pm | afternoon", "2pm | afternoon");
        assertEquals("1pm | afternoon", pipeTimeEvent.getFrom(),
                "Event should preserve pipe characters in from time");
        assertEquals("2pm | afternoon", pipeTimeEvent.getTo(),
                "Event should preserve pipe characters in to time");
    }

    @Test
    public void toFileFormat_afterMarkAndUnmark_correctFormat() {
        event.markAsDone();
        event.markAsNotDone();
        assertEquals("E | 0 | Team meeting | 2pm | 4pm", event.toFileFormat(),
                "File format should reflect current done status after mark/unmark");
    }

    @Test
    public void constructor_longDescription_handlesCorrectly() {
        String longDesc = "This is a very long event description that contains many words "
                + "and should be handled correctly by the Event class";
        Event longEvent = new Event(longDesc, "8am", "6pm");
        assertEquals(longDesc, longEvent.getDescription(),
                "Event should handle long descriptions");
    }

    @Test
    public void constructor_longTimeStrings_handlesCorrectly() {
        Event longTimeEvent = new Event("Meeting",
                "Monday 9am to Tuesday 5pm",
                "Wednesday 10am");
        assertEquals("Monday 9am to Tuesday 5pm", longTimeEvent.getFrom(),
                "Event should handle long from time strings");
        assertEquals("Wednesday 10am", longTimeEvent.getTo(),
                "Event should handle long to time strings");
    }

    @Test
    public void toString_multipleSpacesInTimes_preservesSpaces() {
        Event spacesEvent = new Event("Event", "2 pm", "4 pm");
        assertTrue(spacesEvent.toString().contains("from: 2 pm to: 4 pm"),
                "toString should preserve spaces in time strings");
    }
}
