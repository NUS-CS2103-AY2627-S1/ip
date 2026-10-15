package greenchonk.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class TaskDateTest {
    private static final LocalDate LEAP_DAY = LocalDate.of(2028, 2, 29);

    @Test
    void parse_supportedFormats_sameDateReturned() {
        assertEquals(LEAP_DAY, TaskDate.parse("2028-02-29"));
        assertEquals(LEAP_DAY, TaskDate.parse("29/2/2028"));
        assertEquals(LEAP_DAY, TaskDate.parse("29 Feb 2028"));
        assertEquals(LEAP_DAY, TaskDate.parse("29 fEb 2028"));
    }

    @Test
    void parse_invalidOrUnsupportedDates_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> TaskDate.parse("2026-02-29"));
        assertThrows(DateTimeParseException.class, () -> TaskDate.parse("29/2/2026"));
        assertThrows(DateTimeParseException.class, () -> TaskDate.parse("29-02-2028"));
        assertThrows(DateTimeParseException.class, () -> TaskDate.parse("tomorrow"));
    }

    @Test
    void format_validDate_friendlyEnglishDateReturned() {
        assertEquals("Feb 29 2028", TaskDate.format(LEAP_DAY));
    }
}
