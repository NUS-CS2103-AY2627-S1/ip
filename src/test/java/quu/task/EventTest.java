package quu.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import quu.exception.InvalidDateException;
import quu.exception.InvalidDurationException;
import quu.exception.MissingArgumentException;
import quu.exception.QuuException;
import quu.exception.UnsupportedEditException;

/**
 * Tests {@link Event}'s rendering of its two dates and its rejection of a backwards duration.
 */
public class EventTest {

    @Test
    public void toString_rendersBothDatesAsMonthDayYear() throws InvalidDurationException {
        Event event = new Event("project meeting", "2026-08-06", "2026-08-08");
        assertEquals("[E][ ] project meeting (from: Aug 6 2026 to: Aug 8 2026)", event.toString());
    }

    @Test
    public void toFileString_keepsTheIsoDates() throws InvalidDurationException {
        Event event = new Event("project meeting", "2026-08-06", "2026-08-08");
        assertEquals("E | 0 | project meeting /from 2026-08-06 /to 2026-08-08", event.toFileString());
    }

    @Test
    public void constructor_endBeforeStart_throwsInvalidDuration() {
        assertThrows(InvalidDurationException.class, () ->
                new Event("project meeting", "2026-08-08", "2026-08-06"));
    }

    @Test
    public void constructor_sameStartAndEnd_isAccepted() {
        assertDoesNotThrow(() -> new Event("project meeting", "2026-08-06", "2026-08-06"));
    }

    @Test
    public void toString_sameStartAndEnd_rendersBothDates() throws InvalidDurationException {
        Event event = new Event("project meeting", "2026-08-06", "2026-08-06");
        assertEquals("[E][ ] project meeting (from: Aug 6 2026 to: Aug 6 2026)", event.toString());
    }

    @Test
    public void toFileString_thenFromFileString_roundTripsTask()
            throws InvalidDurationException, InvalidDateException, MissingArgumentException {
        Event original = new Event("project meeting", "2026-08-06", "2026-08-08");
        Event restored = Event.fromFileString(original.toFileString().split("\\s*\\|\\s*", 3));
        assertEquals(original.toString(), restored.toString());
    }

    @Test
    public void fromFileString_missingToClause_throwsMissingArgument() {
        String[] fields = {"E", "0", "project meeting /from 2026-08-06"};
        assertThrows(MissingArgumentException.class, () -> Event.fromFileString(fields));
    }

    @Test
    public void withEdits_endDateOnly_keepsStartDateAndDescription() throws QuuException {
        Event editedEvent = new Event("project meeting", "2026-08-06", "2026-08-08")
                .withEdits(Map.of("to", "2026-08-10"));
        assertEquals("[E][ ] project meeting (from: Aug 6 2026 to: Aug 10 2026)", editedEvent.toString());
    }

    @Test
    public void withEdits_descriptionOnly_keepsBothDates() throws QuuException {
        Event editedEvent = new Event("project meeting", "2026-08-06", "2026-08-08")
                .withEdits(Map.of(Task.KEY_DESCRIPTION, "team meeting"));
        assertEquals("[E][ ] team meeting (from: Aug 6 2026 to: Aug 8 2026)", editedEvent.toString());
    }

    @Test
    public void withEdits_endDateBeforeUneditedStartDate_throwsInvalidDuration() {
        assertThrows(InvalidDurationException.class, () ->
                new Event("project meeting", "2026-08-06", "2026-08-08").withEdits(Map.of("to", "2026-08-01")));
    }

    @Test
    public void withEdits_flagAnEventDoesNotHave_throwsUnsupportedEdit() {
        assertThrows(UnsupportedEditException.class, () ->
                new Event("project meeting", "2026-08-06", "2026-08-08").withEdits(Map.of("by", "2026-08-10")));
    }

    @Test
    public void withEdits_dateThatIsNotADate_throwsInvalidDate() {
        assertThrows(InvalidDateException.class, () ->
                new Event("project meeting", "2026-08-06", "2026-08-08").withEdits(Map.of("from", "sometime")));
    }

}
