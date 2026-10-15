package serina.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import serina.exception.SerinaException;

/** Tests strict calendar-date validation. */
public class DateParserTest {
    @Test
    public void parseInputDate_validLeapDay_returnsDate() throws SerinaException {
        assertEquals(LocalDate.of(2028, 2, 29), DateParser.parseInputDate("2028-02-29"));
    }

    @Test
    public void parseInputDate_invalidDates_throwsSerinaException() {
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026-02-29"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026-02-30"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("26-09-14"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026-9-14"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026/09/14"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate(" 2026-09-14 "));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026-09-14T10:00"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026-00-14"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026-13-14"));
        assertThrows(SerinaException.class, () -> DateParser.parseInputDate("2026-04-31"));
    }

    @Test
    public void parseFileDate_invalidDate_reportsLoadError() {
        SerinaException exception = assertThrows(SerinaException.class, () ->
                DateParser.parseFileDate("2026-02-30"));

        assertEquals("Captain Cutter, I couldn't load your saved tasks.", exception.getMessage());
    }

    @Test
    public void formatDates_chineseDefaultLocale_remainsEnglishAndIso() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
            LocalDate date = LocalDate.of(2026, 9, 4);

            assertEquals("Sep 4 2026", DateParser.formatDisplayDate(date));
            assertEquals("2026-09-04", DateParser.formatFileDate(date));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
