package bot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bot.exception.BotException;

class TaskDateTimeTest {

    @Test
    void parse_dateOnly_parsesSuccessfully() throws BotException {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15");
        assertEquals(LocalDate.of(2019, 10, 15), dateTime.toLocalDate());
    }

    @Test
    void parse_dateAndTime_parsesSuccessfully() throws BotException {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1800");
        assertEquals(LocalDate.of(2019, 10, 15), dateTime.toLocalDate());
        assertEquals("Oct 15 2019, 6:00PM", dateTime.toString());
    }

    @Test
    void parse_surroundingWhitespace_isTrimmed() throws BotException {
        TaskDateTime dateTime = TaskDateTime.parse("  2019-10-15  ");
        assertEquals(LocalDate.of(2019, 10, 15), dateTime.toLocalDate());
    }

    @Test
    void parse_unrecognizedShape_throwsBotException() {
        assertThrows(BotException.class, () -> TaskDateTime.parse("15 Oct 2019"));
    }

    @Test
    void parse_empty_throwsBotException() {
        assertThrows(BotException.class, () -> TaskDateTime.parse(""));
    }

    @Test
    void parseDateOnly_validDate_parsesSuccessfully() throws BotException {
        assertEquals(LocalDate.of(2019, 10, 15), TaskDateTime.parseDateOnly("2019-10-15"));
    }

    @Test
    void parseDateOnly_dateWithTime_throwsBotException() {
        assertThrows(BotException.class, () -> TaskDateTime.parseDateOnly("2019-10-15 1800"));
    }

    @Test
    void parseDateOnly_malformed_throwsBotException() {
        assertThrows(BotException.class, () -> TaskDateTime.parseDateOnly("not-a-date"));
    }

    @Test
    void formatDateOnly_returnsDisplayForm() {
        assertEquals("Oct 15 2019", TaskDateTime.formatDateOnly(LocalDate.of(2019, 10, 15)));
    }

    @Test
    void toSaveFormat_dateOnly_roundTrips() throws BotException {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15");
        assertEquals("2019-10-15", dateTime.toSaveFormat());
    }

    @Test
    void toSaveFormat_dateAndTime_roundTrips() throws BotException {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1800");
        assertEquals("2019-10-15 1800", dateTime.toSaveFormat());
    }

    @Test
    void toString_dateOnly_hasNoTimeComponent() throws BotException {
        assertEquals("Oct 15 2019", TaskDateTime.parse("2019-10-15").toString());
    }

    @Test
    void toString_midnightWithTime_showsTwelveAm() throws BotException {
        assertEquals("Oct 15 2019, 12:00AM", TaskDateTime.parse("2019-10-15 0000").toString());
    }
}
