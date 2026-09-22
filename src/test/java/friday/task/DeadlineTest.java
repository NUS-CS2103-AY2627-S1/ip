package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests tasks with a deadline. */
class DeadlineTest {

    @Test
    void getDeadlineDate_validDate_returnsParsedDate() {
        Deadline deadline = new Deadline(new String[] {"submit report", "2026-08-30"});

        assertEquals(LocalDate.of(2026, 8, 30), deadline.getDeadlineDate());
    }

    @Test
    void toString_validDeadline_returnsReadableDate() {
        Deadline deadline = new Deadline(new String[] {"submit report", "2026-08-30"});

        assertEquals("[D][ ] submit report (by: Aug 30 2026)", deadline.toString());
    }

    @Test
    void constructor_invalidDate_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                new Deadline(new String[] {"submit report", "30-08-2026"}));
    }
}
