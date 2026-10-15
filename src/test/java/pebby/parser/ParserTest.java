package pebby.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ParserTest {

    @Test
    void parse_supportedCommandWithArgument_returnsTypeAndTrimmedArgument() {
        ParsedCommand command = Parser.parse("  deadline return book /by 2019-12-02  ");

        assertEquals(CommandType.DEADLINE, command.getType());
        assertEquals("return book /by 2019-12-02", command.getArgument());
        assertFalse(command.hasError());
    }

    @Test
    void parse_commandsWithoutArguments_returnsRecognizedTypes() {
        assertEquals(CommandType.LIST, Parser.parse("list").getType());
        assertEquals(CommandType.HELP, Parser.parse("help").getType());
        assertEquals(CommandType.BYE, Parser.parse("bye").getType());
    }

    @Test
    void parse_blankUnknownAndWhitespaceCommands_returnsRelevantErrors() {
        assertError("", "Please enter a command.");
        assertError("  ", "Please enter a command.");
        assertError("dance", "Unknown command. Type help to see the available commands.");
        assertError("todo  read", "Use single spaces between command parts.");
        assertError("todo\tread", "Use single spaces between command parts.");
    }

    @Test
    void parse_argumentForNoArgumentCommand_returnsError() {
        assertError("list now", "The list command does not take an argument.");
        assertError("help please", "The help command does not take an argument.");
        assertError("bye now", "The bye command does not take an argument.");
    }

    @Test
    void commandTypeFrom_identifiesOnlyWholeCommandWords() {
        assertEquals(CommandType.TODO, CommandType.from("todo buy milk"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("todone"));
        assertEquals(CommandType.UNKNOWN, CommandType.from(null));
    }

    @Test
    void commandTypeArgumentFrom_returnsArgumentWithoutSurroundingSpaces() {
        assertEquals("buy milk", CommandType.TODO.argumentFrom("  todo buy milk  "));
        assertEquals("", CommandType.LIST.argumentFrom("list"));
    }

    private void assertError(String input, String expectedMessage) {
        ParsedCommand command = Parser.parse(input);

        assertEquals(CommandType.UNKNOWN, command.getType());
        assertTrue(command.hasError());
        assertEquals(expectedMessage, command.getErrorMessage());
    }
}
