package quackie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the task-specific fields and display values of ToDos and events. */
class TaskSubtypeTest {
    /** Verifies ToDo type formatting. */
    @Test
    void formatsToDo() {
        assertEquals("[T][ ] buy bread", new ToDo("buy bread").toString());
    }

    /** Verifies event fields and type formatting. */
    @Test
    void storesEventDetails() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    /** Verifies that structured event times form a valid chronological range. */
    @Test
    void validatesStructuredEventTimes() {
        Event event = new Event("meeting", "2/12/2019 1400", "2/12/2019 1600");

        assertEquals("2/12/2019 1400", event.getFrom());
        assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", "2/12/2019 1600", "2/12/2019 1400"));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", "2/12/2019 1400", "2/12/2019 1400"));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", "2/12/2019 2500", "3/12/2019 1400"));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("meeting", "2/12/2019 1400", "tomorrow"));
    }

    /** Verifies that structured event times display like deadline date-times while keeping raw values. */
    @Test
    void displaysStructuredEventTimesInFriendlyFormat() {
        Event event = new Event("project meeting", "24/9/2026 1400", "24/9/2026 1600");

        assertEquals("[E][ ] project meeting (from: Sep 24 2026, 2:00 PM to: Sep 24 2026, 4:00 PM)",
                event.toString());
        assertEquals("24/9/2026 1400", event.getFrom());
        assertEquals("24/9/2026 1600", event.getTo());
        assertEquals("[D][ ] submit report (by: Sep 25 2026, 11:59 PM)",
                new Deadline("submit report", "25/9/2026 2359").toString());
    }

    /** Verifies that free-form event values are displayed unchanged. */
    @Test
    void displaysFreeFormEventTimesUnchanged() {
        assertEquals("[E][ ] trip (from: Monday to: Wednesday)",
                new Event("trip", "Monday", "Wednesday").toString());
    }
}
