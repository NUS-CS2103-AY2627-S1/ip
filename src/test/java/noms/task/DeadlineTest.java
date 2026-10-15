package noms.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Deadline#occursOn(LocalDate)}, which reports whether a
 * deadline falls exactly on a given date. This backs the {@code on} command's
 * matching of deadlines, so both the matching and non-matching cases are
 * checked.
 */
public class DeadlineTest {

    private final Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 1));

    @Test
    public void occursOn_sameDate_returnsTrue() {
        assertTrue(deadline.occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_differentDate_returnsFalse() {
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void rescheduleTo_newDate_replacesDueDate() {
        deadline.rescheduleTo(LocalDate.of(2026, 9, 20));

        assertEquals("[D][ ] return book (by: Sep 20 2026)", deadline.toString());
    }
}
