package seedu.mattchatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

/**
 * Tests both of Ui's modes: the console one that prints and reads, and the
 * capturing one a window uses to collect a reply as text.
 */
public class UiTest {

    /** Builds a console Ui reading the given text, and returns what it printed. */
    private static String printedBy(String typed, Consumer<Ui> use) {
        InputStream realIn = System.in;
        PrintStream realOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
            use.accept(new Ui());
        } finally {
            System.setIn(realIn);
            System.setOut(realOut);
        }
        return printed.toString(StandardCharsets.UTF_8);
    }

    // ---------- capturing mode ----------

    @Test
    public void takeShownText_afterSeveralShows_linesCollectedInOrder() {
        Ui ui = Ui.forGui();
        ui.show("first", "second");
        ui.show("third");
        assertEquals("first" + System.lineSeparator() + "second"
                + System.lineSeparator() + "third", ui.takeShownText());
    }

    @Test
    public void takeShownText_calledTwice_secondCallSeesNothing() {
        // Each reply a window collects must cover exactly one command, so the
        // text has to be forgotten once handed over.
        Ui ui = Ui.forGui();
        ui.show("something");
        ui.takeShownText();
        assertEquals("", ui.takeShownText());
    }

    @Test
    public void showWelcome_capturingMode_bannerLeftOut() {
        // The banner is fixed-width art that only lines up in a console.
        Ui ui = Ui.forGui();
        ui.showWelcome();
        String shown = ui.takeShownText();
        assertTrue(shown.startsWith("Good day. Winston, at your service."), shown);
        assertFalse(shown.contains("IIIII"), shown);
    }

    @Test
    public void showGoodbye_capturingMode_farewellCollected() {
        Ui ui = Ui.forGui();
        ui.showGoodbye();
        assertEquals("Very good. I shall be here when you return.", ui.takeShownText());
    }

    @Test
    public void showError_capturingMode_messageCollectedAsGiven() {
        Ui ui = Ui.forGui();
        ui.showError("something went wrong");
        assertEquals("something went wrong", ui.takeShownText());
    }

    @Test
    public void hasNextCommand_capturingMode_assertionFails() {
        // A window feeds input in through getResponse; if it ever reached here
        // it would be reading standard input behind the user's back.
        Ui ui = Ui.forGui();
        assertThrows(AssertionError.class, ui::hasNextCommand);
    }

    // ---------- console mode ----------

    @Test
    public void takeShownText_consoleMode_exceptionThrown() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
                printedBy("", Ui::takeShownText));
        assertEquals("This Ui prints its replies rather than capturing them.", e.getMessage());
    }

    @Test
    public void show_consoleMode_linesPrintedBetweenDividers() {
        String printed = printedBy("", ui -> ui.show("first", "second"));
        String divider = "_".repeat(60);
        assertEquals(divider + System.lineSeparator()
                + "first" + System.lineSeparator()
                + "second" + System.lineSeparator()
                + divider + System.lineSeparator(), printed);
    }

    @Test
    public void showWelcome_consoleMode_bannerIncluded() {
        String printed = printedBy("", Ui::showWelcome);
        assertTrue(printed.contains("IIIII"), printed);
        assertTrue(printed.contains("Good day. Winston, at your service."), printed);
    }

    @Test
    public void readCommand_consoleMode_surroundingSpacesRemoved() {
        String[] read = new String[1];
        printedBy("   todo read book   " + System.lineSeparator(),
                ui -> read[0] = ui.readCommand());
        assertEquals("todo read book", read[0]);
    }

    @Test
    public void hasNextCommand_consoleMode_trueUntilInputRunsOut() {
        boolean[] seen = new boolean[2];
        printedBy("one" + System.lineSeparator(), ui -> {
            seen[0] = ui.hasNextCommand();
            ui.readCommand();
            seen[1] = ui.hasNextCommand();
        });
        assertTrue(seen[0]);
        assertFalse(seen[1]);
    }
}
