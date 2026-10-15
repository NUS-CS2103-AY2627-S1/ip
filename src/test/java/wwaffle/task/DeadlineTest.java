package wwaffle.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    @Test
    void constructor_validIsoDate_storesDate() {
        Deadline deadline = new Deadline("return book", "2026-12-02");

        assertEquals(LocalDate.of(2026, 12, 2), deadline.getDueDate());
    }

    @Test
    void constructor_invalidDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("return book", "tomorrow"));
    }

    @Test
    void toString_validDeadline_usesFriendlyDateFormat() {
        Deadline deadline = new Deadline("return book", "2026-12-02");

        assertEquals("[D][ ] return book (by: Dec 2 2026)", deadline.toString());
    }
}
