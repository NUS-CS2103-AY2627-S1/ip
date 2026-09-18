package plana.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import plana.command.AddCommand;
import plana.command.ClientCommand;
import plana.command.CommandType;
import plana.command.DeleteCommand;
import plana.command.ExitCommand;
import plana.command.FindCommand;
import plana.command.HelpCommand;
import plana.command.InvalidCommand;
import plana.command.ListCommand;
import plana.command.MarkCommand;
import plana.command.OnCommand;
import plana.command.UnmarkCommand;
import plana.exception.PlanaException;

/**
 * Tests command classification and structured input validation.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    /**
     * Verifies that raw input is classified correctly and that arguments are
     * extracted only for commands that accept them.
     */
    @Test
    public void parse_knownAndUnknownInputs_correctTypeAndArgumentsReturned() {
        assertEquals(new Parser.ParsedCommand(CommandType.TODO, "buy milk"),
                parser.parse("todo buy milk"));
        assertEquals(new Parser.ParsedCommand(CommandType.DELETE, "2"),
                parser.parse("delete 2"));
        assertEquals(new Parser.ParsedCommand(CommandType.HELP, ""), parser.parse("please help me"));
        assertEquals(new Parser.ParsedCommand(CommandType.UNKNOWN, ""),
                parser.parse("not a command"));
    }

    /**
     * Verifies that null, whitespace, aliases, and case variations are
     * normalized before command arguments are extracted.
     */
    @Test
    public void parse_nullWhitespaceAndHelpAliases_normalizedValuesReturned() {
        assertEquals(new Parser.ParsedCommand(CommandType.UNKNOWN, ""), parser.parse(null));
        assertEquals(new Parser.ParsedCommand(CommandType.UNKNOWN, ""), parser.parse("   "));
        assertEquals(new Parser.ParsedCommand(CommandType.HELP, "extra"), parser.parse("help extra"));
        assertEquals(new Parser.ParsedCommand(CommandType.HELP, ""), parser.parse(" please HELP me "));
        assertEquals(new Parser.ParsedCommand(CommandType.TODO, "buy milk"),
                parser.parse("  TODO   buy milk  "));
    }

    /**
     * Verifies that each command category is dispatched to the appropriate
     * executable command class.
     */
    @Test
    public void parseCommand_allCommandCategories_expectedCommandReturned() throws PlanaException {
        assertInstanceOf(ExitCommand.class, parser.parseCommand("bye"));
        assertTrue(parser.parseCommand("bye").isExit());
        assertInstanceOf(HelpCommand.class, parser.parseCommand("?"));
        assertInstanceOf(ListCommand.class, parser.parseCommand("list"));
        assertInstanceOf(OnCommand.class, parser.parseCommand("on 2026-08-31"));
        assertInstanceOf(FindCommand.class, parser.parseCommand("find book"));
        assertInstanceOf(DeleteCommand.class, parser.parseCommand("delete 1"));
        assertInstanceOf(MarkCommand.class, parser.parseCommand("mark 1"));
        assertInstanceOf(UnmarkCommand.class, parser.parseCommand("unmark 1"));
        assertInstanceOf(ClientCommand.class, parser.parseCommand("client list"));
        assertInstanceOf(ClientCommand.class,
                parser.parseCommand("client add Alice /email alice@example.com"));
        assertInstanceOf(ClientCommand.class, parser.parseCommand("client view C1"));
        assertInstanceOf(ClientCommand.class, parser.parseCommand("client find alice"));
        assertInstanceOf(ClientCommand.class, parser.parseCommand("client edit C1 /phone 91234567"));
        assertInstanceOf(ClientCommand.class, parser.parseCommand("client delete C1"));
        assertInstanceOf(AddCommand.class, parser.parseCommand("todo buy milk"));
        assertInstanceOf(AddCommand.class, parser.parseCommand("deadline report /by 2026-08-31"));
        assertInstanceOf(AddCommand.class,
                parser.parseCommand("event meeting /from 2026-08-31 /to 2026-09-01"));
        assertInstanceOf(InvalidCommand.class, parser.parseCommand("not a command"));
    }

    /**
     * Verifies that valid ToDo, deadline, and event arguments are returned in
     * their typed representation.
     */
    @Test
    public void parseTaskArguments_validTaskInputs_typedArgumentsReturned() throws PlanaException {
        assertEquals(new Parser.TaskArguments("buy milk", null, null),
                parser.parseTaskArguments(parser.parse("todo buy milk")));
        assertEquals(new Parser.TaskArguments("submit report", LocalDate.of(2026, 8, 31), null),
                parser.parseTaskArguments(parser.parse("deadline submit report /by 2026-08-31")));
        assertEquals(new Parser.TaskArguments("team meeting", LocalDate.of(2026, 8, 31),
                        LocalDate.of(2026, 9, 1)),
                parser.parseTaskArguments(parser.parse(
                        "event team meeting /from 2026-08-31 /to 2026-09-01")));
    }

    /**
     * Verifies valid ISO dates and command-specific messages for invalid dates.
     */
    @Test
    public void parseDate_validAndInvalidDates_expectedResultOrMessage() throws PlanaException {
        assertEquals(LocalDate.of(2026, 8, 31), parser.parseDate("2026-08-31", "on"));

        PlanaException deadlineError = assertThrows(PlanaException.class, () ->
                parser.parseDate("31-08-2026", "deadline"));
        assertEquals("Oops, that deadline date isn't valid."
                + " Use the date format yyyy-MM-dd, like 2019-10-15."
                + " Try: deadline <description> /by <date>.", deadlineError.getMessage());

        PlanaException eventError = assertThrows(PlanaException.class, () ->
                parser.parseDate("2026-02-30", "event"));
        assertEquals("Oops, that event date isn't valid."
                + " Use the date format yyyy-MM-dd, like 2019-10-15."
                + " Try: event <description> /from <start> /to <end>.", eventError.getMessage());

        PlanaException queryError = assertThrows(PlanaException.class, () ->
                parser.parseDate("tomorrow", "on"));
        assertEquals("Oops, that query date isn't valid."
                + " Use the date format yyyy-MM-dd, like 2019-10-15. Try: on <date>.", queryError.getMessage());
    }

    /**
     * Verifies that empty or malformed ToDo and deadline inputs are rejected
     * with actionable messages.
     */
    @Test
    public void parseTaskArguments_invalidTodoAndDeadlineInputs_exceptionReturned() {
        assertParserException("todo", "Oops, a ToDo description can't be empty."
                + " Try: todo <description>.");
        assertParserException("deadline", "Oops, a deadline needs a description and a due date."
                + " Try: deadline <description> /by <date>.");
        assertParserException("deadline report", "Oops, I couldn't find /by and a due date is missing."
                + " Try: deadline <description> /by <date>.");
        assertParserException("deadline /by 2026-08-31", "Oops, that deadline is missing its description."
                + " Try: deadline <description> /by <date>.");
        assertParserException("deadline report /by", "Oops, that deadline is missing its due date."
                + " Try: deadline <description> /by <date>.");
        assertParserException("deadline report /by 2026-08-31 /by 2026-09-01",
                "Oops, the /by marker was provided more than once."
                        + " Try: deadline <description> /by <date>.");
    }

    /**
     * Verifies that a find command requires a non-blank keyword.
     */
    @Test
    public void parseCommand_findWithoutKeyword_exceptionReturned() throws PlanaException {
        assertParserCommandException("find", "Oops, find needs a keyword. Try: find <keyword>.");
        assertParserCommandException("list extra", "Oops, list doesn't take any arguments. Try: list.");
        assertParserCommandException("bye later", "Oops, bye doesn't take any arguments. Try: bye.");
        assertInstanceOf(InvalidCommand.class, parser.parseCommand(""));
        assertParserCommandException("on", "Oops, on needs a date. Try: on <date>.");
    }

    /**
     * Verifies that malformed event markers, fields, ordering, and dates are
     * rejected without producing a partially valid task.
     */
    @Test
    public void parseTaskArguments_invalidEventInputs_exceptionReturned() {
        assertParserException("event", "Oops, an event needs a description, a start, and an end."
                + " Try: event <description> /from <start> /to <end>.");
        assertParserException("event meeting /to 2026-09-01", "Oops, that event is missing its start marker /from."
                + " Try: event <description> /from <start> /to <end>.");
        assertParserException("event meeting /from 2026-08-31", "Oops, that event is missing its end marker /to."
                + " Try: event <description> /from <start> /to <end>.");
        assertParserException("event meeting /to 2026-09-01 /from 2026-08-31",
                "Oops, use /from before /to in an event."
                        + " Try: event <description> /from <start> /to <end>.");
        assertParserException("event /from 2026-08-31 /to 2026-09-01",
                "Oops, that event is missing its description."
                        + " Try: event <description> /from <start> /to <end>.");
        assertParserException("event meeting /from /to 2026-09-01",
                "Oops, that event is missing its start time."
                        + " Try: event <description> /from <start> /to <end>.");
        assertParserException("event meeting /from 2026-08-31 /to",
                "Oops, that event is missing its end time."
                        + " Try: event <description> /from <start> /to <end>.");
        assertParserException("event meeting /from invalid /to 2026-09-01",
                "Oops, that event date isn't valid."
                        + " Use the date format yyyy-MM-dd, like 2019-10-15."
                        + " Try: event <description> /from <start> /to <end>.");
        assertParserException("event meeting /from 2026-09-01 /to 2026-09-01",
                "Oops, an event's start date must be before its end date."
                        + " Try: event <description> /from <start> /to <end>.");
        assertParserException("event meeting /from 2026-09-01 /from 2026-09-02 /to 2026-09-03",
                "Oops, the /from marker was provided more than once."
                        + " Try: event <description> /from <start> /to <end>.");
    }

    /**
     * Verifies that task-argument parsing is only available for task commands.
     */
    @Test
    public void parseTaskArguments_nonTaskCommand_illegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () ->
                parser.parseTaskArguments(new Parser.ParsedCommand(CommandType.LIST, "")));
    }

    /**
     * Verifies client fields, normalization, and optional-field clearing syntax.
     */
    @Test
    public void parseClientCommands_validInputsAccepted() throws PlanaException {
        assertInstanceOf(ClientCommand.class,
                parser.parseCommand("client add Alice Tan /email ALICE@example.com /preferences no nuts"));
        assertInstanceOf(ClientCommand.class,
                parser.parseCommand("client edit C1 /phone \"\" /notes \"\""));
        assertInstanceOf(ClientCommand.class, parser.parseCommand("client view c1"));
        assertInstanceOf(ClientCommand.class, parser.parseCommand("client view 1"));
    }

    /**
     * Verifies client command validation provides actionable errors.
     */
    @Test
    public void parseClientCommands_invalidInputsRejected() {
        assertParserCommandException("client add Alice", "Oops, that client is missing its email."
                + " Try: client add <name> /email <email>.");
        assertParserCommandException("client add Alice /email invalid",
                "Oops, that email isn't valid. Use an address like alice@example.com.");
        assertParserCommandException("client add Alice /email alice@example.com /nickname baker",
                "Oops, I don't recognize the client field /nickname."
                        + " Use /phone, /address, /preferences, or /notes.");
        assertParserCommandException("client view C0", "Oops, 'C0' isn't a valid client position."
                + " Use a reference like C1.");
        assertParserCommandException("client edit C1", "Oops, client edit needs at least one field to change."
                + " Try: client edit <position> /phone <phone>.");
    }

    /**
     * Verifies client actions reject missing subcommands, malformed
     * references, duplicate fields, and invalid optional values.
     */
    @Test
    public void parseClientCommands_missingAndInvalidFields_rejected() {
        assertParserCommandException("client", "Oops, client needs an action."
                + " Try: client add <name> /email <email>.");
        assertParserCommandException("client archive", "Oops, I don't recognize client action 'archive'."
                + " Try: client add <name> /email <email>.");
        assertParserCommandException("client list extra",
                "Oops, client list doesn't take any arguments. Try: client list.");
        assertParserCommandException("client view", "Oops, client view needs a client position."
                + " Try: client view C1.");
        assertParserCommandException("client delete C0", "Oops, 'C0' isn't a valid client position."
                + " Use a reference like C1.");
        assertParserCommandException("client add /email alice@example.com", "Oops, that client is missing its name."
                + " Try: client add <name> /email <email>.");
        assertParserCommandException("client add Alice /email", "Oops, that client is missing its email."
                + " Try: client add <name> /email <email>.");
        assertParserCommandException("client add Alice /email alice@example.com /name Alicia",
                "Oops, put the client name before the field markers."
                        + " Try: client add <name> /email <email>.");
        assertParserCommandException("client add Alice /email alice@example.com /email other@example.com",
                "Oops, the client field /email was provided more than once.");
        assertParserCommandException("client add Alice /email alice@example.com /phone",
                "Oops, /phone needs a value or should be left out.");
        assertParserCommandException("client add Alice /email alice@example.com /phone 123",
                "Oops, that phone number isn't valid. Use 7 to 15 digits.");
        assertParserCommandException("client add Alice /email alice@example.com /phone 123-4567x",
                "Oops, that phone number isn't valid. Use 7 to 15 digits.");
        assertParserCommandException("client edit C1 Alice",
                "Oops, client edit fields must use markers such as /phone or /notes.");
        assertParserCommandException("client edit C1 /phone 91234567 /phone 98765432",
                "Oops, the client field /phone was provided more than once.");
    }

    /**
     * Verifies task and client text limits reject control characters and
     * values longer than their documented limits.
     */
    @Test
    public void textValidation_overlongAndControlCharacterValues_rejected() {
        String longDescription = "a".repeat(201);
        assertParserException("todo " + longDescription, "Oops, that ToDo description is too long"
                + " or contains invalid characters. Use 200 characters or fewer.");
        assertParserException("deadline " + longDescription + " /by 2026-08-31",
                "Oops, that deadline description is too long"
                        + " or contains invalid characters. Use 200 characters or fewer.");
        assertParserException("event " + longDescription + " /from 2026-08-31 /to 2026-09-01",
                "Oops, that event description is too long"
                        + " or contains invalid characters. Use 200 characters or fewer.");
        assertParserException("todo line1\nline2", "Oops, that ToDo description is too long"
                + " or contains invalid characters. Use 200 characters or fewer.");
        assertParserCommandException("client add " + "a".repeat(101)
                        + " /email alice@example.com",
                "Oops, that client name isn't valid. Use 100 characters or fewer.");
        assertParserCommandException("client add Alice /email alice@example.com /notes bad\nnotes",
                "Oops, /notes is too long or contains invalid characters.");
    }

    private void assertParserException(String input, String expectedMessage) {
        PlanaException exception = assertThrows(PlanaException.class, () ->
                parser.parseTaskArguments(parser.parse(input)));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void assertParserCommandException(String input, String expectedMessage) {
        PlanaException exception = assertThrows(PlanaException.class, () -> parser.parseCommand(input));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
