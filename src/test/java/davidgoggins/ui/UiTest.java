package davidgoggins.ui;

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
 * Tests {@link Ui}: what it prints for the text UI and what it captures for the GUI.
 *
 * <p>{@code System.out} is swapped for an in-memory stream before each test, and put back
 * afterwards, so the printed text can be compared without anything reaching the console.
 */
public class UiTest {

    /** A line break as this platform prints it, so the tests pass on Windows too. */
    private static final String NEWLINE = System.lineSeparator();

    private final ByteArrayOutputStream printed = new ByteArrayOutputStream();

    private PrintStream originalOut;

    private InputStream originalIn;

    @BeforeEach
    public void redirectConsole() {
        originalOut = System.out;
        originalIn = System.in;
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreConsole() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private String printed() {
        return printed.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void show_notCapturing_linesPrintedBetweenDividers() {
        new Ui().show(" first", " second");
        String divider = "_".repeat(60);
        assertEquals(divider + NEWLINE + " first" + NEWLINE + " second" + NEWLINE
                + divider + NEWLINE + NEWLINE, printed());
    }

    /** A chat bubble has no use for dividers or the terminal's indentation. */
    @Test
    public void show_capturing_dividersAndLeadingSpacesDropped() {
        Ui ui = new Ui();
        ui.startCapture();
        ui.show(" Here's what you signed up for:", " 1.[T][ ] read book" + NEWLINE + " 2.[T][ ] run");
        assertEquals("Here's what you signed up for:" + NEWLINE + "1.[T][ ] read book" + NEWLINE
                + "2.[T][ ] run", ui.takeCaptured());
        assertEquals("", printed());
    }

    @Test
    public void takeCaptured_nothingShown_emptyStringReturned() {
        Ui ui = new Ui();
        ui.startCapture();
        assertEquals("", ui.takeCaptured());
    }

    /** Capturing stops once the text is taken, so later replies are printed again. */
    @Test
    public void takeCaptured_thenShow_printedAgain() {
        Ui ui = new Ui();
        ui.startCapture();
        ui.takeCaptured();
        ui.show(" after");
        assertTrue(printed().contains(" after"));
    }

    @Test
    public void showError_notCapturing_prefixPrinted() {
        new Ui().showError("bad input");
        assertTrue(printed().contains(" " + Ui.ERROR_LABEL + " bad input"), printed());
    }

    /** The GUI shows the label as a tag, so it must not also appear in the text. */
    @Test
    public void showError_capturing_prefixLeftOut() {
        Ui ui = new Ui();
        ui.startCapture();
        ui.showError("bad input");
        assertEquals("bad input", ui.takeCaptured());
    }

    @Test
    public void showWarning_notCapturing_bareLinePrinted() {
        new Ui().showWarning("disk full");
        assertEquals(" Warning: disk full" + NEWLINE, printed());
    }

    @Test
    public void showWarning_capturing_labelKept() {
        Ui ui = new Ui();
        ui.startCapture();
        ui.showWarning("disk full");
        assertEquals("Warning: disk full", ui.takeCaptured());
    }

    @Test
    public void showCapturedWarnings_twoLines_eachPrintedIndented() {
        new Ui().showCapturedWarnings("Warning: one" + NEWLINE + "Warning: two");
        assertEquals(" Warning: one" + NEWLINE + " Warning: two" + NEWLINE, printed());
    }

    @Test
    public void showCapturedWarnings_emptyString_nothingPrinted() {
        new Ui().showCapturedWarnings("");
        assertEquals("", printed());
    }

    @Test
    public void readCommand_lineWithSurroundingSpaces_trimmedLineReturned() {
        System.setIn(new ByteArrayInputStream("   list   \n".getBytes(StandardCharsets.UTF_8)));
        // The Ui reads System.in when it is built, so it must be built after the swap.
        Ui ui = new Ui();
        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertFalse(ui.hasNextCommand());
        ui.close();
    }

    /** New users must be able to find the commands without guessing one first. */
    @Test
    public void getGreeting_always_pointsToHelp() {
        assertTrue(new Ui().getGreeting().contains("Type help"));
    }

    @Test
    public void showWelcome_always_bannerAndGreetingPrinted() {
        Ui ui = new Ui();
        ui.showWelcome();
        assertTrue(printed().contains("STAY HARD"), printed());
        assertTrue(printed().contains(ui.getGreeting()), printed());
    }

    @Test
    public void showFarewell_always_farewellPrinted() {
        Ui ui = new Ui();
        ui.showFarewell();
        assertTrue(printed().contains(ui.getFarewell()), printed());
    }
}
