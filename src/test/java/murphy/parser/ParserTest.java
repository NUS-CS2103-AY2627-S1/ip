package murphy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests Murphy's conversion of user input into structured commands. */
class ParserTest {
    private final Parser parser = new Parser();

    /** Verifies that supported commands preserve their arguments and original input. */
    @Test
    void parse_supportedCommands_returnsCommandAndArgument() {
        assertParsed("  TODO buy milk  ", Parser.Command.TODO, "buy milk", "TODO buy milk");
        assertParsed("deadline report /by 2026-08-26", Parser.Command.DEADLINE,
                "report /by 2026-08-26", "deadline report /by 2026-08-26");
        assertParsed("event meeting /from 2pm /to 4pm", Parser.Command.EVENT,
                "meeting /from 2pm /to 4pm", "event meeting /from 2pm /to 4pm");
        assertParsed("on 2026-08-26", Parser.Command.ON, "2026-08-26", "on 2026-08-26");
        assertParsed("delete 2", Parser.Command.DELETE, "2", "delete 2");
        assertParsed("mark 1", Parser.Command.MARK, "1", "mark 1");
        assertParsed("unmark 1", Parser.Command.UNMARK, "1", "unmark 1");
        assertParsed("list", Parser.Command.LIST, "", "list");
        assertParsed("find book", Parser.Command.FIND, "book", "find book");
        assertParsed("bye", Parser.Command.BYE, "", "bye");
    }

    /** Verifies that unsupported commands are not accidentally accepted. */
    @Test
    void parse_unknownCommand_returnsUnknownWithTrimmedText() {
        Parser.ParsedCommand result = parser.parse("  postpone tomorrow  ");

        assertEquals(Parser.Command.UNKNOWN, result.command());
        assertEquals("", result.argument());
        assertEquals("postpone tomorrow", result.originalText());
    }

    /** Verifies that command names must be separated from their arguments. */
    @Test
    void parse_commandPrefixWithoutSeparator_returnsUnknown() {
        assertEquals(Parser.Command.UNKNOWN, parser.parse("today is busy").command());
    }

    private void assertParsed(String input, Parser.Command command, String argument, String originalText) {
        Parser.ParsedCommand result = parser.parse(input);

        assertEquals(command, result.command());
        assertEquals(argument, result.argument());
        assertEquals(originalText, result.originalText());
    }
}
