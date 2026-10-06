package caesar.parser;

import caesar.Caesar;
import caesar.command.AddCommand;
import caesar.command.ClearMissedCommand;
import caesar.command.DeleteCommand;
import caesar.command.ExitCommand;
import caesar.command.FindCommand;
import caesar.command.HelpCommand;
import caesar.command.ListCommand;
import caesar.command.MarkCommand;
import caesar.command.ReminderCommand;
import caesar.command.RescheduleCommand;
import caesar.command.UnmarkCommand;
import caesar.exception.CaesarException;
import caesar.task.Deadline;
import caesar.task.Event;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {

    @Test
    public void testCommandInstructionsContainSupportedCommands() {
        assertEquals("Type a command or click HELP to see available formats.",
                Parser.getCommandInstructions());
        assertTrue(Parser.getHelpCommands().contains("find <keyword>"));
        assertTrue(Parser.getHelpCommands().contains("help"));
        assertTrue(Parser.getHelpCommands().contains("reminder"));
    }

    @Test
    public void testParseCommandAndDetails() {
        Parser parser = new Parser();

        Parser.ParsedCommand parsed = parser.parse("  todo   Read a book  ");

        assertEquals(Caesar.CommandType.TODO, parsed.getType());
        assertEquals("Read a book", parsed.getDetails());
    }

    @Test
    public void testParseNullCommandAsUnknown() {
        Parser.ParsedCommand parsed = new Parser().parse(null);

        assertEquals(Caesar.CommandType.UNKNOWN, parsed.getType());
        assertNull(parsed.getDetails());
    }

    @Test
    public void testParseCommandCreatesTodoCommand() throws CaesarException {
        assertInstanceOf(AddCommand.class, new Parser().parseCommand("todo Read a book"));
    }

    @Test
    public void testParseCommandCreatesDeadlineCommand() throws CaesarException {
        assertInstanceOf(AddCommand.class,
                new Parser().parseCommand("deadline Submit report /by 2024-06-01"));
    }

    @Test
    public void testParseCommandCreatesEventCommand() throws CaesarException {
        assertInstanceOf(AddCommand.class,
                new Parser().parseCommand("event Project meeting /from 2024-06-01 /to 2024-06-02"));
    }

    @Test
    public void testParseCommandCreatesListCommand() throws CaesarException {
        assertInstanceOf(ListCommand.class, new Parser().parseCommand("list"));
        assertInstanceOf(ListCommand.class, new Parser().parseCommand("list sorted"));
    }

    @Test
    public void testParseCommandCreatesFindCommand() throws CaesarException {
        assertInstanceOf(FindCommand.class,
                new Parser().parseCommand("find report"));
    }

    @Test
    public void testParseCommandCreatesHelpCommand() throws CaesarException {
        assertInstanceOf(HelpCommand.class, new Parser().parseCommand("HELP"));
    }

    @Test
    public void testParseCommandCreatesReminderCommand() throws CaesarException {
        assertInstanceOf(ReminderCommand.class, new Parser().parseCommand("reminder"));
    }

    @Test
    public void testParseCommandCreatesClearMissedCommand() throws CaesarException {
        assertInstanceOf(ClearMissedCommand.class, new Parser().parseCommand("clear missed"));
        assertInstanceOf(ClearMissedCommand.class, new Parser().parseCommand("clear"));
    }

    @Test
    public void testParseCommandCreatesStatusAndDeleteCommands() throws CaesarException {
        assertInstanceOf(MarkCommand.class, new Parser().parseCommand("mark 1"));
        assertInstanceOf(UnmarkCommand.class, new Parser().parseCommand("unmark 1"));
        assertInstanceOf(DeleteCommand.class, new Parser().parseCommand("delete 1"));
    }

    @Test
    public void testParseCommandCreatesRescheduleCommand() throws CaesarException {
        assertInstanceOf(RescheduleCommand.class,
                new Parser().parseCommand("reschedule 1 05/06/2024"));
        assertInstanceOf(RescheduleCommand.class,
                new Parser().parseCommand("reschedule 1 05/06/2024 06/06/2024"));
    }

    @Test
    public void testParseCommandRejectsRescheduleWithoutDate() {
        assertThrows(
                CaesarException.class,
                () -> new Parser().parseCommand("reschedule 1")
        );
    }

    @Test
    public void testParseCommandCreatesExitCommand() throws CaesarException {
        assertTrue(new Parser().parseCommand("bye").isExit());
        assertInstanceOf(ExitCommand.class, new Parser().parseCommand("bye"));
    }

    @Test
    public void testParseCommandRejectsUnknownCommand() {
        CaesarException exception = assertThrows(
                CaesarException.class,
                () -> new Parser().parseCommand("schedule meeting")
        );

        assertEquals(
                "I'm not quite sure I caught that command. :(click HELP to find useful commands)",
                exception.getMessage()
        );
    }

    @Test
    public void testParseCommandRejectsDetailsAfterBye() {
        assertThrows(
                CaesarException.class,
                () -> new Parser().parseCommand("bye now")
        );
    }

    @Test
    public void testCreateDeadlineWithIsoDate() throws CaesarException {
        Deadline deadline = (Deadline) new Parser().createDeadline(
                "Submit report /by 2024-06-01"
        );

        assertEquals("Submit report", deadline.getDescription());
        assertEquals("Jun 1 2024", deadline.getBy());
    }

    @Test
    public void testCreateDeadlineWithDisplayDate() throws CaesarException {
        Deadline deadline = (Deadline) new Parser().createDeadline(
                "Submit report /by Jun 1 2024"
        );

        assertEquals("Jun 1 2024", deadline.getBy());
    }

    @Test
    public void testCreateDeadlineWithMissingDetails() {
        CaesarException exception = assertThrows(
                CaesarException.class,
                () -> new Parser().createDeadline("Submit report")
        );

        assertTrue(exception.getMessage().contains("deadline <description> /by <date or time>"));
    }

    @Test
    public void testCreateDeadlineWithInvalidDate() {
        CaesarException exception = assertThrows(
                CaesarException.class,
                () -> new Parser().createDeadline("Submit report /by 2024-99-99")
        );

        assertEquals(
                "Invalid date format. Please use YYYY-MM-DD, DD/MM/YYYY, or MMM d yyyy.",
                exception.getMessage()
        );
    }

    @Test
    public void testCreateEventWithDates() throws CaesarException {
        Event event = (Event) new Parser().createEvent(
                "Project meeting /from 2024-06-01 /to 2024-06-02"
        );

        assertEquals("Project meeting", event.getDescription());
        assertEquals("Jun 1 2024", event.getStart());
        assertEquals("Jun 2 2024", event.getEnd());
    }

    @Test
    public void testCreateEventWithMissingFromOrToDetails() {
        Parser parser = new Parser();

        assertThrows(
                CaesarException.class,
                () -> parser.createEvent("Project meeting")
        );
        assertThrows(
                CaesarException.class,
                () -> parser.createEvent("Project meeting /from 2024-06-01")
        );
    }

    @Test
    public void testCreateEventWithInvalidDate() {
        CaesarException exception = assertThrows(
                CaesarException.class,
                () -> new Parser().createEvent(
                        "Project meeting /from 2024-99-99 /to 2024-06-02"
                )
        );

        assertEquals(
                "Invalid date format. Please use YYYY-MM-DD, DD/MM/YYYY, or MMM d yyyy.",
                exception.getMessage()
        );
    }

    @Test
    public void testParseTaskNumberWithSpaces() throws CaesarException {
        assertEquals(12, new Parser().parseTaskNumber(" 12 ", "mark <task number>"));
    }

    @Test
    public void testParseTaskNumberWithMissingDetails() {
        assertThrows(
                CaesarException.class,
                () -> new Parser().parseTaskNumber("  ", "mark <task number>")
        );
    }

    @Test
    public void testParseTaskNumberWithInvalidNumber() {
        CaesarException exception = assertThrows(
                CaesarException.class,
                () -> new Parser().parseTaskNumber("one", "mark <task number>")
        );

        assertEquals("Please provide a valid task number.", exception.getMessage());
    }

    @Test
    public void testRequireDetailsTrimsInput() throws CaesarException {
        assertEquals("Read a book",
                new Parser().requireDetails("  Read a book  ", "todo <description>"));
    }

    @Test
    public void testRequireDetailsRejectsMissingDetails() {
        Parser parser = new Parser();

        assertThrows(
                CaesarException.class,
                () -> parser.requireDetails(null, "todo <description>")
        );
        assertThrows(
                CaesarException.class,
                () -> parser.requireDetails("   ", "todo <description>")
        );
    }

    @Test
    public void testUnknownCommandCreatesExpectedException() {
        CaesarException exception = new Parser().unknownCommand();

        assertFalse(exception.getMessage().isBlank());
        assertTrue(exception.getMessage().contains("HELP"));
    }
}
