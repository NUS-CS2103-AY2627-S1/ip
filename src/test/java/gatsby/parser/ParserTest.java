package gatsby.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import gatsby.command.CommandType;

/** Tests the command and payload extraction performed by {@link Parser}. */
class ParserTest {

    /** Verifies that a normal task command is split into its command and payload. */
    @Test
    void parse_todoCommand_returnsCommandAndPayload() {
        Parser parser = new Parser();

        Parser.ParsedCommand result = parser.parse("todo buy milk");

        assertEquals(CommandType.TODO, result.getCommand());
        assertEquals("buy milk", result.getPayload());
    }

    /** Verifies case-insensitive commands and trimming around user input. */
    @Test
    void parse_mixedCaseCommandWithExtraSpaces_preservesPayload() {
        Parser parser = new Parser();

        Parser.ParsedCommand result = parser.parse("  ToDo   Buy milk  ");

        assertEquals(CommandType.TODO, result.getCommand());
        assertEquals("Buy milk", result.getPayload());
    }

    /** Verifies every command that accepts a payload is mapped to its command type. */
    @Test
    void parse_supportedPayloadCommands_returnsMatchingCommandTypes() {
        Parser parser = new Parser();

        assertEquals(CommandType.MARK, parser.parse("mark 1").getCommand());
        assertEquals(CommandType.UNMARK, parser.parse("unmark 1").getCommand());
        assertEquals(CommandType.TODO, parser.parse("todo buy milk").getCommand());
        assertEquals(CommandType.DEADLINE, parser.parse("deadline return book").getCommand());
        assertEquals(CommandType.EVENT, parser.parse("event project meeting").getCommand());
        assertEquals(CommandType.DELETE, parser.parse("delete 1").getCommand());
        assertEquals(CommandType.FIND, parser.parse("find book").getCommand());
        assertEquals("book", parser.parse("find book").getPayload());
    }

    /** Verifies that whole-line commands are recognized without a payload. */
    @Test
    void parse_wholeLineCommands_returnsMatchingCommand() {
        Parser parser = new Parser();

        assertEquals(CommandType.LIST, parser.parse("list").getCommand());
        assertEquals(CommandType.BYE, parser.parse("BYE").getCommand());
        assertEquals(CommandType.BYE, parser.parse("byebye").getCommand());
        assertEquals(CommandType.BYE, parser.parse("bye bye").getCommand());
        assertEquals("", parser.parse("list").getPayload());
    }

    /** Verifies that blank and unrecognized input does not become a valid command. */
    @Test
    void parse_blankOrUnknownInput_returnsUnknownCommand() {
        Parser parser = new Parser();

        assertEquals(CommandType.UNKNOWN, parser.parse("").getCommand());
        assertEquals(CommandType.UNKNOWN, parser.parse("  ").getCommand());
        assertEquals(CommandType.UNKNOWN, parser.parse("remove milk").getCommand());
        assertEquals("milk", parser.parse("remove milk").getPayload());
    }

    /** Verifies that null input and help text with an unexpected argument are safe. */
    @Test
    void parse_nullOrHelpWithPayload_returnsUnknownCommand() {
        Parser parser = new Parser();

        assertEquals(CommandType.UNKNOWN, parser.parse(null).getCommand());
        assertEquals(CommandType.UNKNOWN, parser.parse("help me").getCommand());
        assertEquals(CommandType.UNKNOWN, parser.parse("list now").getCommand());
    }
}
