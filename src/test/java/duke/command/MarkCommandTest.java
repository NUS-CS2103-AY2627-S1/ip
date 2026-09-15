package duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import duke.exception.GaryException;
import duke.task.TaskList;
import duke.ui.CapturingUi;

public class MarkCommandTest {

    @Test
    public void execute_blankArgument_throwsGaryException() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        GaryException exception = assertThrows(GaryException.class, () ->
                new MarkCommand("   ", true).execute(tasks, ui));

        assertEquals("Please indicate which task number to update!", exception.getMessage());
    }

    @Test
    public void execute_nonNumber_throwsGaryException() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        CapturingUi ui = new CapturingUi();

        GaryException exception = assertThrows(GaryException.class, () ->
                new MarkCommand("abc", true).execute(tasks, ui));

        assertEquals("Please provide a positive whole-number task number.", exception.getMessage());
    }

    @Test
    public void execute_zero_throwsGaryException() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        CapturingUi ui = new CapturingUi();

        GaryException exception = assertThrows(GaryException.class, () ->
                new MarkCommand("0", true).execute(tasks, ui));

        assertEquals("Please provide a positive whole-number task number.", exception.getMessage());
    }

    @Test
    public void execute_outOfRange_throwsGaryException() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        CapturingUi ui = new CapturingUi();

        GaryException exception = assertThrows(GaryException.class, () ->
                new MarkCommand("2", true).execute(tasks, ui));

        assertEquals("I can't find a task with that number!", exception.getMessage());
    }

    @Test
    public void execute_numberLargerThanInteger_throwsGaryException() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        GaryException exception = assertThrows(GaryException.class, () ->
                new MarkCommand("9".repeat(20), true).execute(tasks, ui));

        assertEquals("That task number is too large.", exception.getMessage());
    }

    @Test
    public void execute_markDone_marksTaskAndShowsUi() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        CapturingUi ui = new CapturingUi();

        new MarkCommand("1", true).execute(tasks, ui);

        assertEquals("T | 1 | read book", tasks.get(0).toDataString());
        assertEquals(1, ui.getLastMarkedTaskNumber());
        assertEquals(true, ui.isLastMarkedTaskDone());
    }

    @Test
    public void execute_markUndone_marksTaskAndShowsUi() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.mark(0, true);
        CapturingUi ui = new CapturingUi();

        new MarkCommand("1", false).execute(tasks, ui);

        assertEquals("T | 0 | read book", tasks.get(0).toDataString());
        assertEquals(1, ui.getLastMarkedTaskNumber());
        assertEquals(false, ui.isLastMarkedTaskDone());
    }
}
