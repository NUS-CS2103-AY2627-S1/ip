package task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import exception.CbtException;

/** Tests task collection mutations, validation, and filtering. */
public class TaskListTest {
    /** Verifies that tasks are appended and retain their insertion order. */
    @Test
    public void addTask_validTask_taskStoredAtEnd() throws CbtException {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");

        tasks.addTask(first);
        tasks.addTask(second);

        assertEquals(2, tasks.getSize());
        assertSame(first, tasks.getTask(0));
        assertSame(second, tasks.getTask(1));
    }

    /** Verifies that duplicate detection ignores description case but distinguishes task details. */
    @Test
    public void addTaskIfUnique_duplicateAndDistinctTasks_duplicateRejected() throws CbtException {
        TaskList tasks = new TaskList();
        tasks.addTaskIfUnique(new Todo("read book"));

        assertThrows(CbtException.class, () -> tasks.addTaskIfUnique(new Todo("READ BOOK")));
        tasks.addTaskIfUnique(new Deadline("read book", LocalDateTime.of(2026, 8, 27, 12, 0)));
        tasks.addTaskIfUnique(new Deadline("read book", LocalDateTime.of(2026, 8, 28, 12, 0)));

        assertEquals(3, tasks.getSize());
    }

    /** Verifies that negative and past-the-end indexes are rejected. */
    @Test
    public void getTask_invalidIndexes_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("only task"));

        assertThrows(CbtException.class, () -> tasks.getTask(-1));
        assertThrows(CbtException.class, () -> tasks.getTask(1));
    }

    /** Verifies that deletion removes and returns the selected task. */
    @Test
    public void deleteTask_validIndex_taskRemovedAndReturned() throws CbtException {
        TaskList tasks = new TaskList();
        Todo removed = new Todo("remove me");
        Todo retained = new Todo("keep me");
        tasks.addTask(removed);
        tasks.addTask(retained);

        Task result = tasks.deleteTask(0);

        assertSame(removed, result);
        assertEquals(1, tasks.getSize());
        assertSame(retained, tasks.getTask(0));
    }

    /** Verifies that invalid deletions do not mutate the task list. */
    @Test
    public void deleteTask_invalidIndexes_exceptionThrownAndListUnchanged() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("keep me"));

        assertThrows(CbtException.class, () -> tasks.deleteTask(-1));
        assertThrows(CbtException.class, () -> tasks.deleteTask(1));
        assertEquals(1, tasks.getSize());
    }

    /** Verifies that marking and unmarking update the selected task's completion state. */
    @Test
    public void markAndUnmarkTask_validIndex_completionStatusChanged() throws CbtException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        tasks.addTask(todo);

        assertSame(todo, tasks.markTask(0));
        assertTrue(todo.isDone());

        assertSame(todo, tasks.unmarkTask(0));
        assertFalse(todo.isDone());
    }

    /** Verifies invalid and redundant completion transitions are rejected. */
    @Test
    public void markAndUnmarkTask_invalidTransitions_exceptionThrown() throws CbtException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        tasks.addTask(todo);

        assertThrows(CbtException.class, () -> tasks.unmarkTask(0));
        assertThrows(CbtException.class, () -> tasks.markTask(1));
        tasks.markTask(0);
        assertThrows(CbtException.class, () -> tasks.markTask(0));
        assertThrows(CbtException.class, () -> tasks.unmarkTask(-1));
    }

    /** Verifies that date filtering returns only matching dated tasks in list order. */
    @Test
    public void findTasksOn_mixedTasks_onlyTasksOccurringOnDateReturned() throws CbtException {
        LocalDate targetDate = LocalDate.of(2026, 8, 26);
        Todo todo = new Todo("undated task");
        Deadline deadline = new Deadline("submit", targetDate.atTime(23, 59));
        Event spanningEvent = new Event("camp",
                targetDate.minusDays(1).atStartOfDay(),
                targetDate.plusDays(1).atStartOfDay());
        Deadline otherDeadline = new Deadline("later", targetDate.plusDays(1).atStartOfDay());
        TaskList tasks = new TaskList();
        tasks.addTask(todo);
        tasks.addTask(deadline);
        tasks.addTask(spanningEvent);
        tasks.addTask(otherDeadline);

        TaskList result = tasks.findTasksOn(targetDate);

        assertEquals(2, result.getSize());
        assertSame(deadline, result.getTask(0));
        assertSame(spanningEvent, result.getTask(1));
        assertEquals(4, tasks.getSize());
    }

    /** Verifies that date filtering returns an empty list when no tasks match. */
    @Test
    public void findTasksOn_noMatches_emptyTaskListReturned() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Deadline("submit",
                LocalDateTime.of(2026, 8, 27, 12, 0)));

        assertEquals(0, tasks.findTasksOn(LocalDate.of(2026, 8, 26)).getSize());
    }

    /** Verifies that keyword filtering ignores case and preserves task order. */
    @Test
    public void findTasksContaining_mixedDescriptions_matchingTasksReturnedInOrder() throws CbtException {
        Todo firstMatch = new Todo("read Book");
        Todo nonMatch = new Todo("write report");
        Deadline secondMatch = new Deadline("return book", LocalDateTime.of(2026, 8, 27, 12, 0));
        TaskList tasks = new TaskList();
        tasks.addTask(firstMatch);
        tasks.addTask(nonMatch);
        tasks.addTask(secondMatch);

        TaskList result = tasks.findTasksContaining("BOOK");

        assertEquals(2, result.getSize());
        assertSame(firstMatch, result.getTask(0));
        assertSame(secondMatch, result.getTask(1));
        assertEquals(3, tasks.getSize());
        assertEquals(0, tasks.findTasksContaining("missing").getSize());
    }

    /** Verifies that task formatting includes one-based list numbers and no trailing newline. */
    @Test
    public void formatTasks_multipleTasks_numberedLinesReturned() throws CbtException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first"));
        tasks.addTask(new Todo("second"));

        String expected = "1.[T][ ] first" + System.lineSeparator() + "2.[T][ ] second";

        assertEquals(expected, tasks.formatTasks());
        assertEquals("", new TaskList().formatTasks());
    }

    /** Verifies date formatting keeps original task numbers and omits nonmatches. */
    @Test
    public void formatTasksOnDate_mixedTasks_originalNumbersRetained() throws CbtException {
        LocalDate targetDate = LocalDate.of(2026, 8, 26);
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first undated"));
        tasks.addTask(new Deadline("matching", targetDate.atTime(12, 0)));
        tasks.addTask(new Deadline("different", targetDate.plusDays(1).atTime(12, 0)));

        assertEquals("2.[D][ ] matching (by: Aug 26 2026, 12:00pm)",
                tasks.formatTasksOnDate(targetDate));
        assertEquals("", tasks.formatTasksOnDate(targetDate.minusDays(1)));
    }

    /** Verifies chronological sorting across task types, including stable ties and undated tasks. */
    @Test
    public void sortChronologically_mixedTasks_datedTasksSortedAndTodosLast() throws CbtException {
        Todo firstTodo = new Todo("first todo");
        Deadline laterDeadline = new Deadline("later", LocalDateTime.of(2026, 8, 28, 12, 0));
        Event earlierEvent = new Event("earlier",
                LocalDateTime.of(2026, 8, 27, 9, 0),
                LocalDateTime.of(2026, 8, 27, 10, 0));
        Deadline tiedDeadline = new Deadline("tied", LocalDateTime.of(2026, 8, 27, 9, 0));
        Todo secondTodo = new Todo("second todo");
        TaskList tasks = new TaskList();
        tasks.addTask(firstTodo);
        tasks.addTask(laterDeadline);
        tasks.addTask(earlierEvent);
        tasks.addTask(tiedDeadline);
        tasks.addTask(secondTodo);

        tasks.sortChronologically();

        assertSame(earlierEvent, tasks.getTask(0));
        assertSame(tiedDeadline, tasks.getTask(1));
        assertSame(laterDeadline, tasks.getTask(2));
        assertSame(firstTodo, tasks.getTask(3));
        assertSame(secondTodo, tasks.getTask(4));
    }

    /** Verifies that sorting an empty task list does not fail or add tasks. */
    @Test
    public void sortChronologically_emptyList_listRemainsEmpty() {
        TaskList tasks = new TaskList();

        tasks.sortChronologically();

        assertEquals(0, tasks.getSize());
    }
}
