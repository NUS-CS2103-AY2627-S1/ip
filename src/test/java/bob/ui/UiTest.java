package bob.ui;

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

/** Tests console input and output behavior of the user interface. */
class UiTest {
    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream output;
    private Ui ui;

    @BeforeEach
    void setUpStreams() {
        originalInput = System.in;
        originalOutput = System.out;
        System.setIn(new ByteArrayInputStream(
                "first command\nsecond command\n".getBytes(StandardCharsets.UTF_8)));
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        ui = new Ui();
    }

    @AfterEach
    void restoreStreams() {
        ui.close();
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    void hasNextLineAndReadCommand_multipleLines_readsCommandsInOrder() {
        assertTrue(ui.hasNextLine());
        assertEquals("first command", ui.readCommand());
        assertTrue(ui.hasNextLine());
        assertEquals("second command", ui.readCommand());
        assertFalse(ui.hasNextLine());
    }

    @Test
    void showWelcome_displaysBannerAndMessages() {
        ui.showWelcome();

        String displayedText = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayedText.contains("Hi! I'm Bob, your task companion."));
        assertTrue(displayedText.contains("What can I do for you?"));
        assertTrue(displayedText.contains("____        _"));
    }

    @Test
    void showResponse_multipleLines_displaysEveryLine() {
        ui.showResponse("First line\nSecond line");

        String displayedText = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayedText.contains("First line"));
        assertTrue(displayedText.contains("Second line"));
    }

    @Test
    void showLine_displaysDivider() {
        ui.showLine();

        String displayedText = output.toString(StandardCharsets.UTF_8);
        assertTrue(displayedText.contains("____________________________________________________________"));
    }
}
