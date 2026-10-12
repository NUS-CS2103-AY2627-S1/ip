package braun.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import braun.exception.BraunException;

/**
 * Unit tests for {@link Deadline} model behaviors.
 */
public class DeadlineTest {

    @Test
    public void constructor_validDate_success() throws BraunException {
        Deadline deadline = new Deadline("submit exploration report", "2026-08-30 1700");
        assertEquals("submit exploration report", deadline.getDescription());
        assertEquals("[D][ ] submit exploration report (by: Aug 30 2026, 5:00PM)", deadline.toString());
        assertEquals("D | 0 | submit exploration report | 2026-08-30 1700", deadline.toFileFormat());
    }

    @Test
    public void markAsDone_stateToggled_updatesStatus() throws BraunException {
        Deadline deadline = new Deadline("submit exploration report", "2026-08-30 1700");
        deadline.markAsDone();
        assertTrue(deadline.isDone());
        assertEquals("D | 1 | submit exploration report | 2026-08-30 1700", deadline.toFileFormat());

        deadline.markAsUndone();
        assertFalse(deadline.isDone());
    }

    @Test
    public void isOnDate_matchingDate_returnsTrue() throws BraunException {
        Deadline deadline = new Deadline("submit exploration report", "2026-08-30 1700");
        assertTrue(deadline.isOnDate(LocalDate.of(2026, 8, 30)));
        assertFalse(deadline.isOnDate(LocalDate.of(2026, 8, 31)));
    }

    @Test
    public void equals_sameDescriptionAndDate_returnsTrue() throws BraunException {
        Deadline first = new Deadline("submit exploration report", "2026-08-30 1700");
        Deadline second = new Deadline("SUBMIT EXPLORATION REPORT", "2026-08-30 1700");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void equals_differentDate_returnsFalse() throws BraunException {
        Deadline first = new Deadline("submit exploration report", "2026-08-30 1700");
        Deadline second = new Deadline("submit exploration report", "2026-08-31 1700");
        assertFalse(first.equals(second));
    }
}
