package lenzabot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ParserTest {
    @Test
    void parse_commandWithoutArgument_returnsEmptyArgument() {
        Parser.ParsedInput parsedInput = Parser.parse("list");

        assertEquals("list", parsedInput.getCommand());
        assertEquals("", parsedInput.getArgument());
    }

    @Test
    void parse_commandWithArgument_splitsAtFirstSpace() {
        Parser.ParsedInput parsedInput = Parser.parse("todo read book");

        assertEquals("todo", parsedInput.getCommand());
        assertEquals("read book", parsedInput.getArgument());
    }

    @Test
    void parse_argumentWithExtraSpaces_trimsArgument() {
        Parser.ParsedInput parsedInput = Parser.parse("todo    read book   ");

        assertEquals("todo", parsedInput.getCommand());
        assertEquals("read book", parsedInput.getArgument());
    }

    @Test
    void parse_tabsAndMixedCase_normalizesInput() {
        Parser.ParsedInput parsedInput = Parser.parse("  ToDo\t  read   book  ");

        assertEquals("todo", parsedInput.getCommand());
        assertEquals("read book", parsedInput.getArgument());
    }

    @Test
    void parse_supportedAliases_returnsCanonicalCommands() {
        assertParsedInput("t read book", "todo", "read book");
        assertParsedInput(
                "d return book /by 2/12/2019 1800",
                "deadline",
                "return book /by 2/12/2019 1800"
        );
        assertParsedInput("e meeting /from 2/12/2019 1400 /to 2/12/2019 1600",
                "event", "meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        assertParsedInput("ls", "list", "");
    }

    @Test
    void parse_unknownShortCommand_leavesCommandUnchanged() {
        assertParsedInput("td read book", "td", "read book");
    }

    private void assertParsedInput(String input, String expectedCommand, String expectedArgument) {
        Parser.ParsedInput parsedInput = Parser.parse(input);

        assertEquals(expectedCommand, parsedInput.getCommand());
        assertEquals(expectedArgument, parsedInput.getArgument());
    }
}
