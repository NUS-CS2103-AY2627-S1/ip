package fifi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests console input and output helpers used by the chatbot.
 */
public class UiTest {
    private final PrintStream originalOut = System.out;

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    public void readCommand_oneInputLine_inputLineReturned() {
        System.setIn(new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertEquals("list", ui.readCommand());
    }

    @Test
    public void showWelcome_noInput_bannerAndGreetingPrinted() {
        ByteArrayOutputStream output = replaceSystemOut();
        Ui ui = new Ui();

        ui.showWelcome();

        assertEquals(normalizeLineEndings("""
                _____ _  __ __
                |  ___(_)/ _(_)
                | |_  | | |_| |
                |  _| | |  _| |
                |_|   |_|_| |_|
                ____________________________________________________________
                Hello! My name is Fifi ^^
                How may I help?
                ____________________________________________________________
                """), normalizeLineEndings(output.toString(StandardCharsets.UTF_8)));
    }

    @Test
    public void showResponse_message_messagePrintedBetweenLines() {
        ByteArrayOutputStream output = replaceSystemOut();
        Ui ui = new Ui();

        ui.showResponse("Hello");

        assertEquals("""
                ____________________________________________________________
                Hello
                ____________________________________________________________
                """.replace("\n", System.lineSeparator()),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void showResponse_multipleMessages_messagesPrintedOnSeparateLines() {
        ByteArrayOutputStream output = replaceSystemOut();
        Ui ui = new Ui();

        ui.showResponse("First line", "Second line", "Third line");

        assertEquals("""
                ____________________________________________________________
                First line
                Second line
                Third line
                ____________________________________________________________
                """.replace("\n", System.lineSeparator()),
                output.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void showError_thenNormalResponse_onlyErrorHasHeading() {
        ByteArrayOutputStream output = replaceSystemOut();
        Ui ui = new Ui();

        ui.showError("Oops! Please use yyyy-MM-dd for dates.");
        ui.showResponse("Here are the tasks in your list:");

        assertEquals(normalizeLineEndings("""
                ____________________________________________________________
                [ERROR]
                Oops! Please use yyyy-MM-dd for dates.
                ____________________________________________________________
                ____________________________________________________________
                Here are the tasks in your list:
                ____________________________________________________________
                """), normalizeLineEndings(output.toString(StandardCharsets.UTF_8)));
    }

    private ByteArrayOutputStream replaceSystemOut() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        return output;
    }

    private String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n");
    }
}
