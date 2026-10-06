package moon;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests the user-facing and persistence formats of event tasks. */
public class EventTest {
    /** Verifies that an event formats its dates and completion status correctly. */
    @Test
    public void eventFormats_datesAndStatusCorrectly() {
        Event event = new Event("team meeting", LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 21));

        assertEquals("[E][ ] team meeting (from: Sep 20 2026 to: Sep 21 2026)", event.toString());
        assertEquals("E | 0 | team meeting | 2026-09-20 | 2026-09-21", event.toSaveFormat());

        event.markAsDone();
        assertEquals("[E][X] team meeting (from: Sep 20 2026 to: Sep 21 2026)", event.toString());
        assertEquals("E | 1 | team meeting | 2026-09-20 | 2026-09-21", event.toSaveFormat());
    }
}
