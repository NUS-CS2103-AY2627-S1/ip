package melodie.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import melodie.MelodieException;

public class FindParserTest {

    @Test
    public void parse_findCommand_returnsFindKeyword() throws MelodieException {
        Parser parser = new Parser();
        ParsedCommand parsedCommand = parser.parse("find   Baron Nashor  ");

        String actualKeyword = parser.parseFindKeyword(parsedCommand.getArguments());

        assertEquals(Command.FIND, parsedCommand.getCommand());
        assertEquals("Baron Nashor", actualKeyword);
    }

    @Test
    public void parseFindKeyword_blankKeyword_throwsMelodieException() {
        Parser parser = new Parser();

        MelodieException exception =
                assertThrows(MelodieException.class, () -> parser.parseFindKeyword("   "));

        assertEquals("Please enter a keyword to search for :(\n"
                + "    Format: find <keyword>", exception.getMessage());
    }
}
