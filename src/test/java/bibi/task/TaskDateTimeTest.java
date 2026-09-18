package bibi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import bibi.BibiException;

/**
 * Tests the parsing, comparison, and formatting of task dates.
 */
public class TaskDateTimeTest {

    @Test
    public void parse_isoDateOnly_noTimeShown() throws BibiException {
        assertEquals("15 Oct 2019", TaskDateTime.parse("2019-10-15").toString());
    }

    @Test
    public void parse_isoDateAndTime_timeShown() throws BibiException {
        assertEquals("02 Dec 2019 6:00PM", TaskDateTime.parse("2019-12-02 1800").toString());
    }

    @Test
    public void parse_slashDateAndTime_timeShown() throws BibiException {
        assertEquals("02 Dec 2019 6:00PM", TaskDateTime.parse("2/12/2019 1800").toString());
    }

    @Test
    public void parse_slashDateOnly_noTimeShown() throws BibiException {
        assertEquals("02 Dec 2019", TaskDateTime.parse("2/12/2019").toString());
    }

    @Test
    public void parse_midnightGivenExplicitly_timeStillShown() throws BibiException {
        // A date with an explicit 0000 must stay distinguishable from a whole-day date.
        assertEquals("02 Dec 2019 12:00AM", TaskDateTime.parse("2019-12-02 0000").toString());
    }

    @Test
    public void parse_surroundingSpaces_ignored() throws BibiException {
        assertEquals("15 Oct 2019", TaskDateTime.parse("  2019-10-15  ").toString());
    }

    @Test
    public void parse_dayFirstDateFormats_sameDateAndStorage() throws BibiException {
        List<String> dateTexts = List.of("2/12/2019", "02/12/2019", "2-12-2019", "02-12-2019",
                "2.12.2019", "02.12.2019", "2 Dec 2019", "02 December 2019", "2/12/19");

        for (String dateText : dateTexts) {
            TaskDateTime parsed = TaskDateTime.parse(dateText);

            assertEquals(LocalDate.of(2019, 12, 2), parsed.getDate(), dateText);
            assertEquals("02 Dec 2019", parsed.toString(), dateText);
            assertEquals("2019-12-02", parsed.toStorageString(), dateText);
        }
    }

    @Test
    public void parse_eachDateAndTimeFormat_sameDateTimeAndStorage() throws BibiException {
        List<String> dateTexts = List.of("2019-12-02", "2/12/2019", "2-12-2019", "2.12.2019",
                "2 Dec 2019", "2 December 2019", "2/12/19");
        List<String> timeTexts = List.of("1800", "18:00", "6 pm", "6:00 pm", "6PM", "6:00PM");

        for (String dateText : dateTexts) {
            for (String timeText : timeTexts) {
                String dateTimeText = dateText + " " + timeText;
                TaskDateTime parsed = TaskDateTime.parse(dateTimeText);

                assertEquals("02 Dec 2019 6:00PM", parsed.toString(), dateTimeText);
                assertEquals("2019-12-02 1800", parsed.toStorageString(), dateTimeText);
                assertEquals(parsed.toString(), TaskDateTime.parse(parsed.toStorageString()).toString(), dateTimeText);
            }
        }
    }

    @Test
    public void parse_mixedCaseAndWhitespace_sameDateTime() throws BibiException {
        String nonBreakingSpace = Character.toString((char) 0xA0);
        List<String> dateTimeTexts = List.of(" 2  dEc  2019   6  pM ", "2\tDECEMBER\t2019\t6:00\tPm",
                "2" + nonBreakingSpace + "December 2019 6:00" + nonBreakingSpace + "pm");

        for (String dateTimeText : dateTimeTexts) {
            TaskDateTime parsed = TaskDateTime.parse(dateTimeText);

            assertEquals("02 Dec 2019 6:00PM", parsed.toString(), dateTimeText);
            assertEquals("2019-12-02 1800", parsed.toStorageString(), dateTimeText);
        }
    }

    @Test
    public void parse_twoDigitYearBounds_usesTwentyFirstCentury() throws BibiException {
        assertEquals("2000-01-01", TaskDateTime.parse("1/1/00").toStorageString());
        assertEquals("2099-12-31", TaskDateTime.parse("31/12/99").toStorageString());
        assertEquals("2000-02-29 1800", TaskDateTime.parse("29/2/00 6 pm").toStorageString());
    }

    @Test
    public void parse_midnightAndNoon_correctTwelveHourInterpretation() throws BibiException {
        assertEquals("2019-12-02 0000", TaskDateTime.parse("2/12/2019 12 am").toStorageString());
        assertEquals("2019-12-02 0000", TaskDateTime.parse("2/12/2019 12:00 AM").toStorageString());
        assertEquals("02 Dec 2019 12:00AM", TaskDateTime.parse("2/12/2019 12 am").toString());
        assertEquals("2019-12-02 1200", TaskDateTime.parse("2/12/2019 12 pm").toStorageString());
        assertEquals("2019-12-02 1200", TaskDateTime.parse("2/12/2019 12:00 PM").toStorageString());
        assertEquals("02 Dec 2019 12:00PM", TaskDateTime.parse("2/12/2019 12 pm").toString());
    }

    @Test
    public void parse_leapYearDates_acceptedWithoutChangingTheDate() throws BibiException {
        List<String> dateTexts = List.of("2024-02-29", "29/2/2024", "29-2-2024", "29.2.2024",
                "29 Feb 2024", "29 February 2024", "29/2/24", "29/2/2000");

        for (String dateText : dateTexts) {
            TaskDateTime parsed = TaskDateTime.parse(dateText);

            assertEquals(2, parsed.getDate().getMonthValue(), dateText);
            assertEquals(29, parsed.getDate().getDayOfMonth(), dateText);
        }
    }

    @Test
    public void parse_invalidDateInEachFormat_exceptionThrown() {
        List<String> dateTexts = List.of("2023-02-29", "29/2/2023", "31-4-2024", "31.4.2024",
                "29 Feb 1900", "29 February 2100", "29/2/23", "0/12/2024", "1/0/2024");

        for (String dateText : dateTexts) {
            assertThrows(BibiException.class, () -> TaskDateTime.parse(dateText), dateText);
            assertThrows(BibiException.class, () -> TaskDateTime.parse(dateText + " 1800"), dateText);
        }
    }

    @Test
    public void parse_invalidTimeInEachFormat_exceptionThrown() {
        List<String> timeTexts = List.of("2400", "2360", "24:00", "18:60", "0 am", "13 pm",
                "0:00 am", "13:00 pm", "6:60 pm", "-1:00", "18:00:00", "6", "6:0 pm");

        for (String timeText : timeTexts) {
            assertThrows(BibiException.class, () -> TaskDateTime.parse("2019-12-02 " + timeText), timeText);
            assertThrows(BibiException.class, () -> TaskDateTime.parse("2 December 2019 " + timeText), timeText);
        }
    }

    @Test
    public void parse_numericMonthFirstDate_neverUsed() throws BibiException {
        assertEquals("2019-03-02", TaskDateTime.parse("2/3/2019").toStorageString());
        assertEquals("2019-03-02", TaskDateTime.parse("2-3-2019").toStorageString());
        assertEquals("2019-03-02", TaskDateTime.parse("2.3.2019").toStorageString());
        assertThrows(BibiException.class, () -> TaskDateTime.parse("12/31/2019"));
        assertThrows(BibiException.class, () -> TaskDateTime.parse("12-31-2019"));
        assertThrows(BibiException.class, () -> TaskDateTime.parse("12.31.2019"));
    }

    @Test
    public void parse_extraDateOrTimeText_exceptionThrown() {
        assertThrows(BibiException.class, () -> TaskDateTime.parse("2 December 2019 tomorrow"));
        assertThrows(BibiException.class, () -> TaskDateTime.parse("2/12/2019 1800 1900"));
        assertThrows(BibiException.class, () -> TaskDateTime.parse("2/12/2019 6 pm later"));
    }

    @Test
    public void parse_unrecognizedText_exceptionThrown() {
        BibiException thrown = assertThrows(BibiException.class, () ->
                TaskDateTime.parse("next Tuesday"));
        assertTrue(thrown.getMessage().contains("next Tuesday"));
        assertTrue(thrown.getMessage().contains("yyyy-MM-dd"));
    }

    @Test
    public void parse_impossibleCalendarDate_exceptionThrown() {
        assertThrows(BibiException.class, () -> TaskDateTime.parse("2019-13-45"));
    }

    @Test
    public void parse_dayOutOfRangeForMonth_exceptionThrown() {
        assertThrows(BibiException.class, () -> TaskDateTime.parse("2019-02-30"));
    }

    @Test
    public void parse_blankOrNull_exceptionThrown() {
        assertThrows(BibiException.class, () -> TaskDateTime.parse("   "));
        assertThrows(BibiException.class, () -> TaskDateTime.parse(null));
    }

    @Test
    public void toStorageString_dateOnly_roundTripsBackToTheSameValue() throws BibiException {
        TaskDateTime original = TaskDateTime.parse("2019-10-15");
        assertEquals("2019-10-15", original.toStorageString());
        assertEquals(original.toString(),
                TaskDateTime.parse(original.toStorageString()).toString());
    }

    @Test
    public void toStorageString_dateAndTime_roundTripsBackToTheSameValue() throws BibiException {
        TaskDateTime original = TaskDateTime.parse("2/12/2019 1800");
        assertEquals("2019-12-02 1800", original.toStorageString());
        assertEquals(original.toString(),
                TaskDateTime.parse(original.toStorageString()).toString());
    }

    @Test
    public void toStorageString_singleDigitTime_paddedToFourDigits() throws BibiException {
        assertEquals("2019-12-02 0905", TaskDateTime.parse("2019-12-02 0905").toStorageString());
    }

    @Test
    public void isOn_matchingAndDifferentDates_reportedCorrectly() throws BibiException {
        TaskDateTime value = TaskDateTime.parse("2019-10-15 1800");
        assertTrue(value.isOn(LocalDate.of(2019, 10, 15)));
        assertFalse(value.isOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void isBefore_earlierAndLaterValues_reportedCorrectly() throws BibiException {
        TaskDateTime earlier = TaskDateTime.parse("2019-10-15 0900");
        TaskDateTime later = TaskDateTime.parse("2019-10-15 1700");
        assertTrue(earlier.isBefore(later));
        assertFalse(later.isBefore(earlier));
    }

    @Test
    public void isBefore_sameValue_false() throws BibiException {
        TaskDateTime value = TaskDateTime.parse("2019-10-15 0900");
        assertFalse(value.isBefore(TaskDateTime.parse("2019-10-15 0900")));
    }

    @Test
    public void isBefore_wholeDayAgainstTimeOnSameDay_countsAsStartOfDay() throws BibiException {
        // A date with no time is treated as the start of its day.
        assertTrue(TaskDateTime.parse("2019-10-15").isBefore(TaskDateTime.parse("2019-10-15 0900")));
    }

    @Test
    public void formatDate_anyDate_usesTheDisplayFormat() {
        assertEquals("06 Aug 2019", TaskDateTime.formatDate(LocalDate.of(2019, 8, 6)));
    }
}
