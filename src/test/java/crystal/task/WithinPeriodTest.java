package crystal.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests within-period task date matching and representations.
 */
public class WithinPeriodTest {
    /** Verifies normalized display and storage forms for a within-period task. */
    @Test
    public void representations_dateRange_usesNormalizedDates() {
        WithinPeriod task = new WithinPeriod("collect certificate", "15Jan27", "25 January 2027");

        assertEquals("W | 0 | collect certificate | 15 Jan 2027 | 25 Jan 2027",
                task.toDataString());
        assertEquals("[W][ ] collect certificate (from: 15 Jan 2027 to: 25 Jan 2027)",
                task.toString());
    }

    /** Verifies that the completion period includes both endpoints and intervening dates. */
    @Test
    public void occursOn_dateRange_matchesInclusiveRangeOnly() {
        WithinPeriod task = new WithinPeriod("collect certificate", "15Jan27", "25Jan27");

        assertTrue(task.occursOn(LocalDate.of(2027, 1, 15)));
        assertTrue(task.occursOn(LocalDate.of(2027, 1, 20)));
        assertTrue(task.occursOn(LocalDate.of(2027, 1, 25)));
        assertFalse(task.occursOn(LocalDate.of(2027, 1, 14)));
        assertFalse(task.occursOn(LocalDate.of(2027, 1, 26)));
    }

    /** Verifies that unrestricted scheduling text is preserved and matches no date. */
    @Test
    public void representations_textPeriod_preservesTextAndMatchesNoDate() {
        WithinPeriod task = new WithinPeriod(
                "collect certificate", "after the ceremony", "before the office closes");

        assertEquals("W | 0 | collect certificate | after the ceremony | before the office closes",
                task.toDataString());
        assertEquals("[W][ ] collect certificate "
                + "(from: after the ceremony to: before the office closes)", task.toString());
        assertFalse(task.occursOn(LocalDate.of(2027, 1, 20)));
    }

    /** Verifies that a reversed dated period follows event endpoint matching behavior. */
    @Test
    public void occursOn_reversedDateRange_matchesEndpointsOnly() {
        WithinPeriod task = new WithinPeriod("collect certificate", "25Jan27", "15Jan27");

        assertTrue(task.occursOn(LocalDate.of(2027, 1, 25)));
        assertTrue(task.occursOn(LocalDate.of(2027, 1, 15)));
        assertFalse(task.occursOn(LocalDate.of(2027, 1, 20)));
    }

    /** Verifies that completed within-period tasks persist their completed status. */
    @Test
    public void toDataString_completedTask_includesCompletedStatus() {
        WithinPeriod task = new WithinPeriod("collect certificate", "15Jan27", "25Jan27");
        task.markAsDone();

        assertEquals("W | 1 | collect certificate | 15 Jan 2027 | 25 Jan 2027",
                task.toDataString());
    }
}
