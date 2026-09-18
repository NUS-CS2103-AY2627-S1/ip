package computah.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import computah.command.AddClientCommand;
import computah.command.AddCommand;
import computah.command.Command;
import computah.command.DeleteClientCommand;
import computah.command.EditClientCommand;
import computah.command.FindCommand;
import computah.command.ListClientsCommand;
import computah.exception.ComputahException;

/**
 * Tests command parsing behavior in {@link Parser}.
 */
public class ParserTest {
    @Test
    public void parse_findWithKeyword_returnsFindCommand() throws ComputahException {
        Command command = Parser.parse("find book", 0, 0);

        assertInstanceOf(FindCommand.class, command);
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () -> Parser.parse("find", 0, 0));

        assertEquals("The keyword of a find command cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_findWithBlankKeyword_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () -> Parser.parse("find   ", 0, 0));

        assertEquals("The keyword of a find command cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_wordStartingWithFindButNotFindCommand_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () -> Parser.parse("findbook", 0, 0));

        assertEquals("I'm sorry, but I don't know what that means :-(", exception.getMessage());
    }

    @Test
    public void parse_clientCommands_returnsMatchingCommandTypes() throws ComputahException {
        assertInstanceOf(AddClientCommand.class,
                Parser.parse("client add Alice /phone 123 /email alice@example.com", 0, 0));
        assertInstanceOf(ListClientsCommand.class, Parser.parse("client list", 0, 1));
        assertInstanceOf(EditClientCommand.class, Parser.parse("client edit 1 /phone -", 0, 1));
        assertInstanceOf(DeleteClientCommand.class, Parser.parse("client delete 1", 0, 1));
    }

    @Test
    public void parse_clientAddWithoutName_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                Parser.parse("client add", 0, 0));

        assertEquals("The name of a client cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_clientAddWithFieldButNoName_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                Parser.parse("client add /phone 123", 0, 0));

        assertEquals("The name of a client cannot be empty.", exception.getMessage());
    }

    @Test
    public void parse_clientEditWithUnknownField_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                Parser.parse("client edit 1 /fax 123", 0, 1));

        assertEquals("Unknown client field: /fax.", exception.getMessage());
    }

    @Test
    public void parse_clientEditWithDuplicateField_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                Parser.parse("client edit 1 /phone 123 /phone 456", 0, 1));

        assertEquals("Client field /phone cannot be specified more than once.", exception.getMessage());
    }

    @Test
    public void parse_clientDeleteWithInvalidIndex_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                Parser.parse("client delete 2", 0, 1));

        assertEquals("The client number is not in the list.", exception.getMessage());
    }

    @Test
    public void parse_extraWhitespace_returnsCommand() throws ComputahException {
        assertInstanceOf(AddCommand.class, Parser.parse("  todo    read   book  ", 0, 0));
        assertInstanceOf(AddClientCommand.class,
                Parser.parse(" client   add   Alice Tan   /phone   +65 1234 5678 ", 0, 0));
    }

    @Test
    public void parse_nullInput_exceptionThrown() {
        assertParseError(null, 0, 0, "Please enter a command.");
    }

    @Test
    public void parse_noArgumentCommandWithArguments_exceptionThrown() {
        assertParseError("list extra", 0, 0, "The list command does not accept additional arguments.");
        assertParseError("bye now", 0, 0, "The bye command does not accept additional arguments.");
    }

    @Test
    public void parse_taskNumberWithExtraValue_exceptionThrown() {
        assertParseError("mark 1 2", 2, 0, "Please specify exactly one task number after mark.");
    }

    @Test
    public void parse_clientNumberWithExtraValue_exceptionThrown() {
        assertParseError("client delete 1 2", 0, 2, "Please specify exactly one client number.");
    }

    @Test
    public void parse_deadlineWithDuplicateBy_exceptionThrown() {
        assertParseError("deadline submit /by 2026-10-01 /by 2026-10-02", 0, 0,
                "The /by parameter can only be specified once.");
    }

    @Test
    public void parse_eventWithDuplicateParameters_exceptionThrown() {
        assertParseError("event meeting /from 2026-10-01 /from 2026-10-02 /to 2026-10-03", 0, 0,
                "The /from parameter can only be specified once.");
        assertParseError("event meeting /from 2026-10-01 /to 2026-10-02 /to 2026-10-03", 0, 0,
                "The /to parameter can only be specified once.");
    }

    @Test
    public void parse_eventWithParametersOutOfOrder_exceptionThrown() {
        assertParseError("event meeting /to 2026-10-02 /from 2026-10-01", 0, 0,
                "The /from parameter must appear before the /to parameter.");
    }

    @Test
    public void parse_eventEndingBeforeStart_exceptionThrown() {
        assertParseError("event meeting /from 2026-10-02 /to 2026-10-01", 0, 0,
                "The end date/time of an event cannot be before its start date/time.");
    }

    /**
     * Checks the error reported for an invalid command.
     *
     * @param input command input.
     * @param taskCount available task count.
     * @param clientCount available client count.
     * @param expectedMessage expected error message.
     */
    private void assertParseError(String input, int taskCount, int clientCount, String expectedMessage) {
        ComputahException exception = assertThrows(ComputahException.class, () ->
                Parser.parse(input, taskCount, clientCount));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
