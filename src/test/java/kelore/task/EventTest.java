package kelore.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the date range behavior of {@link Event}. */
public class EventTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 10, 9, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 8, 12, 17, 0);
    private static final Event MULTI_DAY_EVENT = new Event("Conference", START, END);

    @Test
    public void occursOn_dateBeforeEvent_returnsFalse() {
        assertFalse(MULTI_DAY_EVENT.occursOn(LocalDate.of(2026, 8, 9)));
    }

    @Test
    public void occursOn_startDate_returnsTrue() {
        assertTrue(MULTI_DAY_EVENT.occursOn(LocalDate.of(2026, 8, 10)));
    }

    @Test
    public void occursOn_dateBetweenStartAndEnd_returnsTrue() {
        assertTrue(MULTI_DAY_EVENT.occursOn(LocalDate.of(2026, 8, 11)));
    }

    @Test
    public void occursOn_endDate_returnsTrue() {
        assertTrue(MULTI_DAY_EVENT.occursOn(LocalDate.of(2026, 8, 12)));
    }

    @Test
    public void occursOn_dateAfterEvent_returnsFalse() {
        assertFalse(MULTI_DAY_EVENT.occursOn(LocalDate.of(2026, 8, 13)));
    }

    @Test
    public void occursOn_sameDayEventDate_returnsTrue() {
        Event sameDayEvent = new Event(
                "Workshop",
                LocalDateTime.of(2026, 8, 10, 9, 0),
                LocalDateTime.of(2026, 8, 10, 17, 0));

        assertTrue(sameDayEvent.occursOn(LocalDate.of(2026, 8, 10)));
    }

    @Test
    public void toString_incompleteEvent_formatsDescriptionAndDates() {
        assertEquals("[E][ ] Conference (from: Aug 10 2026, 9:00 AM "
                + "to: Aug 12 2026, 5:00 PM)", MULTI_DAY_EVENT.toString());
    }

    @Test
    public void toStorageString_markedEvent_formatsAllFields() {
        Event event = new Event("Conference", START, END);
        event.markAsDone();

        assertEquals("E | 1 | Conference | 2026-08-10T09:00 | 2026-08-12T17:00",
                event.toStorageString());
    }

    @Test
    public void constructor_sameStartAndEnd_assertionError() {
        assertThrows(AssertionError.class, () -> new Event("Instant", START, START));
    }

    @Test
    public void constructor_endBeforeStart_assertionError() {
        assertThrows(AssertionError.class, () -> new Event("Workshop", END, START));
    }

    @Test
    public void constructor_nullStartOrEnd_assertionError() {
        assertThrows(AssertionError.class, () -> new Event("Workshop", null, END));
        assertThrows(AssertionError.class, () -> new Event("Workshop", START, null));
    }
}
