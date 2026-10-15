package thomas.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.task.TodoTask;

/**
 * Tests History.
 * What makes this worth testing is that it is the only thing standing between a
 * user and a change they cannot take back, and that getting it wrong is quiet:
 * an undo that reverses the wrong change, or reverses one that never happened,
 * leaves a plausible-looking list rather than an error on screen.
 * Three things are pinned down here. That a step puts the list back exactly as
 * it was, for each of the three kinds of change. That steps come off most recent
 * first, since every number a step carries is only valid in the state it was
 * recorded in. And that the line quoted back is the one that caused the change
 * being reversed, not whichever line happened to be typed last.
 * The kinds are pushed directly rather than by running commands, so that a case
 * is about this class alone; the commands pushing the right steps is what the
 * text-UI plan covers end to end.
 */
public class HistoryTest {

    /**
     * Returns a list holding to-dos with the given descriptions, in order.
     *
     * @param descriptions Task text, one per task.
     * @return List holding those tasks.
     */
    private static TaskList listOf(String... descriptions) {
        // Built through the constructor rather than add(), which refuses a duplicate and so would make every test
        // declare the exception; no case here is about duplicates.
        ArrayList<Task> tasks = new ArrayList<>();
        for (String description : descriptions) {
            tasks.add(new TodoTask(description));
        }
        return new TaskList(tasks);
    }

    /**
     * Returns how a list's tasks show themselves, in order, for comparing lists.
     *
     * @param list List to read.
     * @return One entry per task, in list order.
     */
    private static List<String> contentsOf(TaskList list) {
        List<String> shown = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            shown.add(list.get(i).toString());
        }
        return shown;
    }

    // ---- nothing to undo ----

    @Test
    public void undo_neverUsed_exceptionThrown() {
        History history = new History();

        ThomasException e = assertThrows(ThomasException.class, () -> history.undo(listOf()));
        assertEquals("I can't reverse any further! There's nothing to undo.", e.getMessage());
    }

    @Test
    public void undo_everyStepAlreadyUndone_exceptionThrown() throws ThomasException {
        TaskList list = listOf("read book");
        History history = new History();
        history.startCommand("todo read book");
        history.pushRemove(1);

        history.undo(list);

        // The one step has been used up, so the history is empty again rather
        // than holding a step that could be applied twice.
        ThomasException e = assertThrows(ThomasException.class, () -> history.undo(list));
        assertEquals("I can't reverse any further! There's nothing to undo.", e.getMessage());
    }

    @Test
    public void undo_startCommandWithoutAPush_exceptionThrown() {
        History history = new History();
        // A command that changes nothing still announces itself, and must leave
        // the history exactly as it found it.
        history.startCommand("list");

        assertThrows(ThomasException.class, () -> history.undo(listOf("read book")));
    }

    // ---- undoing an add ----

    @Test
    public void undo_pushRemove_takesTheLastTaskBackOut() throws ThomasException {
        TaskList list = listOf("read book", "buy milk");
        History history = new History();
        history.startCommand("todo buy milk");
        history.pushRemove(2);

        history.undo(list);

        assertEquals(List.of("[T][ ] read book"), contentsOf(list));
    }

    @Test
    public void undo_pushRemoveOfOnlyTask_leavesListEmpty() throws ThomasException {
        TaskList list = listOf("read book");
        History history = new History();
        history.startCommand("todo read book");
        history.pushRemove(1);

        history.undo(list);

        assertEquals(0, list.size());
    }

    // ---- undoing a delete ----

    @Test
    public void undo_pushInsertInTheMiddle_taskReturnsToItsOwnNumber() throws ThomasException {
        TaskList list = listOf("read book", "pay fine");
        Task removed = new TodoTask("buy milk");
        History history = new History();
        history.startCommand("delete 2");
        history.pushInsert(2, removed);

        history.undo(list);

        // Back at 2, not appended at 3: the numbers the user sees have to close
        // around the task exactly as deleting it opened them.
        assertEquals(List.of("[T][ ] read book", "[T][ ] buy milk", "[T][ ] pay fine"),
                contentsOf(list));
        assertSame(removed, list.get(1));
    }

    @Test
    public void undo_pushInsertAtTheFront_taskReturnsToTheFirstNumber() throws ThomasException {
        TaskList list = listOf("buy milk");
        History history = new History();
        history.startCommand("delete 1");
        history.pushInsert(1, new TodoTask("read book"));

        history.undo(list);

        assertEquals(List.of("[T][ ] read book", "[T][ ] buy milk"), contentsOf(list));
    }

    @Test
    public void undo_pushInsertPastTheEnd_taskReturnsToTheLastNumber() throws ThomasException {
        TaskList list = listOf("read book");
        History history = new History();
        history.startCommand("delete 2");
        // Undoing the delete of what had been the last task reaches one past the
        // end of the list as it now stands. This is the case a range check
        // borrowed from getByNumber would wrongly refuse.
        history.pushInsert(2, new TodoTask("buy milk"));

        history.undo(list);

        assertEquals(List.of("[T][ ] read book", "[T][ ] buy milk"), contentsOf(list));
    }

    @Test
    public void undo_pushInsertOfDoneTask_taskIsStillDone() throws ThomasException {
        TaskList list = listOf("read book");
        Task removed = new TodoTask("buy milk");
        removed.markAsDone();
        History history = new History();
        history.startCommand("delete 2");
        history.pushInsert(2, removed);

        history.undo(list);

        // The task itself is put back, not a fresh one built from its text, so
        // its tick comes back with it.
        assertTrue(list.get(1).isDone());
    }

    // ---- undoing a mark or unmark ----

    @Test
    public void undo_pushSetDoneFalse_taskGoesBackToNotDone() throws ThomasException {
        TaskList list = listOf("read book");
        Task task = list.get(0);
        History history = new History();
        history.startCommand("mark 1");
        history.pushSetDone(task, task.isDone());
        task.markAsDone();

        history.undo(list);

        assertFalse(task.isDone());
    }

    @Test
    public void undo_pushSetDoneTrueOnAnAlreadyDoneTask_taskStaysDone() throws ThomasException {
        TaskList list = listOf("read book");
        Task task = list.get(0);
        task.markAsDone();
        History history = new History();
        history.startCommand("mark 1");
        // Marking a task that is already done changes nothing, so what is recorded
        // is that it was done. An undo that flipped the flag instead would take
        // away a tick this command never put there.
        history.pushSetDone(task, task.isDone());
        task.markAsDone();

        history.undo(list);

        assertTrue(task.isDone());
    }

    @Test
    public void undo_pushSetDoneTrue_taskGoesBackToDone() throws ThomasException {
        TaskList list = listOf("read book");
        Task task = list.get(0);
        task.markAsDone();
        History history = new History();
        history.startCommand("unmark 1");
        history.pushSetDone(task, task.isDone());
        task.unmarkAsDone();

        history.undo(list);

        assertTrue(task.isDone());
    }

    @Test
    public void undo_pushSetDone_listLengthUnchanged() throws ThomasException {
        TaskList list = listOf("read book", "buy milk");
        History history = new History();
        history.startCommand("mark 1");
        history.pushSetDone(list.get(0), false);

        history.undo(list);

        assertEquals(2, list.size());
    }

    // ---- order ----

    @Test
    public void undo_severalSteps_mostRecentIsAppliedFirst() throws ThomasException {
        TaskList list = listOf("read book", "buy milk");
        History history = new History();
        history.startCommand("todo read book");
        history.pushRemove(1);
        history.startCommand("todo buy milk");
        history.pushRemove(2);

        history.undo(list);

        // The second add is reversed, leaving the first task. Taking the steps in
        // the order they were recorded would have removed task 1 and left "buy
        // milk" holding a number that no longer describes where it sits.
        assertEquals(List.of("[T][ ] read book"), contentsOf(list));
    }

    @Test
    public void undo_everyStep_walksBackToTheStartingList() throws ThomasException {
        TaskList list = listOf("read book", "buy milk", "pay fine");
        History history = new History();
        history.startCommand("todo buy milk");
        history.pushRemove(2);
        history.startCommand("todo pay fine");
        history.pushRemove(3);

        history.undo(list);
        history.undo(list);

        assertEquals(List.of("[T][ ] read book"), contentsOf(list));
    }

    @Test
    public void undo_stepsOfDifferentKinds_eachReversesItsOwnChange() throws ThomasException {
        TaskList list = listOf("read book");
        Task task = list.get(0);
        History history = new History();
        history.startCommand("mark 1");
        history.pushSetDone(task, false);
        task.markAsDone();
        history.startCommand("todo buy milk");
        list.add(new TodoTask("buy milk"));
        history.pushRemove(2);

        history.undo(list);
        history.undo(list);

        assertEquals(List.of("[T][ ] read book"), contentsOf(list));
        assertFalse(task.isDone());
    }

    // ---- the line quoted back ----

    @Test
    public void undo_afterStartCommand_returnsThatLine() throws ThomasException {
        TaskList list = listOf("read book");
        History history = new History();
        history.startCommand("todo read book");
        history.pushRemove(1);

        assertEquals("todo read book", history.undo(list));
    }

    @Test
    public void undo_laterCommandsRun_returnsTheLineOfTheStepBeingUndone()
            throws ThomasException {
        TaskList list = listOf("read book", "buy milk");
        History history = new History();
        history.startCommand("todo read book");
        history.pushRemove(1);
        history.startCommand("todo buy milk");
        history.pushRemove(2);
        // Read-only commands run afterwards and push nothing, but they do announce
        // themselves. A step must keep the line it was stamped with rather than
        // picking up whichever line was typed most recently.
        history.startCommand("list");

        assertEquals("todo buy milk", history.undo(list));
        assertEquals("todo read book", history.undo(list));
    }

    @Test
    public void undo_lineTypedWithOddSpacing_returnsItExactlyAsTyped() throws ThomasException {
        TaskList list = listOf("read book", "buy milk");
        History history = new History();
        history.startCommand("delete   2");
        history.pushInsert(2, new TodoTask("pay fine"));

        // The line is kept, not rebuilt from the task number, so however the user
        // spaced it is what they are shown.
        assertEquals("delete   2", history.undo(list));
    }
}
