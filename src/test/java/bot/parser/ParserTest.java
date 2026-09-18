package bot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bot.exception.BotException;
import bot.task.Deadline;
import bot.task.Event;
import bot.task.Todo;

class ParserTest {

    @Test
    void parseCommand_withArguments_splitsWordAndRest() {
        Parser.ParsedCommand command = Parser.parseCommand("todo read book");
        assertEquals("todo", command.commandWord());
        assertEquals("read book", command.arguments());
    }

    @Test
    void parseCommand_noArguments_argumentsIsEmpty() {
        Parser.ParsedCommand command = Parser.parseCommand("list");
        assertEquals("list", command.commandWord());
        assertEquals("", command.arguments());
    }

    @Test
    void parseCommand_extraWhitespace_isNormalized() {
        Parser.ParsedCommand command = Parser.parseCommand("  todo   read book  ");
        assertEquals("todo", command.commandWord());
        assertEquals("read book", command.arguments());
    }

    @Test
    void parseEvent_duplicateMarker_isRejected() {
        assertThrows(BotException.class, () -> Parser.parseEvent(
                "meeting /from 2019-10-15 /from 2019-10-16 /to 2019-10-17"));
    }

    @Test
    void parseEvent_endDateNotAfterStartDate_isRejected() {
        assertThrows(BotException.class, () -> Parser.parseEvent(
                "meeting /from 2019-10-16 /to 2019-10-15"));
    }

    @Test
    void parseEvent_sameDateWithLaterTime_isAccepted() throws BotException {
        assertEquals("[E][ ] meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)", Parser.parseEvent(
                "meeting /from 2019-10-15 1400 /to 2019-10-15 1600").toString());
    }

    @Test
    void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws BotException {
        assertEquals(1, Parser.parseTaskIndex("2", "mark", 5));
    }

    @Test
    void parseTaskIndex_emptyArgument_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseTaskIndex("  ", "mark", 5));
    }

    @Test
    void parseTaskIndex_nonNumeric_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseTaskIndex("abc", "mark", 5));
    }

    @Test
    void parseTaskIndex_belowRange_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseTaskIndex("0", "mark", 5));
    }

    @Test
    void parseTaskIndex_aboveRange_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseTaskIndex("6", "mark", 5));
    }

    @Test
    void parseTodo_validDescription_returnsTodo() throws BotException {
        Todo todo = Parser.parseTodo(" read book ");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void parseTodo_emptyDescription_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseTodo("   "));
    }

    @Test
    void parseDeadline_validInput_returnsDeadline() throws BotException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    void parseDeadline_missingByMarker_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseDeadline("return book 2019-10-15"));
    }

    @Test
    void parseDeadline_emptyDescription_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseDeadline("/by 2019-10-15"));
    }

    @Test
    void parseDeadline_emptyDate_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseDeadline("return book /by "));
    }

    @Test
    void parseDeadline_invalidDate_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseDeadline("return book /by not-a-date"));
    }

    @Test
    void parseEvent_validInput_returnsEvent() throws BotException {
        Event event = Parser.parseEvent("project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        assertEquals("[E][ ] project meeting (from: Oct 15 2019, 2:00PM to: Oct 15 2019, 4:00PM)", event.toString());
    }

    @Test
    void parseEvent_missingFromMarker_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseEvent("project meeting /to 2019-10-15 1600"));
    }

    @Test
    void parseEvent_missingToMarker_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseEvent("project meeting /from 2019-10-15 1400"));
    }

    @Test
    void parseEvent_toBeforeFrom_throwsBotException() {
        assertThrows(BotException.class, () ->
                Parser.parseEvent("project meeting /to 2019-10-15 1600 /from 2019-10-15 1400"));
    }

    @Test
    void parseEvent_emptyDescription_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseEvent("/from 2019-10-15 1400 /to 2019-10-15 1600"));
    }

    @Test
    void parseOnDate_validDate_returnsDate() throws BotException {
        assertEquals(LocalDate.of(2019, 10, 15), Parser.parseOnDate(" 2019-10-15 "));
    }

    @Test
    void parseOnDate_empty_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseOnDate("  "));
    }

    @Test
    void parseOnDate_malformed_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseOnDate("15 Oct 2019"));
    }

    @Test
    void parseFindKeyword_validKeyword_isTrimmed() throws BotException {
        assertEquals("book", Parser.parseFindKeyword(" book "));
    }

    @Test
    void parseFindKeyword_empty_throwsBotException() {
        assertThrows(BotException.class, () -> Parser.parseFindKeyword("   "));
    }
}
