package baemax;

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
 * Tests for {@link Ui}, which reads commands from and prints replies to the
 * console. System.in/System.out are redirected for each test and restored
 * afterwards, since Ui talks to them directly.
 */
public class UiTest {
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    public void redirectOut() {
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    /** Creates a Ui whose input is the given text, one command per line. */
    private Ui uiReading(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        return new Ui();
    }

    @Test
    public void readCommand_returnsEachLineInOrder() {
        Ui ui = uiReading("todo a\nlist\n");
        assertTrue(ui.hasNextCommand());
        assertEquals("todo a", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    public void hasNextCommand_falseOnEmptyInput() {
        assertFalse(uiReading("").hasNextCommand());
    }

    @Test
    public void show_printsEachBlockOnItsOwnLine() {
        uiReading("").show("first", "second");
        assertEquals("first" + System.lineSeparator() + "second" + System.lineSeparator(),
                capturedOut.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void showLine_printsTheDivider() {
        uiReading("").showLine();
        assertEquals("__________________________________________" + System.lineSeparator(),
                capturedOut.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void showBanner_mentionsBaemaxWithoutThrowing() {
        uiReading("").showBanner();
        assertTrue(capturedOut.toString(StandardCharsets.UTF_8).contains("Baemax"));
    }

    @Test
    public void close_doesNotThrow() {
        uiReading("").close();
    }
}
