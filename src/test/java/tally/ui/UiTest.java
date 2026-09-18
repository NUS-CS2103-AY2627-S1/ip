package tally.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the half of Ui that only a terminal reaches.
 *
 * <p>The console suite drives these lines through the whole program, which shows they
 * work but not what happens at their edges. Standard input and output are stood in for
 * here so the reading and the printing can be asked about directly.
 */
public class UiTest {
    private final InputStream realInput = System.in;
    private final PrintStream realOutput = System.out;
    private ByteArrayOutputStream printed;

    @AfterEach
    public void putTheStreamsBack() {
        System.setIn(realInput);
        System.setOut(realOutput);
    }

    /**
     * Stands in for standard input and output, and returns a Ui reading the one and
     * printing to the other.
     *
     * <p>The streams stay replaced until the test ends, when putTheStreamsBack puts the
     * real ones back.
     *
     * @param typed what the user is to have typed.
     * @return a console Ui reading it.
     */
    private Ui replaceConsoleWith(String typed) {
        System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
        // The scanner is taken at construction, so the input has to be in place first.
        return new Ui(true);
    }

    /**
     * Returns everything printed to the stood-in output since the test began.
     *
     * @return what the console would have shown.
     */
    private String readPrinted() {
        return printed.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void readCommand_linesTyped_areReadInOrderAndTrimmed() {
        Ui ui = replaceConsoleWith("  list  \ntodo read book\n");
        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertEquals("todo read book", ui.readCommand());
        assertFalse(ui.hasNextCommand(), "the input had only two lines");
    }

    @Test
    public void show_onTheConsole_fencesTheMessageBetweenRules() {
        Ui ui = replaceConsoleWith("");
        ui.show("first", "second");
        String[] lines = readPrinted().split(System.lineSeparator());
        assertEquals(4, lines.length, readPrinted());
        assertTrue(lines[0].startsWith("____"), lines[0]);
        assertEquals("first", lines[1]);
        assertEquals("second", lines[2]);
        assertTrue(lines[3].startsWith("____"), lines[3]);
    }

    @Test
    public void show_nothingToSay_isRefused() {
        Ui ui = replaceConsoleWith("");
        // A message of no lines would print a pair of rules with a gap between them.
        assertThrows(AssertionError.class, ui::show);
    }

    @Test
    public void showWelcome_console_carriesTheBannerAndTheWindowDoesNot() {
        Ui console = replaceConsoleWith("");
        console.showWelcome();
        assertTrue(readPrinted().contains("|_   _|_ _| | |_"), "the console lost its banner");
        assertTrue(readPrinted().contains("Tally."));

        Ui window = new Ui(false);
        window.showWelcome();
        String reply = window.takePendingResponse();
        assertFalse(reply.contains("|_   _|_ _| | |_"), "the window drew the banner: " + reply);
        assertTrue(reply.contains("Tally."), reply);
    }

    @Test
    public void takePendingResponse_saidTwice_returnsOnlyWhatIsNew() {
        Ui window = new Ui(false);
        window.show("first");
        assertEquals("first", window.takePendingResponse());
        window.show("second");
        assertEquals("second", window.takePendingResponse());
    }

    @Test
    public void readingCommands_onAWindowUi_isRefusedRatherThanMeetingANullScanner() {
        Ui window = new Ui(false);
        assertThrows(IllegalStateException.class, window::hasNextCommand);
        assertThrows(IllegalStateException.class, window::readCommand);
        assertThrows(IllegalStateException.class, window::close);
    }

    @Test
    public void close_afterReading_stopsTakingInput() {
        Ui ui = replaceConsoleWith("list\n");
        assertEquals("list", ui.readCommand());
        ui.close();
        // Reading on past a closed scanner is a programming error, not a quiet false.
        assertThrows(IllegalStateException.class, ui::hasNextCommand);
    }
}
