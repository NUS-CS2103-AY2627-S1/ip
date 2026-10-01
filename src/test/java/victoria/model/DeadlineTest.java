package victoria.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import victoria.exception.InvalidDeadlineException;

class DeadlineTest {
    @Test
    void constructor_isoDate_formatsAndExposesDate() {
        Deadline deadline = new Deadline("submit report", "2026-09-12");

        assertEquals("2026-09-12", deadline.getBy());
        assertEquals(LocalDate.of(2026, 9, 12), deadline.getDate());
        assertEquals("[D][ ] submit report (by: Sep 12 2026)", deadline.toString());
    }

    @Test
    void constructor_invalidOrMissingDate_throwsUserFacingException() {
        assertThrows(InvalidDeadlineException.class, () -> new Deadline("task", "12/09/2026"));
        assertThrows(InvalidDeadlineException.class, () -> new Deadline("task", (String) null));
    }
}
