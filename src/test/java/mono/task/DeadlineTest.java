package mono.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline construction, display, and persistence representations.
 */
public class DeadlineTest {
    @Test
    public void constructor_validDate_createsIncompleteDeadline() {
        Deadline deadline = new Deadline("submit report", "2026-09-01");

        assertEquals("[D][ ] submit report (by: Sep 01 2026)", deadline.toString());
        assertEquals("D | 0 | submit report | 2026-09-01", deadline.toFileString());
    }

    @Test
    public void representations_completedDeadline_includeCompletedStatus() {
        Deadline deadline = new Deadline("submit report", "2026-09-01");
        deadline.markDone();

        assertEquals("[D][X] submit report (by: Sep 01 2026)", deadline.toString());
        assertEquals("D | 1 | submit report | 2026-09-01", deadline.toFileString());
    }

    @Test
    public void constructor_invalidDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("submit report", "2026-09-31"));
    }
}
