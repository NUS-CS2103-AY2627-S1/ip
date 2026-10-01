package victoria.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import victoria.exception.InvalidEventException;

class EventTest {
    @Test
    void constructor_isoDates_formatsAndExposesDateRange() {
        Event event = new Event("project meeting", "2026-09-12", "2026-09-14");

        assertEquals("2026-09-12", event.getFrom());
        assertEquals("2026-09-14", event.getTo());
        assertEquals(LocalDate.of(2026, 9, 12), event.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 14), event.getEndDate());
        assertEquals("[E][ ] project meeting (from: Sep 12 2026 to: Sep 14 2026)", event.toString());
    }

    @Test
    void constructor_invalidOrMissingDate_throwsUserFacingException() {
        assertThrows(InvalidEventException.class, () -> new Event("event", "2026-09-12", "tomorrow"));
        assertThrows(InvalidEventException.class, () -> new Event("event", null, "2026-09-12"));
    }
}
