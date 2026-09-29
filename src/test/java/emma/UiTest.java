package emma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Checks the console front end by handing it input through {@code System.in} and reading
 * back what it writes to {@code System.out}.
 *
 * <p>{@link Ui} takes its input stream when it is built, so every test sets the input up
 * before creating the Ui it tests. Both streams are put back afterwards so that one test
 * cannot affect the next.
 */
public class UiTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream printed = new ByteArrayOutputStream();

    /** Points System.in at the given lines and System.out at a buffer this test can read. */
    private Ui uiReading(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
        return new Ui();
    }

    private String printed() {
        return printed.toString(StandardCharsets.UTF_8);
    }

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void getGreeting_always_returnsEmmasOpeningLine() {
        assertEquals("Hey there! I'm Emma.\nWhat can I do for you?", Ui.getGreeting());
    }

    @Test
    public void showResponse_message_printsItUnderEmmasName() {
        uiReading("").showResponse("Got it, I've added this:\n  [T][ ] read book");
        assertTrue(printed().contains("Emma"), printed());
        assertTrue(printed().contains("Got it, I've added this:\n  [T][ ] read book"), printed());
    }

    @Test
    public void showWelcome_always_printsTheBannerAndTheGreeting() {
        uiReading("").showWelcome();
        assertTrue(printed().contains("_____"), printed());
        assertTrue(printed().contains(Ui.getGreeting()), printed());
    }

    @Test
    public void readCommand_lineTyped_returnsItWithoutTheLineBreak() {
        assertEquals("todo read book", uiReading("todo read book\n").readCommand());
    }

    @Test
    public void readCommand_severalLines_returnsThemInOrder() {
        Ui ui = uiReading("todo read book\nlist\nbye\n");
        assertEquals("todo read book", ui.readCommand());
        assertEquals("list", ui.readCommand());
        assertEquals("bye", ui.readCommand());
    }

    @Test
    public void readCommand_noMoreInput_returnsNull() {
        assertNull(uiReading("").readCommand());
    }

    @Test
    public void readCommand_afterTheLastLine_returnsNull() {
        Ui ui = uiReading("list\n");
        assertEquals("list", ui.readCommand());
        assertNull(ui.readCommand());
    }

    @Test
    public void readCommand_blankLine_isReturnedAsAnEmptyString() {
        assertEquals("", uiReading("\nlist\n").readCommand());
    }

    @Test
    public void readCommand_always_promptsUnderTheUsersName() {
        uiReading("list\n").readCommand();
        assertTrue(printed().contains("user"), printed());
    }

    @Test
    public void close_afterReading_doesNotThrow() {
        Ui ui = uiReading("list\n");
        ui.readCommand();
        ui.close();
    }
}
