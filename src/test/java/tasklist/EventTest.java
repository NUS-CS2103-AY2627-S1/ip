package tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import cxception.InvalidDateException;

public class EventTest {

    @Test
    public void constructor_validDates_success() throws Exception {
        Event event = new Event("orientation week", "2026-08-28", "2026-08-30");
        assertNotNull(event);
    }

    @Test
    public void constructor_validDateTimes_success() throws Exception {
        Event event = new Event("team meeting", "2026-08-28 1400", "2026-08-28 1600");
        assertNotNull(event);
    }

    @Test
    public void constructor_invalidFromDate_exceptionThrown() {
        assertThrows(InvalidDateException.class, () ->
            new Event("team meeting", "28-08-2026", "2026-08-28 1600")
        );
    }

    @Test
    public void constructor_invalidToDate_exceptionThrown() {
        assertThrows(InvalidDateException.class, () ->
            new Event("team meeting", "2026-08-28 1400", "invalid-date")
        );
    }

    @Test
    public void toString_unmarkedEvent_correctFormat() throws Exception {
        Event event = new Event("team meeting", "2026-08-28 1400", "2026-08-28 1600");
        // Adjust the expected strings to match your MyDateTime.toDisplayString() output format
        assertEquals("[E] [ ] team meeting (FROM: Aug 28 2026, 2:00PM, TO: Aug 28 2026, 4:00PM)", event.toString());
    }

    @Test
    public void toString_markedEvent_correctFormat() throws Exception {
        Event event = new Event("team meeting", "2026-08-28 1400", "2026-08-28 1600");
        event.setDone();
        assertEquals("[E] [X] team meeting (FROM: Aug 28 2026, 2:00PM, TO: Aug 28 2026, 4:00PM)", event.toString());
    }

    @Test
    public void fileFormatText_validEvent_correctFormat() throws Exception {
        Event event = new Event("team meeting", "2026-08-28 1400", "2026-08-28 1600");
        // Adjust the expected strings to match your MyDateTime.toFileString() output format
        assertEquals("E | team meeting | 2026-08-28 1400 | 2026-08-28 1600", event.fileFormatText());
    }
}
