package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests the creation, validation, and serialization of Deadline tasks.
 */
public class DeadlineTest {

    @Test
    public void initialise_invalidDateFormat_returnsNull() {
        assertNull(new Deadline().initialise("submit report /by 2026-13-01"));
    }

    @Test
    public void initialise_nonExistentDate_returnsNull() {
        assertNull(new Deadline().initialise("submit report /by 2026-02-30"));
    }

    @Test
    public void initialise_repeatedOrTrailingParameter_returnsNull() {
        assertNull(new Deadline().initialise("submit report /by 2026-09-01 /by 2026-09-02"));
        assertNull(new Deadline().initialise("submit report /by 2026-09-01 unexpected"));
    }

    @Test
    public void initialise_multipleSpaces_trimsAndAcceptsDeadline() {
        Task deadline = new Deadline().initialise("  submit report   /by   2026-09-01  ");
        assertEquals("{D}{0}{submit report}{2026-09-01}", deadline.stringify());
    }

    @Test
    public void initialise_validDate_returnsDeadline() {
        assertNotEquals(null, new Deadline().initialise("submit report /by 2026-09-01"));
    }

    @Test
    public void stringify_unmarkedAndMarkedDeadline_returnsCorrectStorageString() {
        assertEquals("{D}{0}{submit report}{2026-09-01}",
                new Deadline().initialise("submit report /by 2026-09-01").stringify());

        Task deadline = new Deadline().initialise("submit report /by 2026-09-01");
        assertEquals("{D}{0}{submit report}{2026-09-01}", deadline.stringify());
        deadline.mark();
        assertEquals("{D}{1}{submit report}{2026-09-01}", deadline.stringify());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t", "report", "/by 2026-09-01", "report /by", "report /bye 2026-09-01",
        "report/by 2026-09-01", "report /by2026-09-01", "report /by 2025-02-29", "report /by 1900-02-29"})
    void initialise_missingOrMalformedDetails_returnsHelpfulError(String input) {
        Deadline deadline = new Deadline();
        assertNull(deadline.initialise(input));
        assertFalse(deadline.getInitialisationError().isBlank());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2000-02-29", "2024-02-29", "2026-12-31"})
    void initialise_calendarBoundaries_preservesDate(String date) {
        Task deadline = new Deadline().initialise("report\t/by\t" + date);
        assertEquals("{D}{0}{report}{" + date + "}", deadline.stringify());
        deadline.mark();
        deadline.unmark();
        assertEquals("{D}{0}{report}{" + date + "}", deadline.stringify());
    }
}
