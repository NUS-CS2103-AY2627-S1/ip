package gooble.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import gooble.GoobleException;

/** Tests command parsing and validation rules. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseType_commandWithArguments_returnsCommandType() {
        assertEquals(CommandType.TODO, parser.parseType("todo read book"));
        assertEquals(CommandType.UNKNOWN, parser.parseType("launch app"));
    }

    @Test
    void argumentAfter_commandWithArgument_returnsTrimmedArgument() {
        assertEquals("read book", parser.argumentAfter("todo   read book", "todo"));
        assertEquals("", parser.argumentAfter("todo", "todo"));
    }

    @Test
    void parse_supportedAndUnknownCommands_returnsExpectedCommand() {
        assertEquals(TodoCommand.class, parser.parse("todo read book").getClass());
        assertEquals(FindCommand.class, parser.parse("find book").getClass());
        assertEquals(TagCommand.class, parser.parse("tag 1 #fun").getClass());
        assertEquals(UntagCommand.class, parser.parse("untag 1").getClass());
        assertEquals(UnknownCommand.class, parser.parse("launch app").getClass());
    }

    @Test
    void parseTag_validTag_returnsTaskNumberAndNormalizedTag() throws GoobleException {
        assertArrayEquals(new String[] {"1", "#fun"}, parser.parseTag("tag 1 #Fun"));
    }

    @Test
    void parseTag_missingOrMalformedTag_throwsException() {
        assertThrows(GoobleException.class, () -> parser.parseTag("tag 1"));
        assertThrows(GoobleException.class, () -> parser.parseTag("tag 1 fun!"));
        assertThrows(GoobleException.class, () -> parser.parseTag("tag 1 #fun #school"));
    }

    @Test
    void parseDeadline_validCommand_returnsDescriptionAndDeadline() throws GoobleException {
        assertArrayEquals(new String[] {"return book", "2026-02-01 0900"},
                parser.parseDeadline("deadline return book /by 2026-02-01 0900"));
    }

    @Test
    void parseDeadline_missingMarkerOrDescription_throwsException() {
        assertThrows(GoobleException.class, () -> parser.parseDeadline("deadline return book"));
        assertThrows(GoobleException.class, () -> parser.parseDeadline("deadline  /by 2026-02-01"));
    }

    @Test
    void parseEvent_validCommand_returnsDescriptionAndTimes() throws GoobleException {
        assertArrayEquals(new String[] {"project meeting", "2026-03-12 1400", "2026-03-12 1600"},
                parser.parseEvent("event project meeting /from 2026-03-12 1400 /to 2026-03-12 1600"));
    }

    @Test
    void parseEvent_missingTimeMarker_throwsException() {
        assertThrows(GoobleException.class, () -> parser.parseEvent("event project meeting"));
        assertThrows(GoobleException.class, () -> parser.parseEvent("event project meeting /from Mon 2pm /to"));
    }

    @Test
    void parseEvent_reversedDateTimes_throwsException() {
        assertThrows(GoobleException.class, () -> parser.parseEvent(
                "event project meeting /from 2026-03-12 1000 /to 2026-03-12 0900"));
    }

    @Test
    void parseEvent_invalidDateOrTime_throwsException() {
        assertThrows(GoobleException.class, () -> parser.parseEvent(
                "event project meeting /from 2026-02-30 0800 /to 2026-03-01 1800"));
        assertThrows(GoobleException.class, () -> parser.parseEvent(
                "event project meeting /from 2026-03-01 0800 /to 2026-03-01 6am"));
    }

    @Test
    void parseDeadline_repeatedMarker_throwsException() {
        assertThrows(GoobleException.class, () -> parser.parseDeadline(
                "deadline submit report /by 2026-02-01 /by 2026-02-02"));
    }

    @Test
    void parseDateRange_validOrderedRange_returnsBothDeadlines() throws GoobleException {
        var range = parser.parseDateRange("list from 2026-02-01 0900 to 2026-02-01 1700");

        assertEquals(LocalDate.of(2026, 2, 1), range[0].date());
        assertEquals(LocalTime.of(9, 0), range[0].time());
        assertEquals(LocalTime.of(17, 0), range[1].time());
    }

    @Test
    void parseDateRange_reversedOrMissingTimes_throwsException() {
        assertThrows(GoobleException.class, () -> parser.parseDateRange(
                "list from 2026-02-02 0900 to 2026-02-01 1700"));
        assertThrows(GoobleException.class, () -> parser.parseDateRange("list from 2026-02-01 to 2026-02-02"));
    }
}
