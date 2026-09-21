package answerme.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import answerme.command.AddDeadlineCommand;
import answerme.command.AddEventCommand;
import answerme.command.AddToDoCommand;
import answerme.command.DedupCommand;
import answerme.command.DeleteCommand;
import answerme.command.ExitCommand;
import answerme.command.FindCommand;
import answerme.command.ListCommand;
import answerme.command.MarkCommand;
import answerme.command.UnmarkCommand;
import answerme.exception.AnswerMeException;

public class ParserTest {
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Ensure that date/time is formatted correctly.\n"
                    + "Use a date such as 11/9/2026 or a date and time such as 11/9/2026 1800.";

    @Test
    public void parse_eventWithAllParameters_returnsAddEventCommand()
            throws AnswerMeException {
        String input = "event wedding dinner /from 31/8/2026 17:00 /to 1/9/2026 02:00";
        LocalDateTime startTime = LocalDateTime.of(2026, 8, 31, 17, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 9, 1, 2, 0);
        AddEventCommand expectedCommand =
                new AddEventCommand("wedding dinner", startTime, endTime);

        assertEquals(expectedCommand, Parser.parse(input));
    }

    @Test
    public void parse_simpleCommands_returnsMatchingCommandTypes()
            throws AnswerMeException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(DedupCommand.class, Parser.parse("dedup"));
    }

    @Test
    public void parse_indexCommands_returnsMatchingCommandTypes()
            throws AnswerMeException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 3"));
    }

    @Test
    public void parse_todoWithDescription_returnsAddToDoCommand()
            throws AnswerMeException {
        assertEquals(new AddToDoCommand("read textbook"),
                Parser.parse("todo read textbook"));
    }

    @Test
    public void parse_deadlineWithDateTime_returnsAddDeadlineCommand()
            throws AnswerMeException {
        LocalDateTime deadline = LocalDateTime.of(2026, 9, 30, 23, 59);

        assertEquals(new AddDeadlineCommand("submit report", deadline),
                Parser.parse("deadline submit report /by 30/9/2026 23:59"));
    }

    @Test
    public void parse_findWithSearchString_returnsFindCommand()
            throws AnswerMeException {
        assertInstanceOf(FindCommand.class, Parser.parse("find project meeting"));
    }

    @Test
    public void parse_irregularWhitespaceAndMixedCase_returnsExpectedCommand()
            throws AnswerMeException {
        assertEquals(new AddToDoCommand("read textbook"),
                Parser.parse("  ToDo   read textbook  "));
    }

    @Test
    public void parse_mixedCaseFlag_returnsExpectedCommand()
            throws AnswerMeException {
        LocalDateTime deadline = LocalDateTime.of(2026, 9, 30, 0, 0);

        assertEquals(new AddDeadlineCommand("submit report", deadline),
                Parser.parse("deadline submit report /BY 30/9/2026"));
    }

    @Test
    public void parse_nullOrBlankInput_throwsAnswerMeException() {
        String expectedMessage = "Please enter a command.";

        assertParseFails(null, expectedMessage);
        assertParseFails("", expectedMessage);
        assertParseFails("   ", expectedMessage);
    }

    @Test
    public void parse_unknownCommand_throwsAnswerMeException() {
        assertParseFails("remind read textbook", "I'm not sure what you mean :(");
    }

    @Test
    public void parse_indexCommandWithoutIndex_throwsAnswerMeException() {
        assertParseFails("mark", "An index must be supplied for this command.");
    }

    @Test
    public void parse_indexCommandWithNonNumericIndex_throwsAnswerMeException() {
        assertParseFails("unmark first", "The list index must be a valid integer.");
    }

    @Test
    public void parse_indexCommandBelowOne_throwsAnswerMeException() {
        assertParseFails("delete 0", "The list index must be at least 1.");
        assertParseFails("delete -1", "The list index must be at least 1.");
    }

    @Test
    public void parse_todoWithoutDescription_throwsAnswerMeException() {
        assertParseFails("todo", "Format: todo <description>");
    }

    @Test
    public void parse_findWithoutSearchString_throwsAnswerMeException() {
        assertParseFails("find", "Format: find <string>");
    }

    @Test
    public void parse_deadlineWithoutByFlag_throwsAnswerMeException() {
        assertParseFails("deadline submit report",
                "Format: deadline <description> /by <when>");
    }

    @Test
    public void parse_flagWithoutValue_throwsAnswerMeException() {
        assertParseFails("deadline submit report /by",
                "Every flag must be followed by an argument.");
    }

    @Test
    public void parse_repeatedFlag_throwsAnswerMeException() {
        assertParseFails("deadline submit report /by 30/9/2026 /by 1/10/2026",
                "Flag /by cannot be repeated!");
    }

    @Test
    public void parse_eventMissingDescription_throwsAnswerMeException() {
        assertParseFails("event /from 31/8/2026 17:00 /to 1/9/2026 02:00",
                "Format: event <description> /from <when> /to <when>");
    }

    @Test
    public void parse_eventMissingBoundaryFlag_throwsAnswerMeException() {
        String expectedMessage = "Format: event <description> /from <when> /to <when>";

        assertParseFails("event dinner /to 1/9/2026 02:00", expectedMessage);
        assertParseFails("event dinner /from 31/8/2026 17:00", expectedMessage);
    }

    @Test
    public void parse_eventInvalidDateTime_throwsAnswerMeException() {
        assertParseFails("event wedding dinner /from 2/9/2026 1700 /to 3/9/2026 0267",
                INVALID_DATE_TIME_MESSAGE);
    }

    @Test
    public void parse_eventStartAfterEnd_throwsAnswerMeException() {
        assertParseFails("event wedding dinner /from 2/9/2026 17:00 /to 1/9/2026 02:00",
                "'From' datetime cannot occur after 'To'.");
    }

    @Test
    public void parse_eventStartEqualsEnd_returnsAddEventCommand()
            throws AnswerMeException {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 2, 17, 0);
        AddEventCommand expectedCommand =
                new AddEventCommand("wedding dinner", dateTime, dateTime);

        assertEquals(expectedCommand,
                Parser.parse("event wedding dinner /from 2/9/2026 17:00 /to 2/9/2026 17:00"));
    }

    @Test
    public void parse_descriptionContainingPipe_throwsAnswerMeException() {
        String expectedMessage = "Task description cannot contain pipe characters!";

        assertParseFails("todo read | write", expectedMessage);
        assertParseFails("deadline submit | report /by 30/9/2026", expectedMessage);
        assertParseFails("event team | meeting /from 1/9/2026 /to 2/9/2026",
                expectedMessage);
    }

    private void assertParseFails(String input, String expectedMessage) {
        AnswerMeException exception = assertThrows(
                AnswerMeException.class, () -> Parser.parse(input));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
