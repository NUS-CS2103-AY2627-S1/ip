package pdd.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    public void toString_includesFromDateAndToText() {
        Event event = new Event("project meeting", LocalDate.of(2019, 12, 2), "4pm");
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: 4pm)", event.toString());
    }

    @Test
    public void toFileFormat_usesIsoFromDateAndRawToText() {
        Event event = new Event("project meeting", LocalDate.of(2019, 12, 2), "4pm");
        assertEquals("E | 0 | project meeting | 2019-12-02 | 4pm", event.toFileFormat());
    }

    @Test
    public void occursOn_matchesFromDateOnly() {
        Event event = new Event("project meeting", LocalDate.of(2019, 12, 2), "4pm");
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_differentDate_returnsFalse() {
        Event event = new Event("project meeting", LocalDate.of(2019, 12, 2), "4pm");
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 3)));
    }
}
