package auto.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Tests strict user/storage date parsing and locale-stable formatting. */
class DateUtilTest {
    @Test
    void parseInput_validBoundaryAndLeapDates_returnsExpectedDates() {
        assertAll(
                () -> assertEquals(LocalDate.of(2026, 1, 1),
                        DateUtil.parseInput("01/01/2026")),
                () -> assertEquals(LocalDate.of(2026, 12, 31),
                        DateUtil.parseInput("31/12/2026")),
                () -> assertEquals(LocalDate.of(2024, 2, 29),
                        DateUtil.parseInput("29/02/2024")));
    }

    @Test
    void parseInput_wrongFormatWhitespaceAndImpossibleDates_throwsIllegalArgumentException() {
        assertAll(
                () -> assertInvalidInputDate("2026-08-22"),
                () -> assertInvalidInputDate("22/8/2026"),
                () -> assertInvalidInputDate(" 22/08/2026 "),
                () -> assertInvalidInputDate(""),
                () -> assertInvalidInputDate("31/04/2026"),
                () -> assertInvalidInputDate("29/02/2025"));
    }

    @Test
    void parseStored_validEnglishDate_returnsExpectedDate() {
        assertEquals(LocalDate.of(2026, 8, 22), DateUtil.parseStored("Aug 22 2026"));
    }

    @Test
    void parseStored_wrongFormatCaseAndImpossibleDate_throwsIllegalArgumentException() {
        assertAll(
                () -> assertInvalidStoredDate("22/08/2026"),
                () -> assertInvalidStoredDate("aug 22 2026"),
                () -> assertInvalidStoredDate("Aug 32 2026"),
                () -> assertInvalidStoredDate("Aug 22 26"),
                () -> assertInvalidStoredDate(""));
    }

    @Test
    void format_nonEnglishDefaultLocale_stillUsesStableEnglishFormat() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.FRENCH);

            assertEquals("Aug 22 2026", DateUtil.format(LocalDate.of(2026, 8, 22)));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    private void assertInvalidInputDate(String value) {
        assertThrows(IllegalArgumentException.class, () -> DateUtil.parseInput(value));
    }

    private void assertInvalidStoredDate(String value) {
        assertThrows(IllegalArgumentException.class, () -> DateUtil.parseStored(value));
    }
}
