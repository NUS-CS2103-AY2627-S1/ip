package serangooner.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ConsoleTest {
    private static final String DIVIDER = "━━━ . °‧ 𓆝 𓆟 𓆞 ·｡";

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private Console consoleReading(String input) {
        return new Console(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private String printed() {
        return output.toString(StandardCharsets.UTF_8);
    }

    private List<String> printedLines() {
        String printed = printed();
        return printed.isEmpty() ? List.of() : List.of(printed.split(System.lineSeparator(), -1));
    }

    @Test
    public void readCommand_severalLines_returnsThemInTurn() {
        Console console = consoleReading("list" + System.lineSeparator()
                + "bye" + System.lineSeparator());

        assertTrue(console.hasNextCommand());
        assertEquals("list", console.readCommand());
        assertEquals("bye", console.readCommand());
        assertFalse(console.hasNextCommand());
    }

    @Test
    public void hasNextCommand_noInput_returnsFalse() {
        assertFalse(consoleReading("").hasNextCommand());
    }

    @Test
    public void hasNextCommand_inputAvailable_returnsTrue() {
        assertTrue(consoleReading("list" + System.lineSeparator()).hasNextCommand());
    }

    @Test
    public void hasNextCommand_lastLineAlreadyRead_returnsFalse() {
        Console console = consoleReading("list" + System.lineSeparator());
        console.readCommand();
        assertFalse(console.hasNextCommand());
    }

    @Test
    public void readCommand_blankLine_returnsItUnchanged() {
        assertEquals("", consoleReading(System.lineSeparator()).readCommand());
    }

    @Test
    public void readCommand_lineWithSurroundingSpaces_leavesThemForTheParser() {
        assertEquals("  list  ",
                consoleReading("  list  " + System.lineSeparator()).readCommand());
    }

    @Test
    public void show_anyMessage_printsItAlone() {
        consoleReading("").show("added todo: [T][ ] read book");
        assertEquals("added todo: [T][ ] read book" + System.lineSeparator(), printed());
    }

    @Test
    public void showError_anyMessage_printsItAlone() {
        consoleReading("").showError("invalid command :/");
        assertEquals("invalid command :/" + System.lineSeparator(), printed());
    }

    @Test
    public void showDivider_always_drawsTheDividerAlone() {
        consoleReading("").showDivider();
        assertEquals(List.of(DIVIDER, ""), printedLines());
    }

    @Test
    public void showBanner_always_namesTheChatbotAboveADivider() {
        consoleReading("").showBanner();

        List<String> lines = printedLines();
        assertTrue(lines.get(0).contains("____"));
        assertEquals(DIVIDER, lines.get(lines.size() - 2));
    }

    @Test
    public void showBlock_message_printsItAboveADivider() {
        consoleReading("").showBlock("loaded 3 task(s) from your last visit");
        assertEquals(List.of("loaded 3 task(s) from your last visit", DIVIDER, ""),
                printedLines());
    }

    @Test
    public void showBlock_emptyMessage_printsNothingAtAll() {
        consoleReading("").showBlock("");
        assertEquals("", printed());
    }

    @Test
    public void readCommand_windowsLineEndings_returnsLinesWithoutCarriageReturns() {
        Console console = consoleReading("list\r\nbye\r\n");

        assertEquals("list", console.readCommand());
        assertEquals("bye", console.readCommand());
        assertFalse(console.hasNextCommand());
    }
}
