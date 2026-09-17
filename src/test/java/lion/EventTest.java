package lion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests event start/end range validation.
 */
public class EventTest {

    @Test
    void constructor_nullFrom_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("desc", null, "to"));
    }

    @Test
    void constructor_nullTo_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("desc", "from", null));
    }

    @Test
    void validateRange_startBeforeEnd_doesNotThrow() {
        assertDoesNotThrow(() -> Event.validateRange("2/12/2026 1800", "2/12/2026 2000"));
    }

    @Test
    void validateRange_startAfterEnd_throwsLionException() {
        assertThrows(LionException.class, () -> Event.validateRange("2/12/2026 2000", "2/12/2026 1800"));
    }

    @Test
    void validateRange_startEqualsEnd_throwsLionException() {
        assertThrows(LionException.class, () -> Event.validateRange("2/12/2026 1800", "2/12/2026 1800"));
    }

    @Test
    void validateRange_freeTextTimes_doesNotThrow() {
        // Event has always accepted arbitrary text for its start/end (e.g. "8pm"); since
        // that is not a comparable date-time, the range check must not reject it.
        assertDoesNotThrow(() -> Event.validateRange("8pm", "7pm"));
    }

    @Test
    void validateRange_oneSideFreeTextOneSideDate_doesNotThrow() {
        assertDoesNotThrow(() -> Event.validateRange("8pm", "2/12/2026 1800"));
    }

    @Test
    void validateRange_startIsNonExistentDate_treatedAsFreeTextAndDoesNotThrow() {
        // 30 February is not a real date, so it is not treated as a comparable
        // date-time — this matches how Deadline's own strict parser rejects it, but
        // here it simply falls back to being incomparable free text rather than an error.
        assertDoesNotThrow(() -> Event.validateRange("30/2/2026 1800", "2/12/2026 1800"));
    }

    @Test
    void validateRange_dateStartFreeTextEnd_doesNotThrow() {
        assertDoesNotThrow(() -> Event.validateRange("2/12/2026 1800", "8pm"));
    }

    @Test
    void hasSameDetails_sameDescriptionStartAndEnd_returnsTrue() {
        Event first = new Event("meeting", "Monday 2pm", "Monday 4pm");
        Event second = new Event("meeting", "Monday 2pm", "Monday 4pm");

        assertTrue(first.hasSameDetails(second));
    }

    @Test
    void hasSameDetails_differentDescription_returnsFalse() {
        Event first = new Event("meeting", "Monday 2pm", "Monday 4pm");
        Event second = new Event("standup", "Monday 2pm", "Monday 4pm");

        assertFalse(first.hasSameDetails(second));
    }

    @Test
    void hasSameDetails_differentTaskType_returnsFalse() {
        Event event = new Event("meeting", "Monday 2pm", "Monday 4pm");

        assertFalse(event.hasSameDetails(new Todo("meeting")));
    }

    @Test
    void hasSameDetails_differentStart_returnsFalse() {
        Event first = new Event("meeting", "Monday 2pm", "Monday 4pm");
        Event second = new Event("meeting", "Monday 3pm", "Monday 4pm");

        assertFalse(first.hasSameDetails(second));
    }

    @Test
    void hasSameDetails_differentEnd_returnsFalse() {
        Event first = new Event("meeting", "Monday 2pm", "Monday 4pm");
        Event second = new Event("meeting", "Monday 2pm", "Monday 5pm");

        assertFalse(first.hasSameDetails(second));
    }
}
