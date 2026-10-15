package alzara.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import alzara.AlzaraException;
import alzara.command.AddCommand;
import alzara.command.Command;
import alzara.command.DeleteCommand;
import alzara.command.ExitCommand;
import alzara.command.FindCommand;
import alzara.command.ListCommand;
import alzara.command.MarkCommand;
import alzara.command.UnmarkCommand;
import alzara.command.ViewCommand;

/**
 * Tests for {@link CommandParser#parse(String)}.
 *
 * Scope note: AddCommand/MarkCommand/UnmarkCommand/DeleteCommand expose no getters for
 * their parsed fields, and their execute() calls the static Storage.save(...), which
 * would write to the real data/alzara.txt if run. So the "valid input" tests below only
 * assert the returned Command's type (assertInstanceOf) and that no exception is thrown -
 * they deliberately never call execute(). The "invalid input" tests are the more valuable
 * half: they assert the specific AlzaraException and its message, which is fully checkable
 * through the public API alone.
 */
class CommandParserTest {

    // --- bye ---

    // parse("bye") should return an ExitCommand, no exception thrown.
    @Test
    void parse_byeCommand_returnsExitCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("bye"));
        assertInstanceOf(ExitCommand.class, result);
    }

    // CommandType.from uses equalsIgnoreCase for "bye" - parse("BYE") should also
    // return an ExitCommand.
    @Test
    void parse_byeCommandDifferentCase_returnsExitCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("BYE"));
        assertInstanceOf(ExitCommand.class, result);
    }

    // --- list ---

    // parse("list") should return a ListCommand, no exception thrown.
    @Test
    void parse_listCommand_returnsListCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("list"));
        assertInstanceOf(ListCommand.class, result);
    }

    // --- mark ---

    // parse("mark 1") should return a MarkCommand, no exception thrown.
    @Test
    void parse_markValidIndex_returnsMarkCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, result);
    }

    // parse("mark") (no index token at all) should throw AlzaraException with
    // message AlzaraException.MISSING_TASK_NUMBER_MESSAGE.
    @Test
    void parse_markMissingIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("mark"));
        assertEquals(AlzaraException.MISSING_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // parse("mark abc") (index token isn't a number) should throw AlzaraException
    // with message AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE.
    @Test
    void parse_markNonNumericIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("mark abc"));
        assertEquals(AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // parse("mark 1 2") (an extra token after the index) should throw AlzaraException
    // with message AlzaraException.TOO_MANY_ARGUMENTS_MESSAGE.
    @Test
    void parse_markTooManyArguments_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("mark 1 2"));
        assertEquals(AlzaraException.TOO_MANY_ARGUMENTS_MESSAGE, exception.getMessage());
    }

    // --- unmark ---

    // parse("unmark 1") should return an UnmarkCommand, no exception thrown.
    @Test
    void parse_unmarkValidIndex_returnsUnmarkCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("unmark 1"));
        assertInstanceOf(UnmarkCommand.class, result);
    }

    // parse("unmark") should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_NUMBER_MESSAGE.
    @Test
    void parse_unmarkMissingIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("unmark"));
        assertEquals(AlzaraException.MISSING_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // parse("unmark abc") should throw AlzaraException with message
    // AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE.
    @Test
    void parse_unmarkNonNumericIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("unmark abc"));
        assertEquals(AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // parse("unmark 1 2") should throw AlzaraException with message
    // AlzaraException.TOO_MANY_ARGUMENTS_MESSAGE.
    @Test
    void parse_unmarkTooManyArguments_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("unmark 1 2"));
        assertEquals(AlzaraException.TOO_MANY_ARGUMENTS_MESSAGE, exception.getMessage());
    }

    // --- delete ---

    // parse("delete 1") should return a DeleteCommand, no exception thrown.
    @Test
    void parse_deleteValidIndex_returnsDeleteCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("delete 1"));
        assertInstanceOf(DeleteCommand.class, result);
    }

    // parse("delete") should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_NUMBER_MESSAGE.
    @Test
    void parse_deleteMissingIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("delete"));
        assertEquals(AlzaraException.MISSING_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // parse("delete abc") should throw AlzaraException with message
    // AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE.
    @Test
    void parse_deleteNonNumericIndex_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("delete abc"));
        assertEquals(AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    // parse("delete 1 2") should throw AlzaraException with message
    // AlzaraException.TOO_MANY_ARGUMENTS_MESSAGE.
    @Test
    void parse_deleteTooManyArguments_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("delete 1 2"));
        assertEquals(AlzaraException.TOO_MANY_ARGUMENTS_MESSAGE, exception.getMessage());
    }

    // --- todo ---

    // parse("todo read book") should return an AddCommand, no exception thrown.
    @Test
    void parse_todoWithDescription_returnsAddCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, result);
    }

    // parse("todo") (bare command, no description at all) should throw
    // AlzaraException with message AlzaraException.MISSING_TASK_DESC.
    @Test
    void parse_todoMissingDescription_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("todo"));
        assertEquals(AlzaraException.MISSING_TASK_DESC, exception.getMessage());
    }

    // parse("TODO read book") (command word in a different case) should still
    // return an AddCommand - command words are matched case-insensitively.
    @Test
    void parse_todoDifferentCase_returnsAddCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("TODO read book"));
        assertInstanceOf(AddCommand.class, result);
    }

    // parse("  todo read book  ") (leading/trailing spaces around the whole line)
    // should still return an AddCommand - the whole line is trimmed before parsing.
    @Test
    void parse_todoWithSurroundingWhitespace_returnsAddCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("  todo read book  "));
        assertInstanceOf(AddCommand.class, result);
    }

    // parse("todo read | book") (description contains the save-file field
    // separator character) should throw AlzaraException with message
    // AlzaraException.FORBIDDEN_CHARACTER_MESSAGE.
    @Test
    void parse_todoDescriptionContainsPipe_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("todo read | book"));
        assertEquals(AlzaraException.FORBIDDEN_CHARACTER_MESSAGE, exception.getMessage());
    }

    // --- deadline ---

    // parse("deadline return book /by 2019-10-15") should return an AddCommand,
    // no exception thrown.
    @Test
    void parse_deadlineWithDescriptionAndDate_returnsAddCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("deadline return book /by 2019-10-15"));
        assertInstanceOf(AddCommand.class, result);
    }

    // parse("deadline") (bare command) should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_DESC.
    @Test
    void parse_deadlineMissingDescription_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("deadline"));
        assertEquals(AlzaraException.MISSING_TASK_DESC, exception.getMessage());
    }

    // parse("deadline return book") (no " /by " marker at all) should throw
    // AlzaraException with message AlzaraException.MISSING_DEADLINE_MARKER_MESSAGE.
    @Test
    void parse_deadlineMissingByMarker_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("deadline return book"));
        assertEquals(AlzaraException.MISSING_DEADLINE_MARKER_MESSAGE, exception.getMessage());
    }

    // parse("deadline  /by 2019-10-15") (marker present but nothing but whitespace
    // before it) should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_DESC.
    @Test
    void parse_deadlineBlankDescriptionBeforeMarker_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("deadline  /by 2019-10-15"));
        assertEquals(AlzaraException.MISSING_TASK_DESC, exception.getMessage());
    }

    // parse("deadline return book /by not-a-date") should throw AlzaraException
    // with message AlzaraException.INVALID_DEADLINE_DATE_MESSAGE.
    @Test
    void parse_deadlineInvalidDate_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("deadline return book /by not-a-date"));
        assertEquals(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE, exception.getMessage());
    }

    // parse("deadline return book /by 2019-10-15 /by 2019-10-16") (the /by marker
    // appears twice) should throw AlzaraException with message
    // AlzaraException.DUPLICATE_MARKER_MESSAGE.
    @Test
    void parse_deadlineDuplicateByMarker_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("deadline return book /by 2019-10-15 /by 2019-10-16"));
        assertEquals(AlzaraException.DUPLICATE_MARKER_MESSAGE, exception.getMessage());
    }

    // parse("deadline return | book /by 2019-10-15") (description contains the
    // save-file field separator character) should throw AlzaraException with
    // message AlzaraException.FORBIDDEN_CHARACTER_MESSAGE.
    @Test
    void parse_deadlineDescriptionContainsPipe_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("deadline return | book /by 2019-10-15"));
        assertEquals(AlzaraException.FORBIDDEN_CHARACTER_MESSAGE, exception.getMessage());
    }

    // --- event ---

    // parse("event project meeting /from 2019-10-15 /to 2019-10-16") should return
    // an AddCommand, no exception thrown.
    @Test
    void parse_eventWithDescriptionAndDates_returnsAddCommand() {
        Command result = assertDoesNotThrow(() ->
                CommandParser.parse("event project meeting /from 2019-10-15 /to 2019-10-16"));
        assertInstanceOf(AddCommand.class, result);
    }

    // parse("event") (bare command) should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_DESC.
    @Test
    void parse_eventMissingDescription_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("event"));
        assertEquals(AlzaraException.MISSING_TASK_DESC, exception.getMessage());
    }

    // parse("event project meeting") (no " /from "/" /to " markers at all) should
    // throw AlzaraException with message AlzaraException.MISSING_EVENT_MARKER_MESSAGE.
    @Test
    void parse_eventMissingFromToMarkers_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("event project meeting"));
        assertEquals(AlzaraException.MISSING_EVENT_MARKER_MESSAGE, exception.getMessage());
    }

    // parse("event project meeting /to 2019-10-16 /from 2019-10-15") (both markers
    // present, but /to appears before /from) should throw AlzaraException with
    // message AlzaraException.MISSING_EVENT_MARKER_MESSAGE.
    @Test
    void parse_eventToBeforeFrom_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("event project meeting /to 2019-10-16 /from 2019-10-15"));
        assertEquals(AlzaraException.MISSING_EVENT_MARKER_MESSAGE, exception.getMessage());
    }

    // parse("event  /from 2019-10-15 /to 2019-10-16") (markers present but nothing
    // but whitespace before them) should throw AlzaraException with message
    // AlzaraException.MISSING_TASK_DESC.
    @Test
    void parse_eventBlankDescriptionBeforeMarker_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("event  /from 2019-10-15 /to 2019-10-16"));
        assertEquals(AlzaraException.MISSING_TASK_DESC, exception.getMessage());
    }

    // parse("event project meeting /from not-a-date /to 2019-10-16") should throw
    // AlzaraException with message AlzaraException.INVALID_DEADLINE_DATE_MESSAGE.
    @Test
    void parse_eventInvalidStartDate_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("event project meeting /from not-a-date /to 2019-10-16"));
        assertEquals(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE, exception.getMessage());
    }

    // parse("event project meeting /from 2019-10-15 /to not-a-date") should throw
    // AlzaraException with message AlzaraException.INVALID_DEADLINE_DATE_MESSAGE.
    @Test
    void parse_eventInvalidEndDate_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("event project meeting /from 2019-10-15 /to not-a-date"));
        assertEquals(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE, exception.getMessage());
    }

    // parse("event trip /from 2019-10-16 /to 2019-10-15") (both dates valid and in
    // marker order, but the start date is chronologically after the end date) should
    // throw AlzaraException with message AlzaraException.EVENT_DATES_OUT_OF_ORDER_MESSAGE.
    @Test
    void parse_eventStartDateAfterEndDate_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("event trip /from 2019-10-16 /to 2019-10-15"));
        assertEquals(AlzaraException.EVENT_DATES_OUT_OF_ORDER_MESSAGE, exception.getMessage());
    }

    // parse("event trip /from 2019-10-15 /to 2019-10-15") (start equals end - a
    // single-day event) should still return an AddCommand, no exception thrown -
    // the new start-after-end check must not reject the equal-dates boundary.
    @Test
    void parse_eventStartDateEqualsEndDate_returnsAddCommand() {
        Command result = assertDoesNotThrow(() ->
                CommandParser.parse("event trip /from 2019-10-15 /to 2019-10-15"));
        assertInstanceOf(AddCommand.class, result);
    }

    // parse("event trip /from 2019-10-15 /from 2019-10-16 /to 2019-10-17") (the
    // /from marker appears twice) should throw AlzaraException with message
    // AlzaraException.DUPLICATE_MARKER_MESSAGE.
    @Test
    void parse_eventDuplicateFromMarker_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("event trip /from 2019-10-15 /from 2019-10-16 /to 2019-10-17"));
        assertEquals(AlzaraException.DUPLICATE_MARKER_MESSAGE, exception.getMessage());
    }

    // parse("event trip /from 2019-10-15 /to 2019-10-16 /to 2019-10-17") (the /to
    // marker appears twice) should throw AlzaraException with message
    // AlzaraException.DUPLICATE_MARKER_MESSAGE.
    @Test
    void parse_eventDuplicateToMarker_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("event trip /from 2019-10-15 /to 2019-10-16 /to 2019-10-17"));
        assertEquals(AlzaraException.DUPLICATE_MARKER_MESSAGE, exception.getMessage());
    }

    // parse("event pro | ject /from 2019-10-15 /to 2019-10-16") (description
    // contains the save-file field separator character) should throw
    // AlzaraException with message AlzaraException.FORBIDDEN_CHARACTER_MESSAGE.
    @Test
    void parse_eventDescriptionContainsPipe_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                CommandParser.parse("event pro | ject /from 2019-10-15 /to 2019-10-16"));
        assertEquals(AlzaraException.FORBIDDEN_CHARACTER_MESSAGE, exception.getMessage());
    }

    // --- find ---

    // parse("find book") should return a FindCommand, no exception thrown.
    @Test
    void parse_findWithKeyword_returnsFindCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("find book"));
        assertInstanceOf(FindCommand.class, result);
    }

    // parse("find") (no keyword at all) should throw AlzaraException with
    // message AlzaraException.MISSING_KEYWORD_MESSAGE.
    @Test
    void parse_findMissingKeyword_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("find"));
        assertEquals(AlzaraException.MISSING_KEYWORD_MESSAGE, exception.getMessage());
    }

    // parse("find   ") (only whitespace after the command word) should also throw
    // AlzaraException with message AlzaraException.MISSING_KEYWORD_MESSAGE.
    @Test
    void parse_findOnlyWhitespaceAfterCommand_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("find   "));
        assertEquals(AlzaraException.MISSING_KEYWORD_MESSAGE, exception.getMessage());
    }

    // parse("find book urgent") (several whitespace-separated keywords) should
    // also return a FindCommand, no exception thrown.
    @Test
    void parse_findWithMultipleKeywords_returnsFindCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("find book urgent"));
        assertInstanceOf(FindCommand.class, result);
    }

    // --- view ---

    // parse("view 2019-10-15") should return a ViewCommand, no exception thrown.
    @Test
    void parse_viewValidDate_returnsViewCommand() {
        Command result = assertDoesNotThrow(() -> CommandParser.parse("view 2019-10-15"));
        assertInstanceOf(ViewCommand.class, result);
    }

    // parse("view") (bare command, no date at all) should throw AlzaraException
    // with message AlzaraException.MISSING_VIEW_DATE_MESSAGE.
    @Test
    void parse_viewMissingDate_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("view"));
        assertEquals(AlzaraException.MISSING_VIEW_DATE_MESSAGE, exception.getMessage());
    }

    // parse("view   ") (only whitespace after the command word) should also throw
    // AlzaraException with message AlzaraException.MISSING_VIEW_DATE_MESSAGE.
    @Test
    void parse_viewOnlyWhitespaceAfterCommand_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("view   "));
        assertEquals(AlzaraException.MISSING_VIEW_DATE_MESSAGE, exception.getMessage());
    }

    // parse("view Sunday") (date fails LocalDate.parse) should throw AlzaraException
    // with message AlzaraException.INVALID_DEADLINE_DATE_MESSAGE (reused, not a new constant).
    @Test
    void parse_viewInvalidDate_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("view Sunday"));
        assertEquals(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE, exception.getMessage());
    }

    // --- unrecognised input ---

    // parse("/list") (matches no known command) should throw AlzaraException with
    // message AlzaraException.UNRECOGNISED_COMMAND_MESSAGE.
    @Test
    void parse_unrecognisedCommand_exceptionThrown() {
        AlzaraException exception = assertThrows(AlzaraException.class, () -> CommandParser.parse("/list"));
        assertEquals(AlzaraException.UNRECOGNISED_COMMAND_MESSAGE, exception.getMessage());
    }
}
