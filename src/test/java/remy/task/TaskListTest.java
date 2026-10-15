package remy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the task collection operations provided by {@link TaskList}.
 *
 * @author LEE-wz
 */
class TaskListTest {

    /**
     * Tests that the constructor creates an empty task list when no arguments are provided.
     */
    @Test
    void constructor_noArguments_emptyTaskListCreated() {
        TaskList taskList = new TaskList();

        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
    }

    @Test
    void constructor_nullInitialTasks_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    void constructor_initialTasksContainNull_assertionErrorThrown() {
        List<Task> initialTasks = new java.util.ArrayList<>();
        initialTasks.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(initialTasks));
    }

    /**
     * Tests that the constructor copies the supplied task list and is unaffected by
     * later changes to the source list.
     */
    @Test
    void constructor_initialTasksSourceListChanges_taskListRemainsUnchanged() {
        Task firstTask = new Todo("Read book");
        List<Task> initialTasks = new java.util.ArrayList<>(List.of(firstTask));

        TaskList taskList = new TaskList(initialTasks);
        initialTasks.add(new Todo("Submit assignment"));

        assertEquals(1, taskList.size());
        assertEquals(firstTask, taskList.get(0));
    }

    /**
     * Tests that adding a task to the task list appends it to the end and increases the size of the list.
     */
    @Test
    void add_task_taskAppendedAndSizeIncreases() {
        Task task = new Todo("Read book");
        TaskList taskList = new TaskList();

        boolean isAdded = taskList.add(task);

        assertTrue(isAdded);
        assertFalse(taskList.isEmpty());
        assertEquals(1, taskList.size());
        assertEquals(task, taskList.get(0));
    }

    @Test
    void add_nullTask_assertionErrorThrown() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.add(null));
    }

    @Test
    void add_duplicateTaskWithDifferentStatus_taskRejectedAndOriginalRetained() {
        Task originalTask = new Todo("Read book");
        Task duplicateTask = new Todo("Read book");
        duplicateTask.markAsDone();
        TaskList taskList = new TaskList(List.of(originalTask));

        boolean isAdded = taskList.add(duplicateTask);

        assertFalse(isAdded);
        assertEquals(List.of(originalTask), taskList.getTasks());
    }

    @Test
    void add_sameDescriptionWithDifferentTaskDetails_bothTasksAdded() {
        Task todo = new Todo("Submit report");
        Task firstDeadline = new Deadline("Submit report", LocalDate.of(2026, 9, 8));
        Task secondDeadline = new Deadline("Submit report", LocalDate.of(2026, 9, 9));
        TaskList taskList = new TaskList(List.of(todo));

        boolean isFirstDeadlineAdded = taskList.add(firstDeadline);
        boolean isSecondDeadlineAdded = taskList.add(secondDeadline);

        assertTrue(isFirstDeadlineAdded);
        assertTrue(isSecondDeadlineAdded);
        assertEquals(List.of(todo, firstDeadline, secondDeadline), taskList.getTasks());
    }

    @Test
    void constructor_duplicateTasks_illegalArgumentExceptionThrown() {
        Task firstTask = new Todo("Read book");
        Task duplicateTask = new Todo("Read book");
        List<Task> duplicateTasks = List.of(firstTask, duplicateTask);

        assertThrows(IllegalArgumentException.class, () -> new TaskList(duplicateTasks));
    }

    /**
     * Tests that retrieving a task by a valid index returns the corresponding task.
     */
    @Test
    void get_validIndex_correspondingTaskReturned() {
        Task firstTask = new Todo("Read book");
        Task secondTask = new Todo("Submit assignment");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        assertEquals(secondTask, taskList.get(1));
    }

    /**
     * Tests that retrieving a task by an invalid index throws an IndexOutOfBoundsException.
     */
    @Test
    void remove_middleTask_removedTaskReturnedAndRemainingTasksRetainOrder() {
        Task firstTask = new Todo("Read book");
        Task removedTask = new Todo("Submit assignment");
        Task lastTask = new Todo("Prepare presentation");
        TaskList taskList = new TaskList(List.of(firstTask, removedTask, lastTask));

        Task result = taskList.remove(1);

        assertEquals(removedTask, result);
        assertEquals(2, taskList.size());
        assertIterableEquals(List.of(firstTask, lastTask), taskList.getTasks());
    }

    /**
     * Tests that removing the only task from the task list results in an empty list.
     */
    @Test
    void isEmpty_onlyTaskRemoved_taskListBecomesEmpty() {
        TaskList taskList = new TaskList(List.of(new Todo("Read book")));

        taskList.remove(0);

        assertTrue(taskList.isEmpty());
    }

    /**
     * Tests that the getTasks method returns an unmodifiable snapshot of the current tasks in the task list.
     */
    @Test
    void find_matchingKeywordIgnoringCase_matchingTasksReturnedInListOrder() {
        Task firstMatch = new Todo("Read book");
        Task nonMatch = new Todo("Submit assignment");
        Task secondMatch = new Todo("Return BOOK");
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matches = taskList.find("bOoK");

        assertIterableEquals(List.of(firstMatch, secondMatch), matches);
    }

    @Test
    void find_noMatchingKeyword_emptyListReturned() {
        TaskList taskList = new TaskList(List.of(new Todo("Read book")));

        assertTrue(taskList.find("assignment").isEmpty());
    }

    @Test
    void find_blankKeyword_assertionErrorThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("Read book")));

        assertThrows(AssertionError.class, () -> taskList.find("   "));
    }

    @Test
    void find_nullKeyword_assertionErrorThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("Read book")));

        assertThrows(AssertionError.class, () -> taskList.find(null));
    }

    @Test
    void sortByDate_mixedTasksAscending_datedTasksSortedAndTodosRemainLast() {
        Task firstTodo = new Todo("Buy ingredients");
        Task timedDeadline = new Deadline("Submit report", LocalDateTime.of(2026, 9, 23, 18, 0));
        timedDeadline.markAsDone();
        Task laterEvent = new Event("Workshop", LocalDateTime.of(2026, 9, 25, 9, 0),
                LocalDateTime.of(2026, 9, 25, 11, 0));
        Task dateOnlyDeadline = new Deadline("Pay fees", LocalDate.of(2026, 9, 23));
        Task earlierEvent = new Event("Tutorial", LocalDateTime.of(2026, 9, 23, 14, 0),
                LocalDateTime.of(2026, 9, 23, 15, 0));
        Task secondTodo = new Todo("Clean kitchen");
        TaskList taskList = new TaskList(List.of(
                firstTodo, timedDeadline, laterEvent, dateOnlyDeadline, earlierEvent, secondTodo));

        taskList.sortByDate(true);

        assertIterableEquals(List.of(
                dateOnlyDeadline, earlierEvent, timedDeadline, laterEvent, firstTodo, secondTodo),
                taskList.getTasks());
    }

    @Test
    void sortByDate_mixedTasksDescending_datedTasksReversedAndTodosRemainLast() {
        Task todo = new Todo("Buy ingredients");
        Task dateOnlyDeadline = new Deadline("Pay fees", LocalDate.of(2026, 9, 23));
        Task timedDeadline = new Deadline("Submit report", LocalDateTime.of(2026, 9, 23, 18, 0));
        Task event = new Event("Workshop", LocalDateTime.of(2026, 9, 25, 9, 0),
                LocalDateTime.of(2026, 9, 30, 11, 0));
        TaskList taskList = new TaskList(List.of(todo, dateOnlyDeadline, event, timedDeadline));

        taskList.sortByDate(false);

        assertIterableEquals(List.of(event, timedDeadline, dateOnlyDeadline, todo), taskList.getTasks());
    }

    @Test
    void sortByDate_equalKeysAndTodos_relativeOrderPreserved() {
        Task firstTodo = new Todo("Buy ingredients");
        Task firstDatedTask = new Deadline("Pay fees", LocalDate.of(2026, 9, 23));
        Task secondTodo = new Todo("Clean kitchen");
        Task secondDatedTask = new Event("Midnight event", LocalDateTime.of(2026, 9, 23, 0, 0),
                LocalDateTime.of(2026, 10, 1, 0, 0));
        TaskList taskList = new TaskList(List.of(firstTodo, firstDatedTask, secondTodo, secondDatedTask));

        taskList.sortByDate(true);

        assertIterableEquals(List.of(firstDatedTask, secondDatedTask, firstTodo, secondTodo),
                taskList.getTasks());
    }

    @Test
    void sortByDate_eventEndsAfterDeadline_eventOrderedByEarlierStart() {
        Task longEvent = new Event("Conference", LocalDate.of(2026, 9, 20),
                LocalDate.of(2026, 9, 30));
        Task deadline = new Deadline("Submit report", LocalDate.of(2026, 9, 25));
        TaskList taskList = new TaskList(List.of(deadline, longEvent));

        taskList.sortByDate(true);

        assertIterableEquals(List.of(longEvent, deadline), taskList.getTasks());
    }

    @Test
    void add_taskAfterSorting_taskAppendedWithoutAutomaticResort() {
        Task laterTask = new Deadline("Submit report", LocalDate.of(2026, 9, 25));
        Task earlierTask = new Deadline("Pay fees", LocalDate.of(2026, 9, 23));
        Task newlyAddedEarlierTask = new Deadline("Book venue", LocalDate.of(2026, 9, 20));
        TaskList taskList = new TaskList(List.of(laterTask, earlierTask));
        taskList.sortByDate(true);

        taskList.add(newlyAddedEarlierTask);

        assertIterableEquals(List.of(earlierTask, laterTask, newlyAddedEarlierTask), taskList.getTasks());
    }

    @Test
    void getTasks_emptyTaskList_emptyUnmodifiableListReturned() {
        List<Task> tasks = new TaskList().getTasks();

        assertTrue(tasks.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> tasks.add(new Todo("Read book")));
    }

    /**
     * Tests that the getTasks method returns the tasks in the order they were added to the task list.
     */
    @Test
    void getTasks_taskListContainsTasks_tasksReturnedInAdditionOrder() {
        Task firstTask = new Todo("Read book");
        Task secondTask = new Todo("Submit assignment");
        TaskList taskList = new TaskList();
        taskList.add(firstTask);
        taskList.add(secondTask);

        List<Task> tasks = taskList.getTasks();

        assertIterableEquals(List.of(firstTask, secondTask), tasks);
    }

    /**
     * Tests that the task snapshot remains stable even after the list changes.
     */
    @Test
    void getTasks_taskListChangesAfterSnapshot_snapshotRemainsUnchanged() {
        Task firstTask = new Todo("Read book");
        TaskList taskList = new TaskList();
        taskList.add(firstTask);
        List<Task> tasks = taskList.getTasks();

        taskList.add(new Todo("Submit assignment"));

        assertEquals(List.of(firstTask), tasks);
    }
}
