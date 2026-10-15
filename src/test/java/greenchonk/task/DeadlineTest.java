package greenchonk.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    private static final LocalDate DUE_DATE = LocalDate.of(2026, 8, 28);

    @Test
    void occursOn_beforeOnAndAfterDueDate_matchesOnlyDueDate() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);

        assertFalse(deadline.occursOn(DUE_DATE.minusDays(1)));
        assertTrue(deadline.occursOn(DUE_DATE));
        assertFalse(deadline.occursOn(DUE_DATE.plusDays(1)));
    }

    @Test
    void accessorsAndDisplay_validDeadline_valuesAndFormatMatch() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);

        assertEquals(DUE_DATE, deadline.getDueDate());
        assertEquals("D", deadline.getTypeIcon());
        assertEquals("[D][ ] submit report (by: Aug 28 2026)", deadline.toString());
    }
}
