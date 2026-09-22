package tony.ui;

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
 * Tests console input and output through {@link Ui}.
 */
public class UiTest {
    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream output;

    /** Saves the process streams before replacing them with isolated test streams. */
    @BeforeEach
    public void setUpStreams() {
        originalInput = System.in;
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream("list\nbye\n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    /** Restores the process streams so this class cannot affect other tests. */
    @AfterEach
    public void restoreStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies that the console wrapper reads complete commands until input is exhausted. */
    @Test
    public void readCommand_multipleInputLines_readsLinesInOrder() {
        Ui ui = new Ui();

        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    /** Verifies the exact welcome, divider, and response text written to the console. */
    @Test
    public void outputMethods_messages_printExpectedText() {
        Ui ui = new Ui();

        ui.showWelcome();
        ui.showLine();
        ui.showMessage("A response");

        assertEquals(" _____   ___   _   _ __   __\n"
                + "|_   _| / _ \\ | \\ | |\\ \\ / /\n"
                + "  | |  | | | ||  \\| | \\ V /\n"
                + "  | |  | |_| || |\\  |  | |\n"
                + "  |_|   \\___/ |_| \\_|  |_|\n"
                + "________________________________________________\n"
                + "Good day, Chief. What shall I arrange for you?\n"
                + "________________________________________________\n"
                + "________________________________________________\n"
                + "A response\n", output.toString(StandardCharsets.UTF_8));
    }
}
