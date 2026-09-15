package monkey.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventTest {
    @Test
    void constructor_freeFormTimes_normalizesAndStoresValues() {
        Event event = new Event("class", " Mon   2pm ", " 4pm ");

        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
        assertEquals("[E][ ] class (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    void constructor_nonClockFreeFormTimes_preservesValues() {
        Event event = new Event("conference", "Monday morning", "Tuesday afternoon");

        assertEquals("Monday morning", event.getFrom());
        assertEquals("Tuesday afternoon", event.getTo());
    }

    @Test
    void constructor_validDateAndDateTimeRanges_acceptsValues() {
        Event dateEvent = new Event("trip", "1/2/2099", "2/2/2099");
        Event dateTimeEvent = new Event("class", "1/2/2099 1400", "1/2/2099 1500");

        assertEquals("1/2/2099", dateEvent.getFrom());
        assertEquals("1/2/2099 1500", dateTimeEvent.getTo());
    }

    @Test
    void constructor_invalidTimeRange_rejectsInput() {
        assertThrows(IllegalArgumentException.class, () -> new Event("class", "4pm", "2pm"));
        assertThrows(IllegalArgumentException.class, () -> new Event("class", "25pm", "26pm"));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("class", "2099-01-01 1400", "tomorrow"));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("class", "2099-01-02", "2099-01-01"));
    }

    @Test
    void constructor_blankOrDelimitedTimes_rejectsInput() {
        assertThrows(IllegalArgumentException.class, () -> new Event("class", null, "3pm"));
        assertThrows(IllegalArgumentException.class, () -> new Event("class", "2pm", " "));
        assertThrows(IllegalArgumentException.class, () -> new Event("class", "2|pm", "3pm"));
    }

    @Test
    void hasSameDetails_allDetailsMatchIgnoringCase_returnsTrue() {
        Event event = new Event("Class", "Mon 2pm", "4pm");

        assertTrue(event.hasSameDetails(new Event("class", "mon 2PM", "4PM")));
        assertFalse(event.hasSameDetails(new Event("class", "Mon 3pm", "4pm")));
        assertFalse(event.hasSameDetails(new ToDos("class")));
    }
}
