package bob.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Verifies console responses are formatted for chat bubbles.
 */
public class MainWindowTest {
    @Test
    public void cleanOutput_coloredGreeting_removesColorsAndTrailingNewline() {
        assertEquals("hi im bob", MainWindow.cleanOutput("\u001B[34mhi im bob\u001B[0m\r\n"));
    }

    @Test
    public void cleanOutput_multilineReply_preservesLeadingWhitespaceAndInternalLines() {
        String output = "  Here are the tasks:\n\n\u001B[32m1.[T][ ] read book\u001B[0m\n\n";

        assertEquals("  Here are the tasks:\n\n1.[T][ ] read book", MainWindow.cleanOutput(output));
    }

    @Test
    public void cleanOutput_emptyOrAlreadyCleanReply_returnsUnchangedText() {
        assertEquals("", MainWindow.cleanOutput(""));
        assertEquals("sorry bro, that aint a command",
                MainWindow.cleanOutput("sorry bro, that aint a command"));
    }
}
