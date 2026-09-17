package lobby.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection operations and one-based task numbering in {@link TaskList}.
 */
public class TaskListTest {

    /**
     * Verifies that find preserves the original order of matching tasks.
     */
    @Test
    public void find_keywordInDescriptions_returnsMatchingTasksInOriginalOrder() {
        Task matchingTodo = new Todo("read book");
        Task nonMatchingTodo = new Todo("buy groceries");
        Task matchingDeadline = new Deadline("return book", java.time.LocalDate.of(2026, 6, 6));
        TaskList taskList = new TaskList(List.of(matchingTodo, nonMatchingTodo, matchingDeadline));

        TaskList matches = taskList.find("book");

        assertIterableEquals(List.of(matchingTodo, matchingDeadline), matches.asList());
    }

    @Test
    public void find_keywordCaseDiffers_returnsMatchingTask() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        TaskList matches = taskList.find("Book");

        assertEquals(1, matches.size());
    }

    @Test
    public void find_multipleKeywords_requiresEveryPartialKeywordInAnyOrder() {
        Task matchingTask = new Todo("Finish MARKING lab reports");
        TaskList tasks = new TaskList(matchingTask, new Todo("prepare lab"), new Todo("marking essays"));

        assertEquals(List.of(matchingTask), tasks.find("  LAB\tmark  ").asList());
        assertEquals(0, tasks.find("lab missing").size());
        assertEquals(0, new TaskList().find("lab").size());
    }

    @Test
    public void find_keywords_matchesBothCompletionStatesWithoutChangingSourceTasks() {
        Task completed = new Todo("mark lab");
        completed.markAsDone();
        Task pending = new Todo("prepare lab");
        Task other = new Todo("buy book");
        TaskList tasks = new TaskList(completed, pending, other);

        assertEquals(List.of(completed, pending), tasks.find("LAB").asList());
        assertEquals(List.of(completed, pending, other), tasks.asList());
        assertTrue(completed.isDone());
        assertFalse(pending.isDone());
    }

    @Test
    public void find_dateOnlyInDeadlineDetails_doesNotMatchDescriptionSearch() {
        TaskList tasks = new TaskList(new Deadline("return book", java.time.LocalDate.of(2026, 6, 6)));

        assertEquals(0, tasks.find("2026").size());
    }

    @Test
    public void constructor_sourceListChanged_taskListUnaffected() {
        List<Task> source = new ArrayList<>();
        source.add(new Todo("first"));
        TaskList taskList = new TaskList(source);

        source.add(new Todo("second"));

        assertEquals(1, taskList.size());
        assertEquals("first", taskList.get(1).getDescription());
    }

    @Test
    public void constructor_varargsTasks_containsTasksInArgumentOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");

        TaskList taskList = new TaskList(first, second);

        assertIterableEquals(List.of(first, second), taskList.asList());
    }

    @Test
    public void constructor_sourceArrayChanged_taskListUnaffected() {
        Task first = new Todo("first");
        Task[] source = {first};
        TaskList taskList = new TaskList(source);

        source[0] = new Todo("replacement");

        assertSame(first, taskList.get(1));
    }

    /**
     * Verifies that tasks added to the end retain their insertion order.
     */
    @Test
    public void add_tasksAdded_returnsTasksInInsertionOrder() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        taskList.add(first);
        taskList.add(second);

        assertAll(() ->
                assertEquals(2, taskList.size()), () ->
                assertSame(first, taskList.get(1)), () ->
                assertSame(second, taskList.get(2)));
    }

    /**
     * Verifies that insertion uses the task list's one-based positions.
     */
    @Test
    public void add_oneBasedPosition_insertsTaskAtPosition() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        Task inserted = new Todo("inserted");
        TaskList taskList = new TaskList(List.of(first, second));

        taskList.add(2, inserted);

        assertIterableEquals(List.of(first, inserted, second), taskList.asList());
    }

    /**
     * Verifies that deletion removes and returns the selected task.
     */
    @Test
    public void delete_validTaskNumber_removesAndReturnsTask() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList taskList = new TaskList(List.of(first, second));

        Task deleted = taskList.delete(1);

        assertAll(() ->
                assertSame(first, deleted), () ->
                assertEquals(1, taskList.size()), () ->
                assertSame(second, taskList.get(1)));
    }

    /**
     * Verifies that marking and unmarking update and return the selected task.
     */
    @Test
    public void markAndUnmark_validTaskNumber_updatesAndReturnsTask() {
        Task task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task));

        Task markedTask = taskList.mark(1);

        assertSame(task, markedTask);
        assertTrue(task.isDone());

        Task unmarkedTask = taskList.unmark(1);

        assertSame(task, unmarkedTask);
        assertFalse(task.isDone());
    }

    /**
     * Verifies the valid lower and upper bounds for one-based task numbers.
     */
    @Test
    public void containsTaskNumber_boundaries_returnsExpectedResult() {
        TaskList taskList = new TaskList(List.of(new Todo("first"), new Todo("second")));

        assertAll(() ->
                assertFalse(taskList.containsTaskNumber(-1)), () ->
                assertFalse(taskList.containsTaskNumber(0)), () ->
                assertTrue(taskList.containsTaskNumber(1)), () ->
                assertTrue(taskList.containsTaskNumber(2)), () ->
                assertFalse(taskList.containsTaskNumber(3)));
    }

    /**
     * Verifies that list snapshots are immutable and independent of later changes.
     */
    @Test
    public void asList_listModified_snapshotAndTaskListUnaffected() {
        Task first = new Todo("first");
        TaskList taskList = new TaskList(List.of(first));
        List<Task> snapshot = taskList.asList();

        taskList.add(new Todo("second"));

        assertAll(() ->
                assertEquals(List.of(first), snapshot), () ->
                assertThrows(UnsupportedOperationException.class, () ->
                        snapshot.add(new Todo("third"))), () ->
                assertEquals(2, taskList.size()));
    }

    /**
     * Verifies that task operations reject numbers outside the list bounds.
     */
    @Test
    public void taskOperations_invalidTaskNumber_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("only task")));

        assertAll(() ->
                assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0)), () ->
                assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(2)), () ->
                assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(0)), () ->
                assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(2)), () ->
                assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(0)));
    }
}
