package herta.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import herta.task.Todo;

/**
 * Tests console input handling and the standard output formatting used by Herta.
 */
class UiTest {

    @Test
    void showMessageAndTaskCount_formatOutputWithExpectedIndentation() throws Exception {
        String output = captureOutput(() -> {
            Ui ui = new Ui();
            ui.showMessage("first\nsecond");
            ui.showTask(new Todo("read book"));
            ui.showTask(2, new Todo("write book"));
            ui.showTaskCount(1);
            ui.showTaskCount(2);
            ui.showSeparator();
            ui.showGoodbye();
        });

        assertTrue(output.contains("     first"));
        assertTrue(output.contains("     second"));
        assertTrue(output.contains("       [T][ ] read book"));
        assertTrue(output.contains("     2. [T][ ] write book"));
        assertTrue(output.contains("That makes 1 active task. Try to keep up."));
        assertTrue(output.contains("That makes 2 active tasks. Try to keep up."));
        assertTrue(output.contains("Leaving already? Goodbye."));
    }

    @Test
    void showWelcome_containsBannerAndGreeting() throws Exception {
        String output = captureOutput(() -> new Ui().showWelcome());

        assertTrue(output.contains("_   _           _"));
        assertTrue(output.contains("Oh, you're here. I'm Herta."));
        assertTrue(output.contains("Well? What do you want?"));
    }

    @Test
    void readCommand_trimsInputAndReturnsNullAtEndOfInput() throws Exception {
        java.io.InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayInputStream input = new ByteArrayInputStream(
                "  todo read book  \n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = null;
        try {
            System.setIn(input);
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            ui = new Ui();

            assertEquals("todo read book", ui.readCommand());
            assertNull(ui.readCommand());
            assertEquals("Your command? Your command? ", output.toString(StandardCharsets.UTF_8));
        } finally {
            if (ui != null) {
                ui.close();
            }
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }

    @Test
    void readCommand_blankLine_returnsEmptyCommandAndClosesSafely() throws Exception {
        java.io.InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayInputStream input = new ByteArrayInputStream("   \n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = null;
        try {
            System.setIn(input);
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            ui = new Ui();

            assertEquals("", ui.readCommand());
        } finally {
            if (ui != null) {
                ui.close();
            }
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }

    private String captureOutput(OutputAction action) throws Exception {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            action.run();
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOutput);
        }
    }

    /** Represents a UI test action that may fail during execution. */
    @FunctionalInterface
    private interface OutputAction {
        void run() throws Exception;
    }
}
