package allmind.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests event task data, validation, and display behavior. */
public class EventTest {
    @Test
    public void constructor_validDateRange_storesDatesAndFormatsTask() {
        Event event = new Event("team meeting", LocalDate.of(2026, 10, 21),
                LocalDate.of(2026, 10, 22));

        assertEquals(LocalDate.of(2026, 10, 21), event.getFrom());
        assertEquals(LocalDate.of(2026, 10, 22), event.getTo());
        assertEquals("[E][ ] team meeting (from: Oct 21 2026 to: Oct 22 2026)", event.toString());
    }

    @Test
    public void constructor_endBeforeStart_assertionThrown() {
        assertThrows(AssertionError.class, () -> new Event("invalid", LocalDate.of(2026, 10, 22),
                LocalDate.of(2026, 10, 21)));
    }

    @Test
    public void constructor_nullDate_assertionThrown() {
        assertThrows(AssertionError.class, () -> new Event("invalid", null, LocalDate.of(2026, 10, 21)));
        assertThrows(AssertionError.class, () -> new Event("invalid", LocalDate.of(2026, 10, 21), null));
    }
}
