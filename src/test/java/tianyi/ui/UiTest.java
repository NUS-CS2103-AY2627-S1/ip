package tianyi.ui;

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

/**
 * Tests console input and exact output formatting of {@link Ui}.
 */
public class UiTest {
    private static final String LINE =
            "____________________________________________________________";
    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream output;

    @BeforeEach
    public void setUpStreams() {
        originalInput = System.in;
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(new byte[0]));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void readCommand_multipleLines_readsInOrderUntilEnd() {
        setInput("todo read book\nbye\n");
        Ui ui = new Ui();

        assertTrue(ui.hasNextCommand());
        assertEquals("todo read book", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void readCommand_blankLine_returnsEmptyString() {
        setInput("\n");
        Ui ui = new Ui();

        assertEquals("", ui.readCommand());
    }

    @Test
    public void showWelcome_multilineMessage_printsMessageBetweenLines() {
        Ui ui = new Ui();

        ui.showWelcome("Welcome to Tianyi.\nHow can I help?");

        assertEquals(LINE + "\n"
                + "Welcome to Tianyi.\n"
                + "How can I help?\n"
                + LINE + "\n", getOutput());
    }

    @Test
    public void showResponse_multilineResponse_printsContentBetweenLines() {
        Ui ui = new Ui();

        ui.showResponse("first line\nsecond line");

        assertEquals(LINE + "\n"
                + "first line\nsecond line\n"
                + LINE + "\n", getOutput());
    }

    @Test
    public void showError_message_printsPrefixedMessageBetweenLines() {
        Ui ui = new Ui();

        ui.showError("Something went wrong.");

        assertEquals(LINE + "\n"
                + "Oops! Something went wrong.\n"
                + LINE + "\n", getOutput());
    }

    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    private String getOutput() {
        return output.toString(StandardCharsets.UTF_8)
                .replace("\r\n", "\n")
                .replace('\r', '\n');
    }
}
