package chatty.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests recurring-task schedule advancement and display behavior.
 */
public class RecurringTaskTest {
    /** Verifies that a monthly schedule uses calendar-aware date arithmetic. */
    @Test
    public void advanceToNextOccurrence_endOfMonth_dateAdjustedByCalendar() {
        RecurringTask task = new RecurringTask("month-end review",
                LocalDate.of(2027, 1, 31), 1, RecurrenceUnit.MONTH);

        task.advanceToNextOccurrence();

        assertEquals(LocalDate.of(2027, 2, 28), task.getNextOccurrenceDate());
    }

    /** Verifies that singular and plural user inputs map to supported recurrence units. */
    @Test
    public void fromInput_singularAndPluralUnits_unitsParsed() {
        assertEquals(RecurrenceUnit.DAY, RecurrenceUnit.fromInput("day"));
        assertEquals(RecurrenceUnit.DAY, RecurrenceUnit.fromInput("days"));
        assertEquals(RecurrenceUnit.WEEK, RecurrenceUnit.fromInput("week"));
        assertEquals(RecurrenceUnit.WEEK, RecurrenceUnit.fromInput("weeks"));
        assertEquals(RecurrenceUnit.MONTH, RecurrenceUnit.fromInput("month"));
        assertEquals(RecurrenceUnit.MONTH, RecurrenceUnit.fromInput("months"));
        assertEquals(RecurrenceUnit.YEAR, RecurrenceUnit.fromInput("year"));
        assertEquals(RecurrenceUnit.YEAR, RecurrenceUnit.fromInput("years"));
    }

    /** Verifies that every supported recurrence unit advances by its calendar meaning. */
    @Test
    public void addTo_supportedUnits_datesAdvancedCorrectly() {
        LocalDate date = LocalDate.of(2026, 9, 8);

        assertEquals(LocalDate.of(2026, 9, 10), RecurrenceUnit.DAY.addTo(date, 2));
        assertEquals(LocalDate.of(2026, 9, 22), RecurrenceUnit.WEEK.addTo(date, 2));
        assertEquals(LocalDate.of(2026, 11, 8), RecurrenceUnit.MONTH.addTo(date, 2));
        assertEquals(LocalDate.of(2028, 9, 8), RecurrenceUnit.YEAR.addTo(date, 2));
    }

    /** Verifies that recurring tasks cannot enter an invalid completed state directly. */
    @Test
    public void markAsDone_recurringTask_completionRejected() {
        RecurringTask task = new RecurringTask("project meeting",
                LocalDate.of(2026, 9, 14), 1, RecurrenceUnit.WEEK);

        assertThrows(UnsupportedOperationException.class, task::markAsDone);
        assertFalse(task.isDone());
    }

    /** Verifies that display output includes the next date and pluralized interval. */
    @Test
    public void toString_twoWeekSchedule_scheduleDetailsDisplayed() {
        RecurringTask task = new RecurringTask("project meeting",
                LocalDate.of(2026, 9, 14), 2, RecurrenceUnit.WEEK);

        assertEquals("[R][ ] project meeting (on: Sep 14 2026, every: 2 weeks)",
                task.toString());
    }
}
