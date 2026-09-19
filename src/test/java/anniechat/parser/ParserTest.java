package anniechat.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the command interpretation performed by {@link Parser}. */
public class ParserTest {
    @Test
    public void getTaskNumber_validInput_returnsZeroBasedIndex() {
        Parser parser = new Parser("mark 2");

        int result = parser.getTaskNumber();

        assertEquals(1, result);
    }

    @Test
    public void getCommandWord_validCommand_returnsCorrectCommand() {
        Parser parser = new Parser("todo read book");

        String result = parser.getCommandWord();

        assertEquals("todo", result);
    }
}
