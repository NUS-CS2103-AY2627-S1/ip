package allmind.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests deadline task data and display behavior. */
public class DeadlineTest {
    @Test
    public void constructor_validDate_storesDateAndFormatsTask() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 10, 20));

        assertEquals(LocalDate.of(2026, 10, 20), deadline.getBy());
        assertEquals("[D][ ] submit report (by: Oct 20 2026)", deadline.toString());
    }

    @Test
    public void constructor_nullDate_assertionThrown() {
        assertThrows(AssertionError.class, () -> new Deadline("submit report", null));
    }
}
