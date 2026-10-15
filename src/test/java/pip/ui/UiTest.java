package pip.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pip.task.TaskList;
import pip.task.Todo;

/**
 * Tests {@link Ui}'s console output methods and its readCommand() behaviour.
 * System.out/System.in are redirected per test and restored afterwards
 * since Ui talks to them directly rather than through injectable streams.
 */
class UiTest {

    private final ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        originalIn = System.in;
        System.setOut(new PrintStream(capturedOut, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private String output() {
        return capturedOut.toString(StandardCharsets.UTF_8);
    }

    @Test
    void showWelcome_printsGreeting() {
        new Ui().showWelcome();
        assertTrue(output().contains("Hey there! I'm Pip, your chipmunk task buddy."));
    }

    @Test
    void showGoodbye_printsFarewell() {
        new Ui().showGoodbye();
        assertTrue(output().contains("Scampering off! See you next time, task buddy."));
    }

    @Test
    void showError_printsGivenMessage() {
        new Ui().showError("Sorry! Something went wrong.");
        assertTrue(output().contains("Sorry! Something went wrong."));
    }

    @Test
    void showTaskAdded_printsTaskAndUpdatedCount() {
        Todo todo = new Todo("buy milk");
        new Ui().showTaskAdded(todo, 3);

        String out = output();
        assertTrue(out.contains("Acorn secured! I've added this task:"));
        assertTrue(out.contains(todo.toString()));
        assertTrue(out.contains("Now you have 3 task(s) in your stash."));
    }

    @Test
    void showTaskDeleted_printsTaskAndUpdatedCount() {
        Todo todo = new Todo("buy milk");
        new Ui().showTaskDeleted(todo, 1);

        String out = output();
        assertTrue(out.contains("I've removed this task:"));
        assertTrue(out.contains(todo.toString()));
        assertTrue(out.contains("Now you have 1 task(s) in your stash."));
    }

    @Test
    void showTaskMarked_completed_printsDoneMessage() {
        Todo todo = new Todo("buy milk");
        todo.markDone();
        new Ui().showTaskMarked(todo, true);

        assertTrue(output().contains("I've marked this task as done:"));
    }

    @Test
    void showTaskMarked_notCompleted_printsNotDoneMessage() {
        Todo todo = new Todo("buy milk");
        new Ui().showTaskMarked(todo, false);

        assertTrue(output().contains("I've marked this task as not done yet:"));
    }

    @Test
    void showTaskList_printsEachTaskWithOneBasedNumbering() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        new Ui().showTaskList(tasks);

        String out = output();
        assertTrue(out.contains("1.[T][ ] first"));
        assertTrue(out.contains("2.[T][ ] second"));
    }

    @Test
    void showTaskList_emptyList_printsOnlyHeader() {
        new Ui().showTaskList(new TaskList());
        assertTrue(output().contains("Here is your task stash:"));
    }

    @Test
    void showMatchingTasks_printsEachMatchWithOneBasedNumbering() {
        TaskList matches = new TaskList();
        matches.add(new Todo("read book"));
        matches.add(new Todo("return book"));

        new Ui().showMatchingTasks(matches);

        String out = output();
        assertTrue(out.contains("Sniffed them out! Here are your matching tasks:"));
        assertTrue(out.contains("1.[T][ ] read book"));
        assertTrue(out.contains("2.[T][ ] return book"));
    }

    @Test
    void showMatchingTasks_noMatches_printsOnlyHeader() {
        new Ui().showMatchingTasks(new TaskList());
        assertTrue(output().contains("Sniffed them out! Here are your matching tasks:"));
    }

    @Test
    void readCommand_returnsTrimmedLine() {
        System.setIn(new ByteArrayInputStream("  todo buy milk  \n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertEquals("todo buy milk", ui.readCommand());
    }

    @Test
    void readCommand_noMoreInput_returnsNull() {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        Ui ui = new Ui();

        assertNull(ui.readCommand());
    }

    @Test
    void readCommand_multipleLines_readsSequentially() {
        System.setIn(new ByteArrayInputStream("list\nbye\n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertEquals("list", ui.readCommand());
        assertEquals("bye", ui.readCommand());
    }
}
