package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for Ui's console formatting and input/output handling.
 */
public class UiTest {
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    public void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    public void styleString_wrapsTextInSpeechBubbleBorders() {
        Ui ui = new Ui();
        String result = ui.styleString("hello");

        assertEquals(
                "\t______________________________________________\n"
                        + "\thello\n"
                        + "\t______________________________________________",
                result);
    }

    @Test
    public void styleString_indentsEachLineOfMultilineText() {
        Ui ui = new Ui();
        String result = ui.styleString("line one\nline two");

        assertEquals(
                "\t______________________________________________\n"
                        + "\tline one\n"
                        + "\tline two\n"
                        + "\t______________________________________________",
                result);
    }

    @Test
    public void echo_printsStyledTextAndReturnsIt() {
        ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputBuffer, true, StandardCharsets.UTF_8));

        Ui ui = new Ui();
        String result = ui.echo("hi");

        assertEquals(result + System.lineSeparator(), outputBuffer.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void readCommand_returnsNextLineFromInput() {
        System.setIn(new ByteArrayInputStream("todo read book\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertEquals("todo read book", ui.readCommand());
    }

    @Test
    public void readCommand_returnsPlaceholderWhenNoInput() {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        Ui ui = new Ui();

        assertEquals("No input detected, closing", ui.readCommand());
    }
}
