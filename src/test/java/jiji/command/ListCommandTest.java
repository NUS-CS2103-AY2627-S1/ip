package jiji.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jiji.task.TaskList;
import jiji.task.Todo;
import jiji.ui.Ui;

public class ListCommandTest {

    private TaskList tasks;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
    }

    @Test
    public void execute_listAll_displaysAllTasks() {
        Todo t1 = new Todo("task 1");
        Todo t2 = new Todo("task 2");
        t2.markAsDone();
        tasks.addAll(t1, t2);

        ListCommand cmd = new ListCommand();
        assertEquals(ListFilter.ALL, cmd.getFilter());

        String result = cmd.execute(tasks, ui, null);
        assertTrue(result.contains("1.[T][ ] task 1"));
        assertTrue(result.contains("2.[T][X] task 2"));
    }

    @Test
    public void execute_listPending_displaysOnlyPendingWithOriginalIndex() {
        Todo t1 = new Todo("task 1");
        t1.markAsDone();
        Todo t2 = new Todo("task 2");
        tasks.addAll(t1, t2);

        ListCommand cmd = new ListCommand(ListFilter.PENDING);
        String result = cmd.execute(tasks, ui, null);

        assertTrue(!result.contains("task 1"));
        assertTrue(result.contains("2.[T][ ] task 2"));
        assertTrue(result.contains("Here are the pending tasks in your list:"));
    }

    @Test
    public void execute_listDone_displaysOnlyCompletedWithOriginalIndex() {
        Todo t1 = new Todo("task 1");
        t1.markAsDone();
        Todo t2 = new Todo("task 2");
        tasks.addAll(t1, t2);

        ListCommand cmd = new ListCommand(ListFilter.DONE);
        String result = cmd.execute(tasks, ui, null);

        assertTrue(result.contains("1.[T][X] task 1"));
        assertTrue(!result.contains("task 2"));
        assertTrue(result.contains("Here are the completed tasks in your list:"));
    }

    @Test
    public void execute_listPendingWhenNone_returnsFriendlyNotice() {
        Todo t1 = new Todo("task 1");
        t1.markAsDone();
        tasks.add(t1);

        ListCommand cmd = new ListCommand(ListFilter.PENDING);
        String result = cmd.execute(tasks, ui, null);

        assertTrue(result.contains("You have no pending tasks! Great job!"));
    }

    @Test
    public void execute_listDoneWhenNone_returnsFriendlyNotice() {
        Todo t1 = new Todo("task 1");
        tasks.add(t1);

        ListCommand cmd = new ListCommand(ListFilter.DONE);
        String result = cmd.execute(tasks, ui, null);

        assertTrue(result.contains("You have no completed tasks yet."));
    }
}
