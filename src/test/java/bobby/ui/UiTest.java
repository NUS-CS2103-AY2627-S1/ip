package bobby.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests console input and output independently of Bobby's command processing.
 */
class UiTest {
    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    /**
     * Restores the process streams after each test.
     */
    @AfterEach
    void restoreSystemStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /**
     * Verifies that command availability and reading follow the supplied input lines.
     */
    @Test
    void inputMethods_twoCommands_commandsReadInOrderUntilExhausted() {
        System.setIn(new ByteArrayInputStream("list\nbye\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    /**
     * Verifies the complete text emitted by every console output method.
     */
    @Test
    void outputMethods_methodsCalled_expectedConsoleTextPrinted() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        Ui ui = new Ui();

        ui.showWelcome();
        ui.showLine();
        ui.showResponse("first line\nsecond line");
        ui.showLoadingError();
        ui.showGoodbye();

        String line = "____________________________________________________________";
        String lineSeparator = System.lineSeparator();
        String expectedOutput = String.join(lineSeparator,
                line,
                "BBBB   OOO   BBBB  BBBB  Y   Y",
                "B   B O   O  B   B B   B  Y Y",
                "BBBB  O   O  BBBB  BBBB    Y",
                "B   B O   O  B   B B   B   Y",
                "BBBB   OOO   BBBB  BBBB    Y",
                line,
                "     Well met, most honoured patron. I am Lord Bobby, Royal Steward of the Register.",
                "     What charge wouldst thou have me enter, amend, or proclaim?",
                line,
                line) + lineSeparator
                + "first line\nsecond line" + lineSeparator
                + String.join(lineSeparator,
                line,
                "     Regrettably, the saved register could not be read.",
                "     I shall therefore commence with an empty register.",
                line,
                line,
                "     I humbly take my leave. May good fortune attend thee until next we meet.",
                line,
                "");

        assertEquals(normalizeLineEndings(expectedOutput),
                normalizeLineEndings(output.toString(StandardCharsets.UTF_8)));
    }

    /**
     * Normalizes platform line endings while preserving all other whitespace.
     *
     * @param text the text whose line endings should be normalized
     * @return the text with LF line endings
     */
    private static String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n");
    }
}
