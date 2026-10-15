package amigobot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for Event's toString formatting and getter behavior.
 */
public class EventTest {

    @Test
    public void toString_withStringDates_formatsCorrectly() {
        Event event = new Event("meeting", "Mon 2pm", "4pm");
        assertEquals("[E][ ] meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void toString_withLocalDates_formatsAsMmmDYyyy() {
        Event event = new Event("conference",
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 3));
        assertEquals("[E][ ] conference (from: Aug 1 2025 to: Aug 3 2025)", event.toString());
    }

    @Test
    public void toString_markedDone_showsX() {
        Event event = new Event("meeting", "Mon 2pm", "4pm");
        event.markAsDone();
        assertEquals("[E][X] meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void toString_mixedDateAndString_formatsCorrectly() {
        Event event = new Event("trip",
                LocalDate.of(2025, 6, 1), null,
                null, "sometime");
        assertEquals("[E][ ] trip (from: Jun 1 2025 to: sometime)", event.toString());
    }

    @Test
    public void getFromDate_withLocalDate_returnsDate() {
        LocalDate from = LocalDate.of(2025, 3, 10);
        Event event = new Event("workshop", from, LocalDate.of(2025, 3, 12));
        assertEquals(from, event.getFromDate());
        assertNull(event.getFromString());
    }

    @Test
    public void getFromString_withString_returnsString() {
        Event event = new Event("meeting", "Mon 2pm", "4pm");
        assertEquals("Mon 2pm", event.getFromString());
        assertNull(event.getFromDate());
    }

    @Test
    public void getToDate_withLocalDate_returnsDate() {
        LocalDate to = LocalDate.of(2025, 3, 12);
        Event event = new Event("workshop", LocalDate.of(2025, 3, 10), to);
        assertEquals(to, event.getToDate());
        assertNull(event.getToString());
    }

    @Test
    public void getToString_withString_returnsString() {
        Event event = new Event("meeting", "Mon 2pm", "4pm");
        assertEquals("4pm", event.getToString());
        assertNull(event.getToDate());
    }
}
