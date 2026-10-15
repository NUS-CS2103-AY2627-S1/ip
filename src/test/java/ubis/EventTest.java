package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests the creation, validation, and serialization of Event tasks.
 */
public class EventTest {

    @Test
    public void initialise_validDateRange_returnsEvent() {
        Task event = new Event().initialise("busy times /from 2026-09-01 /to 2026-09-12");
        assertNotEquals(null, event);
    }

    @Test
    public void initialise_invalidDateFormat_returnsNull() {
        Task event = new Event().initialise("busy times /from 2026-99-01 /to 2026-09-12");
        assertNull(event);
    }

    @Test
    public void initialise_nonExistentDate_returnsNull() {
        assertNull(new Event().initialise("trip /from 2026-02-30 /to 2026-03-02"));
    }

    @Test
    public void initialise_reversedOrEqualDates_returnsNull() {
        assertNull(new Event().initialise("trip /from 2026-09-03 /to 2026-09-01"));
        assertNull(new Event().initialise("trip /from 2026-09-03 /to 2026-09-03"));
    }

    @Test
    public void initialise_repeatedReorderedOrTrailingParameters_returnsNull() {
        assertNull(new Event().initialise(
                "trip /from 2026-09-01 /from 2026-09-02 /to 2026-09-03"));
        assertNull(new Event().initialise("trip /to 2026-09-03 /from 2026-09-01"));
        assertNull(new Event().initialise("trip /from 2026-09-01 /to 2026-09-03 unexpected"));
    }

    @Test
    public void initialise_multipleSpaces_trimsAndAcceptsEvent() {
        Task event = new Event().initialise("  trip   /from   2026-09-01   /to   2026-09-03  ");
        assertEquals("{E}{0}{trip}{2026-09-01}{2026-09-03}", event.stringify());
    }

    @Test
    public void stringify_unmarkedAndMarkedEvent_returnsCorrectStorageString() {
        Task event = new Event().initialise("busy times /from 2026-09-01 /to 2026-09-12");
        assertEquals("{E}{0}{busy times}{2026-09-01}{2026-09-12}", event.stringify());
        event.mark();
        assertEquals("{E}{1}{busy times}{2026-09-01}{2026-09-12}", event.stringify());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t", "trip", "trip /from 2026-09-01", "trip /to 2026-09-03",
        "/from 2026-09-01 /to 2026-09-03", "trip /from /to 2026-09-03", "trip /from 2026-09-01 /to",
        "trip /from 2026-09-01 /to 2026-09-03 /to 2026-09-04",
        "trip /from 2026-09-01 /to 2026-02-30", "trip/from 2026-09-01 /to 2026-09-03",
        "trip /from2026-09-01 /to 2026-09-03", "trip /from 2026-09-01 /today 2026-09-03"})
    void initialise_missingOrMalformedDetails_returnsHelpfulError(String input) {
        Event event = new Event();
        assertNull(event.initialise(input));
        assertFalse(event.getInitialisationError().isBlank());
    }

    @ParameterizedTest
    @CsvSource({"2024-02-29,2024-03-01", "2026-12-31,2027-01-01"})
    void initialise_calendarBoundaries_preservesDates(String from, String to) {
        Task event = new Event().initialise("trip\t/from\t" + from + "\t/to\t" + to);
        assertEquals("{E}{0}{trip}{" + from + "}{" + to + "}", event.stringify());
        event.mark();
        event.unmark();
        assertEquals("{E}{0}{trip}{" + from + "}{" + to + "}", event.stringify());
    }
}
