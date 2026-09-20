package duchess.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests deadline-specific date storage and display behavior. */
public class DeadlineTest {
    /** Verifies that a deadline parses and displays its ISO date correctly. */
    @Test
    public void deadline_validDate_storesAndFormatsDate() {
        Deadline deadline = new Deadline("return book", "2019-12-02");

        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.toString());
    }

    /** Verifies that malformed deadline dates are rejected by the date model. */
    @Test
    public void deadline_invalidDate_throwsDateParseException() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("return book", "not-a-date"));
    }

    /** Verifies that a null date violates the deadline constructor precondition. */
    @Test
    public void deadline_nullDate_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
    }
}
