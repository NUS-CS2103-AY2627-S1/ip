package lion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline date-time validation.
 */
public class DeadlineTest {

    @Test
    void constructor_nullByText_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("desc", null));
    }

    @Test
    void constructor_unparseableDateText_fallsBackToNowInsteadOfThrowing() {
        // The constructor is also used by Storage.decode() while loading the save
        // file, where rejecting one unparseable line would stop the whole task list
        // from loading, so it falls back to "now" rather than propagating the error —
        // unlike validateByText(), which newly typed deadlines are checked against first.
        assertDoesNotThrow(() -> new Deadline("desc", "not a date"));
    }

    @Test
    void validateByText_validDate_doesNotThrow() {
        assertDoesNotThrow(() -> Deadline.validateByText("2/12/2026 1800"));
    }

    @Test
    void validateByText_leapYearFeb29_doesNotThrow() {
        assertDoesNotThrow(() -> Deadline.validateByText("29/2/2028 1800"));
    }

    @Test
    void validateByText_nonExistentDate_throwsLionException() {
        // 30 February does not exist on any calendar.
        assertThrows(LionException.class, () -> Deadline.validateByText("30/2/2026 1800"));
    }

    @Test
    void validateByText_nonLeapYearFeb29_throwsLionException() {
        assertThrows(LionException.class, () -> Deadline.validateByText("29/2/2026 1800"));
    }

    @Test
    void validateByText_wrongFormat_throwsLionException() {
        assertThrows(LionException.class, () -> Deadline.validateByText("next Monday"));
    }

    @Test
    void hasSameDetails_sameDescriptionAndDate_returnsTrue() {
        Deadline first = new Deadline("return book", "2/12/2026 1800");
        Deadline second = new Deadline("return book", "2/12/2026 1800");

        assertTrue(first.hasSameDetails(second));
    }

    @Test
    void hasSameDetails_sameDescriptionDifferentDate_returnsFalse() {
        Deadline first = new Deadline("return book", "2/12/2026 1800");
        Deadline second = new Deadline("return book", "3/12/2026 1800");

        assertFalse(first.hasSameDetails(second));
    }

    @Test
    void hasSameDetails_differentDescriptionSameDate_returnsFalse() {
        Deadline first = new Deadline("return book", "2/12/2026 1800");
        Deadline second = new Deadline("return dvd", "2/12/2026 1800");

        assertFalse(first.hasSameDetails(second));
    }
}
