package goat.ui;

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
 * Tests the shape of what Goat prints and how it reads a line.
 *
 * The layout matters more than it looks: text-ui-test compares Goat's whole
 * output against a saved file byte for byte, so a stray space or a missing
 * divider would fail that suite with a diff that takes a while to read. These
 * tests fail immediately and say which part is wrong.
 *
 * Ui takes System.in and System.out as it finds them when it is built, so each
 * test swaps in its own and puts the originals back afterwards.
 */
public class UiTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /** Builds a Ui reading the given text, and captures everything it prints. */
    private static ByteArrayOutputStream capture() {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
        return captured;
    }

    private static Ui uiReading(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new Ui();
    }

    @Test
    public void show_oneLine_wrappedInDividersAndIndented() {
        ByteArrayOutputStream captured = capture();
        Ui ui = uiReading("");

        ui.show("hello");

        // println ends a line with whatever the platform uses, so the expected
        // text has to as well, or this passes on Unix and fails on Windows.
        String newLine = System.lineSeparator();
        String divider = "    " + "_".repeat(60);
        assertEquals(divider + newLine
                + "     hello" + newLine
                + divider + newLine
                + newLine, captured.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void show_severalLines_sharesOnePairOfDividers() {
        ByteArrayOutputStream captured = capture();
        Ui ui = uiReading("");

        ui.show("first", "second", "third");

        String output = captured.toString(StandardCharsets.UTF_8);
        // A task listing spans many lines but is still one reply.
        assertEquals(2, output.split("_".repeat(60), -1).length - 1);
        String newLine = System.lineSeparator();
        assertTrue(output.contains("     first" + newLine + "     second" + newLine
                + "     third" + newLine), output);
    }

    @Test
    public void show_noLines_stillPrintsBothDividers() {
        ByteArrayOutputStream captured = capture();
        Ui ui = uiReading("");

        ui.show();

        assertEquals(2, captured.toString(StandardCharsets.UTF_8)
                .split("_".repeat(60), -1).length - 1);
    }

    @Test
    public void showError_message_prefixedWithOops() {
        ByteArrayOutputStream captured = capture();
        Ui ui = uiReading("");

        ui.showError("something went wrong");

        assertTrue(captured.toString(StandardCharsets.UTF_8)
                .contains("     Hm. something went wrong"), captured.toString());
    }

    @Test
    public void showLoadingError_message_addsTheReassurance() {
        ByteArrayOutputStream captured = capture();
        Ui ui = uiReading("");

        ui.showLoadingError("the file is damaged");

        String output = captured.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Hm. the file is damaged"), output);
        assertTrue(output.contains("Starting with an empty list"), output);
    }

    @Test
    public void showWelcome_always_printsBannerThenGreeting() {
        ByteArrayOutputStream captured = capture();
        Ui ui = uiReading("");

        ui.showWelcome();

        String output = captured.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Goat here."), output);
        assertTrue(output.contains("I keep the list."), output);
        // The banner is outside the dividers, so it comes before the first one.
        assertTrue(output.indexOf("____   ___") < output.indexOf("_".repeat(60)), output);
    }

    @Test
    public void showGoodbye_always_printsTheFarewell() {
        ByteArrayOutputStream captured = capture();
        Ui ui = uiReading("");

        ui.showGoodbye();

        assertTrue(captured.toString(StandardCharsets.UTF_8)
                .contains("Off up the hill."), captured.toString());
    }

    @Test
    public void takeResponse_afterOneReply_returnsItsLinesWithoutTheFrame() {
        capture();
        Ui ui = uiReading("");

        ui.show("first", "second");

        // A GUI draws its own frame, so the dividers and indent are dropped.
        assertEquals("first" + System.lineSeparator() + "second", ui.takeResponse());
    }

    @Test
    public void takeResponse_calledTwice_secondCallIsEmpty() {
        capture();
        Ui ui = uiReading("");
        ui.show("hello");

        ui.takeResponse();

        // The buffer is emptied, so a reply is never shown twice.
        assertEquals("", ui.takeResponse());
    }

    @Test
    public void takeResponse_severalReplies_collectsThemAll() {
        capture();
        Ui ui = uiReading("");

        ui.show("one");
        ui.showError("two");

        String response = ui.takeResponse();
        assertTrue(response.contains("one"), response);
        assertTrue(response.contains("Hm. two"), response);
    }

    @Test
    public void forGui_show_printsNothing() {
        ByteArrayOutputStream captured = capture();
        Ui ui = Ui.forGui();

        ui.show("hello");

        // Nothing reaches the terminal; the caller reads takeResponse instead.
        assertEquals("", captured.toString(StandardCharsets.UTF_8));
        assertEquals("hello", ui.takeResponse());
    }

    @Test
    public void forGui_showWelcome_omitsTheAsciiBanner() {
        capture();
        Ui ui = Ui.forGui();

        ui.showWelcome();

        assertFalse(ui.takeResponse().contains("____"));
    }

    @Test
    public void forGui_hasNextCommand_falseWithoutTouchingStandardInput() {
        capture();
        System.setIn(new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = Ui.forGui();

        // A GUI never reads standard input, so it must not claim the stream.
        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void forGui_close_doesNotThrow() {
        capture();
        Ui.forGui().close();
    }

    @Test
    public void readCommand_lineWithSurroundingSpaces_trimmed() {
        capture();
        Ui ui = uiReading("   todo read book   \n");

        assertEquals("todo read book", ui.readCommand());
    }

    @Test
    public void hasNextCommand_inputRemaining_true() {
        capture();
        Ui ui = uiReading("list\nbye\n");

        assertTrue(ui.hasNextCommand());
        ui.readCommand();
        assertTrue(ui.hasNextCommand());
    }

    @Test
    public void hasNextCommand_inputExhausted_false() {
        capture();
        Ui ui = uiReading("list\n");

        ui.readCommand();

        // This is what lets Goat stop when piped commands run out without "bye".
        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void hasNextCommand_noInputAtAll_false() {
        capture();
        assertFalse(uiReading("").hasNextCommand());
    }

    @Test
    public void readCommand_blankLine_returnsEmptyString() {
        capture();
        Ui ui = uiReading("\nbye\n");

        // Parser turns this into "unknown command", so Ui must not swallow it.
        assertEquals("", ui.readCommand());
    }
}
