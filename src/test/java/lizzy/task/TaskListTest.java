package lizzy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import lizzy.exception.LizzyException;

/**
 * Tests task ordering, one-based command access, and safe list snapshots.
 */
public class TaskListTest {
    @Test
    void addAndGet_tasksKeepInsertionOrder() throws LizzyException {
        TaskList tasks = new TaskList();
        Todo first = new Todo("read chapter");
        Todo second = new Todo("revise notes");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.getLast());
    }

    @Test
    void constructor_inputListLaterChanged_taskListKeepsOwnCopy() {
        List<Task> sourceTasks = new ArrayList<>();
        Todo first = new Todo("read chapter");
        sourceTasks.add(first);

        TaskList tasks = new TaskList(sourceTasks);
        sourceTasks.add(new Todo("revise notes"));

        assertEquals(1, tasks.size());
        assertSame(first, tasks.getLast());
    }

    @Test
    void constructor_varargsTasks_tasksKeepArgumentOrderAndOwnCopy() {
        Todo first = new Todo("read chapter");
        Todo second = new Todo("revise notes");
        Task[] initialTasks = {first, second};

        TaskList tasks = new TaskList(initialTasks);
        initialTasks[0] = new Todo("replacement");

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.getLast());
    }

    @Test
    void getTask_validOneBasedNumber_matchingTaskReturned() throws LizzyException {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"));

        Task task = tasks.getTask(2, "mark");

        assertEquals("second", task.getDescription());
    }

    @Test
    void getTask_emptyOrOutOfRangeNumber_helpfulExceptionThrown() {
        TaskList emptyTasks = new TaskList();
        TaskList oneTask = new TaskList(new Todo("first"));

        assertTaskNumberError(emptyTasks, 1, "mark",
                "Add a task first, and then we shall have something to mark.");
        assertTaskNumberError(oneTask, 0, "mark", "Choose a task number from 1 to 1.");
        assertTaskNumberError(oneTask, 2, "mark", "Choose a task number from 1 to 1.");
    }

    @Test
    void getTask_emptyList_eachMutatingCommandNamedInGuidance() {
        TaskList tasks = new TaskList();

        assertTaskNumberError(tasks, 1, "mark", "something to mark");
        assertTaskNumberError(tasks, 1, "unmark", "something to unmark");
        assertTaskNumberError(tasks, 1, "delete", "something to delete");
    }

    @Test
    void deleteTask_validOneBasedNumber_taskRemovedAndFollowingTaskReindexed() throws LizzyException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(first, second);

        Task deletedTask = tasks.deleteTask(1, "delete");

        assertSame(first, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(second, tasks.getTask(1, "delete"));
    }

    @Test
    void asList_snapshotIsImmutableAndUnaffectedByLaterAdditions() throws LizzyException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        List<Task> snapshot = tasks.asList();
        tasks.add(new Todo("second"));

        assertEquals(1, snapshot.size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("third")));
    }

    @Test
    void findMatchingTaskNumbers_matchingDescriptionsKeepOriginalNumbersAndOrder() {
        TaskList tasks = new TaskList(new Todo("read book"), new Todo("buy milk"),
                new Todo("return book"), new Todo("Book review"));

        assertEquals(List.of(1, 3, 4), tasks.findMatchingTaskNumbers("book"));
        assertEquals(List.of(1, 3, 4), tasks.findMatchingTaskNumbers("OOK"));
        assertEquals(List.of(), tasks.findMatchingTaskNumbers("pen"));
    }

    @Test
    void add_sameTaskDetailsTwice_duplicateRejected() throws LizzyException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 10)));

        LizzyException exception = assertThrows(LizzyException.class, () ->
                tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 10), true)));

        assertTrue(exception.getMessage().contains("already keeping its place"));
        assertEquals(1, tasks.size());
    }

    @Test
    void add_similarButDifferentTaskDetails_tasksAccepted() throws LizzyException {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("submit report"));
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 10)));
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 11)));

        assertEquals(3, tasks.size());
    }

    @Test
    void add_duplicateTodoEventAndPeriodTask_eachTypeRejected() throws LizzyException {
        TaskList tasks = new TaskList();
        LocalDate firstDate = LocalDate.of(2026, 9, 10);
        LocalDate secondDate = LocalDate.of(2026, 9, 11);
        tasks.add(new Todo("read chapter"));
        tasks.add(new Event("conference", firstDate, secondDate));
        tasks.add(new WithinPeriodTask("collect certificate", firstDate, secondDate));

        assertThrows(LizzyException.class, () -> tasks.add(new Todo("read chapter", true)));
        assertThrows(LizzyException.class, () ->
                tasks.add(new Event("conference", firstDate, secondDate, true)));
        assertThrows(LizzyException.class, () ->
                tasks.add(new WithinPeriodTask("collect certificate", firstDate, secondDate, true)));
        assertEquals(3, tasks.size());
    }

    private static void assertTaskNumberError(TaskList tasks, int number, String command,
                                              String expectedMessage) {
        LizzyException exception = assertThrows(LizzyException.class, () -> tasks.getTask(number, command));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
