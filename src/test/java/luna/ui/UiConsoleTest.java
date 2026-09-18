package luna.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
 * Tests Luna's command-line UI mode with isolated input and output streams.
 */
public class UiConsoleTest {
    private InputStream originalInput;
    private PrintStream originalOutput;

    @BeforeEach
    public void saveSystemStreams() {
        originalInput = System.in;
        originalOutput = System.out;
    }

    @AfterEach
    public void restoreSystemStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void readCommand_inputHasOuterWhitespace_returnsTrimmedCommand() {
        System.setIn(new ByteArrayInputStream("  list  \n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        Ui ui = new Ui();

        String command = ui.readCommand();

        assertEquals("list", command);
        ui.close();
    }

    @Test
    public void readCommand_inputEnds_returnsByeCommand() {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        Ui ui = new Ui();

        String command = ui.readCommand();

        assertEquals("bye", command);
        ui.close();
    }

    @Test
    public void getWelcomeMessage_consoleMode_returnsBannerAndHelpHint() {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        Ui ui = new Ui();

        String welcomeMessage = ui.getWelcomeMessage();

        assertTrue(welcomeMessage.contains("|_____|"));
        assertTrue(welcomeMessage.contains("Type help to view all commands."));
        ui.close();
    }

    @Test
    public void showError_consoleMode_printsAndBuffersDecoratedMessage() {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        Ui ui = new Ui();

        ui.showError("test error");

        String bufferedResponse = ui.consumeLatestResponse();
        String printedResponse = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals(bufferedResponse, printedResponse);
        assertTrue(bufferedResponse.contains("I lost that signal. test error"));
        assertTrue(bufferedResponse.contains("___"));
        ui.close();
    }

    @Test
    public void readCommand_guiMode_throwsIllegalStateException() {
        Ui ui = new Ui(false);

        assertThrows(IllegalStateException.class, ui::readCommand);
    }
}
