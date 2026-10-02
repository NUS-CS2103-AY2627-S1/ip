package clsl.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import clsl.ClslException;

/** Tests the conversion of user input into parsed commands. */
public class ParserTest {
    @Test
    void parse_todoCommand_returnsTodoDetails() throws Exception {
        ParsedCommand command = Parser.parse("todo read book");

        assertEquals(ParsedCommand.Type.TODO, command.getType());
        assertEquals("read book", command.getDescription());
    }

    @Test
    void parse_commandsIgnoresSurroundingWhitespace() throws Exception {
        ParsedCommand command = Parser.parse("  todo   read   book  ");

        assertEquals(ParsedCommand.Type.TODO, command.getType());
        assertEquals("read   book", command.getDescription());
    }

    @Test
    void parse_listAndByeCommandsReturnsExpectedTypes() throws Exception {
        assertEquals(ParsedCommand.Type.LIST, Parser.parse("list").getType());
        assertEquals(ParsedCommand.Type.BYE, Parser.parse("bye").getType());
    }

    @Test
    void parse_taskIndexCommandsConvertsToZeroBasedIndex() throws Exception {
        assertEquals(ParsedCommand.Type.MARK, Parser.parse("mark 3").getType());
        assertEquals(2, Parser.parse("mark 3").getTaskIndex());
        assertEquals(ParsedCommand.Type.UNMARK, Parser.parse("unmark 2").getType());
        assertEquals(1, Parser.parse("delete 2").getTaskIndex());
    }

    @Test
    void parse_deadlineCommand_returnsDescriptionAndDate() throws Exception {
        ParsedCommand command = Parser.parse("deadline submit report /by 2026-09-01");

        assertEquals(ParsedCommand.Type.DEADLINE, command.getType());
        assertEquals("submit report", command.getDescription());
        assertEquals("2026-09-01", command.getFirstDate().toString());
    }

    @Test
    void parse_eventCommand_returnsDescriptionStartAndEndDates() throws Exception {
        ParsedCommand command = Parser.parse("event conference /from 2026-09-01 /to 2026-09-03");

        assertEquals(ParsedCommand.Type.EVENT, command.getType());
        assertEquals("conference", command.getDescription());
        assertEquals("2026-09-01", command.getFirstDate().toString());
        assertEquals("2026-09-03", command.getSecondDate().toString());
    }

    @Test
    void parse_eventWithSameStartAndEndDate_returnsEventDetails() throws Exception {
        ParsedCommand command = Parser.parse(
                "event conference /from 2026-09-01 /to 2026-09-01");

        assertEquals(ParsedCommand.Type.EVENT, command.getType());
        assertEquals("2026-09-01", command.getFirstDate().toString());
        assertEquals("2026-09-01", command.getSecondDate().toString());
    }

    @Test
    void parse_eventWithStartDateAfterEndDate_throwsHelpfulException() {
        ClslException exception = assertThrows(ClslException.class, () ->
                Parser.parse("event conference /from 2026-09-03 /to 2026-09-01"));

        assertEquals("from date cannot be later than to date", exception.getMessage());
    }

    @Test
    void parse_deadlineWithMissingParts_throwsHelpfulExceptions() {
        assertEquals("deadline of what?", parseError("deadline /by 2026-09-01"));
        assertEquals("by when?", parseError("deadline submit report"));
        assertEquals("by when?", parseError("deadline submit report /by"));
        assertEquals("Please use yyyy-mm-dd format",
                parseError("deadline submit report /by 2026-02-30"));
    }

    @Test
    void parse_eventWithMissingParts_throwsHelpfulExceptions() {
        assertEquals("event of what?", parseError("event /from 2026-09-01 /to 2026-09-02"));
        assertEquals("from when?", parseError("event conference"));
        assertEquals("to when?", parseError("event conference /from 2026-09-01"));
        assertEquals("from when?", parseError("event conference /from /to 2026-09-02"));
        assertEquals("to when?", parseError("event conference /from 2026-09-01 /to"));
    }

    @Test
    void parse_findCommand_returnsKeyword() throws Exception {
        ParsedCommand command = Parser.parse("find book");

        assertEquals(ParsedCommand.Type.FIND, command.getType());
        assertEquals("book", command.getDescription());
    }

    @Test
    void parse_markWithInvalidTaskNumber_throwsHelpfulException() {
        ClslException exception = assertThrows(ClslException.class, () -> Parser.parse("mark zero"));

        assertEquals("task number must be a whole number", exception.getMessage());
    }

    @Test
    void parse_invalidTaskNumbersHaveHelpfulErrors() {
        assertEquals("task number missing", parseError("delete"));
        assertEquals("task number must be at least 1", parseError("delete 0"));
        assertEquals("task number must be at least 1", parseError("delete -4"));
        assertEquals("task number must be a whole number", parseError("delete 1.5"));
    }

    @Test
    void parse_todoWithoutDescription_throwsHelpfulException() {
        ClslException exception = assertThrows(ClslException.class, () -> Parser.parse("todo"));

        assertEquals("todo what exactly?", exception.getMessage());
    }

    @Test
    void parse_findWithoutKeyword_throwsHelpfulException() {
        ClslException exception = assertThrows(ClslException.class, () -> Parser.parse("find"));

        assertEquals("find what?", exception.getMessage());
    }

    @Test
    void parse_onCommandReturnsDate() throws Exception {
        ParsedCommand command = Parser.parse("on 2026-09-01");

        assertEquals(ParsedCommand.Type.ON, command.getType());
        assertEquals("2026-09-01", command.getFirstDate().toString());
    }

    @Test
    void parse_invalidDatesAndOnCommandsHaveHelpfulErrors() {
        assertEquals("on what date?", parseError("on"));
        assertEquals("Please use yyyy-mm-dd format", parseError("on 2026-02-30"));
        assertEquals("Please use yyyy-mm-dd format", parseError("on tomorrow"));
    }

    @Test
    void parse_commandsWithUnexpectedArgumentsAreRejected() {
        assertEquals("I don't understand", parseError("list now"));
        assertEquals("I don't understand", parseError("bye later"));
        assertEquals("I don't understand", parseError("unknown command"));
    }

    @Test
    void parse_blankCommandsAreRejected() {
        assertEquals("command cannot be blank", parseError("   "));
        assertEquals("command cannot be blank", parseError(null));
    }

    private String parseError(String input) {
        ClslException exception = assertThrows(ClslException.class, () -> Parser.parse(input));
        return exception.getMessage();
    }
}
