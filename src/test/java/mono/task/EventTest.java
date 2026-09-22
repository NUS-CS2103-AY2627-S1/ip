package mono.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests event construction, display, and persistence representations.
 */
public class EventTest {
    @Test
    public void constructor_validDates_createsIncompleteEvent() {
        Event event = new Event("project retreat", "2026-09-01", "2026-09-03");

        assertEquals(
                "[E][ ] project retreat (from: Sep 01 2026 to: Sep 03 2026)",
                event.toString());
        assertEquals(
                "E | 0 | project retreat | 2026-09-01 | 2026-09-03",
                event.toFileString());
    }

    @Test
    public void representations_completedEvent_includeCompletedStatus() {
        Event event = new Event("project retreat", "2026-09-01", "2026-09-03");
        event.markDone();

        assertEquals(
                "[E][X] project retreat (from: Sep 01 2026 to: Sep 03 2026)",
                event.toString());
        assertEquals(
                "E | 1 | project retreat | 2026-09-01 | 2026-09-03",
                event.toFileString());
    }

    @Test
    public void constructor_invalidStartDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> new Event("project retreat",
                "not-a-date", "2026-09-03"));
    }

    @Test
    public void constructor_invalidEndDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> new Event("project retreat",
                "2026-09-01", "not-a-date"));
    }
}
