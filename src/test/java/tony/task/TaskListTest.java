package tony.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list mutation and protection of its internal list structure.
 */
public class TaskListTest {
    /** Verifies that constructing from a null collection fails at the class boundary. */
    @Test
    public void constructor_nullList_throwsAssertionError() {
        AssertionError error = assertThrows(AssertionError.class, () -> new TaskList(null));

        assertEquals("The source task collection must not be null", error.getMessage());
    }

    /** Verifies that a task list rejects null elements that would break every list operation. */
    @Test
    public void constructor_nullTask_throwsAssertionError() {
        ArrayList<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(null);

        AssertionError error = assertThrows(AssertionError.class, () -> new TaskList(sourceTasks));

        assertEquals("A task list must not contain null tasks", error.getMessage());
    }

    /** Verifies that construction protects the task list from later source-list changes. */
    @Test
    public void constructor_sourceListChangedAfterConstruction_taskListUnaffected() {
        Todo originalTask = new Todo("original task");
        ArrayList<Task> sourceTasks = new ArrayList<>(List.of(originalTask));
        TaskList taskList = new TaskList(sourceTasks);

        sourceTasks.clear();

        assertEquals(1, taskList.size());
        assertSame(originalTask, taskList.get(0));
    }

    /** Verifies that adding a task appends it to the list. */
    @Test
    public void add_task_appendsTask() {
        Todo firstTask = new Todo("first task");
        Todo secondTask = new Todo("second task");
        TaskList taskList = new TaskList(List.of(firstTask));

        taskList.add(secondTask);

        assertEquals(2, taskList.size());
        assertSame(secondTask, taskList.get(1));
    }

    /** Verifies that add preserves the invariant that every list element is a task. */
    @Test
    public void add_nullTask_throwsAssertionError() {
        TaskList taskList = new TaskList();

        AssertionError error = assertThrows(AssertionError.class, () -> taskList.add(null));

        assertEquals("A task list must not contain null tasks", error.getMessage());
    }

    /** Verifies detail lookup finds equivalent tasks and rejects unrelated ones. */
    @Test
    public void containsSameDetails_variedTasks_returnsWhetherEquivalentTaskExists() {
        TaskList taskList = new TaskList(List.of(new Todo("Read Book")));

        assertTrue(taskList.containsSameDetails(new Todo("read book")));
        assertFalse(taskList.containsSameDetails(new Todo("write report")));
    }

    /** Verifies detail lookup rejects a null comparison task. */
    @Test
    public void containsSameDetails_nullTask_throwsAssertionError() {
        TaskList taskList = new TaskList();

        AssertionError error = assertThrows(AssertionError.class, () -> taskList.containsSameDetails(null));

        assertEquals("A task to compare must not be null", error.getMessage());
    }

    /** Verifies that deletion removes and returns the selected task. */
    @Test
    public void delete_middleTask_removesAndReturnsTask() {
        Todo firstTask = new Todo("first task");
        Todo middleTask = new Todo("middle task");
        Todo lastTask = new Todo("last task");
        TaskList taskList = new TaskList(List.of(firstTask, middleTask, lastTask));

        Task deletedTask = taskList.delete(1);

        assertSame(middleTask, deletedTask);
        assertEquals(List.of(firstTask, lastTask), taskList.getTasks());
    }

    /** Verifies that bulk deletion uses original indexes and returns tasks in original list order. */
    @Test
    public void deleteTasks_unorderedIndexes_deletesAndReturnsTasksInOriginalOrder() {
        Todo firstTask = new Todo("first task");
        Todo secondTask = new Todo("second task");
        Todo thirdTask = new Todo("third task");
        Todo fourthTask = new Todo("fourth task");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask, thirdTask, fourthTask));

        List<Task> deletedTasks = taskList.deleteTasks(List.of(3, 1));

        assertEquals(List.of(secondTask, fourthTask), deletedTasks);
        assertEquals(List.of(firstTask, thirdTask), taskList.getTasks());
    }

    /** Verifies that bulk deletion checks all preconditions before changing the list. */
    @Test
    public void deleteTasks_invalidIndexes_throwAssertionErrorWithoutDeletingTasks() {
        Todo task = new Todo("task");
        TaskList taskList = new TaskList(List.of(task));

        assertThrows(AssertionError.class, () -> taskList.deleteTasks(null));
        assertThrows(AssertionError.class, () -> taskList.deleteTasks(java.util.Arrays.asList(0, null)));
        assertThrows(AssertionError.class, () -> taskList.deleteTasks(List.of(0, 0)));
        assertThrows(AssertionError.class, () -> taskList.deleteTasks(List.of(1)));
        assertEquals(List.of(task), taskList.getTasks());
    }

    /** Verifies that marking a task completes and returns the selected task. */
    @Test
    public void mark_validIndex_marksAndReturnsSelectedTask() {
        Todo firstTask = new Todo("first task");
        Todo selectedTask = new Todo("selected task");
        TaskList taskList = new TaskList(List.of(firstTask, selectedTask));

        Task markedTask = taskList.mark(1);

        assertSame(selectedTask, markedTask);
        assertEquals("X", selectedTask.getStatusIcon());
        assertEquals(" ", firstTask.getStatusIcon());
    }

    /** Verifies that unmarking a task makes and returns it as incomplete. */
    @Test
    public void unmark_completedTask_marksAndReturnsSelectedTaskAsIncomplete() {
        Todo selectedTask = new Todo("selected task");
        selectedTask.markAsDone();
        TaskList taskList = new TaskList(List.of(selectedTask));

        Task unmarkedTask = taskList.unmark(0);

        assertSame(selectedTask, unmarkedTask);
        assertEquals(" ", selectedTask.getStatusIcon());
    }

    /** Verifies that the completed count reflects marking and unmarking tasks. */
    @Test
    public void countCompletedTasks_mixedTaskStates_returnsCompletedCount() {
        Todo completedTask = new Todo("completed task");
        completedTask.markAsDone();
        TaskList taskList = new TaskList(List.of(completedTask, new Todo("incomplete task")));

        assertEquals(1, taskList.countCompletedTasks());

        taskList.unmark(0);
        assertEquals(0, taskList.countCompletedTasks());
    }

    /** Verifies that callers cannot modify the returned task snapshot. */
    @Test
    public void find_keywordWithDifferentCase_returnsMatchingDescriptionsInOriginalOrder() {
        Todo firstMatch = new Todo("Read Book");
        Todo nonMatch = new Todo("write report");
        Deadline secondMatch = new Deadline("return book", java.time.LocalDate.of(2019, 6, 6));
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        TaskList matches = taskList.find("BOOK");

        assertEquals(2, matches.size());
        assertSame(firstMatch, matches.get(0));
        assertSame(secondMatch, matches.get(1));
    }

    @Test
    public void find_keywordOnlyInFormattedDate_returnsNoMatches() {
        Deadline task = new Deadline("return library item", java.time.LocalDate.of(2019, 6, 6));
        TaskList taskList = new TaskList(List.of(task));

        TaskList matches = taskList.find("Jun");

        assertEquals(0, matches.size());
    }

    /** Verifies the command parser's contract that searches have a non-blank keyword. */
    @Test
    public void find_blankKeyword_throwsAssertionError() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        AssertionError error = assertThrows(AssertionError.class, () -> taskList.find("  "));

        assertEquals("A search keyword must not be blank", error.getMessage());
    }

    /** Verifies that a null search keyword fails at the class boundary. */
    @Test
    public void find_nullKeyword_throwsAssertionError() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        AssertionError error = assertThrows(AssertionError.class, () -> taskList.find(null));

        assertEquals("A search keyword must not be null", error.getMessage());
    }

    @Test
    public void getTasks_returnedListCannotBeModified() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));
        List<Task> tasks = taskList.getTasks();

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.add(new Todo("another task")));
    }

    /** Verifies that a task snapshot is unaffected by later additions. */
    @Test
    public void getTasks_taskAddedAfterSnapshot_snapshotUnaffected() {
        TaskList taskList = new TaskList(List.of(new Todo("original task")));
        List<Task> snapshot = taskList.getTasks();

        taskList.add(new Todo("later task"));

        assertEquals(1, snapshot.size());
        assertEquals(2, taskList.size());
    }

    /** Verifies that list operations reject an out-of-range index. */
    @Test
    public void mutationMethods_indexOutOfRange_throwIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
    }
}
