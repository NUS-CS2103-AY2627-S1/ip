package bott.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Event}'s two textual representations: {@code toString}
 * (display format "MMM dd yyyy" for both dates) and {@code toFileFormat}
 * (save format, plain ISO "yyyy-MM-dd" for both dates).
 */
public class EventTest {

    @Test
    public void toString_notDone_correctFormat() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 4), LocalDate.of(2019, 8, 5));
        assertEquals("[E][ ] trip (from: Aug 04 2019 to: Aug 05 2019)", event.toString());
    }

    @Test
    public void toString_done_correctFormat() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 4), LocalDate.of(2019, 8, 5));
        event.markAsDone();
        assertEquals("[E][X] trip (from: Aug 04 2019 to: Aug 05 2019)", event.toString());
    }

    @Test
    public void toString_singleDigitDays_padBothWithZero() {
        Event event = new Event("trip", LocalDate.of(2019, 1, 2), LocalDate.of(2019, 1, 3));
        assertEquals("[E][ ] trip (from: Jan 02 2019 to: Jan 03 2019)", event.toString());
    }

    @Test
    public void toFileFormat_notDone_correctFormat() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 4), LocalDate.of(2019, 8, 5));
        assertEquals("E | 0 | trip | 2019-08-04 | 2019-08-05", event.toFileFormat());
    }

    @Test
    public void toFileFormat_done_correctFormat() {
        Event event = new Event("trip", LocalDate.of(2019, 8, 4), LocalDate.of(2019, 8, 5));
        event.markAsDone();
        assertEquals("E | 1 | trip | 2019-08-04 | 2019-08-05", event.toFileFormat());
    }
}
