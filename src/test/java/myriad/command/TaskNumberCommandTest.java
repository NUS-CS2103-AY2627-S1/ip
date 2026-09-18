package myriad.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import myriad.MyriadException;
import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.task.ToDo;
import myriad.ui.Ui;

/**
 * Tests for {@link TaskNumberCommand#resolveIndex}, the check shared by
 * mark, unmark and delete. It is the one place that turns the 1-based
 * number the user typed into a 0-based index, so an error here would send
 * every one of those three commands at the wrong task.
 * <p>
 * {@code resolveIndex} is protected, so this test lives in the same
 * package and reaches it through the minimal subclass at the bottom of
 * the file. Nothing here calls {@code execute}, so no test touches the
 * console or the disk.
 */
public class TaskNumberCommandTest {

    /** Builds a list of ToDos with the given descriptions, in the order given. */
    private static TaskList taskListOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new ToDo(description));
        }
        return tasks;
    }

    /** A list of three tasks, so valid numbers are 1 to 3. */
    private static TaskList threeTasks() {
        return taskListOf("first", "second", "third");
    }

    /** Resolves the given task number against the given list. */
    private static int resolve(int taskNumber, TaskList tasks) throws MyriadException {
        return new ResolveOnlyCommand(taskNumber).resolveIndex(tasks);
    }

    /** Resolves a number expected to be rejected, returning the exception. */
    private static MyriadException resolveExpectingFailure(int taskNumber, TaskList tasks) {
        return assertThrows(MyriadException.class, () -> resolve(taskNumber, tasks));
    }

    /**
     * Fails the test unless every one of the given task numbers is rejected
     * against an empty list with the empty-list message.
     */
    private static void assertAllRejectedAsEmptyList(int... taskNumbers) {
        for (int taskNumber : taskNumbers) {
            MyriadException e = resolveExpectingFailure(taskNumber, new TaskList());
            assertTrue(e.getMessage().contains("your task list is empty"),
                    "task number " + taskNumber + " gave: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Valid task numbers
    // ---------------------------------------------------------------

    @Test
    public void resolveIndex_firstTaskNumber_zeroReturned() throws MyriadException {
        // The whole point of the method: the user's "1" is the list's 0.
        assertEquals(0, resolve(1, threeTasks()));
    }

    @Test
    public void resolveIndex_lastTaskNumber_lastIndexReturned() throws MyriadException {
        TaskList tasks = threeTasks();
        assertEquals(tasks.size() - 1, resolve(tasks.size(), tasks));
    }

    @Test
    public void resolveIndex_middleTaskNumber_correctIndexReturned() throws MyriadException {
        assertEquals(1, resolve(2, threeTasks()));
    }

    @Test
    public void resolveIndex_onlyTaskInSingleTaskList_zeroReturned() throws MyriadException {
        assertEquals(0, resolve(1, taskListOf("only")));
    }

    // ---------------------------------------------------------------
    // Out of range, with tasks in the list
    // ---------------------------------------------------------------

    @Test
    public void resolveIndex_zero_exceptionThrown() {
        // 0 is not a task number, even though it is a valid index -- catching
        // it here is what stops "mark 0" from marking the first task.
        assertThrows(MyriadException.class, () -> resolve(0, threeTasks()));
    }

    @Test
    public void resolveIndex_negativeNumber_exceptionThrown() {
        // The Parser accepts a negative number and defers the check to here,
        // so this is the only thing standing between "mark -1" and an
        // IndexOutOfBoundsException that would end the session.
        assertThrows(MyriadException.class, () -> resolve(-1, threeTasks()));
    }

    @Test
    public void resolveIndex_oneAboveLastTaskNumber_exceptionThrown() {
        TaskList tasks = threeTasks();
        assertThrows(MyriadException.class, () -> resolve(tasks.size() + 1, tasks));
    }

    @Test
    public void resolveIndex_outOfRange_messageNamesTheValidRange() {
        MyriadException e = resolveExpectingFailure(4, threeTasks());
        assertTrue(e.getMessage().contains("choose a number from 1 to 3"),
                "message should tell the user the valid range: " + e.getMessage());
    }

    @Test
    public void resolveIndex_outOfRange_messageQuotesTheNumberTyped() {
        MyriadException e = resolveExpectingFailure(4, threeTasks());
        assertTrue(e.getMessage().contains("4"), "message should echo the number: " + e.getMessage());
    }

    @Test
    public void resolveIndex_outOfRange_messageReportsCurrentTaskCount() {
        MyriadException e = resolveExpectingFailure(2, taskListOf("only"));
        assertTrue(e.getMessage().contains("1 task(s)"),
                "message should state how many tasks exist: " + e.getMessage());
    }

    // ---------------------------------------------------------------
    // Out of range, with an empty list -- the special-cased message
    // ---------------------------------------------------------------

    @Test
    public void resolveIndex_emptyList_exceptionThrown() {
        assertThrows(MyriadException.class, () -> resolve(1, new TaskList()));
    }

    @Test
    public void resolveIndex_emptyList_messageSaysListIsEmpty() {
        // Special-cased so the message does not read "choose a number from 1
        // to 0", which the general branch would produce.
        MyriadException e = resolveExpectingFailure(1, new TaskList());
        assertTrue(e.getMessage().contains("your task list is empty"),
                "empty list should get its own message: " + e.getMessage());
        assertTrue(!e.getMessage().contains("from 1 to 0"),
                "the nonsensical range must not appear: " + e.getMessage());
    }

    @Test
    public void resolveIndex_emptyList_messageSuggestsAddingATask() {
        MyriadException e = resolveExpectingFailure(1, new TaskList());
        assertTrue(e.getMessage().contains("todo"),
                "message should point at how to add a task: " + e.getMessage());
    }

    @Test
    public void resolveIndex_emptyListWithAnyNumber_sameEmptyMessage() {
        // No number can be valid against an empty list, so they all get the
        // empty-list message rather than the range one.
        assertAllRejectedAsEmptyList(-1, 0, 1, 99);
    }

    // ---------------------------------------------------------------
    // Extreme values
    // ---------------------------------------------------------------

    @Test
    public void resolveIndex_integerMaxValue_exceptionThrown() {
        assertThrows(MyriadException.class, () -> resolve(Integer.MAX_VALUE, threeTasks()));
    }

    @Test
    public void resolveIndex_integerMinValue_exceptionThrown() {
        // Worth stating: taskNumber - 1 overflows to Integer.MAX_VALUE here,
        // so the value that reaches isValidIndex is positive and enormous
        // rather than negative. It is still rejected, so the overflow is
        // harmless -- but it is rejected by the upper bound, not the lower
        // one it looks like it should hit.
        assertThrows(MyriadException.class, () -> resolve(Integer.MIN_VALUE, threeTasks()));
    }

    @Test
    public void resolveIndex_integerMinValue_messageQuotesTheNumberTyped() {
        // The message prints the number as typed, not the overflowed index.
        MyriadException e = resolveExpectingFailure(Integer.MIN_VALUE, threeTasks());
        assertTrue(e.getMessage().contains(String.valueOf(Integer.MIN_VALUE)),
                "message should echo what the user typed: " + e.getMessage());
    }

    // ---------------------------------------------------------------
    // The list is consulted at resolve time, not at construction time
    // ---------------------------------------------------------------

    @Test
    public void resolveIndex_listGrewSinceConstruction_numberNowValid() throws MyriadException {
        // The range check deliberately happens when the command runs, because
        // whether a number is in range depends on the list at that moment.
        TaskList tasks = new TaskList();
        ResolveOnlyCommand command = new ResolveOnlyCommand(1);

        assertThrows(MyriadException.class, () -> command.resolveIndex(tasks));

        tasks.add(new ToDo("added later"));
        assertEquals(0, command.resolveIndex(tasks));
    }

    @Test
    public void resolveIndex_listShrankSinceConstruction_numberNowInvalid() throws MyriadException {
        TaskList tasks = threeTasks();
        ResolveOnlyCommand command = new ResolveOnlyCommand(3);

        assertEquals(2, command.resolveIndex(tasks));

        tasks.remove(2);
        assertThrows(MyriadException.class, () -> command.resolveIndex(tasks));
    }

    /**
     * A concrete TaskNumberCommand that does nothing when executed, existing
     * only so these tests can reach the protected resolveIndex without
     * running a real command's console output and disk writes.
     */
    private static class ResolveOnlyCommand extends TaskNumberCommand {
        ResolveOnlyCommand(int taskNumber) {
            super(taskNumber);
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) {
            // Intentionally empty: these tests never execute the command.
        }
    }
}
