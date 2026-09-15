package chatty.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import chatty.exception.ChattyException;

/**
 * Tests task collection mutations and one-based task selection.
 */
public class TaskListTest {
    /** Verifies that supplied tasks are copied and exposed through a read-only view. */
    @Test
    public void constructor_suppliedList_copiesTasksAndExposesReadOnlyView() {
        Todo originalTask = new Todo("read book");
        List<Task> suppliedTasks = new ArrayList<>(List.of(originalTask));
        TaskList taskList = new TaskList(suppliedTasks);

        suppliedTasks.clear();

        assertEquals(1, taskList.size());
        assertSame(originalTask, taskList.getTasks().get(0));
        assertThrows(UnsupportedOperationException.class, () ->
                taskList.getTasks().add(new Todo("write notes")));
    }

    /** Verifies that finding a keyword returns matching tasks in their existing order. */
    @Test
    public void find_matchingKeyword_matchingTasksReturnedInOrder() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("write notes");
        Todo secondMatch = new Todo("return book");
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matchingTasks = taskList.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
        assertEquals(3, taskList.size());
    }

    /** Verifies that finding an absent keyword returns an empty list. */
    @Test
    public void find_nonMatchingKeyword_emptyListReturned() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertTrue(taskList.find("notes").isEmpty());
    }

    /**
     * Verifies that marking and unmarking a valid task changes its completion state.
     *
     * @throws ChattyException if a valid task number is rejected unexpectedly.
     */
    @Test
    public void markAndUnmark_validTaskNumber_completionStateChanged() throws ChattyException {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write notes");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        Task markedTask = taskList.mark(2);

        assertSame(secondTask, markedTask);
        assertFalse(firstTask.isDone());
        assertTrue(secondTask.isDone());

        Task unmarkedTask = taskList.unmark(2);

        assertSame(secondTask, unmarkedTask);
        assertFalse(secondTask.isDone());
    }

    /**
     * Verifies that marking a recurring task advances it and that unmarking is rejected.
     *
     * @throws ChattyException if a valid recurring task cannot be marked.
     */
    @Test
    public void markAndUnmark_overdueRecurringTask_oneIntervalAdvancedAndUnmarkRejected()
            throws ChattyException {
        RecurringTask recurringTask = new RecurringTask("project meeting",
                LocalDate.of(2026, 8, 1), 1, RecurrenceUnit.WEEK);
        TaskList taskList = new TaskList(List.of(recurringTask));

        Task markedTask = taskList.mark(1);

        assertSame(recurringTask, markedTask);
        assertEquals(LocalDate.of(2026, 8, 8), recurringTask.getNextOccurrenceDate());
        assertFalse(recurringTask.isDone());
        assertThrows(ChattyException.class, () -> taskList.unmark(1));
    }

    /** Verifies that a recurring schedule beyond the supported date range fails safely. */
    @Test
    public void mark_recurringTaskAtMaximumDate_exceptionThrownWithoutChangingDate() {
        RecurringTask recurringTask = new RecurringTask("far-future task",
                LocalDate.MAX, 1, RecurrenceUnit.DAY);
        TaskList taskList = new TaskList(List.of(recurringTask));

        ChattyException exception = assertThrows(ChattyException.class, () -> taskList.mark(1));

        assertEquals("OOPS!!! This recurring task cannot advance beyond the supported date range.",
                exception.getMessage());
        assertEquals(LocalDate.MAX, recurringTask.getNextOccurrenceDate());
    }

    /**
     * Verifies that deleting a valid task removes and returns the selected task.
     *
     * @throws ChattyException if a valid task number is rejected unexpectedly.
     */
    @Test
    public void delete_validTaskNumber_selectedTaskRemovedAndReturned() throws ChattyException {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write notes");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        Task deletedTask = taskList.delete(1);

        assertSame(firstTask, deletedTask);
        assertEquals(List.of(secondTask), taskList.getTasks());
    }

    /** Verifies that empty and out-of-range task operations raise {@link ChattyException}. */
    @Test
    public void taskOperations_emptyOrOutOfRangeTaskNumber_exceptionThrown() {
        TaskList emptyTaskList = new TaskList();
        ChattyException emptyListException = assertThrows(ChattyException.class, () ->
                emptyTaskList.mark(1));
        assertEquals("OOPS!!! Your task list is empty.", emptyListException.getMessage());

        TaskList taskList = new TaskList(List.of(new Todo("read book")));
        assertThrows(ChattyException.class, () -> taskList.mark(0));
        assertThrows(ChattyException.class, () -> taskList.unmark(2));
        assertThrows(ChattyException.class, () -> taskList.delete(-1));
    }
}
