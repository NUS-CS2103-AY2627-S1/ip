package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests command recognition, parsing, and task-number validation in {@link Parser}. */
public class ParserTest {

    /** Verifies that a command name is recognised with or without arguments. */
    @Test
    public void isCommand_exactNameOrWhitespaceArgument_trueReturned() {
        Parser parser = new Parser();

        assertTrue(parser.isCommand("todo", "todo"));
        assertTrue(parser.isCommand("todo read book", "todo"));
        assertTrue(parser.isCommand("todo\tread book", "todo"));
    }

    /** Verifies that a command name is not matched as part of another word. */
    @Test
    public void isCommand_similarNameOrNonWhitespaceSuffix_falseReturned() {
        Parser parser = new Parser();

        assertFalse(parser.isCommand("tod", "todo"));
        assertFalse(parser.isCommand("todoist", "todo"));
        assertFalse(parser.isCommand("todo-read", "todo"));
    }

    /** Verifies that command recognition accepts any matching name in a varargs group. */
    @Test
    public void isCommand_multipleNamesOrNoNames_expectedResultReturned() {
        Parser parser = new Parser();

        assertTrue(parser.isCommand("deadline task", "todo", "deadline", "event"));
        assertFalse(parser.isCommand("list", "todo", "deadline", "event"));
        assertFalse(parser.isCommand("todo"));
    }

    /** Verifies that the text after a command is trimmed and returned as its argument. */
    @Test
    public void getCommandArgument_commandWithArgument_trimmedArgumentReturned() {
        Parser parser = new Parser();

        assertEquals("read book", parser.getCommandArgument("todo   read book  ", "todo"));
        assertEquals("1", parser.getCommandArgument("delete\t1", "delete"));
    }

    /** Verifies that a command with no following text has an empty argument. */
    @Test
    public void getCommandArgument_commandWithoutArgument_emptyStringReturned() {
        Parser parser = new Parser();

        assertEquals("", parser.getCommandArgument("todo", "todo"));
        assertEquals("", parser.getCommandArgument("todo ", "todo"));
    }

    /** Verifies that exit detection is case-insensitive but does not accept extra text. */
    @Test
    public void isExitCommand_byeInAnyCase_trueAndOtherTextFalse() {
        Parser parser = new Parser();

        assertTrue(parser.isExitCommand("bye"));
        assertTrue(parser.isExitCommand("BYE"));
        assertFalse(parser.isExitCommand("bye now"));
        assertFalse(parser.isExitCommand("goodbye"));
    }

    /** Verifies recognition of each supported task-creation command. */
    @Test
    public void isTaskCreationCommand_supportedTaskCommands_trueReturned() {
        Parser parser = new Parser();

        assertTrue(parser.isTaskCreationCommand("todo"));
        assertTrue(parser.isTaskCreationCommand("deadline return book /by 2026-08-25"));
        assertTrue(parser.isTaskCreationCommand("event meeting /from 2026-08-25 /to 2026-08-26"));
    }

    /** Verifies that non-task commands are not classified as task creation commands. */
    @Test
    public void isTaskCreationCommand_nonTaskCommand_falseReturned() {
        Parser parser = new Parser();

        assertFalse(parser.isTaskCreationCommand("list"));
        assertFalse(parser.isTaskCreationCommand("mark 1"));
        assertFalse(parser.isTaskCreationCommand("todoist"));
    }

    /** Verifies that every supported command is mapped to its command object. */
    @Test
    public void parse_supportedCommand_correctCommandTypeReturned() throws BobbyException {
        Parser parser = new Parser();

        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
        assertInstanceOf(ListCommand.class, parser.parse("list"));
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 1"));
        assertInstanceOf(PriorityCommand.class, parser.parse("priority 1 high"));
        assertInstanceOf(AddCommand.class, parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline return book /by 2026-08-25"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event project meeting /from 2026-08-25 /to 2026-08-26"));
    }

    /** Verifies that leading, trailing, and repeated whitespace is accepted and normalized. */
    @Test
    public void parse_irregularWhitespace_validCommandsReturned() throws BobbyException {
        Parser parser = new Parser();

        assertInstanceOf(AddCommand.class, parser.parse("  todo   read   book  "));
        assertInstanceOf(AddCommand.class,
                parser.parse(" deadline return book   /by   2026-08-25 "));
        assertInstanceOf(AddCommand.class,
                parser.parse(" event meeting  /from   2026-08-25   /to  2026-08-26 "));
    }

    /** Verifies that commands without parameters reject unexpected arguments. */
    @Test
    public void parse_noArgumentCommandWithArguments_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException listException = assertThrows(BobbyException.class, () ->
                parser.parse("list all"));
        BobbyException byeException = assertThrows(BobbyException.class, () ->
                parser.parse("bye now"));

        assertEquals("Error! The list command does not accept arguments.",
                listException.getMessage());
        assertEquals("Error! The bye command does not accept arguments.",
                byeException.getMessage());
    }

    /** Verifies that an unsupported command produces a helpful error. */
    @Test
    public void parse_unsupportedCommand_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("archive old tasks"));

        assertEquals("No such task type available. Try again!", exception.getMessage());
    }

    /** Verifies that a find command without a keyword is rejected. */
    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("find   "));

        assertEquals("Error! The search keyword cannot be empty!", exception.getMessage());
    }

    /** Verifies that a to-do description is trimmed before the task is created. */
    @Test
    public void parse_validTodo_addCommandReturned() throws BobbyException {
        Parser parser = new Parser();

        assertInstanceOf(AddCommand.class, parser.parse("todo   read book  "));
    }

    /** Verifies that a to-do without a description is rejected. */
    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("todo   "));

        assertEquals("Error! The description of a todo cannot be empty!", exception.getMessage());
    }

    /** Verifies that the storage separator is rejected in a to-do description. */
    @Test
    public void parse_todoWithStorageSeparator_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("todo read | book"));

        assertEquals("Error! Task details cannot contain the '|' character!",
                exception.getMessage());
    }

    /** Verifies control characters that survive whitespace normalization are rejected. */
    @Test
    public void parse_todoWithControlCharacter_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("todo read\u0001book"));

        assertEquals("Error! Task details cannot contain control characters!", exception.getMessage());
    }

    /** Verifies that valid ISO date-only and date-time deadlines are accepted. */
    @Test
    public void parse_validDeadline_addCommandReturned() throws BobbyException {
        Parser parser = new Parser();

        assertInstanceOf(AddCommand.class,
                parser.parse("deadline return book /by 2026-08-25"));
        assertInstanceOf(AddCommand.class,
                parser.parse("deadline call client /by 2026-08-25 0930"));
    }

    /** Verifies that a deadline without a description is rejected. */
    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("deadline /by 2026-08-25"));

        assertEquals("Error! The description of a deadline cannot be empty!",
                exception.getMessage());
    }

    /** Verifies that a deadline without a date is rejected. */
    @Test
    public void parse_deadlineWithoutDate_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("deadline return book /by"));

        assertEquals("Error! The date of a deadline cannot be empty!", exception.getMessage());
    }

    /** Verifies that a deadline with an invalid date format is rejected. */
    @Test
    public void parse_deadlineWithInvalidDate_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("deadline return book /by not-a-date"));

        assertEquals("Error! The deadline must be a valid date. "
                + "Use yyyy-MM-dd or yyyy-MM-dd HHmm.", exception.getMessage());
    }

    /** Verifies that a deadline date-time using slashes is rejected. */
    @Test
    public void parse_deadlineWithSlashedDateTime_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("deadline call client /by 25/8/2026 0930"));

        assertEquals("Error! The deadline must be a valid date. "
                + "Use yyyy-MM-dd or yyyy-MM-dd HHmm.", exception.getMessage());
    }

    /** Verifies that repeating the deadline parameter is rejected explicitly. */
    @Test
    public void parse_deadlineWithRepeatedByParameter_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("deadline return book /by 2026-08-25 /by 2026-08-26"));

        assertEquals("Error! The /by parameter can only be specified once!",
                exception.getMessage());
    }

    /** Verifies that a valid event with start and end dates is accepted. */
    @Test
    public void parse_validEvent_addCommandReturned() throws BobbyException {
        Parser parser = new Parser();

        assertInstanceOf(AddCommand.class,
                parser.parse("event project meeting /from 2026-08-25 /to 2026-08-26"));
        assertInstanceOf(AddCommand.class,
                parser.parse("event same-day meeting /from 2026-08-25 /to 2026-08-25"));
    }

    /** Verifies that an event without a description is rejected. */
    @Test
    public void parse_eventWithoutDescription_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("event /from 2026-08-25 /to 2026-08-26"));

        assertEquals("Error! The description of an event cannot be empty!",
                exception.getMessage());
    }

    /** Verifies that an event without a start date is rejected. */
    @Test
    public void parse_eventWithoutStartDate_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("event project meeting /to 2026-08-26"));

        assertEquals("Error! Start date of an event cannot be empty. Try again!",
                exception.getMessage());
    }

    /** Verifies that an event without an end date is rejected. */
    @Test
    public void parse_eventWithoutEndDate_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("event project meeting /from 2026-08-25 /to"));

        assertEquals("Error! End date of an event cannot be empty. Try again!",
                exception.getMessage());
    }

    /** Verifies that the storage separator is rejected in event details. */
    @Test
    public void parse_eventWithStorageSeparator_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("event project | meeting /from 2026-08-25 /to 2026-08-26"));

        assertEquals("Error! Task details cannot contain the '|' character!",
                exception.getMessage());
    }

    /** Verifies that an event with an invalid date is rejected. */
    @Test
    public void parse_eventWithInvalidDate_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parse("event project meeting /from not-a-date /to 2026-08-26"));

        assertEquals("Error! Event dates must be valid dates. Use yyyy-MM-dd.",
                exception.getMessage());
    }

    /** Verifies that an event cannot end before its start date. */
    @Test
    public void parse_eventWithInvalidDateOrder_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException reversedDateException = assertThrows(BobbyException.class, () ->
                parser.parse("event meeting /from 2026-08-26 /to 2026-08-25"));

        assertEquals("Error! An event cannot end before it starts.",
                reversedDateException.getMessage());
    }

    /** Verifies that repeated or reversed event parameters are rejected explicitly. */
    @Test
    public void parse_eventWithMalformedParameters_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException repeatedFromException = assertThrows(BobbyException.class, () ->
                parser.parse("event meeting /from 2026-08-25 /from 2026-08-26 /to 2026-08-27"));
        BobbyException repeatedToException = assertThrows(BobbyException.class, () ->
                parser.parse("event meeting /from 2026-08-25 /to 2026-08-26 /to 2026-08-27"));
        BobbyException reversedMarkersException = assertThrows(BobbyException.class, () ->
                parser.parse("event meeting /to 2026-08-26 /from 2026-08-25"));

        assertEquals("Error! The /from parameter can only be specified once!",
                repeatedFromException.getMessage());
        assertEquals("Error! The /to parameter can only be specified once!",
                repeatedToException.getMessage());
        assertEquals("Error! Event parameters must place /from before /to.",
                reversedMarkersException.getMessage());
    }

    /** Verifies that null and whitespace-only input produce the empty-command error. */
    @Test
    public void parse_emptyInput_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException nullException = assertThrows(BobbyException.class, () ->
                parser.parse(null));
        BobbyException whitespaceException = assertThrows(BobbyException.class, () ->
                parser.parse("   "));

        assertEquals("Error! The command cannot be empty!", nullException.getMessage());
        assertEquals("Error! The command cannot be empty!", whitespaceException.getMessage());
    }

    /**
     * Verifies that valid one-based task numbers are converted to zero-based indices.
     */
    @Test
    public void parseTaskIndex_validTaskNumber_zeroBasedIndexReturned() throws BobbyException {
        Parser parser = new Parser();

        assertEquals(0, parser.parseTaskIndex("delete 1", "delete", 3));
        assertEquals(1, parser.parseTaskIndex("delete  2", "delete", 3));
        assertEquals(2, parser.parseTaskIndex("delete 3", "delete", 3));
    }

    /** Verifies that an empty task list is rejected before a task number is parsed. */
    @Test
    public void parseTaskIndex_emptyTaskList_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parseTaskIndex("delete 1", "delete", 0));

        assertEquals("No tasks available to delete.", exception.getMessage());
    }

    /** Verifies that a missing task number produces a helpful validation error. */
    @Test
    public void parseTaskIndex_missingTaskNumber_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parseTaskIndex("delete", "delete", 1));

        assertEquals("Error! The task number cannot be empty!", exception.getMessage());
    }

    /** Verifies that a non-numeric task number is rejected. */
    @Test
    public void parseTaskIndex_nonNumericTaskNumber_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parseTaskIndex("delete abc", "delete", 1));

        assertEquals("Error! The task number must be a valid integer.", exception.getMessage());
    }

    /** Verifies that zero and negative task numbers are rejected. */
    @Test
    public void parseTaskIndex_nonPositiveTaskNumber_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException zeroException = assertThrows(BobbyException.class, () ->
                parser.parseTaskIndex("delete 0", "delete", 3));
        BobbyException negativeException = assertThrows(BobbyException.class, () ->
                parser.parseTaskIndex("delete -1", "delete", 3));

        assertEquals("Error! The task number must be between 1 and 3.",
                zeroException.getMessage());
        assertEquals("Error! The task number must be between 1 and 3.",
                negativeException.getMessage());
    }

    /** Verifies that task numbers greater than the list size are rejected. */
    @Test
    public void parseTaskIndex_taskNumberBeyondList_exceptionThrown() {
        Parser parser = new Parser();

        BobbyException exception = assertThrows(BobbyException.class, () ->
                parser.parseTaskIndex("delete 4", "delete", 3));

        assertEquals("Error! The task number must be between 1 and 3.", exception.getMessage());
    }

    /** Verifies a negative task count violates the parser contract. */
    @Test
    public void parseTaskIndex_negativeTaskCount_assertionErrorThrown() {
        Parser parser = new Parser();

        assertThrows(AssertionError.class, () -> parser.parseTaskIndex("mark 1", "mark", -1));
    }
}
