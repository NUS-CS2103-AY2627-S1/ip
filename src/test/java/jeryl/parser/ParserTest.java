package jeryl.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void parse_commandWithArguments_splitsOnFirstSpace() {
        Parser.ParsedInput parsed = Parser.parse("todo read book /by tomorrow");
        assertEquals(Command.TODO, parsed.command());
        assertEquals("read book /by tomorrow", parsed.arguments());
    }

    @Test
    public void parse_commandWithNoArguments_returnsEmptyArguments() {
        Parser.ParsedInput parsed = Parser.parse("list");
        assertEquals(Command.LIST, parsed.command());
        assertEquals("", parsed.arguments());
    }

    @Test
    public void parse_unknownCommandWord_returnsUnknownCommand() {
        Parser.ParsedInput parsed = Parser.parse("frobnicate now");
        assertEquals(Command.UNKNOWN, parsed.command());
        assertEquals("now", parsed.arguments());
    }

    @Test
    public void parse_multipleSpacesInArguments_keepsOnlyFirstSpaceAsSeparator() {
        Parser.ParsedInput parsed = Parser.parse("mark  3");
        assertEquals(Command.MARK, parsed.command());
        // Only the first space is the separator; the rest stays in arguments.
        assertEquals(" 3", parsed.arguments());
    }

    @Test
    public void parse_leadingAndTrailingWhitespace_isStrippedBeforeSplitting() {
        Parser.ParsedInput parsed = Parser.parse("  todo read book  ");
        assertEquals(Command.TODO, parsed.command());
        assertEquals("read book", parsed.arguments());
    }

    @Test
    public void parse_wholeInputIsWhitespace_returnsUnknownCommand() {
        Parser.ParsedInput parsed = Parser.parse("   ");
        assertEquals(Command.UNKNOWN, parsed.command());
        assertEquals("", parsed.arguments());
    }
}
