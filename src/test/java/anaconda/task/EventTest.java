package anaconda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Tests event endpoints and type/status/date formatting.
 */
public class EventTest {
    @Test
    public void toString_eventAcrossYears_preservesBothEndpointsAndStatus() {
        LocalDate from = LocalDate.of(2025, 12, 31);
        LocalDate to = LocalDate.of(2026, 1, 2);
        Event task = new Event("holiday", from, to);
        assertEquals(from, task.getFrom());
        assertEquals(to, task.getTo());
        assertEquals(to, task.getEndDate());
        assertEquals("[E][ ] holiday (from: Dec 31 2025 to: Jan 02 2026)", task.toString());
        task.markAsDone();
        assertEquals("[E][X] holiday (from: Dec 31 2025 to: Jan 02 2026)", task.toString());
    }

    @Test
    @ResourceLock("DEFAULT_LOCALE")
    public void toString_sameDayAndNonEnglishLocale_keepsBothEnglishDates() {
        Locale original = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.FRENCH);
            LocalDate date = LocalDate.of(2026, 8, 9);
            assertEquals("[E][ ] meeting (from: Aug 09 2026 to: Aug 09 2026)",
                    new Event("meeting", date, date).toString());
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, original);
        }
    }
}
