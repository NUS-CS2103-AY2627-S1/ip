package lizzy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the inclusive dates associated with {@link WithinPeriodTask}.
 */
public class WithinPeriodTaskTest {
    @Test
    void occursOn_boundaryAndInteriorDates_includesCompletePeriodOnly() {
        WithinPeriodTask task = new WithinPeriodTask("collect certificate", LocalDate.of(2026, 9, 3),
                LocalDate.of(2026, 9, 5));

        assertFalse(task.occursOn(LocalDate.of(2026, 9, 2)));
        assertTrue(task.occursOn(LocalDate.of(2026, 9, 3)));
        assertTrue(task.occursOn(LocalDate.of(2026, 9, 4)));
        assertTrue(task.occursOn(LocalDate.of(2026, 9, 5)));
        assertFalse(task.occursOn(LocalDate.of(2026, 9, 6)));
    }

    @Test
    void constructor_endBeforeStart_rejectsInvalidPeriod() {
        assertThrows(IllegalArgumentException.class, () -> new WithinPeriodTask("collect certificate",
                LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 3)));
    }

    @Test
    void constructor_nullDate_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new WithinPeriodTask("collect certificate", null,
                LocalDate.of(2026, 9, 3)));
        assertThrows(IllegalArgumentException.class, () -> new WithinPeriodTask("collect certificate",
                LocalDate.of(2026, 9, 3), null));
    }

    @Test
    void gettersAndToString_completedPeriodTask_detailsReturnedAndFormatted() {
        WithinPeriodTask task = new WithinPeriodTask("collect certificate", LocalDate.of(2026, 9, 3),
                LocalDate.of(2026, 9, 5), true);

        assertEquals(LocalDate.of(2026, 9, 3), task.getFrom());
        assertEquals(LocalDate.of(2026, 9, 5), task.getTo());
        assertEquals("[W][X] collect certificate (within: Sep 3 2026 to: Sep 5 2026)", task.toString());
        assertTrue(task.isDone());
    }

    @Test
    void constructor_sameStartAndEnd_singleDayPeriodAccepted() {
        WithinPeriodTask task = new WithinPeriodTask("collect certificate", LocalDate.of(2026, 9, 3),
                LocalDate.of(2026, 9, 3));

        assertTrue(task.occursOn(LocalDate.of(2026, 9, 3)));
    }
}
