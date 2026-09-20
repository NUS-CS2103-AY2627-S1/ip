package fifi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the displayed and saved forms of event tasks.
 */
public class EventTest {

    @Test
    public void toString_unmarkedEvent_eventStringReturned() {
        Event event = new Event(false, "project meeting", LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4));

        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)", event.toString());
    }

    @Test
    public void toString_markedEvent_markedEventStringReturned() {
        Event event = new Event(true, "project meeting", LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4));

        assertEquals("[E][X] project meeting (from: Dec 02 2019 to: Dec 04 2019)", event.toString());
    }

    @Test
    public void toFileString_unmarkedEvent_eventStorageStringReturned() {
        Event event = new Event(false, "project meeting", LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4), LocalDateTime.of(2019, 11, 30, 9, 0), null);

        assertEquals("E | 0 | project meeting | 2019-12-02 | 2019-12-04 | 2019-11-30T09:00:00 | -",
                event.toFileString());
    }

    @Test
    public void toFileString_markedEvent_markedEventStorageStringReturned() {
        Event event = new Event(true, "project meeting", LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4), LocalDateTime.of(2019, 11, 30, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0));

        assertEquals("E | 1 | project meeting | 2019-12-02 | 2019-12-04 | "
                + "2019-11-30T09:00:00 | 2019-12-03T17:00:00", event.toFileString());
    }

    @Test
    public void occursOn_boundariesAndSurroundingDates_inclusiveRange() {
        Event event = new Event(false, "project meeting", LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4));

        assertTrue(event.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 1)));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 3)));
        assertFalse(event.occursOn(LocalDate.of(2019, 12, 5)));
        assertTrue(event.occursOn(LocalDate.of(2019, 12, 4)));
    }
}
