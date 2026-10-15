package otaku.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests event-specific date access and display formatting. */
public class EventTest {
    @Test
    public void toString_validEvent_formatsTypeStatusAndDates() {
        Event event = new Event("project meeting",
                LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 7));

        assertEquals("[E][ ] project meeting (from: Aug 05 2026 to: Aug 07 2026)", event.toString());
    }

    @Test
    public void dateGetters_validEvent_returnOriginalDates() {
        LocalDate from = LocalDate.of(2026, 12, 31);
        LocalDate to = LocalDate.of(2027, 1, 1);
        Event event = new Event("countdown", from, to);

        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
    }

    @Test
    public void constructor_invalidDates_throwsAssertionError() {
        LocalDate date = LocalDate.of(2026, 8, 5);

        assertThrows(AssertionError.class, () -> new Event("meeting", date, date));
        assertThrows(AssertionError.class,
                () -> new Event("meeting", date, date.minusDays(1)));
    }
}
