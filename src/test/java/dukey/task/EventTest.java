package dukey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import dukey.exception.DukeyException;

public class EventTest {

    @Test
    public void toString_newEvent_returnsEventTypeStatusAndFormattedDateTimes() throws DukeyException {
        Event event = new Event("project meeting", "2099-08-06 1400", "2099-08-06 1600");

        assertEquals("[E][ ] project meeting (from: Aug 06 2099, 2:00pm to: Aug 06 2099, 4:00pm)",
                event.toString());
    }

    @Test
    public void toFileString_newEvent_returnsStorageFormatWithInputDateTimes() throws DukeyException {
        Event event = new Event("project meeting", "2099-08-06 1400", "2099-08-06 1600");

        assertEquals("E | 0 | project meeting | 2099-08-06 1400 | 2099-08-06 1600 |", event.toFileString());
    }

    @Test
    public void toFileString_doneEvent_returnsStorageFormatWithDoneStatus() throws DukeyException {
        Event event = new Event("project meeting", "2099-08-06 1400", "2099-08-06 1600");
        event.markAsDone(LocalDateTime.of(2026, 9, 14, 14, 30));

        assertEquals("E | 1 | project meeting | 2099-08-06 1400 | 2099-08-06 1600 | 2026-09-14 1430",
                event.toFileString());
    }

    @Test
    public void occursOn_startDate_returnsTrue() throws DukeyException {
        Event event = new Event("conference", "2099-08-06 1400", "2099-08-08 1600");

        assertTrue(event.occursOn(LocalDate.of(2099, 8, 6)));
    }

    @Test
    public void occursOn_middleDate_returnsTrue() throws DukeyException {
        Event event = new Event("conference", "2099-08-06 1400", "2099-08-08 1600");

        assertTrue(event.occursOn(LocalDate.of(2099, 8, 7)));
    }

    @Test
    public void occursOn_endDate_returnsTrue() throws DukeyException {
        Event event = new Event("conference", "2099-08-06 1400", "2099-08-08 1600");

        assertTrue(event.occursOn(LocalDate.of(2099, 8, 8)));
    }

    @Test
    public void occursOn_dateBeforeEvent_returnsFalse() throws DukeyException {
        Event event = new Event("conference", "2099-08-06 1400", "2099-08-08 1600");

        assertFalse(event.occursOn(LocalDate.of(2099, 8, 5)));
    }

    @Test
    public void occursOn_dateAfterEvent_returnsFalse() throws DukeyException {
        Event event = new Event("conference", "2099-08-06 1400", "2099-08-08 1600");

        assertFalse(event.occursOn(LocalDate.of(2099, 8, 9)));
    }

    @Test
    public void constructor_startAfterEnd_throwsDukeyException() {
        DukeyException exception = assertThrows(DukeyException.class, () -> {
            new Event("project meeting", "2099-08-06 1600", "2099-08-06 1400");
        });

        assertEquals("Event start date/time cannot be later than end date/time.", exception.getMessage());
    }
}
