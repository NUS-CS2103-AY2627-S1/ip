package duchess.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests console input and exact output formatting without launching a GUI. */
public class UiTest {
    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream capturedOutput;

    /** Replaces process streams with isolated in-memory streams. */
    @BeforeEach
    public void setUpStreams() {
        originalInput = System.in;
        originalOutput = System.out;
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
    }

    /** Restores process streams so this test class cannot affect other tests. */
    @AfterEach
    public void restoreStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies that commands are read one complete line at a time until input ends. */
    @Test
    public void readCommand_multipleLines_returnsEachLineAndDetectsEnd() {
        setInput("todo read book\nlist\n");
        Ui ui = new Ui();

        assertTrue(ui.hasNextLine());
        assertEquals("todo read book", ui.readCommand());
        assertTrue(ui.hasNextLine());
        assertEquals("list", ui.readCommand());
        assertFalse(ui.hasNextLine());
    }

    /** Verifies the complete startup banner and separators. */
    @Test
    public void showWelcome_printsExactBanner() {
        setInput("");
        Ui ui = new Ui();

        ui.showWelcome();

        assertEquals("""
                ____________________________________________________________
                +------------------------+
                |        Duchess         |
                +------------------------+
                Hello! I'm Duchess.
                What can I do for you?
                ____________________________________________________________
                """, output());
    }

    /** Verifies that ordinary messages and separators each end with a newline. */
    @Test
    public void showMessageAndSeparator_printExactLines() {
        setInput("");
        Ui ui = new Ui();

        ui.showMessage("first line\nsecond line");
        ui.showSeparator();

        assertEquals("""
                first line
                second line
                ____________________________________________________________
                """, output());
    }

    /** Supplies standard input for the next UI instance. */
    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    /** Returns captured text with Windows line endings normalized for portable assertions. */
    private String output() {
        return capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
