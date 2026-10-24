package echo.task; // same package as the class being tested

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {
    @Test
    public void toString_newEvent_showsFormattedRange() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 1), LocalDate.of(2019, 8, 3));
        assertEquals("[E][ ] trip (from: Aug 01 2019 to: Aug 03 2019)", event.toString());
    }

    @Test
    public void toString_markedDone_showsDoneTag() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 1), LocalDate.of(2019, 8, 3));
        event.markAsDone();
        assertEquals("[E][X] trip (from: Aug 01 2019 to: Aug 03 2019)", event.toString());
    }

    @Test
    public void toFileFormat_newEvent_returnsPipeDelimitedLineWithIsoDates() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 1), LocalDate.of(2019, 8, 3));
        assertEquals("E | 0 | trip | 2019-08-01 | 2019-08-03", event.toFileFormat());
    }

    @Test
    public void occursOn_fromDateBoundary_returnsTrue() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 1), LocalDate.of(2019, 8, 3));
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 1)));
    }

    @Test
    public void occursOn_toDateBoundary_returnsTrue() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 1), LocalDate.of(2019, 8, 3));
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 3)));
    }

    @Test
    public void occursOn_dateInsideRange_returnsTrue() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 1), LocalDate.of(2019, 8, 3));
        assertTrue(event.occursOn(LocalDate.of(2019, 8, 2)));
    }

    @Test
    public void occursOn_dateBeforeRange_returnsFalse() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 1), LocalDate.of(2019, 8, 3));
        assertFalse(event.occursOn(LocalDate.of(2019, 7, 31)));
    }

    @Test
    public void occursOn_dateAfterRange_returnsFalse() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 1), LocalDate.of(2019, 8, 3));
        assertFalse(event.occursOn(LocalDate.of(2019, 8, 4)));
    }
}
