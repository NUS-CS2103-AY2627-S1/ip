package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Deadline}.
 */
public class DeadlineTest {

    @Test
    public void toString_septemberDate_usesEnglishMonthAbbreviation() {
        Deadline task = new Deadline("report", LocalDate.of(2026, 9, 20));
        assertEquals("[D][ ] report (by: Sep 20 2026)", task.toString());
    }

    /**
     * Tests that the constructor correctly initializes the description, due date, and completion status.
     */
    @Test
    public void constructor_validParameters_setsDescriptionAndByDate() {
        LocalDate date = LocalDate.of(2026, 6, 6);
        Deadline deadline = new Deadline("submit report", date);

        assertEquals("submit report", deadline.getDescription());
        assertEquals(date, deadline.getBy());
        assertFalse(deadline.isDone());
    }

    /**
     * Tests that toFileString returns the correct serialization string for an unmarked deadline.
     */
    @Test
    public void toFileString_unmarkedDeadline_returnsFormattedString() {
        LocalDate date = LocalDate.of(2026, 6, 6);
        Deadline deadline = new Deadline("submit report", date);

        assertEquals("D | 0 | submit report | 2026-06-06", deadline.toFileString());
    }

    /**
     * Tests that toFileString returns the correct serialization string for a marked deadline.
     */
    @Test
    public void toFileString_markedDeadline_returnsFormattedString() {
        LocalDate date = LocalDate.of(2026, 6, 6);
        Deadline deadline = new Deadline("submit report", date);
        deadline.markDone();

        assertTrue(deadline.isDone());
        assertEquals("D | 1 | submit report | 2026-06-06", deadline.toFileString());
    }

    /**
     * Tests that toString returns the formatted display string for an unmarked deadline.
     */
    @Test
    public void toString_unmarkedDeadline_returnsDisplayString() {
        LocalDate date = LocalDate.of(2026, 6, 6);
        Deadline deadline = new Deadline("submit report", date);

        assertEquals("[D][ ] submit report (by: Jun 06 2026)", deadline.toString());
    }

    /**
     * Tests that toString returns the formatted display string for a marked deadline.
     */
    @Test
    public void toString_markedDeadline_returnsDisplayString() {
        LocalDate date = LocalDate.of(2026, 6, 6);
        Deadline deadline = new Deadline("submit report", date);
        deadline.markDone();

        assertEquals("[D][X] submit report (by: Jun 06 2026)", deadline.toString());
    }
}
