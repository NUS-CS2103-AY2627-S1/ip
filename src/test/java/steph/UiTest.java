package steph;

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
 * Unit tests for {@link Ui}. {@code Ui} reads {@code System.in} and writes
 * {@code System.out}, so each test swaps both for in-memory streams and puts
 * the originals back afterwards.
 */
public class UiTest {

    private static final String LINE = "____________________________________________________________";

    private InputStream originalIn;
    private PrintStream originalOut;
    private ByteArrayOutputStream captured;

    @BeforeEach
    public void redirectOut() {
        originalIn = System.in;
        originalOut = System.out;
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void hasNextCommand_inputAvailable_returnsTrue() {
        assertTrue(newUi("list\n").hasNextCommand());
    }

    @Test
    public void hasNextCommand_noInput_returnsFalse() {
        assertFalse(newUi("").hasNextCommand());
    }

    @Test
    public void readCommand_paddedLine_returnsTrimmedLine() {
        assertEquals("todo read", newUi("   todo read  \n").readCommand());
    }

    @Test
    public void readCommand_consecutiveLines_returnedInOrder() {
        Ui ui = newUi("one\ntwo\n");

        assertEquals("one", ui.readCommand());
        assertEquals("two", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void showMessage_multiLineText_framedByRules() {
        newUi("").showMessage("first\nsecond");

        assertEquals(LINE + "\nfirst\nsecond\n" + LINE + "\n", output());
    }

    @Test
    public void showError_sameFrameAsShowMessage() {
        newUi("").showError("oops");

        assertEquals(LINE + "\noops\n" + LINE + "\n", output());
    }

    @Test
    public void showLoadingError_mentionsEmptyList() {
        newUi("").showLoadingError();

        assertEquals(LINE + "\nSorry, I couldn't read the save file. Starting with an empty list.\n" + LINE + "\n",
                output());
    }

    @Test
    public void showWelcome_containsGreetingAndFraming() {
        newUi("").showWelcome();

        String out = output();
        assertTrue(out.startsWith(LINE + "\n"), out);
        assertTrue(out.contains("Hello! I'm Steph.\nGlad to see you!\nWhat can I help you with?\n"), out);
        assertTrue(out.endsWith(LINE + "\n"), out);
    }

    @Test
    public void showGoodbye_containsFarewellAndFraming() {
        newUi("").showGoodbye();

        assertEquals(LINE + "\nGoodbye. Hope to see you again soon!\n" + LINE + "\n", output());
    }

    @Test
    public void close_doesNotThrow() {
        newUi("").close();
    }

    private Ui newUi(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new Ui();
    }

    /** Returns everything printed so far, with line endings normalised to "\n". */
    private String output() {
        return captured.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
