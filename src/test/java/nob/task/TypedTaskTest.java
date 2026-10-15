package nob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the type-specific validation and display formats of Nob tasks.
 */
public class TypedTaskTest {
    @Test
    public void todo_toString_typePrefixIncluded() {
        assertEquals("[T] read book [ ]", new Todo("read book").toString());
    }

    @Test
    public void deadline_parseableDate_formattedDateDisplayed() {
        assertEquals("[D: Dec 02 2019, 6:00PM] return book [ ]",
                new Deadline("return book", "2/12/2019 1800").toString());
    }

    @Test
    public void deadline_unrecognisedDate_originalTrimmedTextDisplayed() {
        assertEquals("[D: Friday] return book [ ]", new Deadline("return book", "  Friday  ").toString());
    }

    @Test
    public void deadline_nullOrBlankDate_assertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("return book", null));
        assertThrows(AssertionError.class, () -> new Deadline("return book", "   "));
    }

    @Test
    public void event_parseableDates_formattedDatesDisplayed() {
        Event event = new Event("team sync", "2019-12-02 1800", "2019-12-02 1900");

        assertEquals("[E: Dec 02 2019, 6:00PM to Dec 02 2019, 7:00PM] team sync [ ]", event.toString());
    }

    @Test
    public void event_unrecognisedDates_originalTrimmedTextDisplayed() {
        assertEquals("[E: Monday to Tuesday] team sync [ ]",
                new Event("team sync", " Monday ", " Tuesday ").toString());
    }

    @Test
    public void event_nullOrBlankDates_assertionError() {
        assertThrows(AssertionError.class, () -> new Event("team sync", null, "Tuesday"));
        assertThrows(AssertionError.class, () -> new Event("team sync", "   ", "Tuesday"));
        assertThrows(AssertionError.class, () -> new Event("team sync", "Monday", null));
        assertThrows(AssertionError.class, () -> new Event("team sync", "Monday", "   "));
    }
}
