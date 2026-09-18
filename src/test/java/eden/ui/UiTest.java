package eden.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import eden.task.Todo;

/**
 * Tests Eden's console input, output, and response formatting.
 */
public class UiTest {
    private static final String DIVIDER =
            "____________________________________________________________\n";

    private InputStream originalInput;
    private PrintStream originalOutput;

    /**
     * Saves the process streams so each test can restore them safely.
     */
    @BeforeEach
    public void saveSystemStreams() {
        originalInput = System.in;
        originalOutput = System.out;
    }

    /**
     * Restores the process streams after each test.
     */
    @AfterEach
    public void restoreSystemStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /**
     * Verifies that console input returns one complete command line.
     */
    @Test
    public void readCommand_lineOnStandardInput_returnsLineWithoutTerminator() {
        System.setIn(new ByteArrayInputStream(
                "todo read book\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertEquals("todo read book", ui.readCommand());
    }

    /**
     * Verifies that the console welcome includes the Eden banner and dividers.
     */
    @Test
    public void showWelcome_printsBannerGreetingAndDividers() {
        ByteArrayOutputStream output = captureStandardOutput();
        Ui ui = new Ui();

        ui.showWelcome();

        assertEquals(DIVIDER
                + " _____    _            \n"
                + "| ____|__| | ___ _ __  \n"
                + "|  _| / _` |/ _ \\ '_ \\ \n"
                + "| |__| (_| |  __/ | | |\n"
                + "|_____|\\__,_|\\___|_| |_|\n"
                + "Hello! I'm Eden.\nWhat can I do for you?\n"
                + DIVIDER, output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Verifies response decoration adds exactly one trailing newline.
     */
    @Test
    public void showResponse_withOrWithoutNewline_printsOneDecoratedLine() {
        ByteArrayOutputStream output = captureStandardOutput();
        Ui ui = new Ui();

        ui.showResponse("Done");
        assertEquals(DIVIDER + "Done\n" + DIVIDER,
                output.toString(StandardCharsets.UTF_8));

        output.reset();
        ui.showResponse("Done\n");
        assertEquals(DIVIDER + "Done\n" + DIVIDER,
                output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Verifies task confirmations contain the task and updated count.
     */
    @Test
    public void formatTaskChanges_taskAndCount_returnsFriendlyMessages() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");

        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\nNow you have 1 in the list.",
                ui.formatTaskAdded(task, 1));
        assertEquals("Noted. I've removed this task:\n"
                + "  [T][ ] read book\nNow you have 0 in the list.",
                ui.formatTaskDeleted(task, 0));

        task.mark();
        assertEquals("Nice! I've marked this task as done:\n  [T][X] read book",
                ui.formatTaskMarked(task));
        task.unmark();
        assertEquals("OK, I've marked this task as not done yet:\n  [T][ ] read book",
                ui.formatTaskUnmarked(task));
    }

    /**
     * Redirects standard output and returns its byte buffer.
     */
    private ByteArrayOutputStream captureStandardOutput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        return output;
    }
}
