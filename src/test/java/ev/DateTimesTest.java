package ev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DateTimesTest {

    @Test
    public void parse_isoDateWithTime_timeKept() throws EvException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("2019-12-02 1800"));
    }

    @Test
    public void parse_isoDateOnly_startOfDay() throws EvException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), DateTimes.parse("2019-10-15"));
    }

    @Test
    public void parse_slashDateWithTime_timeKept() throws EvException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimes.parse("2/12/2019 1800"));
    }

    @Test
    public void parse_slashDateOnly_startOfDay() throws EvException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), DateTimes.parse("2/12/2019"));
    }

    @Test
    public void parse_paddedSlashDate_sameAsUnpadded() throws EvException {
        assertEquals(DateTimes.parse("2/12/2019"), DateTimes.parse("02/12/2019"));
    }

    @Test
    public void parse_midnightTime_keptAsMidnight() throws EvException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), DateTimes.parse("2019-12-02 0000"));
    }

    @Test
    public void parse_freeText_exceptionThrown() {
        assertThrows(EvException.class, () -> DateTimes.parse("no idea :-p"));
        assertThrows(EvException.class, () -> DateTimes.parse("tomorrow"));
    }

    @Test
    public void parse_emptyText_exceptionThrown() {
        assertThrows(EvException.class, () -> DateTimes.parse(""));
    }

    @Test
    public void parse_monthOutOfRange_exceptionThrown() {
        assertThrows(EvException.class, () -> DateTimes.parse("2019-13-01"));
    }

    @Test
    public void parse_yearInDayPosition_exceptionThrown() {
        assertThrows(EvException.class, () -> DateTimes.parse("2019/12/02"));
    }

    @Test
    public void parse_timeWithoutDate_exceptionThrown() {
        assertThrows(EvException.class, () -> DateTimes.parse("1800"));
    }

    @Test
    public void parse_unreadableDate_messageListsAcceptedFormats() {
        EvException thrown = assertThrows(EvException.class, () -> DateTimes.parse("someday"));
        assertTrue(thrown.getMessage().contains("someday"));
        assertTrue(thrown.getMessage().contains(DateTimes.ACCEPTED_FORMATS));
    }

    @Test
    public void format_midnight_showsDateOnly() {
        assertEquals("Dec 2 2019", DateTimes.format(LocalDateTime.of(2019, 12, 2, 0, 0)));
    }

    @Test
    public void format_afternoonTime_showsTimeWithPm() {
        assertEquals("Dec 2 2019, 6:00 PM", DateTimes.format(LocalDateTime.of(2019, 12, 2, 18, 0)));
    }

    @Test
    public void format_morningTime_showsTimeWithAm() {
        assertEquals("Dec 3 2019, 10:30 AM", DateTimes.format(LocalDateTime.of(2019, 12, 3, 10, 30)));
    }

    @Test
    public void format_singleDigitDay_notPadded() {
        assertEquals("Oct 5 2019", DateTimes.format(LocalDate.of(2019, 10, 5)));
    }

    @Test
    public void fileFormat_roundTrip_valueUnchanged() throws EvException {
        LocalDateTime original = LocalDateTime.of(2019, 12, 2, 18, 0);
        assertEquals(original, DateTimes.fromFileFormat(DateTimes.toFileFormat(original)));
    }

    @Test
    public void fromFileFormat_displayFormat_exceptionThrown() {
        assertThrows(EvException.class, () -> DateTimes.fromFileFormat("Dec 2 2019, 6:00 PM"));
        assertThrows(EvException.class, () -> DateTimes.fromFileFormat("June 6th"));
    }

    @Test
    public void parse_dayThatMonthDoesNotHave_exceptionThrown() {
        assertThrows(EvException.class, () -> DateTimes.parse("2019-02-30"));
        assertThrows(EvException.class, () -> DateTimes.parse("2019-04-31"));
        assertThrows(EvException.class, () -> DateTimes.parse("31/4/2019"));
    }

    @Test
    public void parse_feb29InACommonYear_exceptionThrown() {
        assertThrows(EvException.class, () -> DateTimes.parse("2019-02-29"));
    }

    @Test
    public void parse_feb29InALeapYear_accepted() throws EvException {
        assertEquals(LocalDateTime.of(2020, 2, 29, 0, 0), DateTimes.parse("2020-02-29"));
    }
}
