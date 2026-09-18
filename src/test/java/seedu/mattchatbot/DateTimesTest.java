package seedu.mattchatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the date formats the chatbot accepts and the way it displays them.
 */
public class DateTimesTest {

    @Test
    public void parse_dateOnly_midnightAssumed() throws Exception {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), DateTimes.parse("2019-10-15"));
    }

    @Test
    public void parse_dateAndTime_timeKept() throws Exception {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0),
                DateTimes.parse("2019-10-15 1800"));
        assertEquals(LocalDateTime.of(2019, 1, 2, 9, 5),
                DateTimes.parse("2019-01-02 0905"));
    }

    @Test
    public void parse_surroundingSpaces_stillParsed() throws Exception {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), DateTimes.parse("  2019-10-15  "));
    }

    @Test
    public void parse_freeText_exceptionThrown() {
        MattChatBotException e = assertThrows(MattChatBotException.class, () ->
                DateTimes.parse("Sunday"));
        assertEquals("I could not make sense of the date \"Sunday\". It should be written as yyyy-MM-dd, "
                + "e.g. 2019-10-15, with a time if you wish, e.g. 2019-10-15 1800.",
                e.getMessage());
    }

    @Test
    public void parse_wrongOrderOrSeparator_exceptionThrown() {
        assertThrows(MattChatBotException.class, () -> DateTimes.parse("15/10/2019"));
        assertThrows(MattChatBotException.class, () -> DateTimes.parse("15-10-2019"));
    }

    @Test
    public void parse_impossibleDate_exceptionThrown() {
        assertThrows(MattChatBotException.class, () -> DateTimes.parse("2019-13-01"));
        assertThrows(MattChatBotException.class, () -> DateTimes.parse("2019-02-30"));
    }

    @Test
    public void parse_impossibleTime_exceptionThrown() {
        assertThrows(MattChatBotException.class, () -> DateTimes.parse("2019-10-15 2500"));
    }

    @Test
    public void parseDate_dateWithTime_timeDropped() throws Exception {
        assertEquals(LocalDate.of(2019, 10, 15), DateTimes.parseDate("2019-10-15 1800"));
    }

    @Test
    public void format_midnight_dateOnlyShown() {
        assertEquals("Oct 15 2019", DateTimes.format(LocalDateTime.of(2019, 10, 15, 0, 0)));
    }

    @Test
    public void format_withTime_timeShownInLowerCase() {
        assertEquals("Oct 15 2019, 6:00pm",
                DateTimes.format(LocalDateTime.of(2019, 10, 15, 18, 0)));
        assertEquals("Oct 15 2019, 9:05am",
                DateTimes.format(LocalDateTime.of(2019, 10, 15, 9, 5)));
    }

    @Test
    public void toFileString_anyMoment_alwaysIncludesTime() {
        assertEquals("2019-10-15 0000",
                DateTimes.toFileString(LocalDateTime.of(2019, 10, 15, 0, 0)));
        assertEquals("2019-10-15 1800",
                DateTimes.toFileString(LocalDateTime.of(2019, 10, 15, 18, 0)));
    }

    @Test
    public void parseThenToFileString_roundTrip_valuePreserved() throws Exception {
        String original = "2019-10-15 1800";
        assertEquals(original, DateTimes.toFileString(DateTimes.parse(original)));
    }
}
