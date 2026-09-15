package monkey.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeadlineTest {
    @Test
    void constructor_supportedDateFormats_storesCanonicalDate() {
        assertEquals("2099-01-02", new Deadline("submit", "2099-01-02").getStorageValue());
        assertEquals("2099-01-02", new Deadline("submit", "2/1/2099").getStorageValue());
    }

    @Test
    void constructor_supportedDateTimeFormats_displaysReadableDateTime() {
        Deadline slashDateTime = new Deadline("submit", "2/1/2099 1730");
        Deadline isoDateTime = new Deadline("submit", "2099-01-02 1730");

        assertEquals("Jan 02 2099, 5:30 PM", slashDateTime.getBy());
        assertEquals("2099-01-02 1730", isoDateTime.getStorageValue());
    }

    @Test
    void constructor_blankOrInvalidDate_rejectsInput() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("submit", null));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("submit", ""));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("submit", "2026-02-30"));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("submit", "next week"));
    }

    @Test
    void reschedule_validDate_replacesDeadline() {
        Deadline deadline = new Deadline("submit", "2099-01-01");

        deadline.reschedule("2099-02-01");

        assertEquals("2099-02-01", deadline.getStorageValue());
    }

    @Test
    void isInFuture_pastAndFutureValues_returnsChronologicalResult() {
        assertFalse(new Deadline("past date", "2000-01-01").isInFuture());
        assertFalse(new Deadline("past time", "2000-01-01 1200").isInFuture());
        assertTrue(new Deadline("future date", "2099-01-01").isInFuture());
        assertTrue(new Deadline("future time", "2099-01-01 1200").isInFuture());
    }

    @Test
    void hasSameDetails_typeDescriptionAndDate_match_returnsTrue() {
        Deadline deadline = new Deadline("Submit", "2/1/2099");

        assertTrue(deadline.hasSameDetails(new Deadline("submit", "2099-01-02")));
        assertFalse(deadline.hasSameDetails(new Deadline("submit", "2099-01-03")));
        assertFalse(deadline.hasSameDetails(new ToDos("submit")));
        assertEquals("[D][ ] Submit (by: Jan 02 2099)", deadline.toString());
    }
}
