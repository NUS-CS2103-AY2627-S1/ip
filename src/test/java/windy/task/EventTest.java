package windy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import windy.exception.InvalidInputFormatException;

public class EventTest {

    @Test
    public void constructor_sameStartAndEndDate_isValidAndOccursOnThatDate()
            throws InvalidInputFormatException {
        Event event = new Event("meeting", false, "2026-9-3", "2026-9-3");

        assertTrue(event.occursOnDate(LocalDate.of(2026, 9, 3)));
        assertEquals("E | 0 | meeting | 2026-09-03 | 2026-09-03", event.toDataString());
    }

    @Test
    public void constructor_endDateBeforeStartDate_throwsInvalidInputFormatException() {
        Executable createEvent = () -> new Event("holiday", false, "2026-9-3", "2026-8-27");

        InvalidInputFormatException exception = assertThrows(InvalidInputFormatException.class, createEvent);

        assertEquals("The event end date cannot be before its start date", exception.getMessage());
    }
}
