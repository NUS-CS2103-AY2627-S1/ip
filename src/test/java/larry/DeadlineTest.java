package larry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;
public class DeadlineTest {

    @Test
    public void toString_validDate_formatsDateCorrectly() {
        Deadline deadline = new Deadline("submit assignment", "2026-09-10");
        assertEquals(
                "[D][ ] submit assignment (by: Sep 10 2026)",
                deadline.toString()
        );
    }

    @Test
    public void markAsDone_deadline_updatesStatusAndKeepsDate() {
        Deadline deadline = new Deadline("submit assignment", "2026-09-10");
        deadline.markAsDone();
        assertEquals(
                "[D][X] submit assignment (by: Sep 10 2026)",
                deadline.toString()
        );
    }

    @Test
    public void getBy_validDate_returnsIsoDate() {
        Deadline deadline = new Deadline("submit assignment", "2026-09-10");
        assertEquals("2026-09-10", deadline.getBy());
    }

    @Test
    public void constructor_invalidDate_throwsException() {
        assertThrows(DateTimeParseException.class, () ->
                new Deadline("submit assignment", "not-a-date"));
    }
}
