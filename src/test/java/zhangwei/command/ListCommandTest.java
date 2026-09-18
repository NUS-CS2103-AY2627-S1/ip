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

/**
 * Tests {@link ListCommand}. It only prints, so the tests check the printed
 * text: the tasks are numbered from 1 in list order, and listing changes
 * nothing.
 */
public class ListCommandTest {

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

    private void runListOn(TaskList tasks) {
        new ListCommand().execute(tasks, new Ui(), new Storage("unused.txt"));
    }

    @Test
    public void execute_listWithTasks_tasksNumberedFromOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 12, 2)));

        runListOn(tasks);

        String output = captured.toString();
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[D][ ] return book (by: Dec 2 2019)"));
    }

    @Test
    public void execute_emptyList_emptyMessageShownWithoutHeading() {
        runListOn(new TaskList());
        String output = captured.toString();
        assertTrue(output.contains("Your grove is empty."));
        assertFalse(output.contains("Here are the tasks growing in your grove:"));
    }

    @Test
    public void execute_listWithTasks_listLeftUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        runListOn(tasks);
        assertEquals(1, tasks.size());
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void isExit_listCommand_falseReturned() {
        assertFalse(new ListCommand().isExit());
    }
}
