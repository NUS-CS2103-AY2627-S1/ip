package shannon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import shannon.exception.InvalidDateException;

/** Tests strict calendar validation and deadline display and persistence. */
class DeadlineTest {

    @Test
    void parseBy_leapDayWithWhitespace_returnsDate() throws Exception {
        assertEquals(LocalDate.of(2024, 2, 29), Deadline.parseBy(" 2024-02-29 "));
    }

    @Test
    void parseBy_invalidDates_throwsInvalidDateException() {
        for (String text : new String[] {"2026-02-29", "2026-13-01", "2026-01-00", "09/08/2026"}) {
            assertThrows(InvalidDateException.class, () -> Deadline.parseBy(text), text);
        }
    }

    @Test
    void markDone_thenUnmark_updatesDisplayAndSavedStatus() {
        Deadline task = new Deadline("Submit report", LocalDate.of(2026, 8, 9));
        assertEquals("[D][ ] Submit report (by: Aug 09 2026)", task.toString());
        assertEquals("D | 0 | Submit report | 2026-08-09", task.toFileFormat());

        task.markDone();
        assertEquals("[D][X] Submit report (by: Aug 09 2026)", task.toString());
        assertEquals("D | 1 | Submit report | 2026-08-09", task.toFileFormat());

        task.unmarkDone();
        assertEquals("D | 0 | Submit report | 2026-08-09", task.toFileFormat());
    }
}
