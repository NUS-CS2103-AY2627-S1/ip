package bobby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import bobby.exception.BobbyException;
import bobby.task.Deadline;
import bobby.task.Event;
import bobby.task.Todo;

/**
 * Tests command parsing and input validation in {@link Parser}.
 */
class ParserTest {
    /**
     * Verifies that the exit command ignores surrounding whitespace and letter case.
     */
    @Test
    void isByeCommand_validExitCommand_trueReturned() {
        assertEquals(true, Parser.isByeCommand("  ByE  "));
    }

    /**
     * Verifies that a command that merely contains the exit word is not an exit command.
     */
    @Test
    void isByeCommand_nonExitCommand_falseReturned() {
        assertEquals(false, Parser.isByeCommand("bye now"));
        assertEquals(false, Parser.isByeCommand(null));
    }

    /**
     * Verifies that a list command does not carry task data.
     */
    @Test
    void parse_listCommand_listCommandReturned() throws BobbyException {
        Parser.Command command = Parser.parse(" LIST ", 3);

        assertEquals(Parser.CommandType.LIST, command.getType());
        assertNull(command.getTask());
        assertEquals(-1, command.getTaskIndex());
        assertNull(command.getKeyword());
    }

    /**
     * Verifies that a statistics command does not carry task data.
     */
    @Test
    void parse_statsCommand_statsCommandReturned() throws BobbyException {
        Parser.Command command = Parser.parse(" STATS ", 3);

        assertEquals(Parser.CommandType.STATS, command.getType());
        assertNull(command.getTask());
        assertEquals(-1, command.getTaskIndex());
    }

    /**
     * Verifies that a statistics command rejects arguments with a specific usage message.
     */
    @Test
    void parse_statsCommandWithArguments_exceptionThrown() {
        BobbyException exception = assertThrows(BobbyException.class, () ->
                Parser.parse("stats week", 3));

        assertEquals("Thy decree must take precisely this form: stats.", exception.getMessage());
    }

    /**
     * Verifies that a find command returns its trimmed keyword without task data.
     */
    @Test
    void parse_findCommand_findCommandWithKeywordReturned() throws BobbyException {
        Parser.Command command = Parser.parse(" FIND   project\t  meeting  ", 3);

        assertEquals(Parser.CommandType.FIND, command.getType());
        assertEquals("project meeting", command.getKeyword());
        assertNull(command.getTask());
        assertEquals(-1, command.getTaskIndex());
    }

    /**
     * Verifies that a to-do command produces a to-do task with a trimmed description.
     */
    @Test
    void parse_todoCommand_todoTaskReturned() throws BobbyException {
        Parser.Command command = Parser.parse("todo   read book  ", 0);

        assertEquals(Parser.CommandType.ADD, command.getType());
        Todo task = assertInstanceOf(Todo.class, command.getTask());
        assertEquals("read book", task.getDescription());
        assertEquals(-1, command.getTaskIndex());
    }

    /**
     * Verifies that a deadline command parses its description and date-time.
     */
    @Test
    void parse_deadlineCommand_deadlineTaskReturned() throws BobbyException {
        Parser.Command command = Parser.parse("DEADLINE return book /by 2026-09-01 1400", 0);

        assertEquals(Parser.CommandType.ADD, command.getType());
        Deadline task = assertInstanceOf(Deadline.class, command.getTask());
        assertEquals("D | 0 | return book | 2026-09-01T14:00 | -", task.toFileString());
    }

    /**
     * Verifies that an event command parses its description, start, and end date-times.
     */
    @Test
    void parse_eventCommand_eventTaskReturned() throws BobbyException {
        Parser.Command command = Parser.parse(
                "  EVENT\tproject   meeting  /FROM  2026-09-01  1400  /TO 2026-09-01 1600  ", 0);

        assertEquals(Parser.CommandType.ADD, command.getType());
        Event task = assertInstanceOf(Event.class, command.getTask());
        assertEquals("E | 0 | project meeting | 2026-09-01T14:00 | 2026-09-01T16:00 | -",
                task.toFileString());
    }

    /**
     * Verifies that one-based task numbers are converted to zero-based indices.
     */
    @Test
    void parseTaskCommands_validTaskNumbers_zeroBasedIndicesReturned() throws BobbyException {
        Parser.Command markCommand = Parser.parse("mark 1", 3);
        Parser.Command unmarkCommand = Parser.parse("unmark 2", 3);
        Parser.Command deleteCommand = Parser.parse("delete 3", 3);

        assertEquals(Parser.CommandType.MARK, markCommand.getType());
        assertEquals(0, markCommand.getTaskIndex());
        assertEquals(Parser.CommandType.UNMARK, unmarkCommand.getType());
        assertEquals(1, unmarkCommand.getTaskIndex());
        assertEquals(Parser.CommandType.DELETE, deleteCommand.getType());
        assertEquals(2, deleteCommand.getTaskIndex());
    }

    /**
     * Verifies that task commands reject missing, non-numeric, and out-of-range task numbers.
     */
    @Test
    void parseTaskCommands_invalidTaskNumber_exceptionThrown() {
        assertInvalidTaskNumber("mark", 1);
        assertInvalidTaskNumber("unmark zero", 1);
        assertInvalidTaskNumber("delete 0", 1);
        assertInvalidTaskNumber("mark 2", 1);
        assertInvalidTaskNumber("mark -1", 1);
        assertInvalidTaskNumber("mark +1", 1);
        assertInvalidTaskNumber("mark 01", 1);
        assertInvalidTaskNumber("mark 1 2", 2);
        assertInvalidTaskNumber("mark 999999999999999999999999", 1);
    }

    /**
     * Verifies that a to-do command requires a description.
     */
    @Test
    void parse_todoWithoutDescription_exceptionThrown() {
        BobbyException exception = assertThrows(BobbyException.class, () -> Parser.parse("todo", 0));

        assertEquals("Thy decree containeth no duty to inscribe. Pray use: todo DESCRIPTION.",
                exception.getMessage());
    }

    /**
     * Verifies that a find command requires a keyword.
     */
    @Test
    void parse_findWithoutKeyword_exceptionThrown() {
        BobbyException exception = assertThrows(BobbyException.class, () -> Parser.parse("find", 0));

        assertEquals("Pray furnish a word or phrase for which the register may be searched.",
                exception.getMessage());
    }

    /**
     * Verifies that date-based commands reject invalid dates and missing components.
     */
    @Test
    void parse_dateBasedCommandInvalidDetails_exceptionThrown() {
        BobbyException invalidDateException = assertThrows(BobbyException.class, () ->
                Parser.parse("deadline submit report /by 2026-02-29 1200", 0));
        BobbyException missingEventDetailsException = assertThrows(BobbyException.class, () ->
                Parser.parse("event meeting /from 2026-09-01 1400", 0));

        assertEquals("The appointed date and hour are not in an acceptable form. "
                        + "Pray employ YYYY-MM-DD HHMM.",
                invalidDateException.getMessage());
        assertEquals("Thy decree must take precisely this form: "
                        + "event DESCRIPTION /from YYYY-MM-DD HHMM /to YYYY-MM-DD HHMM.",
                missingEventDetailsException.getMessage());
    }

    /**
     * Verifies that deadline commands reject missing fields and misplaced separators.
     */
    @Test
    void parse_deadlineCommandMalformedStructure_exceptionThrown() {
        assertInvalidDeadlineCommand("deadline");
        assertInvalidDeadlineCommand("deadline report /by");
        assertInvalidDeadlineCommand("deadline /by 2026-09-01 1200");
        assertInvalidDeadlineCommand("deadline report /by 2026-09-01 1200 /by 2026-09-02 1200");
    }

    /**
     * Verifies that event commands reject missing fields and misplaced or repeated separators.
     */
    @Test
    void parse_eventCommandMalformedStructure_exceptionThrown() {
        assertInvalidEventCommand("event");
        assertInvalidEventCommand("event /from 2026-09-01 1200 /to 2026-09-01 1300");
        assertInvalidEventCommand("event meeting /from /to 2026-09-01 1300");
        assertInvalidEventCommand("event meeting /from 2026-09-01 1200 /to");
        assertInvalidEventCommand("event meeting /to 2026-09-01 1300 /from 2026-09-01 1200");
        assertInvalidEventCommand("event meeting /from 2026-09-01 1200 "
                + "/from 2026-09-01 1230 /to 2026-09-01 1300");
    }

    /**
     * Verifies that both event date fields use strict calendar and time validation.
     */
    @Test
    void parse_eventCommandInvalidDateTimes_exceptionThrown() {
        assertInvalidDateTime("event meeting /from 2026-02-30 1200 /to 2026-09-01 1300");
        assertInvalidDateTime("event meeting /from 2026-09-01 1200 /to 2026-09-01 2400");
        assertInvalidDateTime("deadline report /by 2026-09-01 12:00");
    }

    /**
     * Verifies that duplicate date parameters are rejected as malformed commands.
     */
    @Test
    void parse_dateBasedCommandDuplicateParameters_exceptionThrown() {
        BobbyException deadlineException = assertThrows(BobbyException.class, () ->
                Parser.parse("deadline report /by 2026-09-01 1200 /by 2026-09-02 1200", 0));
        BobbyException eventException = assertThrows(BobbyException.class, () ->
                Parser.parse("event meeting /from 2026-09-01 1200 /to 2026-09-01 1300 "
                        + "/to 2026-09-01 1400", 0));

        assertEquals("Thy decree must take precisely this form: "
                        + "deadline DESCRIPTION /by YYYY-MM-DD HHMM.",
                deadlineException.getMessage());
        assertEquals("Thy decree must take precisely this form: "
                        + "event DESCRIPTION /from YYYY-MM-DD HHMM /to YYYY-MM-DD HHMM.",
                eventException.getMessage());
    }

    /**
     * Verifies that an event must end strictly after it starts.
     */
    @Test
    void parse_eventWithInvalidRange_exceptionThrown() {
        BobbyException sameTimeException = assertThrows(BobbyException.class, () ->
                Parser.parse("event meeting /from 2026-09-01 1200 /to 2026-09-01 1200", 0));
        BobbyException reversedTimeException = assertThrows(BobbyException.class, () ->
                Parser.parse("event meeting /from 2026-09-01 1400 /to 2026-09-01 1200", 0));

        assertEquals("An event must commence before it concludeth.", sameTimeException.getMessage());
        assertEquals("An event must commence before it concludeth.", reversedTimeException.getMessage());
    }

    /**
     * Verifies that descriptions cannot contain the save-file field separator.
     */
    @Test
    void parse_descriptionWithReservedCharacter_exceptionThrown() {
        BobbyException exception = assertThrows(BobbyException.class, () ->
                Parser.parse("todo read | write", 0));

        assertEquals("A duty's description must contain readable text and may not contain the character '|'.",
                exception.getMessage());
    }

    /**
     * Verifies that descriptions reject control characters that whitespace normalization does not remove.
     */
    @Test
    void parse_descriptionWithControlCharacter_exceptionThrown() {
        BobbyException exception = assertThrows(BobbyException.class, () ->
                Parser.parse("todo read\u0000book", 0));

        assertEquals("A duty's description must contain readable text and may not contain the character '|'.",
                exception.getMessage());
    }

    /**
     * Verifies that unknown commands and command-word prefixes are rejected.
     */
    @Test
    void parse_unknownCommand_exceptionThrown() {
        assertUnknownCommand("remind me");
        assertUnknownCommand("statistics");
        assertUnknownCommand("todoing read book");
        assertUnknownCommand("list extra");
    }

    /**
     * Verifies that parsing rejects programmer-supplied state that no task list can have.
     */
    @Test
    void parse_negativeTaskCount_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> Parser.parse("list", -1));
    }

    /**
     * Verifies that parsing rejects a missing command before attempting to inspect it.
     */
    @Test
    void parse_nullInput_exceptionThrown() {
        assertUnknownCommand(null);
    }

    /**
     * Verifies that a command reports the standard invalid-task-number message.
     *
     * @param input the command to parse
     * @param taskCount the number of tasks available to the command
     */
    private void assertInvalidTaskNumber(String input, int taskCount) {
        BobbyException exception = assertThrows(BobbyException.class, () -> Parser.parse(input, taskCount));

        assertEquals("The number thou hast named correspondeth to no duty presently held within the register.",
                exception.getMessage());
    }

    /**
     * Verifies that a command reports the standard unknown-command message.
     *
     * @param input the command to parse
     */
    private void assertUnknownCommand(String input) {
        BobbyException exception = assertThrows(BobbyException.class, () -> Parser.parse(input, 0));

        assertEquals("Prithee, forgive this humble steward, for thy decree exceedeth my understanding. "
                        + "I beseech thee, employ one of the appointed commands.",
                exception.getMessage());
    }

    /**
     * Verifies that a malformed deadline reports the standard deadline usage message.
     *
     * @param input the malformed deadline command
     */
    private void assertInvalidDeadlineCommand(String input) {
        BobbyException exception = assertThrows(BobbyException.class, () -> Parser.parse(input, 0));

        assertEquals("Thy decree must take precisely this form: "
                + "deadline DESCRIPTION /by YYYY-MM-DD HHMM.", exception.getMessage());
    }

    /**
     * Verifies that a malformed event reports the standard event usage message.
     *
     * @param input the malformed event command
     */
    private void assertInvalidEventCommand(String input) {
        BobbyException exception = assertThrows(BobbyException.class, () -> Parser.parse(input, 0));

        assertEquals("Thy decree must take precisely this form: "
                + "event DESCRIPTION /from YYYY-MM-DD HHMM /to YYYY-MM-DD HHMM.",
                exception.getMessage());
    }

    /**
     * Verifies that a malformed date reports the standard date-time message.
     *
     * @param input the command containing a malformed date or time
     */
    private void assertInvalidDateTime(String input) {
        BobbyException exception = assertThrows(BobbyException.class, () -> Parser.parse(input, 0));

        assertEquals("The appointed date and hour are not in an acceptable form. "
                + "Pray employ YYYY-MM-DD HHMM.", exception.getMessage());
    }
}
