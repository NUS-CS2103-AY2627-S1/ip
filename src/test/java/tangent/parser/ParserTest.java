package tangent.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import tangent.command.AddCommand;
import tangent.command.DeleteCommand;
import tangent.command.ExitCommand;
import tangent.command.FindCommand;
import tangent.command.ListCommand;
import tangent.command.MarkCommand;
import tangent.command.UnmarkCommand;
import tangent.exception.ErrorMessages;
import tangent.exception.TangentException;
import tangent.task.Deadline;
import tangent.task.Event;
import tangent.task.ToDo;



/** Tests that the parser creates commands without relying on application state. */
public class ParserTest {
    @Test
    public void parseCommand_validCommands_correctCommandTypesReturned() throws TangentException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2/12/2019 1800"));
        assertInstanceOf(AddCommand.class, Parser.parse("event meeting /from 2/12/2019 1800 /to 2/12/2019 1900"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(FindCommand.class, Parser.parse("find stuff"));
    }

    @Test
    public void parseTaskNumber_invalidIndex_exceptionThrown() {
        assertInvalidTaskNumber("mark");
        assertInvalidTaskNumber("unmark abc");
        assertInvalidTaskNumber("delete 0");
        assertInvalidTaskNumber("mark -1");
    }

    @Test
    public void parseTaskNumbers_validIndexes_correctIndexListReturned() throws TangentException {
        List<Integer> indexes = Parser.parseTaskIndexes(new String[]{"delete", "1 2-4 6-8"});

        assertEquals(List.of(0, 1, 2, 3, 5, 6, 7), indexes);
    }

    @Test
    public void parseTaskNumbers_invalidSyntax_exceptionWithHelpThrown() {
        assertInvalidSelectorFormat("delete 2 - 5");
        assertInvalidSelectorFormat("delete 02-05");
        assertInvalidSelectorFormat("delete 2--5");
        assertInvalidSelectorFormat("delete 2-");
        assertInvalidSelectorFormat("delete 1,3");
        assertInvalidTaskNumber("delete -5");
    }

    @Test
    public void parseTaskNumbers_reversedRangeOrDuplicateIndex_exceptionThrown() {
        assertInvalidTaskNumber("delete 5-2");
        assertInvalidTaskNumber("delete 2 2");
        assertInvalidTaskNumber("delete 1-3 3-5");
    }

    @Test
    public void parseTask_missingDescription_exceptionThrown() {
        assertMissingDescription("todo");
        assertMissingDescription("deadline");
        assertMissingDescription("event");
    }

    @Test
    public void parseFind_missingKeyword_exceptionThrown() {
        TangentException exception = assertThrows(TangentException.class, () -> Parser.parse("find"));
        assertEquals(ErrorMessages.MISSING_SEARCH_KEYWORD_MESSAGE, exception.getMessage());
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        TangentException exception = assertThrows(TangentException.class, () -> Parser.parse("remind me"));
        assertEquals(ErrorMessages.INVALID_COMMAND_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseTask_todoTrimsDescription() throws TangentException {
        ToDo task = assertInstanceOf(ToDo.class, Parser.parseTask("  read book  ", CommandTypes.TODO));

        assertEquals("read book", task.getDescription());
    }

    @Test
    public void parseTask_deadlineParsesDescriptionAndDate() throws TangentException {
        Deadline task = assertInstanceOf(Deadline.class,
                Parser.parseTask("return book /by 2/12/2019 1800", CommandTypes.DEADLINE));

        assertEquals("return book", task.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), task.getBy());
    }

    @Test
    public void parseTask_eventParsesDescriptionAndTimes() throws TangentException {
        Event task = assertInstanceOf(Event.class,
                Parser.parseTask("project meeting /from 3/12/2019 0900 /to 3/12/2019 1100", CommandTypes.EVENT));

        assertEquals("project meeting", task.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 3, 9, 0), task.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 11, 0), task.getTo());
    }

    @Test
    public void parseTask_unknownType_throwsUnknownTaskType() {
        assertExceptionMessage(ErrorMessages.UNKNOWN_TASK_TYPE_MESSAGE, () -> Parser.parseTask(
                "some details", CommandTypes.LIST));
    }

    @Test
    public void parseTask_descriptionWithFieldSeparator_throwsDescriptionError() {
        String expectedMessage = String.format(ErrorMessages.DESCRIPTION_SEPARATOR_MESSAGE, " | ");
        assertExceptionMessage(expectedMessage, () -> Parser.parseTask(
                "buy | milk", CommandTypes.TODO));
    }

    @Test
    public void parseTask_deadlineMalformedDetails_throwsDeadlineFormatError() {
        assertExceptionMessage(ErrorMessages.DEADLINE_FORMAT_MESSAGE, () -> Parser.parseTask(
                "return book", CommandTypes.DEADLINE));
        assertExceptionMessage(ErrorMessages.DEADLINE_FORMAT_MESSAGE, () -> Parser.parseTask(
                "return book /by", CommandTypes.DEADLINE));
        assertExceptionMessage(ErrorMessages.DEADLINE_FORMAT_MESSAGE, () -> Parser.parseTask(
                "return /by 2/12/2019 1800 /by 3/12/2019 1800", CommandTypes.DEADLINE));
    }

    @Test
    public void parseTask_eventMalformedDetails_throwsEventFormatError() {
        assertExceptionMessage(ErrorMessages.EVENT_FORMAT_MESSAGE, () -> Parser.parseTask(
                "meeting", CommandTypes.EVENT));
        assertExceptionMessage(ErrorMessages.EVENT_FORMAT_MESSAGE, () -> Parser.parseTask(
                "meeting /to 3/12/2019 1100 /from 3/12/2019 0900", CommandTypes.EVENT));
        assertExceptionMessage(ErrorMessages.EVENT_FORMAT_MESSAGE, () -> Parser.parseTask(
                "meeting /from 3/12/2019 0900 /to", CommandTypes.EVENT));
        assertExceptionMessage(ErrorMessages.EVENT_FORMAT_MESSAGE, () -> Parser.parseTask(
                "meeting /from 3/12/2019 0900 /from 3/12/2019 1000 /to 3/12/2019 1100",
                CommandTypes.EVENT));
        assertExceptionMessage(ErrorMessages.EVENT_FORMAT_MESSAGE, () -> Parser.parseTask(
                "meeting /from 3/12/2019 0900 /to 3/12/2019 1100 /to 3/12/2019 1200", CommandTypes.EVENT));
    }

    @Test
    public void parseTask_invalidDate_throwsInvalidDateError() {
        assertExceptionMessage(ErrorMessages.INVALID_DATE_MESSAGE, () -> Parser.parseTask(
                "return book /by 32/01/2024 1800", CommandTypes.DEADLINE));
        assertExceptionMessage(ErrorMessages.BAD_DATE_MESSAGE, () -> Parser.parseTask(
                "meeting /from 3/12/2019 0900 /to 2560", CommandTypes.EVENT));
    }

    @Test
    public void parseTask_malformedDate_throwsBadDateError() {
        assertExceptionMessage(ErrorMessages.BAD_DATE_MESSAGE, () -> Parser.parseTask(
                "return book /by 31-2-2019 1800", CommandTypes.DEADLINE));
    }

    @Test
    public void parseTask_eventEndNotAfterStart_throwsOrderError() {
        assertExceptionMessage(ErrorMessages.END_TIME_ORDER_MESSAGE, () -> Parser.parseTask(
                "meeting /from 3/12/2019 0900 /to 3/12/2019 0900", CommandTypes.EVENT));
        assertExceptionMessage(ErrorMessages.END_TIME_ORDER_MESSAGE, () -> Parser.parseTask(
                "meeting /from 3/12/2019 0900 /to 2/12/2019 1100", CommandTypes.EVENT));
    }

    @Test
    public void parseTaskIndexes_missingInputs_throwsInvalidIndex() {
        assertExceptionMessage(ErrorMessages.INVALID_TASK_INDEX_MESSAGE, () -> Parser.parseTaskIndexes(
                new String[]{"delete"}));
        assertExceptionMessage(ErrorMessages.INVALID_TASK_INDEX_MESSAGE, () -> Parser.parseTaskIndexes(
                new String[]{"delete", "   "}));
    }

    @Test
    public void parseTaskIndexes_largeNumberOverflow_throwsInvalidIndex() {
        assertExceptionMessage(ErrorMessages.INVALID_TASK_INDEX_MESSAGE, () -> Parser.parseTaskIndexes(
                new String[]{"delete", "2147483648"}));
    }

    @Test
    public void parse_findCommand_returnsFindCommand() throws TangentException {
        assertInstanceOf(FindCommand.class, Parser.parse("find   book"));
    }

    private void assertExceptionMessage(String expectedMessage, ThrowingAction action) {
        TangentException exception = assertThrows(TangentException.class, action::run);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private interface ThrowingAction {
        void run() throws TangentException;
    }

    /** Verifies every invalid task-number format produces the same user-facing message. */
    private void assertInvalidTaskNumber(String input) {
        TangentException exception = assertThrows(TangentException.class, () -> Parser.parse(input));
        assertEquals(ErrorMessages.INVALID_TASK_INDEX_MESSAGE, exception.getMessage());
    }

    /** Verifies every task-creation command requires a description. */
    private void assertMissingDescription(String input) {
        TangentException exception = assertThrows(TangentException.class, () -> Parser.parse(input));
        assertEquals(ErrorMessages.MISSING_TASK_DESCRIPTION_MESSAGE, exception.getMessage());
    }

    /** Verifies malformed selectors produce the selector-format help message. */
    private void assertInvalidSelectorFormat(String input) {
        TangentException exception = assertThrows(TangentException.class, () -> Parser.parse(input));
        assertEquals(ErrorMessages.TASK_INDEX_FORMAT_MESSAGE,
                exception.getMessage());
    }
}
