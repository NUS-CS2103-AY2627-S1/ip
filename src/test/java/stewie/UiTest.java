package stewie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import stewie.model.ToDo;
import stewie.ui.cli.Ui;

/**
 * Verifies console presentation, singular and plural task counts, and the standalone echo loop.
 */
@ResourceLock("java.lang.System.in")
@ResourceLock(Resources.SYSTEM_OUT)
public class UiTest {
    /**
     * Verifies singular and plural confirmations and completed task display.
     */
    @Test
    public void printConfirmation_formatsCountsAndStatus() {
        ToDo task = new ToDo("read book");
        assertEquals("Consider it recorded. A small triumph for competent administration.\n"
                + "[T] [ ] read book\nYour agenda now contains 1 task. Do try to keep up.\n",
                capture("", () -> Ui.printTaskAddConfirmation(task, 1)));
        task.markAsDone();
        assertEquals("Consider it recorded. A small triumph for competent administration.\n"
                + "[T] [X] read book\nYour agenda now contains 2 tasks. Do try to keep up.\n",
                capture("", () -> Ui.printTaskAddConfirmation(task, 2)));
        assertEquals("Revised to your specifications. Yes, even that detail.\n[T] [X] read book\n",
                capture("", () -> Ui.printTaskUpdateConfirmation(task)));
    }

    /**
     * Verifies list numbering and empty-state guidance exactly.
     */
    @Test
    public void printList_formatsEmptyAndPopulatedLists() {
        String heading = "Behold, your agenda. Let us examine the scale of this undertaking.\n";
        assertEquals(heading + "No tasks to show. How suspiciously serene. "
                + "Try adding a task or checking your search.\n", capture("", () -> Ui.printTaskList(new String[0])));
        assertEquals(heading + "1. [T] [ ] first\n2. [T] [X] second\n",
                capture("", () -> Ui.printTaskList(new String[] {"[T] [ ] first", "[T] [X] second"})));
        for (String command : new String[] {"mark", "unmark", "delete", "update"}) {
            assertEquals("That task exists only in your imagination. Use a listed number: " + command + " <number>\n",
                    capture("", () -> Ui.printNumberedCommandFormat(command)));
        }
    }

    /**
     * Verifies blank and Unicode lines are echoed verbatim and bye stops before later input.
     */
    @Test
    public void echoCommands_stopsAtBye() {
        assertEquals("Stewie: Hello\nStewie: \nStewie: စာအုပ်\n"
                + "Very well. Do come back. I mean, someone must supervise your progress.\n",
                capture("Hello\n\nစာအုပ်\nbye\nignored\n", Ui::echoUserCommands));
    }

    /**
     * Verifies EOF stops the echo loop without farewell and a final unterminated line is echoed.
     */
    @Test
    public void echoCommands_handlesEndOfInput() {
        assertEquals("", capture("", Ui::echoUserCommands));
        assertEquals("Stewie: final line\n", capture("final line", Ui::echoUserCommands));
    }

    /**
     * Captures UTF-8 console output and always restores global streams, even if an assertion fails.
     */
    private String capture(String input, Runnable action) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(output);
            action.run();
            return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }
}
