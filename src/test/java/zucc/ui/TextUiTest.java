package zucc.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Tests terminal input and output without requiring an interactive console.
 */
@ResourceLock("SYSTEM_IN")
@ResourceLock("SYSTEM_OUT")
public class TextUiTest {
    /** Verifies that terminal commands are detected and read one complete line at a time. */
    @Test
    public void readCommand_multipleInputLines_linesReturnedInOrder() {
        InputStream originalInput = System.in;
        System.setIn(new ByteArrayInputStream("list\nbye\n".getBytes(StandardCharsets.UTF_8)));

        try (TextUi ui = new TextUi()) {
            assertTrue(ui.hasNextCommand());
            assertEquals("list", ui.readCommand());
            assertTrue(ui.hasNextCommand());
            assertEquals("bye", ui.readCommand());
            assertFalse(ui.hasNextCommand());
        } finally {
            System.setIn(originalInput);
        }
    }

    /** Verifies the separators and indentation used to make terminal responses readable. */
    @Test
    public void showMessage_multilineMessage_messageFramedAndIndented() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        try (TextUi ui = new TextUi()) {
            ui.showMessage("First line\nSecond line");
        } finally {
            System.setOut(originalOutput);
        }

        String separator = "____________________________________________________________";
        assertEquals(
                separator.indent(4)
                        + "First line\nSecond line".indent(5)
                        + separator.indent(4),
                output.toString(StandardCharsets.UTF_8));
    }

    /** Verifies that the terminal greeting contains both the logo and shared welcome text. */
    @Test
    public void showGreeting_terminalUi_logoAndGreetingShown() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        try (TextUi ui = new TextUi()) {
            ui.showGreeting();
        } finally {
            System.setOut(originalOutput);
        }

        String displayedOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayedOutput.contains("/_____|\\__,_|\\___\\___|"));
        assertTrue(displayedOutput.contains("Hello! I'm Zucc.\n     What can I do for you?"));
    }
}
