package nubish.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import nubish.utils.NubishException;

/**
 * Tests event date range validation.
 */
public class EventTest {
    /**
     * Verifies that a new event starts as not done.
     */
    @Test
    public void getStatusIcon_newEvent_returnsUndoneIcon() {
        Task task = new Event("meeting", "12/10/2020 1800", "12/10/2020 1900");

        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that a loaded done event shows the done icon.
     */
    @Test
    public void getStatusIcon_savedDoneEvent_returnsDoneIcon() {
        Task task = new Event(true, "meeting", "12/10/2020 1800", "12/10/2020 1900");

        assertEquals("X", task.getStatusIcon());
    }

    /**
     * Verifies that an undone event is saved in the expected file format.
     */
    @Test
    public void saveString_unmarkedEvent_returnsCorrectFormat() {
        Task task = new Event("meeting", "12/10/2020 1800", "12/10/2020 1900");

        assertEquals("E | 0 | meeting | 12/10/2020 1800 | 12/10/2020 1900", task.saveString());
    }

    /**
     * Verifies that a done event is saved in the expected file format.
     */
    @Test
    public void saveString_markedEvent_returnsCorrectFormat() {
        Task task = new Event(true, "meeting", "12/10/2020 1800", "12/10/2020 1900");

        assertEquals("E | 1 | meeting | 12/10/2020 1800 | 12/10/2020 1900", task.saveString());
    }

    /**
     * Verifies that an event is displayed in the expected user-facing format.
     */
    @Test
    public void toString_validEvent_returnsFormattedEvent() {
        Task task = new Event("meeting", "12/10/2020 1800", "12/10/2020 1900");

        assertEquals("[E][ ] meeting (from: 1800 Oct 12 2020 to: 1900 Oct 12 2020)", task.toString());
    }

    /**
     * Verifies that valid event date-times are parsed.
     */
    @Test
    public void convertToDateTime_validDateTime_returnsParsedDateTime() {
        Event event = new Event("meeting", "12/10/2020 1800", "12/10/2020 1900");

        assertEquals(LocalDateTime.of(2020, 10, 12, 18, 0), event.convertToDateTime("12/10/2020 1800"));
    }

    /**
     * Verifies that event date-times must use the expected format.
     */
    @Test
    public void convertToDateTime_wrongFormat_throwsException() {
        assertThrows(DateTimeParseException.class, () -> new Event("meeting", "12/10/20 1800", "12/10/2020 1900"));
    }

    /**
     * Verifies that events reject non-existent calendar dates.
     */
    @Test
    public void convertToDateTime_nonExistentDate_throwsException() {
        assertThrows(DateTimeParseException.class, () -> new Event("meeting", "30/02/2020 1800",
                "01/03/2020 1900"));
    }

    /**
     * Verifies that an event cannot end at the same time it starts.
     */
    @Test
    public void constructor_sameStartAndEnd_throwsException() {
        assertThrows(NubishException.class, () -> new Event("meeting", "12/10/2020 1800", "12/10/2020 1800"));
    }

    /**
     * Verifies that an event cannot end before it starts.
     */
    @Test
    public void constructor_startAfterEnd_throwsException() {
        assertThrows(NubishException.class, () -> new Event("meeting", "12/10/2020 1800", "12/10/2020 1700"));
    }
}
