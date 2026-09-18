package bobby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list mutation, ordering, and safe task-list access.
 */
class TaskListTest {
    /**
     * Verifies the empty-list state and the constructor's defensive copy.
     */
    @Test
    void constructors_emptyAndSuppliedLists_expectedIndependentStateCreated() {
        TaskList emptyTaskList = new TaskList();
        Task suppliedTask = new Todo("read book");
        List<Task> suppliedTasks = new ArrayList<>(List.of(suppliedTask));
        TaskList populatedTaskList = new TaskList(suppliedTasks);

        suppliedTasks.clear();

        assertTrue(emptyTaskList.isEmpty());
        assertFalse(populatedTaskList.isEmpty());
        assertEquals(1, populatedTaskList.size());
        assertEquals(suppliedTask, populatedTaskList.get(0));
    }

    /**
     * Verifies that constructors and additions reject missing task data.
     */
    @Test
    void constructorsAndAdd_nullTaskData_assertionErrorThrown() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> new TaskList(null));
        assertThrows(AssertionError.class, () -> new TaskList(Arrays.asList((Task) null)));
        assertThrows(AssertionError.class, () -> taskList.add(null));
    }

    /**
     * Verifies that adding and removing tasks preserves order and updates the list size.
     */
    @Test
    void addAndRemove_tasksAddedAndRemoved_expectedOrderAndSize() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        TaskList taskList = new TaskList();

        taskList.add(firstTask);
        taskList.add(secondTask);
        Task removedTask = taskList.remove(0);

        assertEquals(firstTask, removedTask);
        assertEquals(1, taskList.size());
        assertEquals(secondTask, taskList.get(0));
    }

    /**
     * Verifies that mark and unmark operations change only the selected task's state.
     */
    @Test
    void statusMethods_selectedTaskMarkedAndUnmarked_onlySelectedTaskChanges() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        taskList.markAsDone(1);
        assertEquals(" ", firstTask.getStatusIcon());
        assertEquals("X", secondTask.getStatusIcon());

        taskList.markAsNotDone(1);
        assertEquals(" ", secondTask.getStatusIcon());
    }

    /**
     * Verifies that timestamped marking records the supplied completion time on the selected task.
     */
    @Test
    void markAsDone_completionDateTimeSupplied_selectedTaskStoresTimestamp() {
        Task task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task));
        LocalDateTime completionDateTime = LocalDateTime.of(2026, 9, 8, 10, 15, 30);

        taskList.markAsDone(0, completionDateTime);

        assertEquals(completionDateTime, task.getCompletionDateTime().orElseThrow());
    }

    /**
     * Verifies that index-based operations reject negative and past-the-end indices.
     */
    @Test
    void indexBasedMethods_invalidIndices_assertionErrorThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> taskList.get(-1));
        assertThrows(AssertionError.class, () -> taskList.get(1));
        assertThrows(AssertionError.class, () -> taskList.markAsDone(-1));
        assertThrows(AssertionError.class, () -> taskList.markAsDone(1));
        assertThrows(AssertionError.class, () -> taskList.markAsDone(-1, LocalDateTime.now()));
        assertThrows(AssertionError.class, () -> taskList.markAsNotDone(1));
        assertThrows(AssertionError.class, () -> taskList.remove(1));
    }

    /**
     * Verifies that timestamped marking rejects a missing completion time.
     */
    @Test
    void markAsDone_nullCompletionDateTime_assertionErrorThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> taskList.markAsDone(0, null));
    }

    /**
     * Verifies that the task-list view cannot be modified and does not change after later additions.
     */
    @Test
    void asList_taskListChanges_unmodifiableSnapshotReturned() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("first task"));
        List<Task> snapshot = taskList.asList();

        taskList.add(new Todo("second task"));

        assertEquals(1, snapshot.size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("third task")));
    }

    /**
     * Verifies that searching descriptions ignores letter case and preserves task order.
     */
    @Test
    void findTasksContaining_matchingDescriptions_matchingTasksInOrderReturned() {
        Task firstTask = new Todo("Read book");
        Task secondTask = new Todo("return book");
        Task thirdTask = new Todo("buy groceries");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask, thirdTask));

        List<Task> matchingTasks = taskList.findTasksContaining("BOOK");

        assertEquals(List.of(firstTask, secondTask), matchingTasks);
        assertThrows(UnsupportedOperationException.class, () -> matchingTasks.add(thirdTask));
    }

    /**
     * Verifies that searching for an absent keyword returns an empty list.
     */
    @Test
    void findTasksContaining_noMatchingDescriptions_emptyListReturned() {
        TaskList taskList = new TaskList(List.of(new Todo("read book"), new Todo("buy groceries")));

        List<Task> matchingTasks = taskList.findTasksContaining("exercise");

        assertEquals(List.of(), matchingTasks);
    }

    /**
     * Verifies that duplicate detection ignores description case but includes task type and date details.
     */
    @Test
    void hasTaskWithSameDetails_variedTasks_expectedDuplicateDecisionsReturned() {
        TaskList taskList = new TaskList(List.of(
                new Todo("Read book"),
                new Deadline("submit report", LocalDateTime.of(2026, 9, 20, 12, 0))));

        assertEquals(true, taskList.hasTaskWithSameDetails(new Todo("read book")));
        assertEquals(false, taskList.hasTaskWithSameDetails(new Deadline(
                "submit report", LocalDateTime.of(2026, 9, 21, 12, 0))));
        assertEquals(false, taskList.hasTaskWithSameDetails(new Todo("submit report")));
    }

    /**
     * Verifies that duplicate detection rejects a missing candidate task.
     */
    @Test
    void hasTaskWithSameDetails_nullCandidate_assertionErrorThrown() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.hasTaskWithSameDetails(null));
    }

    /**
     * Verifies that searching rejects missing and blank keywords.
     */
    @Test
    void findTasksContaining_invalidKeyword_assertionErrorThrown() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.findTasksContaining(null));
        assertThrows(AssertionError.class, () -> taskList.findTasksContaining("   "));
    }

    /**
     * Verifies that completion counts use current state and a half-open date-time interval.
     */
    @Test
    void completionCounts_variedCompletionStates_expectedCountsReturned() {
        LocalDateTime weekStart = LocalDateTime.of(2026, 9, 7, 0, 0);
        LocalDateTime nextWeekStart = LocalDateTime.of(2026, 9, 14, 0, 0);
        Task completedAtStart = new Todo("start boundary");
        completedAtStart.markAsDone(weekStart);
        Task completedBeforeEnd = new Todo("before end boundary");
        completedBeforeEnd.markAsDone(nextWeekStart.minusSeconds(1));
        Task completedAtEnd = new Todo("end boundary");
        completedAtEnd.markAsDone(nextWeekStart);
        Task completedAtUnknownTime = new Todo("legacy task");
        completedAtUnknownTime.markAsDone();
        Task pendingTask = new Todo("pending task");
        TaskList taskList = new TaskList(List.of(completedAtStart, completedBeforeEnd,
                completedAtEnd, completedAtUnknownTime, pendingTask));

        assertEquals(4, taskList.countCompletedTasks());
        assertEquals(2, taskList.countTasksCompletedBetween(weekStart, nextWeekStart));

        taskList.markAsNotDone(0);
        taskList.remove(1);

        assertEquals(2, taskList.countCompletedTasks());
        assertEquals(0, taskList.countTasksCompletedBetween(weekStart, nextWeekStart));
    }

    /**
     * Verifies that completion counting rejects missing or non-increasing interval bounds.
     */
    @Test
    void countTasksCompletedBetween_invalidInterval_assertionErrorThrown() {
        TaskList taskList = new TaskList();
        LocalDateTime startDateTime = LocalDateTime.of(2026, 9, 7, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2026, 9, 14, 0, 0);

        assertThrows(AssertionError.class, () ->
                taskList.countTasksCompletedBetween(null, endDateTime));
        assertThrows(AssertionError.class, () ->
                taskList.countTasksCompletedBetween(startDateTime, null));
        assertThrows(AssertionError.class, () ->
                taskList.countTasksCompletedBetween(startDateTime, startDateTime));
        assertThrows(AssertionError.class, () ->
                taskList.countTasksCompletedBetween(endDateTime, startDateTime));
    }
}
