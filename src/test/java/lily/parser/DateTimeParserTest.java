package lily.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lily.exception.LilyException;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DateTimeParser}, which turns user-typed date/time text into {@link
 * LocalDateTime} and formats it back out for storage and display.
 *
 * <p>{@link DateTimeParser#parseUserInput(String)} is arguably the single highest-value
 * method to test in the whole codebase: it accepts four distinct input formats, uses
 * {@code STRICT} date resolution specifically to reject impossible calendar dates
 * (rather than Java's default silently "fixing" them, e.g. 31 February becoming 28
 * February — a real bug caught and fixed during this class's development), and its
 * correctness is what every deadline and event in the app ultimately depends on.
 */
public class DateTimeParserTest {

    // ----- parseUserInput: accepted formats -----

    @Test
    public void parseUserInput_isoDateOnly_parsedAsMidnight() throws LilyException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parseUserInput("2019-10-15");
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), parsed.value());
        assertEquals(false, parsed.hasExplicitTime());
    }

    @Test
    public void parseUserInput_isoDateWithTime_parsedWithGivenTime() throws LilyException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parseUserInput("2019-10-15 1800");
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), parsed.value());
        assertEquals(true, parsed.hasExplicitTime());
    }

    @Test
    public void parseUserInput_isoDateUnpaddedMonthAndDay_stillParsed() throws LilyException {
        assertEquals(LocalDateTime.of(2019, 2, 9, 0, 0), DateTimeParser.parseUserInput("2019-2-9").value());
    }

    @Test
    public void parseUserInput_slashDateOnly_parsedAsMidnight() throws LilyException {
        // d/M/uuuu: day first, then month, matching the class's documented format.
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), DateTimeParser.parseUserInput("2/12/2019").value());
    }

    @Test
    public void parseUserInput_slashDateWithTime_parsedWithGivenTime() throws LilyException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseUserInput("2/12/2019 1800").value());
    }

    @Test
    public void parseUserInput_slashDateZeroPadded_stillParsed() throws LilyException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), DateTimeParser.parseUserInput("02/12/2019").value());
    }

    @Test
    public void parseUserInput_surroundingWhitespace_isTrimmedBeforeParsing() throws LilyException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0),
                DateTimeParser.parseUserInput("  2019-10-15  ").value());
    }

    // ----- parseUserInput: STRICT resolution rejects impossible dates -----

    @Test
    public void parseUserInput_february29OnNonLeapYear_exceptionThrown() {
        // The specific bug this class was written to avoid: Java's default (SMART)
        // resolver would silently roll this over to 28 Feb; STRICT must reject it.
        assertThrows(LilyException.class, () -> DateTimeParser.parseUserInput("2019-02-29"));
    }

    @Test
    public void parseUserInput_february29OnLeapYear_parsedSuccessfully() throws LilyException {
        assertEquals(LocalDateTime.of(2020, 2, 29, 0, 0), DateTimeParser.parseUserInput("2020-02-29").value());
    }

    @Test
    public void parseUserInput_monthThirteen_exceptionThrown() {
        assertThrows(LilyException.class, () -> DateTimeParser.parseUserInput("2019-13-01"));
    }

    @Test
    public void parseUserInput_hourTwentyFive_exceptionThrown() {
        assertThrows(LilyException.class, () -> DateTimeParser.parseUserInput("2019-10-15 2500"));
    }

    @Test
    public void parseUserInput_explicitMidnight_marksTimeAsExplicit() throws LilyException {
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parseUserInput("2019-10-15 0000");
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), parsed.value());
        assertEquals(true, parsed.hasExplicitTime());
    }

    // ----- parseUserInput: adjacent-value parsing requires a zero-padded time -----

    @Test
    public void parseUserInput_threeDigitTimeMissingLeadingZero_exceptionThrown() {
        // HHmm has no separator, so Java requires exactly 4 digits (adjacent-value
        // parsing); "800" for 8am must be written "0800".
        assertThrows(LilyException.class, () -> DateTimeParser.parseUserInput("2019-10-15 800"));
    }

    // ----- parseUserInput: rejected input -----

    @Test
    public void parseUserInput_completelyUnrecognisableText_exceptionThrown() {
        LilyException thrown = assertThrows(LilyException.class,
                () -> DateTimeParser.parseUserInput("not a date"));
        assertEquals("I couldn't understand the date/time 'not a date'. "
                + "Accepted formats: yyyy-MM-dd, yyyy-MM-dd HHmm, d/M/yyyy, or d/M/yyyy HHmm.",
                thrown.getMessage());
    }

    @Test
    public void parseUserInput_emptyString_exceptionThrown() {
        LilyException thrown = assertThrows(LilyException.class, () -> DateTimeParser.parseUserInput(""));
        assertEquals("A date/time is required.", thrown.getMessage());
    }

    @Test
    public void parseUserInput_blankWhitespaceOnly_exceptionThrown() {
        LilyException thrown = assertThrows(LilyException.class, () -> DateTimeParser.parseUserInput("   "));
        assertEquals("A date/time is required.", thrown.getMessage());
    }

    @Test
    public void parseUserInput_null_exceptionThrown() {
        LilyException thrown = assertThrows(LilyException.class, () -> DateTimeParser.parseUserInput(null));
        assertEquals("A date/time is required.", thrown.getMessage());
    }

    // ----- parseScheduleDate -----

    @Test
    public void parseScheduleDate_emptyArgument_returnsReferenceDate() throws LilyException {
        LocalDate today = LocalDate.of(2026, 9, 16);
        assertEquals(today, DateTimeParser.parseScheduleDate("", today));
    }

    @Test
    public void parseScheduleDate_today_returnsReferenceDate() throws LilyException {
        LocalDate today = LocalDate.of(2026, 9, 16);
        assertEquals(today, DateTimeParser.parseScheduleDate("today", today));
    }

    @Test
    public void parseScheduleDate_tomorrow_returnsFollowingDate() throws LilyException {
        LocalDate today = LocalDate.of(2026, 9, 16);
        assertEquals(LocalDate.of(2026, 9, 17),
                DateTimeParser.parseScheduleDate("tomorrow", today));
    }

    @Test
    public void parseScheduleDate_supportedAbsoluteFormats_returnDate() throws LilyException {
        LocalDate today = LocalDate.of(2026, 1, 1);
        assertEquals(LocalDate.of(2026, 9, 16),
                DateTimeParser.parseScheduleDate("2026-09-16", today));
        assertEquals(LocalDate.of(2026, 9, 16),
                DateTimeParser.parseScheduleDate("16/9/2026", today));
    }

    @Test
    public void parseScheduleDate_dateWithTime_exceptionThrown() {
        LocalDate today = LocalDate.of(2026, 9, 16);
        assertThrows(LilyException.class,
                () -> DateTimeParser.parseScheduleDate("2026-09-16 1800", today));
    }

    @Test
    public void parseScheduleDate_impossibleDate_exceptionThrown() {
        LocalDate today = LocalDate.of(2026, 9, 16);
        LilyException thrown = assertThrows(LilyException.class,
                () -> DateTimeParser.parseScheduleDate("2026-02-30", today));
        assertEquals("I couldn't understand the date '2026-02-30'. "
                + "Accepted formats: yyyy-MM-dd or d/M/yyyy.", thrown.getMessage());
    }

    // ----- formatForStorage -----

    @Test
    public void formatForStorage_dateWithTime_formattedAsFixedIsoStyleString() {
        assertEquals("2019-12-02T18:00",
                DateTimeParser.formatForStorage(LocalDateTime.of(2019, 12, 2, 18, 0)));
    }

    @Test
    public void formatForStorage_midnight_stillIncludesTimeComponent() {
        // Unlike formatForDisplay, the storage format never hides midnight: it must
        // stay unambiguous and fully round-trippable.
        assertEquals("2019-10-15T00:00",
                DateTimeParser.formatForStorage(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    // ----- parseStorageFormat -----

    @Test
    public void parseStorageFormat_wellFormedText_parsedBackToSameDateTime() throws LilyException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parseStorageFormat("2019-12-02T18:00"));
    }

    @Test
    public void parseStorageFormat_roundTripsWithFormatForStorage() throws LilyException {
        LocalDateTime original = LocalDateTime.of(2019, 10, 15, 9, 30);
        String stored = DateTimeParser.formatForStorage(original);
        assertEquals(original, DateTimeParser.parseStorageFormat(stored));
    }

    @Test
    public void parseStorageFormat_oldPlainTextDate_exceptionThrown() {
        // Guards backward-compatible handling of pre-DateTimeParser save files, whose
        // date fields were plain strings like "June 6th" rather than ISO-style text.
        assertThrows(LilyException.class, () -> DateTimeParser.parseStorageFormat("June 6th"));
    }

    @Test
    public void parseStorageFormat_userInputFormatInsteadOfStorageFormat_exceptionThrown() {
        // The storage format is deliberately distinct from every accepted user-input
        // format, so a value like "2019-10-15" (valid input, but not valid storage
        // text) must still be rejected here.
        assertThrows(LilyException.class, () -> DateTimeParser.parseStorageFormat("2019-10-15"));
    }

    // ----- formatForDisplay -----

    @Test
    public void formatForDisplay_midnight_hidesTimeComponent() {
        assertEquals("Oct 15 2019", DateTimeParser.formatForDisplay(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    @Test
    public void formatForDisplay_nonMidnightTime_showsTimeComponent() {
        assertEquals("Dec 02 2019, 6:00PM",
                DateTimeParser.formatForDisplay(LocalDateTime.of(2019, 12, 2, 18, 0)));
    }

    @Test
    public void formatForDisplay_morningTime_showsAmIndicator() {
        assertEquals("Dec 02 2019, 9:05AM",
                DateTimeParser.formatForDisplay(LocalDateTime.of(2019, 12, 2, 9, 5)));
    }
}
