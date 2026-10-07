package mary.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Verifies the welcome banner and message routing independently of task commands.
 */
class UiTest {
    /**
     * Checks that an empty varargs call emits nothing.
     */
    @Test
    void showMessages_noArguments_emitsNothing() {
        List<String> output = new ArrayList<>();
        new Ui(output::add, false).showMessages();
        assertTrue(output.isEmpty());
    }

    /**
     * Checks that one argument is delivered exactly once without modification.
     */
    @Test
    void showMessages_oneArgument_preservesMessage() {
        List<String> output = new ArrayList<>();
        new Ui(output::add, false).showMessages("   read café book  ");
        assertEquals(List.of("   read café book  "), output);
    }

    /**
     * Checks message order, duplicates, blank lines, and embedded newlines.
     */
    @Test
    void showMessages_multipleArguments_preservesOrderAndFormatting() {
        List<String> output = new ArrayList<>();
        new Ui(output::add, false).showMessages("first", "", "  second\nline  ", "first");
        assertEquals(List.of("first", "", "  second\nline  ", "first"), output);
    }

    /**
     * Checks that callers can pass an existing array without it being changed.
     */
    @Test
    void showMessages_arrayArgument_preservesArrayAndOutput() {
        List<String> output = new ArrayList<>();
        String[] messages = {"first", "second"};
        new Ui(output::add, false).showMessages(messages);
        assertEquals(List.of("first", "second"), output);
        assertEquals(List.of("first", "second"), List.of(messages));
    }

    /**
     * Checks the cat artwork, greeting, and separators without non-ASCII characters.
     */
    @Test
    void showWelcome_catBanner_printsExactAsciiGreeting() {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOutput = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOutput);
            new Ui().showWelcome();
        } finally {
            System.setOut(originalOutput);
        }

        String actual = output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        String separator = "____________________________________________________________\n";
        String expected = separator
                + " /\\_/\\\n"
                + "( o.o )   M A R Y\n"
                + " > ^ <    Your purr-sonal task assistant.\n"
                + "\n"
                + "What's on your list today?\n"
                + separator;
        assertEquals(expected, actual);
        assertTrue(actual.chars().allMatch(character -> character < 128));
    }
}
