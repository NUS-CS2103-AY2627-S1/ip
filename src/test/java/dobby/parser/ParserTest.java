package dobby.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dobby.command.Command;
import dobby.command.DeadlineCommand;
import dobby.command.DeleteCommand;
import dobby.command.EventCommand;
import dobby.command.ExitCommand;
import dobby.command.FindCommand;
import dobby.command.HelpCommand;
import dobby.command.InvalidCommand;
import dobby.command.ListCommand;
import dobby.command.MarkCommand;
import dobby.command.TodoCommand;
import dobby.command.UnmarkCommand;

/** Tests conversion of console input into Dobby commands. */
class ParserTest {
    @Test
    void parse_nullInput_assertionError() {
        assertThrows(AssertionError.class, () -> Parser.parse(null));
    }

    @Test
    void parse_listCommand_returnsListCommand() {
        assertInstanceOf(ListCommand.class, Parser.parse("  LIST  "));
    }

    @Test
    void parse_helpCommandWithDifferentCaseAndWhitespace_returnsHelpCommand() {
        assertInstanceOf(HelpCommand.class, Parser.parse("  HELP  "));
    }

    @Test
    void parse_helpCommandWithAdditionalArgument_returnsHelpfulError() {
        assertInvalidMessage("help todo", "> Dobby is confused. Dobby think you meant 'help'");
    }

    @Test
    void parse_byeCommand_returnsExitCommand() {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    void parse_todoWithDescription_returnsTodoCommand() {
        assertInstanceOf(TodoCommand.class, Parser.parse("todo read book"));
    }

    @Test
    void parse_todoWithoutDescription_returnsHelpfulError() {
        assertInvalidMessage("todo", "> Dobby is confused. Dobby think you meant 'todo <description>'");
    }

    @Test
    void parse_findWithKeyword_returnsFindCommand() {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    void parse_findWithoutKeyword_returnsHelpfulError() {
        assertInvalidMessage("find", "> Dobby is confused. Dobby think you meant 'find <keyword>'");
    }

    @Test
    void parse_deadlineWithDateAndTime_returnsDeadlineCommand() {
        assertInstanceOf(DeadlineCommand.class, Parser.parse("deadline return book /by 2019-12-02 1800"));
    }

    @Test
    void parse_deadlineWithMissingParts_returnsHelpfulError() {
        assertInvalidMessage("deadline return book", "> Dobby is confused. Dobby think you meant "
                + "'deadline <description> /by <date/time>'");
    }

    @Test
    void parse_deadlineWithInvalidDate_returnsHelpfulError() {
        assertInvalidMessage("deadline return book /by 2019-02-29", "> Dobby needs a valid date: yyyy-MM-dd, "
                + "optionally followed by HHmm.");
    }

    @Test
    void parse_eventWithDatesAndTimes_returnsEventCommand() {
        assertInstanceOf(EventCommand.class,
                Parser.parse("event project meeting /from 2019-12-03 0900 /to 2019-12-03 1100"));
    }

    @Test
    void parse_eventEndingBeforeStart_returnsHelpfulError() {
        assertInvalidMessage("event reversed /from 2026-09-22 1600 /to 2026-09-22 1400",
                "> Dobby needs the event end to be at or after its start.");
    }

    @Test
    void parse_eventWithMissingParts_returnsHelpfulError() {
        assertInvalidMessage("event project meeting /from 2019-12-03", "> Dobby is confused. Dobby think you meant "
                + "'event <description> /from <date/time> /to <date/time>'");
    }

    @Test
    void parse_eventWithInvalidDate_returnsHelpfulError() {
        assertInvalidMessage("event project meeting /from 2019-02-29 /to 2019-03-01",
                "> Dobby needs valid dates: yyyy-MM-dd, optionally followed by HHmm.");
    }

    @Test
    void parse_numberedCommands_returnsMatchingCommandTypes() {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    void parse_numberedCommandWithNonNumber_returnsHelpfulError() {
        assertInvalidMessage("mark one", "> Dobby is confused. Dobby expected a task number");
    }

    @Test
    void parse_numberedCommandWithMissingNumber_returnsHelpfulError() {
        assertInvalidMessage("delete", "> Dobby is confused. Dobby think you meant 'delete <Task number>'");
    }

    @Test
    void parse_blankOrUnknownInput_returnsHelpfulError() {
        assertInvalidMessage("   ", "> Dobby couldn't hear you. Dobby want you to speak louder!");
        assertInvalidMessage("remind me", "> Dobby asks is this a Todo, Deadline, or Event?\n"
                + "> Dobby recommend whisper 'help' so that Dobby can help you!");
    }

    /** Verifies the error reported by an invalid parsed command. */
    private void assertInvalidMessage(String input, String expectedMessage) {
        Command command = Parser.parse(input);

        assertInstanceOf(InvalidCommand.class, command);
        assertFalse(command.isExit());
        assertEquals(expectedMessage, command.execute(null));
    }
}
