package kenbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Ui}, the only class that touches the console.
 *
 * <p>Both streams are swapped for in-memory ones so a test can type at the
 * program and read what it printed. The originals are put back after each test,
 * since leaving them replaced would silence the test runner itself.</p>
 */
public class UiTest {

    private final PrintStream realOut = System.out;
    private final InputStream realIn = System.in;
    private ByteArrayOutputStream printed;

    /** Redirects output, and input when the test supplies some. */
    private Ui uiReading(String typed) {
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
        System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
        // Constructed after the swap: Ui takes System.in when it is created.
        return new Ui();
    }

    private String output() {
        return printed.toString(StandardCharsets.UTF_8);
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(realOut);
        System.setIn(realIn);
    }

    @Test
    public void getGreeting_always_introducesKenbotWithoutTheBanner() {
        String greeting = uiReading("").getGreeting();
        assertTrue(greeting.contains("Kenbot"));
        assertFalse(greeting.contains("_"), "the ASCII art belongs only in the console banner");
    }

    @Test
    public void showGreeting_always_printsTheBannerAndTheWelcome() {
        uiReading("").showGreeting();
        assertTrue(output().contains("Kenbot"));
        assertTrue(output().contains("What are we getting done today?"));
    }

    @Test
    public void show_someText_wrapsItInSeparatorLines() {
        uiReading("").show("hello");
        String[] lines = output().strip().split("\\R");
        assertEquals(3, lines.length, "a block is a line, the body, and a line");
        assertEquals(lines[0], lines[2], "both separators must match");
        assertEquals("hello", lines[1]);
    }

    @Test
    public void show_textWithNewlines_keepsThemInsideOneBlock() {
        uiReading("").show("first\nsecond");
        assertTrue(output().contains("first\nsecond"));
    }

    @Test
    public void showError_someText_looksLikeAnOrdinaryBlockForNow() {
        // Kept separate from show() as a seam, so one can later be made to look
        // different without hunting through every place that prints.
        Ui ui = uiReading("");
        ui.showError("something went wrong");
        assertTrue(output().contains("something went wrong"));
    }

    @Test
    public void hasNextCommand_inputWaiting_isTrue() {
        assertTrue(uiReading("list\n").hasNextCommand());
    }

    @Test
    public void hasNextCommand_noInputLeft_isFalse() {
        assertFalse(uiReading("").hasNextCommand());
    }

    @Test
    public void readCommand_aLine_returnsItTrimmed() {
        assertEquals("list", uiReading("   list   \n").readCommand());
    }

    @Test
    public void readCommand_severalLines_returnsThemInOrder() {
        Ui ui = uiReading("first\nsecond\n");
        assertEquals("first", ui.readCommand());
        assertEquals("second", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void readCommand_blankLine_returnsAnEmptyString() {
        assertEquals("", uiReading("   \n").readCommand());
    }
}
