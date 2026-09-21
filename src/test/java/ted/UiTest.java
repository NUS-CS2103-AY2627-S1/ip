package ted;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import ted.task.Deadline;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Tests {@link Ui}, which words everything Ted says and reads what the user types.
 * <p>
 * Most of Ui only builds up a reply, so its tests read the reply back with
 * {@link Ui#flush()}. The two methods that touch the terminal are tested by
 * swapping {@code System.in} and {@code System.out} for in-memory streams.
 */
public class UiTest {
    /**
     * Verifies that the greeting introduces Ted by name.
     */
    @Test
    public void showWelcome_greeting_introducesTed() {
        Ui ui = new Ui();
        ui.showWelcome();
        assertTrue(ui.flush().startsWith("Oh good, you're here. I'm Ted"));
    }

    /**
     * Verifies that a reply starting with an indented line keeps its indent,
     * since only trailing whitespace is removed.
     */
    @Test
    public void flush_replyStartingWithSpace_keepsIndent() {
        Ui ui = new Ui();
        ui.showError("  indented");
        assertEquals("  indented", ui.flush());
    }

    /**
     * Verifies that a flushed reply has no trailing line break, and that the
     * next reply starts empty.
     */
    @Test
    public void flush_calledTwice_secondReplyIsEmpty() {
        Ui ui = new Ui();
        ui.showGoodbye();

        assertEquals("Leaving already? Fine. I'll be here on the shelf. Bye!", ui.flush());
        assertEquals("", ui.flush());
    }

    /**
     * Verifies that the task count comes with a remark that suits the size of
     * the list, from empty to busy.
     */
    @Test
    public void showAddedOrRemoved_taskCount_remarkSuitsCount() {
        Ui ui = new Ui();
        ui.showRemoved(new Todo("borrow book"), 0);
        assertTrue(ui.flush().contains("Your list is empty now."));

        ui.showAdded(new Todo("borrow book"), 1);
        assertTrue(ui.flush().contains("That's 1 task. A small start, but a start."));

        ui.showAdded(new Todo("borrow book"), 2);
        assertTrue(ui.flush().contains("That's 2 tasks. Still manageable."));

        ui.showAdded(new Todo("borrow book"), 10);
        assertTrue(ui.flush().contains("That's 10 tasks. Maybe finish a few before adding more?"));
    }

    /**
     * Verifies that the skipped-line warning reads naturally for one line and for several.
     */
    @Test
    public void showSkippedLines_count_singularOrPlural() {
        Ui ui = new Ui();
        ui.showSkippedLines(1);
        assertTrue(ui.flush().endsWith("so I skipped 1 line."));

        ui.showSkippedLines(3);
        assertTrue(ui.flush().endsWith("so I skipped 3 lines."));
    }

    /**
     * Verifies that an empty list gets a message rather than a bare header.
     */
    @Test
    public void showTasks_emptyList_saysSo() {
        Ui ui = new Ui();
        ui.showTasks(new TaskList());
        assertTrue(ui.flush().startsWith("Your list is empty."));
    }

    /**
     * Verifies that matching tasks keep their numbers from the full list,
     * and that a search with no matches repeats the keyword back.
     */
    @Test
    public void showMatchingTasks_matchesAndNoMatches_numberedFromFullList() {
        TaskList tasks = new TaskList(List.of(new Todo("buy milk"), new Todo("read book")));
        Ui ui = new Ui();

        ui.showMatchingTasks(tasks, tasks.find("book"), "book");
        assertTrue(ui.flush().contains("2.[T][ ] read book"));

        ui.showMatchingTasks(tasks, tasks.find("bicycle"), "bicycle");
        assertTrue(ui.flush().startsWith("Nothing matches \"bicycle\"."));
    }

    /**
     * Verifies that a loading problem is followed by what Ted does about it,
     * and that the backup's location is given.
     */
    @Test
    public void showLoadingErrorAndBackup_messages_bothShown() {
        Ui ui = new Ui();
        ui.showLoadingError("I couldn't read your saved tasks.");
        ui.showBackup(Path.of("data", "ted.txt.bak"));
        String reply = ui.flush();

        assertTrue(reply.startsWith("I couldn't read your saved tasks."));
        assertTrue(reply.contains("I'll start you on an empty list for now."));
        assertTrue(reply.contains(Path.of("data", "ted.txt.bak").toString()));
    }

    /**
     * Verifies that a reply printed to the terminal sits between two dividers.
     */
    @Test
    public void printReply_reply_framedByDividers() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
            new Ui().printReply("Hello");
        } finally {
            System.setOut(originalOut);
        }

        String[] lines = printed.toString(StandardCharsets.UTF_8).split("\\R");
        assertEquals(3, lines.length);
        assertTrue(lines[0].matches("_+"));
        assertEquals("Hello", lines[1]);
        assertEquals(lines[0], lines[2]);
    }

    /**
     * Verifies that typed lines are read in order with surrounding spaces
     * removed, and that the end of the input is noticed.
     */
    @Test
    public void readCommand_typedLines_readInOrderUntilInputEnds() {
        InputStream originalIn = System.in;
        try {
            System.setIn(new ByteArrayInputStream("  list  \nbye\n".getBytes(StandardCharsets.UTF_8)));
            // The Ui reads from whatever System.in is when the Ui is created.
            Ui ui = new Ui();

            assertTrue(ui.hasNextCommand());
            assertEquals("list", ui.readCommand());
            assertEquals("bye", ui.readCommand());
            assertFalse(ui.hasNextCommand());
        } finally {
            System.setIn(originalIn);
        }
    }

    /**
     * Verifies that a task set for one of Ted's favorite times, morning or
     * afternoon, earns a remark, while a task a minute off does not.
     */
    @Test
    public void showAdded_favoriteTime_remarkedOnlyAtThatTime() {
        Ui ui = new Ui();
        ui.showAdded(new Deadline("essay", LocalDateTime.of(2026, 9, 25, 16, 20)), 1);
        assertTrue(ui.flush().contains("4:20, huh?"));

        ui.showAdded(new Deadline("essay", LocalDateTime.of(2026, 9, 25, 4, 20)), 1);
        assertTrue(ui.flush().contains("4:20, huh?"));

        ui.showAdded(new Deadline("essay", LocalDateTime.of(2026, 9, 25, 16, 21)), 1);
        assertFalse(ui.flush().contains("4:20"));
    }
}
