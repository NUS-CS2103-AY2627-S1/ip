package thomas.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;

class DateTimeUtilTest {
    @Test
    void parseDateTime_isoDateWithoutTime_returnsDateAndNoTime() throws ThomasException {
        DateTimeUtil.ParsedDateTime result = DateTimeUtil.parseDateTime("2026-08-26");

        assertEquals(LocalDate.of(2026, 8, 26), result.getDate());
        assertNull(result.getTime());
    }

    @Test
    void parseDateTime_slashDateWithTime_returnsDateAndTime() throws ThomasException {
        DateTimeUtil.ParsedDateTime result = DateTimeUtil.parseDateTime("26/8/2026 0930");

        assertEquals(LocalDate.of(2026, 8, 26), result.getDate());
        assertEquals(LocalTime.of(9, 30), result.getTime());
    }

    @Test
    void parseDateTime_surroundingWhitespace_isIgnored() throws ThomasException {
        DateTimeUtil.ParsedDateTime result = DateTimeUtil.parseDateTime("  2026-08-26  1800  ");

        assertEquals(LocalDate.of(2026, 8, 26), result.getDate());
        assertEquals(LocalTime.of(18, 0), result.getTime());
    }

    @Test
    void parseDateTime_emptyInput_throwsException() {
        assertThrows(ThomasException.class, () -> DateTimeUtil.parseDateTime("   "));
    }

    @Test
    void parseDateTime_tooManyParts_throwsException() {
        assertThrows(ThomasException.class, () -> DateTimeUtil.parseDateTime("2026-08-26 1800 extra"));
    }

    @Test
    void parseDateTime_invalidDate_throwsException() {
        assertThrows(ThomasException.class, () -> DateTimeUtil.parseDateTime("2026-02-30"));
    }

    @Test
    void parseDateTime_invalidTime_throwsException() {
        assertThrows(ThomasException.class, () -> DateTimeUtil.parseDateTime("2026-08-26 2500"));
    }

    @Test
    void parseDate_isoDate_returnsDate() throws ThomasException {
        assertEquals(LocalDate.of(2026, 8, 26), DateTimeUtil.parseDate("2026-08-26"));
    }

    @Test
    void parseDate_slashDate_returnsDate() throws ThomasException {
        assertEquals(LocalDate.of(2026, 8, 26), DateTimeUtil.parseDate("26/8/2026"));
    }

    @Test
    void parseDate_legacyDisplayDate_returnsDate() throws ThomasException {
        assertEquals(LocalDate.of(2026, 8, 26), DateTimeUtil.parseDate("Aug 26 2026"));
    }

    @Test
    void parseDate_surroundingWhitespace_isIgnored() throws ThomasException {
        assertEquals(LocalDate.of(2026, 8, 26), DateTimeUtil.parseDate("  2026-08-26  "));
    }

    @Test
    void parseDate_invalidDate_throwsException() {
        assertThrows(ThomasException.class, () -> DateTimeUtil.parseDate("not-a-date"));
    }

    @Test
    void parsedDateTime_constructorStoresDateAndTime() {
        LocalDate date = LocalDate.of(2026, 8, 26);
        LocalTime time = LocalTime.of(9, 30);

        DateTimeUtil.ParsedDateTime result = new DateTimeUtil.ParsedDateTime(date, time);

        assertEquals(date, result.getDate());
        assertEquals(time, result.getTime());
    }

    @Test
    void toDisplayString_withoutTime_returnsDateOnly() {
        assertEquals("Aug 26 2026", DateTimeUtil.toDisplayString(LocalDate.of(2026, 8, 26), null));
    }

    @Test
    void toDisplayString_withMorningTime_returnsLowercaseAm() {
        assertEquals("Aug 26 2026, 9:30am",
                DateTimeUtil.toDisplayString(LocalDate.of(2026, 8, 26), LocalTime.of(9, 30)));
    }

    @Test
    void toDisplayString_withAfternoonTime_returnsLowercasePm() {
        assertEquals("Aug 26 2026, 2:05pm",
                DateTimeUtil.toDisplayString(LocalDate.of(2026, 8, 26), LocalTime.of(14, 5)));
    }

    @Test
    void toStorageString_withoutTime_returnsIsoDate() {
        assertEquals("2026-08-26", DateTimeUtil.toStorageString(LocalDate.of(2026, 8, 26), null));
    }

    @Test
    void toStorageString_withTime_returnsIsoDateAndCompactTime() {
        assertEquals("2026-08-26 0930",
                DateTimeUtil.toStorageString(LocalDate.of(2026, 8, 26), LocalTime.of(9, 30)));
    }
}
