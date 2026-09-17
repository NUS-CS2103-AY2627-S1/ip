package harvey.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import harvey.task.TaskList;
import harvey.task.Todo;

/**
 * Tests the wording and layout of replies, and the reading and printing done for the text
 * interface.
 * <p>
 * {@link Ui#formatLines(String...)} is worth testing despite its size, because every
 * reply in the program is built through it and because varargs has edge cases a plain
 * parameter does not: no arguments at all, and a single argument that must not gain a
 * trailing separator.
 * <p>
 * The printing and reading tests swap {@code System.out} and {@code System.in} for
 * in-memory streams, and always put the real ones back in a {@code finally} block.
 */
public class UiTest {
    /** The line separator the replies are joined with. */
    private static final String NEW_LINE = System.lineSeparator();

    private final Ui ui = new Ui();

    @Test
    public void formatTaskCount_oneTask_usesSingular() {
        assertEquals("You've got 1 case on the docket.", ui.formatTaskCount(1));
    }

    @Test
    public void formatTaskCount_severalTasks_usesPlural() {
        assertEquals("You've got 3 cases on the docket.", ui.formatTaskCount(3));
    }

    @Test
    public void formatTaskCount_noTasksLeft_usesPlural() {
        // "0 cases" reads correctly, so zero is not a special case the way one is.
        assertEquals("You've got 0 cases on the docket.", ui.formatTaskCount(0));
    }

    @Test
    public void formatLines_severalLines_joinedBySeparator() {
        assertEquals("one" + NEW_LINE + "two" + NEW_LINE + "three",
                ui.formatLines("one", "two", "three"));
    }

    @Test
    public void formatLines_singleLine_noTrailingSeparator() {
        assertEquals("only", ui.formatLines("only"));
    }

    @Test
    public void formatLines_noLines_returnsEmptyString() {
        assertEquals("", ui.formatLines());
    }

    @Test
    public void formatTaskList_twoTasks_headingThenNumberedTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));

        assertEquals("Here's your docket:" + NEW_LINE
                + "1.[T][ ] read book" + NEW_LINE
                + "2.[T][ ] buy milk", ui.formatTaskList(tasks));
    }

    @Test
    public void formatGreeting_always_twoLinesIntroducingHarvey() {
        String[] lines = ui.formatGreeting().split(NEW_LINE);
        assertEquals(2, lines.length);
        assertTrue(lines[0].contains("Harvey"));
    }

    @Test
    public void formatError_message_objectionPutInFront() {
        assertEquals("Objection! No task 4.", ui.formatError("No task 4."));
    }

    @Test
    public void formatMatchingTasks_oneMatch_ownHeadingThenNumberedFromOne() {
        TaskList matches = new TaskList();
        matches.add(new Todo("buy milk"));

        assertEquals("Here's what I dug up:" + NEW_LINE + "1.[T][ ] buy milk",
                ui.formatMatchingTasks(matches));
    }

    @Test
    public void formatFarewell_always_notEmpty() {
        assertFalse(ui.formatFarewell().isBlank());
    }

    @Test
    public void printReply_message_printedThenDivider() {
        String printed = captureOutput(() -> ui.printReply("hello"));
        String[] lines = printed.split(NEW_LINE);

        assertEquals("hello", lines[0]);
        assertTrue(lines[1].matches("_+"), "second line should be the divider: " + lines[1]);
    }

    @Test
    public void printGreeting_always_bannerAndGreetingBetweenDividers() {
        String printed = captureOutput(ui::printGreeting);

        assertTrue(printed.startsWith("_"), printed);
        assertTrue(printed.contains(ui.formatGreeting()), printed);
    }

    @Test
    public void readCommand_lineWithSurroundingSpaces_returnedTrimmed() {
        // Ui opens standard input when it is built, so the input is swapped in first.
        InputStream original = System.in;
        try {
            System.setIn(new ByteArrayInputStream("   list  \n".getBytes(StandardCharsets.UTF_8)));
            Ui reader = new Ui();

            assertTrue(reader.hasNextCommand());
            assertEquals("list", reader.readCommand());
            assertFalse(reader.hasNextCommand(), "input has run out after the only line");
        } finally {
            System.setIn(original);
        }
    }

    /** Runs the action with standard output redirected, and returns what it printed. */
    private static String captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(buffer, true, StandardCharsets.UTF_8));
            action.run();
        } finally {
            System.setOut(original);
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
