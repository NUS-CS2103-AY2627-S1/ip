package zhangwei.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zhangwei.storage.Storage;
import zhangwei.task.Deadline;
import zhangwei.task.TaskList;
import zhangwei.task.Todo;
import zhangwei.ui.Ui;

/** Tests {@link FindCommand}, which prints tasks with matching descriptions. */
public class FindCommandTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;

    @BeforeEach
    public void redirectOutput() {
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
    }

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOut);
    }

    private void runFindOn(TaskList tasks, String keyword) {
        new FindCommand(keyword).execute(tasks, new Ui(), new Storage("unused.txt"));
    }

    @Test
    public void execute_severalMatchingTasks_onlyMatchesNumberedFromOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy groceries"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 12, 2)));

        runFindOn(tasks, "book");

        String output = captured.toString();
        assertTrue(output.contains("Here are the matching tasks I found in your grove:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[D][ ] return book (by: Dec 2 2019)"));
        assertFalse(output.contains("buy groceries"));
    }

    @Test
    public void execute_noMatchingTasks_noMatchesMessageShown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        runFindOn(tasks, "groceries");

        String output = captured.toString();
        assertTrue(output.contains("I sniffed around, but no tasks in your grove match."));
        assertFalse(output.contains("1."));
    }

    @Test
    public void execute_matchingTask_taskListLeftUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        runFindOn(tasks, "book");

        assertEquals(1, tasks.size());
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void isExit_findCommand_falseReturned() {
        assertFalse(new FindCommand("book").isExit());
    }
}
