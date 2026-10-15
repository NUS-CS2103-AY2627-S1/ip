package samantha.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import samantha.exception.InputException;
import samantha.exception.TaskValidationException;

class EventTest {
    @Test
    void constructor_missingStartOrEnd_taskValidationExceptionThrown() {
        assertThrows(TaskValidationException.class, () ->
                new Event("meeting", "", "2/12/2019 1600"));
        assertThrows(TaskValidationException.class, () ->
                new Event("meeting", "2/12/2019 1400", "   "));
    }

    @Test
    void constructor_dateWithoutTime_taskValidationExceptionThrown() {
        assertThrows(TaskValidationException.class, () ->
                new Event("meeting", "2/12/2019", "2/12/2019 1600"));
    }

    @Test
    void constructor_endNotAfterStart_taskValidationExceptionThrown() {
        assertThrows(TaskValidationException.class, () ->
                new Event("meeting", "2/12/2019 1600", "2/12/2019 1400"));
        assertThrows(TaskValidationException.class, () ->
                new Event("meeting", "2/12/2019 1400", "2/12/2019 1400"));
    }

    @Test
    void event_dateWithinInclusiveRange_reportsDetailsAndSerializesStatus()
            throws TaskValidationException, InputException {
        Event event = new Event("conference", "2/12/2019 1400", "4/12/2019 1600");

        assertEquals("[E]", event.getType());
        assertFalse(event.isOnDate(LocalDate.of(2019, 12, 1)));
        assertTrue(event.isOnDate(LocalDate.of(2019, 12, 2)));
        assertTrue(event.isOnDate(LocalDate.of(2019, 12, 3)));
        assertTrue(event.isOnDate(LocalDate.of(2019, 12, 4)));
        assertFalse(event.isOnDate(LocalDate.of(2019, 12, 5)));
        assertEquals("[E][ ] conference (from: Dec 2 2019, 2:00 PM to: Dec 4 2019, 4:00 PM)",
                event.toString());
        assertEquals("E | 0 | conference | 2/12/2019 1400 to 4/12/2019 1600", event.toFileString());

        event.markDone();
        assertEquals("E | 1 | conference | 2/12/2019 1400 to 4/12/2019 1600", event.toFileString());
    }
}
