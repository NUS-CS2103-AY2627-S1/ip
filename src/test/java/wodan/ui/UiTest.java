package wodan.ui;

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
import org.junit.jupiter.api.parallel.Resources;

import wodan.task.Todo;

/**
 * Unit tests for console {@link Ui} input and output.
 */
public class UiTest {
    @Test
    public void formatTaskCount_oneAndMany_singularThenPlural() {
        assertEquals("0 tasks", Ui.formatTaskCount(0));
        assertEquals("1 task", Ui.formatTaskCount(1));
        assertEquals("2 tasks", Ui.formatTaskCount(2));
    }

    @Test
    public void getGreetingText_twoHailLines() {
        assertEquals(
                "Hail, wanderer. Wodan is listening.\nWhat is your command?",
                Ui.getGreetingText());
    }

    @Test
    public void showWelcome_includesBannerAndHail() {
        String output = captured(ui -> ui.showWelcome());
        assertTrue(output.contains("Wodan is listening."));
        assertTrue(output.contains("What is your command?"));
        assertTrue(output.contains("__"));
    }

    @Test
    public void showErrorAndGoodbyeAndLine_printIndentedText() {
        String output = captured(ui -> {
            ui.showError("That rune is unknown.");
            ui.showGoodbye();
            ui.showLine();
        });
        assertTrue(output.contains("     That rune is unknown."));
        assertTrue(output.contains("     So it is written. Farewell, wanderer."));
        assertTrue(output.contains("____________________________________________________________"));
    }

    @Test
    public void show_zeroLines_printsNothing() {
        assertEquals("", captured(ui -> ui.show()));
    }

    @Test
    public void showNumberedTask_oneBasedIndex() {
        String output = captured(ui -> ui.showNumberedTask(2, new Todo("borrow book")));
        assertEquals("     2. [T][ ] borrow book\n", output.replace("\r\n", "\n"));
    }

    @Test
    @ResourceLock(Resources.SYSTEM_OUT)
    @ResourceLock("system.in")
    public void hasCommandAndReadCommand_oneLineThenEof() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(
                "todo borrow book\n".getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
        try {
            Ui ui = new Ui();
            assertTrue(ui.hasCommand());
            assertEquals("todo borrow book", ui.readCommand());
            assertFalse(ui.hasCommand());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    private String captured(UiAction action) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        action.run(new Ui(stream));
        return buffer.toString(StandardCharsets.UTF_8);
    }

    /**
     * An action that writes through a {@link Ui}.
     */
    private interface UiAction {
        void run(Ui ui);
    }
}
