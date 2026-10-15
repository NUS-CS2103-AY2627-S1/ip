package food.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import food.exception.FoodInputException;

/**
 * Tests the input checks in the {@link Event} constructor: the shape of the line, the dates, and
 * the order of the two dates.
 */
public class EventTest {

    @Test
    public void constructor_validLine_datesShownInDisplayForm() throws FoodInputException {
        Event event = new Event("event camp /from 2026-09-07 /to 2026-09-09");
        assertEquals("[E] [] camp (from: 7/9/2026 to: 9/9/2026)", event.toString());
    }

    @Test
    public void constructor_sameStartAndEnd_accepted() throws FoodInputException {
        // A one-day event starts and ends on the same date.
        Event event = new Event("event camp /from 2026-09-07 /to 2026-09-07");
        assertEquals("[E] [] camp (from: 7/9/2026 to: 7/9/2026)", event.toString());
    }

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        FoodInputException e = assertThrows(FoodInputException.class,
                () -> new Event("event camp /from 2026-09-09 /to 2026-09-07"));
        assertEquals("An event can't end before it starts, chef.", e.getMessage());
    }

    @Test
    public void constructor_nonExistentDate_exceptionThrown() {
        // LocalDate.parse is strict, so 30 February is rejected rather than rolled over to March.
        assertThrows(FoodInputException.class,
                () -> new Event("event camp /from 2026-02-30 /to 2026-03-01"));
    }

    @Test
    public void constructor_fromGivenTwice_exceptionThrown() {
        FoodInputException e = assertThrows(FoodInputException.class,
                () -> new Event("event camp /from 2026-09-07 /from 2026-09-08 /to 2026-09-09"));
        assertEquals("One /from per ticket, chef.", e.getMessage());
    }

    @Test
    public void constructor_missingTo_exceptionThrown() {
        assertThrows(FoodInputException.class, () -> new Event("event camp /from 2026-09-07"));
    }
}
