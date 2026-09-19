package lizzy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests date matching for deadline tasks.
 */
public class DeadlineTest {
    @Test
    void occursOn_matchingDate_trueAndOtherDatesFalse() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 3));

        assertTrue(deadline.occursOn(LocalDate.of(2026, 9, 3)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 9, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 9, 4)));
    }

    @Test
    void constructor_nullDate_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("submit report", null));
    }

    @Test
    void gettersAndToString_completedDeadline_detailsReturnedAndFormatted() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 3), true);

        assertEquals(LocalDate.of(2026, 9, 3), deadline.getBy());
        assertEquals("[D][X] submit report (by: Sep 3 2026)", deadline.toString());
        assertTrue(deadline.isDone());
    }
}
