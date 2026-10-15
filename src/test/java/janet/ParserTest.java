package janet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void formatString_trailingAndLeadingWhitespace_whitespaceRemoved() {
        assertEquals("todo Read book", Parser.formatString("   todo Read book   "));
    }

    @Test
    public void formatString_whitespaceBetweenWords_whitespaceNotRemoved() {
        assertEquals("todo    Read     book", Parser.formatString("todo    Read     book"));
    }

    @Test
    public void exitCommand_differentCapitalization_notExit() {
        assertFalse(Parser.isExitCommand("EXIT"));
    }

    @Test
    public void resetCommand_differentCapitalization_notExit() {
        assertFalse(Parser.isExitCommand("RESET"));
    }

    @Test
    public void processCommand_differentCapitalization_exceptionThrown() {
        assertThrows(JanetException.class, () -> new Parser(new TaskList())
                .processCommand("LIST")
        );
    }
}
