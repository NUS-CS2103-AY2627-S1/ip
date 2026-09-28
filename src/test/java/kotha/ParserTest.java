package kotha;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import kotha.Parser.CommandType;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseExactCommands_returnsCorrectCommandType() {
        assertEquals(CommandType.BYE, parser.parse("bye"));
        assertEquals(CommandType.LIST, parser.parse("list"));
        assertEquals(CommandType.FIND, parser.parse("find book"));
    }

    @Test
    void parseCommandsWithArguments_returnsCorrectCommandType() {
        assertEquals(CommandType.MARK, parser.parse("mark 1"));
        assertEquals(CommandType.UNMARK, parser.parse("unmark 1"));
        assertEquals(CommandType.TODO, parser.parse("todo read book"));
        assertEquals(CommandType.DEADLINE, parser.parse("deadline submit report /by 1/9"));
        assertEquals(CommandType.EVENT, parser.parse("event meeting /from 1/9 /to 2/9"));
        assertEquals(CommandType.CONSTRAINT, parser.parse("constraint return book /after exam"));
        assertEquals(CommandType.DELETE, parser.parse("delete 1"));
    }

    @Test
    void parseInputWithWhitespace_ignoresLeadingAndTrailingWhitespace() {
        assertEquals(CommandType.LIST, parser.parse("  list  "));
        assertEquals(CommandType.TODO, parser.parse("  todo read book  "));
    }

    @Test
    void parseUnknownOrPartialCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, parser.parse("archive 1"));
        assertEquals(CommandType.UNKNOWN, parser.parse("listing"));
        assertEquals(CommandType.UNKNOWN, parser.parse("marking 1"));
        assertEquals(CommandType.UNKNOWN, parser.parse(""));
        assertEquals(CommandType.UNKNOWN, parser.parse("   "));
    }
}
