package computa;

import org.junit.jupiter.api.Test;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

class RecurringTaskTest {
    @Test void ruleAndInstanceAreDistinguishable() {
        RecurringTask rule = new RecurringTask("report", DayOfWeek.MONDAY);
        RecurringTask instance = new RecurringTask("report", DayOfWeek.MONDAY,
                LocalDate.of(2026, 9, 14), false);
        assertFalse(rule.isInstance());
        assertTrue(instance.isInstance());
        assertEquals("[R] report (every MONDAY)\n", rule.getTaskDescription());
        assertTrue(instance.getTaskDescription().contains("week of 2026-09-14"));
    }

    @Test void ruleIsDueForMostRecentScheduledDay() {
        RecurringTask rule = new RecurringTask("report", DayOfWeek.MONDAY);
        assertTrue(rule.isDueToday(LocalDate.of(2026, 9, 15)));
        rule.markGenerated(LocalDate.of(2026, 9, 14));
        assertFalse(rule.isDueToday(LocalDate.of(2026, 9, 15)));
        assertTrue(rule.isDueToday(LocalDate.of(2026, 9, 21)));
    }
}
