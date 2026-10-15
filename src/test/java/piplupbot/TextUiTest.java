package piplupbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TextUi}, the console face: how a reply is laid out, and how a
 * typed line is read.
 *
 * <p>The text-UI test plan already checks whole conversations in the console.
 * These cases check the pieces those conversations are built from, and unlike
 * the plan, they also run on GitHub, with every other JUnit test.</p>
 *
 * <p>The console prints through {@code System.out} and reads through
 * {@code System.in}, which every test in the run shares. Each case swaps in a
 * stream of its own only for as long as it needs one, and always puts the
 * original back, so that no later test is left printing into this one.</p>
 */
public class TextUiTest {

    /** The line printed above and below every reply: four spaces, then sixty underscores. */
    private static final String DIVIDER = "    " + "_".repeat(60);

    /**
     * Returns what the given action prints while it runs.
     * Line breaks come back as {@code \n} on every operating system, so that
     * the expected text can be written the same way everywhere.
     *
     * @param action What to run.
     * @return Everything the action printed.
     */
    private static String capturePrintedOutput(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return printed.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
    }

    /**
     * Creates a console that reads the given text, as though the user had typed
     * it. A {@link TextUi} takes hold of {@code System.in} when it is created,
     * so the stream only needs to be swapped while the console is being made.
     *
     * @param typed Everything the user types, with a line break after each line.
     * @return A console that reads that text.
     */
    private static TextUi createConsoleReading(String typed) {
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
        try {
            return new TextUi();
        } finally {
            System.setIn(originalIn);
        }
    }

    // ---------- Showing a reply ----------

    /**
     * A reply sits between two dividers, with every line indented, and a blank
     * line after it separates it from whatever the user types next.
     */
    @Test
    public void show_twoLines_printsThemIndentedBetweenDividers() {
        TextUi console = new TextUi();

        String printed = capturePrintedOutput(() -> console.show("first line", "second line"));

        assertEquals(DIVIDER + "\n"
                + "     first line\n"
                + "     second line\n"
                + DIVIDER + "\n"
                + "\n", printed);
    }

    /**
     * The banner is drawn first, and the greeting both faces share comes after
     * it. Checking that the output ends with that greeting, with something
     * printed before it, checks the order without copying the banner's drawing
     * into this file.
     */
    @Test
    public void showWelcome_console_drawsBannerThenGreets() {
        TextUi console = new TextUi();
        String greeting = capturePrintedOutput(() ->
                console.show("Hello! I'm PiplupBot. Pip-pip!", "What can I do for you?"));

        String printed = capturePrintedOutput(console::showWelcome);

        assertTrue(printed.endsWith(greeting),
                "Expected the greeting last, but the console printed:\n" + printed);
        String banner = printed.substring(0, printed.length() - greeting.length());
        assertFalse(banner.isBlank(), "Expected a banner before the greeting");
    }

    // ---------- Reading what the user types ----------

    /**
     * Each line comes back without the spaces around it. A line with nothing on
     * it comes back empty rather than being skipped, because what a blank line
     * means is for the bot to decide.
     */
    @Test
    public void readCommand_linesWithSurroundingSpaces_returnsThemTrimmedInOrder() {
        TextUi console = createConsoleReading("  todo read book  \n\nbye\n");

        assertEquals("todo read book", console.readCommand());
        assertEquals("", console.readCommand());
        assertEquals("bye", console.readCommand());
    }

    /**
     * Once every line has been read there is nothing left to read, which is
     * how the conversation ends when the input runs out without a {@code bye}.
     * A last line with no line break after it still counts as a line.
     */
    @Test
    public void hasNextCommand_everyLineRead_false() {
        TextUi console = createConsoleReading("list\nbye");

        assertTrue(console.hasNextCommand());
        assertEquals("list", console.readCommand());
        assertTrue(console.hasNextCommand());
        assertEquals("bye", console.readCommand());
        assertFalse(console.hasNextCommand());
    }
}
