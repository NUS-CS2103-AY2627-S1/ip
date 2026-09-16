package crow.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UiTest {
    private ByteArrayOutputStream outputBytes;
    private Ui ui;

    @BeforeEach
    void setUp() {
        outputBytes = new ByteArrayOutputStream();
        ui = createUi("");
    }

    @Test
    void readCommand_trimsInputAndDetectsEnd() {
        ui = createUi("  todo read book  \nbye\n");

        assertTrue(ui.hasNextCommand());
        assertEquals("todo read book", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    void showWelcome_displaysBannerGreetingAndSeparator() {
        ui.showWelcome();

        String output = getOutputText();
        assertTrue(output.startsWith("____________________________________________________________"));
        assertTrue(output.contains("___  ____   __   _  _"));
        assertTrue(output.contains("Hello! I'm Crow."));
        assertTrue(output.contains("What can I do for you?"));
    }

    @Test
    void errorAndResponse_displayExpectedMessages() {
        ui.showError("Error: Unknown command.");
        ui.showResponse("First line\nSecond line");

        String output = getOutputText();
        assertTrue(output.contains("Error: Unknown command."));
        assertTrue(output.contains(" First line\n Second line"));
    }

    private Ui createUi(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        PrintStream outputStream = new PrintStream(outputBytes, true, StandardCharsets.UTF_8);
        return new Ui(inputStream, outputStream);
    }

    private String getOutputText() {
        return outputBytes.toString(StandardCharsets.UTF_8);
    }
}
