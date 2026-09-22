package lebron.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Ui}: the thin {@code System.in}/{@code System.out}
 * wrapper the console front end reads from and prints through. Both streams
 * are swapped out for in-memory ones before each test (via {@link
 * #newUiReading(String)}) and restored afterward, so these tests never touch
 * the real terminal.
 */
public class UiTest {
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    private ByteArrayOutputStream capturedOut;

    private Ui newUiReading(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
        return new Ui();
    }

    @AfterEach
    public void restoreSystemStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void showWelcome_printsBannerAndGreeting() {
        Ui ui = newUiReading("");
        ui.showWelcome();
        String output = capturedOut.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hey, I'm Lebron, your coach for today."));
        assertTrue(output.contains("What's the game plan?"));
    }

    @Test
    public void readCommand_returnsTheLineTyped() {
        Ui ui = newUiReading("list\n");
        assertEquals("list", ui.readCommand());
    }

    @Test
    public void readCommand_calledTwice_returnsSuccessiveLinesInOrder() {
        Ui ui = newUiReading("todo read book\nbye\n");
        assertEquals("todo read book", ui.readCommand());
        assertEquals("bye", ui.readCommand());
    }

    @Test
    public void showMessage_printsGivenTextFollowedByNewline() {
        Ui ui = newUiReading("");
        ui.showMessage("on the board: read book");
        assertEquals("on the board: read book" + System.lineSeparator(),
                capturedOut.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void showLine_printsOnlyTheDivider() {
        Ui ui = newUiReading("");
        ui.showLine();
        assertEquals("____________________________________________________________" + System.lineSeparator(),
                capturedOut.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void close_onFreshScanner_doesNotThrow() {
        Ui ui = newUiReading("");
        ui.close();
    }
}
