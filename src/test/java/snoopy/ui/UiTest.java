package snoopy.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

/**
 * Tests text-interface input and representative output.
 */
public class UiTest {
    @Test
    public void readCommand_twoCommands_returnsInputInOrder() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi("todo read book\nlist\n", output);

        assertTrue(ui.hasNextCommand());
        assertEquals("todo read book", ui.readCommand());
        assertEquals("list", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void showWelcomeAndResponse_messages_includeBannerAndDividers() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi("", output);

        ui.showWelcome("Hello\n\nOOPS! Loading failed.");
        ui.showResponse("Done");

        String renderedOutput = output.toString(StandardCharsets.UTF_8);
        String lineSeparator = System.lineSeparator();
        assertTrue(renderedOutput.contains("____"));
        assertTrue(renderedOutput.contains("Hello" + lineSeparator));
        assertTrue(renderedOutput.contains("OOPS! Loading failed." + lineSeparator));
        assertTrue(renderedOutput.endsWith("Done" + lineSeparator
                + "____________________________________________________________" + lineSeparator));
    }

    private Ui createUi(String input, ByteArrayOutputStream output) {
        Scanner scanner = new Scanner(input);
        PrintStream printStream = new PrintStream(output, true, StandardCharsets.UTF_8);
        return new Ui(scanner, printStream);
    }
}
