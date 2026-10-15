package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

/**
 * Tests messages displayed by the user interface.
 */
@ResourceLock(Resources.SYSTEM_OUT)
public class UiTest {

    @Test
    public void helpMessage_containsAllCommandSyntaxes() {
        String helpMessage = Ui.Message.HELP.getMessage();
        String[] commandSyntaxes = {
            "todo <task name>",
            "deadline <task name> /by <YYYY-MM-DD>",
            "event <task name> /from <YYYY-MM-DD> /to <YYYY-MM-DD>",
            "list",
            "find <keyword>",
            "mark <task number>",
            "unmark <task number>",
            "delete <task number>",
            "help",
            "bye"
        };

        for (String commandSyntax : commandSyntaxes) {
            assertTrue(helpMessage.contains("  " + commandSyntax + "\n"),
                    "Help message is missing command syntax: " + commandSyntax);
        }
    }

    @Test
    public void printDashLine_called_printsThirtyUnderscores() {
        assertEquals("______________________________\n", captureOutput(Ui::printDashLine));
    }

    @Test
    public void printMessage_stringAndEnum_printsMessageAndDivider() {
        assertEquals("hello\nworld\n______________________________\n",
                captureOutput(() -> Ui.printMessage("hello\nworld")));
        assertEquals("Goodbye. See you soon!\n______________________________\n",
                captureOutput(() -> Ui.printMessage(Ui.Message.GOODBYE)));
    }

    @Test
    public void welcome_called_printsBannerAndGreetingBetweenDividers() {
        String output = captureOutput(Ui::welcome);
        assertEquals("Hello! I am Ubis.\nWhat can I do for you?", Ui.getWelcomeMessage());
        assertTrue(output.startsWith("______________________________\n _   _ ____ ___ ____"));
        assertTrue(output.endsWith(Ui.getWelcomeMessage() + "\n______________________________\n"));
    }

    /**
     * Captures console text and always restores the original output stream.
     */
    private String captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream replacement = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(replacement);
            action.run();
            return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        } finally {
            System.setOut(original);
        }
    }
}
