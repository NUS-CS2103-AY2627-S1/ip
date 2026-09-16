package zucc.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import zucc.ZuccException;

/**
 * Tests collection ownership, task mutations, and numbered task filtering.
 */
public class TaskListTest {
    private Todo firstTask;
    private Todo secondTask;
    private TaskList tasks;

    /**
     * Creates a two-item task list used by the collection-operation tests.
     *
     * @throws ZuccException if either valid task fixture cannot be created.
     */
    @BeforeEach
    public void setUp() throws ZuccException {
        firstTask = new Todo("Read chapter");
        secondTask = new Todo("Write notes");
        tasks = new TaskList(List.of(firstTask, secondTask));
    }

    /**
     * Verifies that a task list is unaffected by later changes to its source list.
     *
     * @throws ZuccException if a valid test task cannot be created.
     */
    @Test
    public void constructor_sourceListChanged_taskListUnaffected() throws ZuccException {
        List<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(firstTask);
        TaskList copiedTasks = new TaskList(sourceTasks);

        sourceTasks.add(new Todo("Added outside TaskList"));

        assertEquals(1, copiedTasks.getTaskCount());
    }

    /** Verifies that the task list rejects null collections and null task elements. */
    @Test
    public void constructor_nullCollectionOrElement_assertionThrown() {
        assertAll(
                () -> assertThrows(AssertionError.class, () -> new TaskList(null)),
                () -> assertThrows(
                        AssertionError.class,
                        () -> new TaskList(Arrays.asList(firstTask, null))));
    }

    /**
     * Verifies that adding a task appends the same task instance to the list.
     *
     * @throws ZuccException if the valid task cannot be created or retrieved.
     */
    @Test
    public void add_newTask_taskAppended() throws ZuccException {
        Todo addedTask = new Todo("Review notes");

        tasks.add(addedTask);

        assertEquals(3, tasks.getTaskCount());
        assertSame(addedTask, tasks.delete(2));
    }

    /** Verifies that null cannot be inserted in an established task list. */
    @Test
    public void add_nullTask_assertionThrownAndListUnchanged() {
        assertThrows(AssertionError.class, () -> tasks.add(null));
        assertEquals(2, tasks.getTaskCount());
    }

    /**
     * Verifies that deletion removes and returns the task at a valid index.
     *
     * @throws ZuccException if the valid task index cannot be deleted.
     */
    @Test
    public void delete_validIndex_taskRemovedAndReturned() throws ZuccException {
        Task removedTask = tasks.delete(0);

        assertSame(firstTask, removedTask);
        assertEquals(1, tasks.getTaskCount());
        assertEquals("1.[T][ ] Write notes", tasks.toString());
    }

    /**
     * Verifies that invalid deletion indexes are rejected without changing the list.
     */
    @Test
    public void delete_indexOutsideList_exceptionThrownAndListUnchanged() {
        assertAll(
                () -> assertThrows(ZuccException.class, () -> tasks.delete(-1)),
                () -> assertThrows(ZuccException.class, () -> tasks.delete(2)));
        assertEquals(2, tasks.getTaskCount());
    }

    /**
     * Verifies that marking and unmarking update and return the selected task.
     *
     * @throws ZuccException if either valid state transition cannot be completed.
     */
    @Test
    public void markAndUnmark_validIndex_taskStateChangedAndReturned() throws ZuccException {
        Task markedTask = tasks.mark(1);

        assertSame(secondTask, markedTask);
        assertEquals("X", secondTask.getStatusIcon());

        Task unmarkedTask = tasks.unmark(1);

        assertSame(secondTask, unmarkedTask);
        assertEquals(" ", secondTask.getStatusIcon());
    }

    /**
     * Verifies that marking or unmarking a task already in that state is rejected.
     *
     * @throws ZuccException if the fixture task cannot first be marked.
     */
    @Test
    public void markOrUnmark_invalidState_exceptionThrown() throws ZuccException {
        tasks.mark(0);

        assertAll(
                () -> assertThrows(ZuccException.class, () -> tasks.mark(0)),
                () -> assertThrows(ZuccException.class, () -> tasks.unmark(1)));
    }

    /**
     * Verifies that callers cannot remove tasks through the list's iterator.
     */
    @Test
    public void iterator_removeAttempt_unsupportedOperationThrown() {
        Iterator<Task> iterator = tasks.iterator();
        iterator.next();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    /**
     * Verifies that date filtering includes only matching scheduled tasks with original numbers.
     *
     * @throws ZuccException if the valid scheduled task fixtures cannot be created.
     */
    @Test
    public void formatTasksOn_matchingDate_onlyScheduledTasksWithOriginalNumbersReturned()
            throws ZuccException {
        TaskList scheduledTasks = new TaskList(List.of(
                new Todo("Undated task"),
                new Deadline("Submit report", "10/9/2026 1200"),
                new Event("Conference", "9/9/2026 0900", "11/9/2026 1700"),
                new Deadline("Pay bill", "12/9/2026 1200")));

        assertEquals(
                "2.[D][ ] Submit report (by: Sep 10 2026, 12:00PM)\n"
                        + "3.[E][ ] Conference "
                        + "(from: Sep 09 2026, 9:00AM to: Sep 11 2026, 5:00PM)",
                scheduledTasks.formatTasksOn(LocalDate.of(2026, 9, 10)));
    }

    /**
     * Verifies that date filtering returns an empty string when no task matches.
     *
     * @throws ZuccException if the valid scheduled task fixture cannot be created.
     */
    @Test
    public void formatTasksOn_noMatchingDate_emptyStringReturned() throws ZuccException {
        TaskList scheduledTasks = new TaskList(List.of(
                new Deadline("Submit report", "10/9/2026 1200")));

        assertEquals("", scheduledTasks.formatTasksOn(LocalDate.of(2026, 9, 11)));
    }

    /**
     * Verifies that keyword filtering returns only matching tasks with their original numbers.
     *
     * @throws ZuccException if the valid task fixtures cannot be created or marked.
     */
    @Test
    public void formatTasksContaining_matchingKeyword_onlyDescriptionsWithOriginalNumbersReturned()
            throws ZuccException {
        TaskList searchableTasks = new TaskList(List.of(
                new Todo("Read book"),
                new Deadline("Submit report", "10/9/2026 1200"),
                new Deadline("Return book", "12/9/2026 1200")));
        searchableTasks.mark(2);

        assertEquals(
                "1.[T][ ] Read book\n"
                        + "3.[D][X] Return book (by: Sep 12 2026, 12:00PM)",
                searchableTasks.formatTasksContaining("book"));
    }

    /**
     * Verifies that keyword filtering returns an empty string when no task matches.
     */
    @Test
    public void formatTasksContaining_noMatchingKeyword_emptyStringReturned() {
        assertEquals("", tasks.formatTasksContaining("book"));
    }

    /**
     * Verifies that multiple tasks are formatted as a one-based numbered list.
     */
    @Test
    public void toString_multipleTasks_oneBasedNumberedListReturned() {
        assertEquals(
                "1.[T][ ] Read chapter\n2.[T][ ] Write notes",
                tasks.toString());
    }
}
