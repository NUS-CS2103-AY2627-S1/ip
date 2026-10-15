package ev.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ev.task.Todo;

public class ConsoleUiTest {

    private static final String DIVIDER = "____________________________________________________________";

    private final PrintStream realOut = System.out;
    private ByteArrayOutputStream printed;

    @BeforeEach
    public void captureOutput() {
        printed = new ByteArrayOutputStream();
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreOutput() {
        System.setOut(realOut);
    }

    private String[] printedLines() {
        return printed.toString(StandardCharsets.UTF_8).split(System.lineSeparator());
    }

    @Test
    public void show_anyReply_printedBetweenDividers() {
        new ConsoleUi().showFarewell();

        String[] lines = printedLines();
        assertEquals(DIVIDER, lines[0]);
        assertEquals("Signing off, Peter.", lines[1]);
        assertEquals(DIVIDER, lines[2]);
    }

    @Test
    public void show_anyReply_alsoRecordedForTakeResponse() {
        ConsoleUi ui = new ConsoleUi();
        ui.showUpdated(new Todo("read book"));
        assertEquals("Updated.\n  [T][ ] read book", ui.takeResponse());
    }

    @Test
    public void showBanner_printsTheLogo() {
        new ConsoleUi().showBanner();
        assertTrue(printed.toString(StandardCharsets.UTF_8).contains("|_______|"));
    }
}
