package moon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests Moon's console input and output behavior. */
public class UiTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private java.io.InputStream originalInput;
    private PrintStream originalOutput;

    /** Captures console output and provides deterministic input for each test. */
    @BeforeEach
    public void captureConsole() {
        originalInput = System.in;
        originalOutput = System.out;
        System.setIn(new ByteArrayInputStream("  todo buy milk  \n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    /** Restores the process console streams after each test. */
    @AfterEach
    public void restoreConsole() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies that console commands are trimmed and end-of-input is handled. */
    @Test
    public void readCommand_trimsInputAndReportsEndOfInput() {
        Ui ui = new Ui();

        assertEquals("todo buy milk", ui.readCommand());
        assertNull(ui.readCommand());
    }

    /** Verifies that the welcome message identifies Moon and explains the tone. */
    @Test
    public void showWelcome_mentionsMoonPersonality() {
        Ui ui = new Ui();

        ui.showWelcome();

        String message = output.toString(StandardCharsets.UTF_8);
        assertTrue(message.contains("Moon"));
        assertTrue(message.contains("task sidekick"));
    }

    /** Verifies that console error and farewell messages are displayed. */
    @Test
    public void showMessages_displaysErrorsAndFarewell() {
        Ui ui = new Ui();

        ui.showLoadingError();
        ui.showError("bad command");
        ui.showSavingError();
        ui.showGoodbye();

        String message = output.toString(StandardCharsets.UTF_8);
        assertTrue(message.contains("couldn't load"));
        assertTrue(message.contains("bad command"));
        assertTrue(message.contains("save got a little scuffed"));
        assertTrue(message.contains("catch you later"));
    }
}
