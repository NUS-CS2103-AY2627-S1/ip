package yqr.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import yqr.command.AddCommand;
import yqr.command.Command;
import yqr.command.FindCommand;
import yqr.command.UndoCommand;
import yqr.exception.YqrException;

/**
 * Tests validation and parsing of user commands.
 */
class ParserTest {
    private static final String MISSING_KEYWORD_MESSAGE = "Please input a keyword to search for";

    @Test
    void parse_validFindCommand_findCommandReturned() throws YqrException {
        Command command = Parser.parse("find book");

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    void parse_findWithoutKeyword_exceptionThrown() {
        YqrException exception = assertThrows(YqrException.class, () -> Parser.parse("find"));

        assertEquals(MISSING_KEYWORD_MESSAGE, exception.getMessage());
    }

    @Test
    void parse_findWithWhitespaceOnlyKeyword_exceptionThrown() {
        YqrException exception = assertThrows(YqrException.class, () -> Parser.parse("find     "));

        assertEquals(MISSING_KEYWORD_MESSAGE, exception.getMessage());
    }

    @Test
    void parse_nullOrBlankCommand_clearErrorReturned() {
        assertParseError(null, "Please enter a command");
        assertParseError("   ", "Please enter a command");
    }

    @Test
    void parse_multipleCommands_clearErrorReturned() {
        assertParseError("list\nbye", "Please enter one command at a time");
    }

    @Test
    void parse_noArgumentCommandWithExtraText_clearErrorReturned() {
        assertParseError("list now", "The list command does not accept parameters");
        assertParseError("bye please", "The bye command does not accept parameters");
    }

    @Test
    void parse_mistypedCommand_helpfulSuggestionReturned() {
        assertParseError("lits", "Unknown command 'lits'. Did you mean 'list'?");
        assertParseError("TOD read book", "Unknown command 'TOD'. Did you mean 'todo'?");
    }

    @Test
    void parse_unrecognisedCommand_availableCommandsReturned() {
        assertParseError("unknown", "Unknown command 'unknown'. Available commands: "
                + "todo, deadline, event, list, mark, unmark, delete, find, undo, bye");
    }

    @Test
    void parse_deadlineWithFlexibleWhitespace_deadlineCommandReturned() throws YqrException {
        Command command = Parser.parse("  deadline   submit report   /by   2026-09-30  ");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parse_deadlineWithDuplicateBy_duplicateParameterRejected() {
        assertParseError("deadline submit /by 2026-09-20 /by 2026-09-21",
                "Please specify /by only once");
    }

    @Test
    void parse_deadlineWithImpossibleDate_invalidDateRejected() {
        assertParseError("deadline submit /by 2026-02-30",
                "Please input a valid deadline in yyyy-MM-dd format");
    }

    @Test
    void parse_eventWithFlexibleWhitespace_eventCommandReturned() throws YqrException {
        Command command = Parser.parse(" event meeting  /from  09:00  /to  10:00 ");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parse_eventWithDuplicateParameters_duplicateParameterRejected() {
        assertParseError("event meeting /from 09:00 /from 09:30 /to 10:00",
                "Please specify /from only once");
        assertParseError("event meeting /from 09:00 /to 10:00 /to 11:00",
                "Please specify /to only once");
    }

    @Test
    void parse_eventWithReversedParameters_formatErrorReturned() {
        assertParseError("event meeting /to 10:00 /from 09:00",
                "Event format: event DESCRIPTION /from START /to END");
    }

    @Test
    void parse_eventWithEqualOrDescendingTimes_rangeRejected() {
        assertParseError("event meeting /from 10:00 /to 10:00",
                "Event start must be earlier than its end");
        assertParseError("event meeting /from 11:00 /to 10:00",
                "Event start must be earlier than its end");
    }

    @Test
    void parse_eventWithInvalidOrMixedDateTimes_formatRejected() {
        assertParseError("event meeting /from 2026-02-30 09:00 /to 2026-03-01 10:00",
                "Please input valid event times in yyyy-MM-dd HH:mm format");
        assertParseError("event meeting /from 2026-09-10 /to tomorrow",
                "Please use yyyy-MM-dd for both event times");
    }

    @Test
    void parse_eventWithFreeFormTimes_eventCommandReturned() throws YqrException {
        Command command = Parser.parse("event meeting /from Monday morning /to Monday afternoon");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parse_taskWithStorageSeparator_separatorRejected() {
        assertParseError("todo read | write", "Task details cannot contain the '|' character");
    }

    @Test
    void parse_taskNumberWithSignZeroOrOverflow_numberRejected() {
        assertParseError("mark +1", "Please input a positive task number");
        assertParseError("delete 0", "Please input a positive task number");
        assertParseError("unmark 999999999999999999999", "The task number is too large");
    }

    @Test
    void parse_undoCommand_undoCommandReturned() throws YqrException {
        Command command = Parser.parse("undo");

        assertInstanceOf(UndoCommand.class, command);
    }

    @Test
    void parse_undoWithArguments_clearErrorReturned() {
        assertParseError("undo 1", "The undo command does not accept parameters");
    }

    /** Asserts that parsing fails with a specific user-facing message. */
    private static void assertParseError(String input, String expectedMessage) {
        YqrException exception = assertThrows(YqrException.class, () -> Parser.parse(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
