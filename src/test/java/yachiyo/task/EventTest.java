package yachiyo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the date-matching behavior of {@link Event}.
 */
public class EventTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 20, 9, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 8, 22, 17, 0);

    private final Event multiDayEvent = new Event("Orientation camp", START, END);

    @Test
    public void constructor_nullStartDateTime_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Event("Orientation camp", null, END));
    }

    @Test
    public void constructor_nullEndDateTime_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Event("Orientation camp", START, null));
    }

    @Test
    public void constructor_endNotAfterStart_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Event("Orientation camp", START, START));
        assertThrows(AssertionError.class, () -> new Event("Orientation camp", START, START.minusHours(1)));
    }

    @Test
    public void occursOn_dateBeforeEvent_falseReturned() {
        assertFalse(multiDayEvent.occursOn(LocalDate.of(2026, 8, 19)));
    }

    @Test
    public void occursOn_startDate_trueReturned() {
        assertTrue(multiDayEvent.occursOn(START.toLocalDate()));
    }

    @Test
    public void occursOn_dateBetweenStartAndEnd_trueReturned() {
        assertTrue(multiDayEvent.occursOn(LocalDate.of(2026, 8, 21)));
    }

    @Test
    public void occursOn_endDate_trueReturned() {
        assertTrue(multiDayEvent.occursOn(END.toLocalDate()));
    }

    @Test
    public void occursOn_dateAfterEvent_falseReturned() {
        assertFalse(multiDayEvent.occursOn(LocalDate.of(2026, 8, 23)));
    }

    @Test
    public void occursOn_singleDayEventDate_trueReturned() {
        LocalDate eventDate = LocalDate.of(2026, 8, 20);
        Event singleDayEvent = new Event(
                "Workshop",
                eventDate.atTime(9, 0),
                eventDate.atTime(17, 0)
        );

        assertTrue(singleDayEvent.occursOn(eventDate));
    }

    @Test
    public void toFileFormat_incompleteEvent_correctFormatReturned() {
        assertEquals(
                "EVENT | 0 | Orientation camp | 2026-08-20T09:00 | 2026-08-22T17:00",
                multiDayEvent.toFileFormat()
        );
    }

    @Test
    public void toFileFormat_completedEvent_correctFormatReturned() {
        multiDayEvent.markAsDone();

        assertEquals(
                "EVENT | 1 | Orientation camp | 2026-08-20T09:00 | 2026-08-22T17:00",
                multiDayEvent.toFileFormat()
        );
    }

    @Test
    public void toString_incompleteEvent_correctDisplayReturned() {
        assertEquals(
                "[E][ ] Orientation camp (from: Aug 20 2026, 9:00 AM, "
                        + "to: Aug 22 2026, 5:00 PM)",
                multiDayEvent.toString()
        );
    }

    @Test
    public void toString_completedEvent_correctDisplayReturned() {
        multiDayEvent.markAsDone();

        assertEquals(
                "[E][X] Orientation camp (from: Aug 20 2026, 9:00 AM, "
                        + "to: Aug 22 2026, 5:00 PM)",
                multiDayEvent.toString()
        );
    }

    @Test
    public void withDescription_completedEvent_onlyDescriptionChanged() {
        multiDayEvent.markAsDone();

        Event editedEvent = multiDayEvent.withDescription("Orientation briefing");

        assertNotSame(multiDayEvent, editedEvent);
        assertEquals("Orientation briefing", editedEvent.getDescription());
        assertEquals(START, editedEvent.getFrom());
        assertEquals(END, editedEvent.getTo());
        assertTrue(editedEvent.isCompleted());
    }

    @Test
    public void withFrom_completedEvent_onlyStartDateTimeChanged() {
        LocalDateTime replacementStart = START.plusHours(1);
        multiDayEvent.markAsDone();

        Event editedEvent = multiDayEvent.withFrom(replacementStart);

        assertNotSame(multiDayEvent, editedEvent);
        assertEquals("Orientation camp", editedEvent.getDescription());
        assertEquals(replacementStart, editedEvent.getFrom());
        assertEquals(END, editedEvent.getTo());
        assertTrue(editedEvent.isCompleted());
    }

    @Test
    public void withTo_completedEvent_onlyEndDateTimeChanged() {
        LocalDateTime replacementEnd = END.plusHours(1);
        multiDayEvent.markAsDone();

        Event editedEvent = multiDayEvent.withTo(replacementEnd);

        assertNotSame(multiDayEvent, editedEvent);
        assertEquals("Orientation camp", editedEvent.getDescription());
        assertEquals(START, editedEvent.getFrom());
        assertEquals(replacementEnd, editedEvent.getTo());
        assertTrue(editedEvent.isCompleted());
    }
}
