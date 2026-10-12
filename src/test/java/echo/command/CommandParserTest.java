package echo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import echo.EchoException;

/** Tests conversion of user input into validated commands. */
public class CommandParserTest {
    /** The parser under test. */
    private final CommandParser parser = new CommandParser();

    @Test
    public void parse_helpInput_returnsHelpCommand() throws EchoException {
        Command command = parser.parse("help");

        assertInstanceOf(HelpCommand.class, command);
        assertEquals(Command.Type.HELP, command.getType());
    }

    @Test
    public void parse_listInput_returnsListCommand() throws EchoException {
        Command command = parser.parse("list");

        assertInstanceOf(ListCommand.class, command);
        assertEquals(Command.Type.LIST, command.getType());
    }

    @Test
    public void parse_findInput_returnsFindCommandWithKeyword() throws EchoException {
        Command command = parser.parse("find book");

        assertInstanceOf(FindCommand.class, command);
        assertEquals(Command.Type.FIND, command.getType());
        assertEquals("book", command.getArgument(0));
    }

    @Test
    public void parse_findInputWithoutExactlyOneKeyword_exceptionThrown() {
        assertParseErrorContaining("find", "exactly one keyword");
        assertParseErrorContaining("find read book", "exactly one keyword");
    }

    @Test
    public void parse_sortInputWithoutCriteria_returnsSortCommandWithDate() throws EchoException {
        Command command = parser.parse("sort");

        assertInstanceOf(SortCommand.class, command);
        assertEquals(Command.Type.SORT, command.getType());
        assertEquals("date", command.getArgument(0));
    }

    @Test
    public void parse_sortInputWithDateCriteria_returnsSortCommandWithDate() throws EchoException {
        Command command = parser.parse("sort date");

        assertInstanceOf(SortCommand.class, command);
        assertEquals(Command.Type.SORT, command.getType());
        assertEquals("date", command.getArgument(0));
    }

    @Test
    public void parse_sortInputWithNameCriteria_returnsSortCommandWithName() throws EchoException {
        Command command = parser.parse("sort name");

        assertInstanceOf(SortCommand.class, command);
        assertEquals(Command.Type.SORT, command.getType());
        assertEquals("name", command.getArgument(0));
    }

    @Test
    public void parse_sortInputWithDescriptionCriteria_returnsSortCommandWithName() throws EchoException {
        Command command = parser.parse("sort description");

        assertInstanceOf(SortCommand.class, command);
        assertEquals(Command.Type.SORT, command.getType());
        assertEquals("name", command.getArgument(0));
    }

    @Test
    public void parse_sortInputWithInvalidCriteria_exceptionThrown() {
        assertParseErrorContaining("sort priority", "sort criterion");
    }

    @Test
    public void parse_sortInputWithTooManyArguments_exceptionThrown() {
        assertParseErrorContaining("sort date now", "Too many arguments");
    }

    @Test
    public void parse_todoInput_returnsTodoCommandWithCompleteDescription() throws EchoException {
        Command command = parser.parse("todo read book");

        assertInstanceOf(TodoCommand.class, command);
        assertEquals(Command.Type.TODO, command.getType());
        assertEquals("read book", command.getArgument(0));
    }

    @Test
    public void parse_todoInputWithExtraWhitespace_trimsDescription() throws EchoException {
        Command command = parser.parse("  todo   read book   ");

        assertEquals("read book", command.getArgument(0));
    }

    @Test
    public void parse_todoInputWithoutDescription_exceptionThrown() {
        assertParseError(
                "todo",
                "A todo needs a description. Try: todo <description>."
        );
    }

    @Test
    public void parse_deadlineInputWithoutTime_returnsDeadlineCommand() throws EchoException {
        Command command = parser.parse("deadline submit report /by 30-09-2026");

        assertInstanceOf(DeadlineCommand.class, command);
        assertEquals(Command.Type.DEADLINE, command.getType());
        assertEquals("submit report", command.getArgument(0));
        assertEquals("30-09-2026", command.getArgument(1));
    }

    @Test
    public void parse_deadlineInputWithTime_returnsDeadlineCommand() throws EchoException {
        Command command = parser.parse("deadline essay /by 30-09-2026 16:00");

        assertInstanceOf(DeadlineCommand.class, command);
        assertEquals(Command.Type.DEADLINE, command.getType());
        assertEquals("essay", command.getArgument(0));
        assertEquals("30-09-2026 16:00", command.getArgument(1));
    }

    @Test
    public void parse_deadlineInputWithUppercaseMarker_returnsDeadlineCommand() throws EchoException {
        Command command = parser.parse("deadline essay /BY 30-09-2026");

        assertInstanceOf(DeadlineCommand.class, command);
        assertEquals("essay", command.getArgument(0));
        assertEquals("30-09-2026", command.getArgument(1));
    }

    @Test
    public void parse_deadlineInputWithoutDescription_exceptionThrown() {
        assertParseError(
                "deadline",
                "A deadline needs a description and a due date. "
                        + "Try: deadline <description> /by <dd-mm-yyyy> [HH:MM]."
        );
    }

    @Test
    public void parse_deadlineInputWithoutDueDateMarker_exceptionThrown() {
        assertParseError(
                "deadline submit report",
                "A deadline must include a due date using '/by <deadline>'. "
                        + "Try: deadline <description> /by <dd-mm-yyyy> [HH:MM]."
        );
    }

    @Test
    public void parse_deadlineInputWithoutDescriptionBeforeMarker_exceptionThrown() {
        assertParseErrorContaining("deadline /by 30-09-2026", "deadline");
    }

    @Test
    public void parse_deadlineInputWithoutDeadline_exceptionThrown() {
        assertParseErrorContaining("deadline submit report /by", "due date");
    }

    @Test
    public void parse_deadlineInputWithWrongDateFormat_exceptionThrown() {
        assertParseErrorContaining(
                "deadline submit report /by 30/09/2026",
                "dd-mm-yyyy"
        );
    }

    @Test
    public void parse_deadlineInputWithInvalidDate_exceptionThrown() {
        assertParseErrorContaining(
                "deadline submit report /by 31-02-2026",
                "not a valid date or time"
        );
    }

    @Test
    public void parse_eventInputWithoutTime_returnsEventCommand() throws EchoException {
        Command command = parser.parse(
                "event project meeting /from 30-09-2026 /to 01-10-2026");

        assertInstanceOf(EventCommand.class, command);
        assertEquals(Command.Type.EVENT, command.getType());
        assertEquals("project meeting", command.getArgument(0));
        assertEquals("30-09-2026", command.getArgument(1));
        assertEquals("01-10-2026", command.getArgument(2));
    }

    @Test
    public void parse_eventInputWithTime_returnsEventCommand() throws EchoException {
        Command command = parser.parse(
                "event meeting /from 30-09-2026 14:00 /to 30-09-2026 16:00");

        assertInstanceOf(EventCommand.class, command);
        assertEquals(Command.Type.EVENT, command.getType());
        assertEquals("meeting", command.getArgument(0));
        assertEquals("30-09-2026 14:00", command.getArgument(1));
        assertEquals("30-09-2026 16:00", command.getArgument(2));
    }

    @Test
    public void parse_eventInputWithoutDescription_exceptionThrown() {
        assertParseErrorContaining("event", "event needs a description");
    }

    @Test
    public void parse_eventInputWithoutFromMarker_exceptionThrown() {
        assertParseErrorContaining(
                "event team meeting /to 01-10-2026",
                "start date"
        );
    }

    @Test
    public void parse_eventInputWithoutStartDate_exceptionThrown() {
        assertParseErrorContaining(
                "event team meeting /from /to 01-10-2026",
                "start date"
        );
    }

    @Test
    public void parse_eventInputWithoutToMarker_exceptionThrown() {
        assertParseErrorContaining(
                "event team meeting /from 30-09-2026",
                "end date"
        );
    }

    @Test
    public void parse_eventInputWithoutEndDate_exceptionThrown() {
        assertParseErrorContaining(
                "event team meeting /from 30-09-2026 /to",
                "end date"
        );
    }

    @Test
    public void parse_eventInputWithWrongDateFormat_exceptionThrown() {
        assertParseErrorContaining(
                "event team meeting /from 30/09/2026 /to 01-10-2026",
                "dd-mm-yyyy"
        );
    }

    @Test
    public void parse_eventInputWithInvalidDate_exceptionThrown() {
        assertParseErrorContaining(
                "event team meeting /from 31-09-2026 /to 01-10-2026",
                "not a valid date or time"
        );
    }

    @Test
    public void parse_markInputWithValidNumber_returnsMarkCommand() throws EchoException {
        Command command = parser.parse("mark 1");

        assertInstanceOf(MarkCommand.class, command);
        assertEquals(Command.Type.MARK, command.getType());
        assertEquals("1", command.getArgument(0));
    }

    @Test
    public void parse_unmarkInputWithValidNumber_returnsUnmarkCommand() throws EchoException {
        Command command = parser.parse("unmark 1");

        assertInstanceOf(UnmarkCommand.class, command);
        assertEquals(Command.Type.UNMARK, command.getType());
        assertEquals("1", command.getArgument(0));
    }

    @Test
    public void parse_deleteInputWithValidNumber_returnsDeleteCommand() throws EchoException {
        Command command = parser.parse("delete 1");

        assertInstanceOf(DeleteCommand.class, command);
        assertEquals(Command.Type.DELETE, command.getType());
        assertEquals("1", command.getArgument(0));
    }

    @Test
    public void parse_taskNumberCommandWithoutNumber_exceptionThrown() {
        assertParseErrorContaining("mark", "exactly one task number");
        assertParseErrorContaining("unmark", "exactly one task number");
        assertParseErrorContaining("delete", "exactly one task number");
    }

    @Test
    public void parse_taskNumberCommandWithNonNumber_exceptionThrown() {
        assertParseErrorContaining("mark one", "not a valid task number");
        assertParseErrorContaining("unmark one", "not a valid task number");
        assertParseErrorContaining("delete one", "not a valid task number");
    }

    @Test
    public void parse_taskNumberCommandWithNonPositiveNumber_exceptionThrown() {
        assertParseErrorContaining("mark 0", "start at 1");
        assertParseErrorContaining("unmark -1", "start at 1");
        assertParseErrorContaining("delete 0", "start at 1");
    }

    @Test
    public void parse_noArgumentCommandWithArguments_exceptionThrown() {
        assertParseErrorContaining("help now", "does not take any arguments");
        assertParseErrorContaining("list now", "does not take any arguments");
        assertParseErrorContaining("bye now", "does not take any arguments");
    }

    @Test
    public void parse_byeInput_returnsExitCommand() throws EchoException {
        Command command = parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertEquals(Command.Type.BYE, command.getType());
        assertTrue(command.isExit());
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        assertParseErrorContaining("   ", "Please enter a command");
    }

    @Test
    public void parse_unknownInput_exceptionThrown() {
        EchoException exception = assertThrows(
                EchoException.class, () -> parser.parse("dance"));

        assertTrue(exception.getMessage().contains("do not recognise 'dance'"));
        assertFalse(exception.getMessage().isBlank());
    }

    @Test
    public void parse_deadlineInputWithDuplicateByMarker_exceptionThrown() {
        assertParseErrorContaining(
                "deadline submit report /by 30-09-2026 /by 01-10-2026",
                "specified multiple times"
        );
    }

    @Test
    public void parse_eventInputWithDuplicateFromMarker_exceptionThrown() {
        assertParseErrorContaining(
                "event camp /from 01-10-2026 /from 02-10-2026 /to 05-10-2026",
                "specified multiple times"
        );
    }

    @Test
    public void parse_eventInputWithDuplicateToMarker_exceptionThrown() {
        assertParseErrorContaining(
                "event camp /from 01-10-2026 /to 05-10-2026 /to 06-10-2026",
                "specified multiple times"
        );
    }

    @Test
    public void parse_eventInputWithReversedMarkerOrder_returnsEventCommand() throws EchoException {
        Command command = parser.parse("event camp /to 05-10-2026 /from 01-10-2026");

        assertInstanceOf(EventCommand.class, command);
        assertEquals("camp", command.getArgument(0));
        assertEquals("01-10-2026", command.getArgument(1));
        assertEquals("05-10-2026", command.getArgument(2));
    }

    @Test
    public void parse_taskDescriptionWithPipeCharacter_exceptionThrown() {
        assertParseErrorContaining("todo read | book", "reserved for data storage");
        assertParseErrorContaining("deadline submit | report /by 30-09-2026", "reserved for data storage");
        assertParseErrorContaining("event camp /from 01-10-2026 | /to 05-10-2026", "reserved for data storage");
    }

    @Test
    public void parse_eventInputWithStartAfterEndDate_exceptionThrown() {
        assertParseErrorContaining(
                "event conference /from 05-10-2026 /to 01-10-2026",
                "cannot be after end date"
        );
    }

    @Test
    public void parse_eventInputWithIdenticalStartAndEndDateWithoutTime_exceptionThrown() {
        assertParseErrorContaining(
                "event seminar /from 01-10-2026 /to 01-10-2026",
                "cannot be identical to end date"
        );
    }

    @Test
    public void parse_eventInputWithStartAfterEndTime_exceptionThrown() {
        assertParseErrorContaining(
                "event meeting /from 01-10-2026 16:00 /to 01-10-2026 14:00",
                "cannot be after end time"
        );
    }

    @Test
    public void parse_eventInputWithSameStartAndEndTime_exceptionThrown() {
        assertParseErrorContaining(
                "event meeting /from 01-10-2026 14:00 /to 01-10-2026 14:00",
                "cannot be the same as end time"
        );
    }

    @Test
    public void parse_taskDescriptionWithMultipleConsecutiveSpaces_normalizesSpaces() throws EchoException {
        Command command = parser.parse("todo   read    multiple    books   ");

        assertEquals("read multiple books", command.getArgument(0));
    }

    /** Asserts that parsing an input fails with the supplied complete message. */
    private void assertParseError(String input, String expectedMessage) {
        EchoException exception = assertThrows(
                EchoException.class, () -> parser.parse(input));

        assertEquals(expectedMessage, exception.getMessage());
    }

    /** Asserts that parsing an input fails with a message containing the supplied text. */
    private void assertParseErrorContaining(String input, String expectedText) {
        EchoException exception = assertThrows(
                EchoException.class, () -> parser.parse(input));

        assertTrue(
                exception.getMessage().contains(expectedText), () -> "Expected error to contain '"
                        + expectedText + "' but was: " + exception.getMessage()
        );
    }
}
