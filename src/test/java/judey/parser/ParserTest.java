package judey.parser;

import judey.command.*;
import judey.exception.JudeyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {

    @Test
    public void parseCommand_validExitCommand_returnsExitCommand() throws Exception {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parseCommand_validListCommandWithExtraSpaces_returnsListCommand() throws Exception {
        Command command = Parser.parse("   list   ");
        assertInstanceOf(ListCommand.class, command);
    }

    @Test
    public void parseCommand_listWithArguments_exceptionThrown() {
        JudeyException exception = assertThrows(JudeyException.class,
                () -> Parser.parse("list extra"));
        assertEquals("The list command does not take any arguments. Try: list", exception.getMessage());
    }

    @Test
    public void parseCommand_help_returnsHelpCommand() throws Exception {
        Command command = Parser.parse("   help   ");
        assertInstanceOf(HelpCommand.class, command);
    }

    @Test
    public void parseCommand_helpWithArguments_exceptionThrown() {
        JudeyException exception = assertThrows(
                JudeyException.class,
                () -> Parser.parse("help extra")
        );
        assertEquals("The help command does not take any arguments. Try: help", exception.getMessage());
    }

    @Test
    public void parseCommand_findWithKeyword_returnsFindCommand() throws Exception {
        Command command = Parser.parse("find read book");
        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parseCommand_findWithoutKeyword_exceptionThrown() {
        JudeyException exception = assertThrows(
                JudeyException.class,
                () -> Parser.parse("find")
        );
        assertTrue(exception.getMessage().contains("The find command is missing a search keyword."));
        assertTrue(exception.getMessage().contains(Parser.HIGHLIGHT_START + "<keyword>"
                + Parser.HIGHLIGHT_END));
    }

    @Test
    public void parseCommand_markMissingTaskNumber_highlightsTaskNumber() {
        JudeyException exception = assertThrows(JudeyException.class, () -> Parser.parse("mark"));

        assertEquals("The mark command is missing a task number.\n\nTry: mark "
                + Parser.HIGHLIGHT_START + "<task number>" + Parser.HIGHLIGHT_END, exception.getMessage());
    }

    @Test
    public void parseCommand_unmarkInvalidTaskNumber_highlightsTaskNumber() {
        JudeyException exception = assertThrows(JudeyException.class, () -> Parser.parse("unmark nope"));

        assertEquals("The unmark command requires a valid task number.\n\nTry: unmark "
                + Parser.HIGHLIGHT_START + "<task number>" + Parser.HIGHLIGHT_END, exception.getMessage());
    }

    @Test
    public void parseCommand_deleteExtraArguments_highlightsTaskNumber() {
        JudeyException exception = assertThrows(JudeyException.class, () -> Parser.parse("delete 1 extra"));

        assertEquals("The delete command requires a valid task number.\n\nTry: delete "
                + Parser.HIGHLIGHT_START + "<task number>" + Parser.HIGHLIGHT_END, exception.getMessage());
    }

    @Test
    public void parseCommand_todoWithDescription_returnsAddCommand() throws Exception {
        Command command = Parser.parse("todo read book");
        assertInstanceOf(AddTodoCommand.class, command);
    }

    @Test
    public void parseCommand_todoMissingDescription_exceptionThrown() {
        JudeyException exception = assertThrows(
                JudeyException.class,
                () -> Parser.parse("todo")
        );
        assertTrue(exception.getMessage().contains("The todo is missing a description."));
        assertTrue(exception.getMessage().contains(Parser.HIGHLIGHT_START + "<description>"
                + Parser.HIGHLIGHT_END));
    }

    @Test
    public void parseCommand_unknownCommand_exceptionThrown() {
        assertThrows(
                JudeyException.class,
                () -> Parser.parse("invalidCommand 123")
        );
    }

    // Deadline tasks
    @Test
    public void parseCommand_validDeadline_returnsAddDeadlineCommand() throws Exception {
        Command command = Parser.parse("deadline submit assignment /by 2026-12-31 2359");
        assertInstanceOf(AddDeadlineCommand.class, command);
    }

    @Test
    public void parseCommand_deadlineMissingDescription_exceptionThrown() {
        JudeyException exception = assertThrows(
                JudeyException.class,
                () -> Parser.parse("deadline /by 2026-12-31 2359")
        );
        assertTrue(exception.getMessage().contains("The deadline is missing a required value."));
    }

    @Test
    public void parseCommand_deadlineMissingByFlag_exceptionThrown() {
        JudeyException exception = assertThrows(
                JudeyException.class,
                () -> Parser.parse("deadline submit assignment")
        );
        assertTrue(exception.getMessage().contains("The deadline is missing a required value."));
    }

    @Test
    public void parseCommand_deadlineEmptyByValue_exceptionThrown() {
        JudeyException exception = assertThrows(
                JudeyException.class,
                () -> Parser.parse("deadline submit assignment /by ")
        );
        assertTrue(exception.getMessage().contains("The deadline is missing a required value."));
    }

    @Test
    public void parseCommand_deadlineInvalidDateFormat_exceptionThrown() {
        assertThrows(
                JudeyException.class,
                () -> Parser.parse("That deadline needs a date! Try: deadline report /by 2/12/2019 1800")
        );
    }

    //Event tests
    @Test
    public void parseCommand_validEvent_returnsAddEventCommand() throws Exception {
        Command command = Parser.parse("event team meeting /from 2026-10-15 1400 /to 2026-10-15 1600");
        assertInstanceOf(AddEventCommand.class, command);
    }

    @Test
    public void parseCommand_eventMissingDescription_exceptionThrown() {
        JudeyException exception = assertThrows(
                JudeyException.class,
                () -> Parser.parse("event /from 2026-10-15 1400 /to 2026-10-15 1600")
        );
        assertTrue(exception.getMessage().contains("The event is missing a description or start time."));
    }

    @Test
    public void parseCommand_eventMissingToFlag_exceptionThrown() {
        JudeyException exception = assertThrows(
                JudeyException.class,
                () -> Parser.parse("event team meeting /from 2026-10-15 1400")
        );
        assertTrue(exception.getMessage().contains("The event is missing a required value."));
    }

    @Test
    public void parseCommand_eventSwappedFlags_exceptionThrown() {
        // Checking behavior when /to appears before /from
        assertThrows(
                JudeyException.class,
                () -> Parser.parse("event team meeting /to 2026-10-15 1600 /from 2026-10-15 1400")
        );
    }

    @Test
    public void parseCommand_eventEmptyFromValue_exceptionThrown() {
        assertThrows(
                JudeyException.class,
                () -> Parser.parse("event team meeting /from  /to 2026-10-15 1600")
        );
    }

    @Test
    public void parseCommand_eventMissingAllArguments_highlightsDescriptionFirst() {
        JudeyException exception = assertThrows(JudeyException.class, () -> Parser.parse("event"));

        assertTrue(exception.getMessage().contains("<start date and start time: d/M/yyyy HHmm>"));
        assertTrue(exception.getMessage().contains("<end date and end time: d/M/yyyy HHmm>"));
        assertTrue(exception.getMessage().contains(Parser.HIGHLIGHT_START + "<description>"
                + Parser.HIGHLIGHT_END));
    }

    @Test
    public void parseCommand_eventMissingEnd_highlightsEndDateAndTime() {
        JudeyException exception = assertThrows(
                JudeyException.class,
                () -> Parser.parse("event team meeting /from 15/10/2026 1400")
        );

        assertTrue(exception.getMessage().contains(Parser.HIGHLIGHT_START
                + "<end date and end time: d/M/yyyy HHmm>" + Parser.HIGHLIGHT_END));
    }

    @Test
    public void parseCommand_eventMissingFrom_highlightsStartDateAndTime() {
        JudeyException exception = assertThrows(JudeyException.class, () -> Parser.parse("event read"));

        assertTrue(exception.getMessage().contains(Parser.HIGHLIGHT_START
                + "<start date and start time: d/M/yyyy HHmm>" + Parser.HIGHLIGHT_END));
    }

    @Test
    public void parseCommand_eventsOnMissingDate_highlightsDate() {
        JudeyException exception = assertThrows(JudeyException.class, () -> Parser.parse("events-on"));

        assertEquals("The events-on command is missing a date.\n\nTry: events-on "
                + Parser.HIGHLIGHT_START + "<date: d/M/yyyy>" + Parser.HIGHLIGHT_END, exception.getMessage());
    }

    @Test
    public void parseCommand_eventsOnInvalidDate_highlightsDate() {
        JudeyException exception = assertThrows(
                JudeyException.class,
                () -> Parser.parse("events-on tomorrow")
        );

        assertEquals("The events-on command requires a valid date.\n\nTry: events-on "
                + Parser.HIGHLIGHT_START + "<date: d/M/yyyy>" + Parser.HIGHLIGHT_END, exception.getMessage());
    }

    @Test
    public void parseCommand_markNonPositiveTaskNumber_highlightsTaskNumber() {
        JudeyException exception = assertThrows(JudeyException.class, () -> Parser.parse("mark 0"));

        assertTrue(exception.getMessage().contains(Parser.HIGHLIGHT_START + "<task number>"
                + Parser.HIGHLIGHT_END));
    }
}
