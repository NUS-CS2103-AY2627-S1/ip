package magnus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the task collection operations provided by {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void constructor_noTasks_createsEmptyTaskList() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void constructor_listOfTasks_createsIndependentTaskListWithSameTasks() {
        Task task1 = new ToDoTask("read book");
        Task task2 = new ToDoTask("return book");
        List<Task> originalTasks = new ArrayList<>(List.of(task1, task2));

        TaskList tasks = new TaskList(originalTasks);
        originalTasks.clear();

        assertEquals(2, tasks.getTaskCount());
        assertSame(task1, tasks.getTask(0));
        assertSame(task2, tasks.getTask(1));
    }

    @Test
    public void constructor_duplicateTaskDetails_throwsIllegalArgumentException() {
        ToDoTask firstTask = new ToDoTask("read book");
        ToDoTask duplicateTask = new ToDoTask("read book");
        duplicateTask.markAsDone();

        assertThrows(IllegalArgumentException.class, () ->
                new TaskList(List.of(firstTask, duplicateTask)));
    }

    @Test
    public void constructor_nullCollectionOrMember_rejectsNull() {
        assertThrows(NullPointerException.class, () -> new TaskList(null));
        assertThrows(IllegalArgumentException.class, () ->
                new TaskList(java.util.Arrays.asList(new ToDoTask("read book"), null)));
    }

    @Test
    public void getTasks_existingTasks_returnsImmutableSnapshot() {
        Task task1 = new ToDoTask("read book");
        Task task2 = new ToDoTask("return book");
        TaskList tasks = new TaskList(List.of(task1));

        List<Task> snapshotTasks = tasks.getTasks();
        tasks.addTask(task2);

        assertEquals(1, snapshotTasks.size());
        assertSame(task1, snapshotTasks.get(0));
        assertThrows(UnsupportedOperationException.class, () -> snapshotTasks.add(task2));
    }

    @Test
    public void filterDeadlinesOnDate_mixedTasks_returnsOnlyDeadlinesOnSpecifiedDate() {
        DeadlineTask matchingTask = new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 8, 27, 18, 0));
        DeadlineTask differentDateTask = new DeadlineTask(
                "pay bill", LocalDateTime.of(2026, 8, 28, 18, 0));
        ToDoTask taskWithoutDate = new ToDoTask("read book");
        TaskList tasks = new TaskList(List.of(matchingTask, differentDateTask, taskWithoutDate));

        TaskList filteredTasks = tasks.filterDeadlinesOnDate(LocalDate.of(2026, 8, 27));

        assertIterableEquals(List.of(matchingTask), filteredTasks.getTasks());
        assertEquals(3, tasks.getTaskCount());
    }

    @Test
    public void filterEventsWithinDateRange_mixedTasks_returnsOnlyEventsWithinInclusiveRange() {
        EventTask boundaryTask = new EventTask(
                "course", LocalDateTime.of(2026, 8, 10, 9, 0),
                LocalDateTime.of(2026, 8, 20, 17, 0));
        EventTask insideRangeTask = new EventTask(
                "conference", LocalDateTime.of(2026, 8, 12, 9, 0),
                LocalDateTime.of(2026, 8, 18, 17, 0));
        EventTask startsBeforeRangeTask = new EventTask(
                "camp", LocalDateTime.of(2026, 8, 9, 9, 0),
                LocalDateTime.of(2026, 8, 18, 17, 0));
        EventTask endsAfterRangeTask = new EventTask(
                "workshop", LocalDateTime.of(2026, 8, 12, 9, 0),
                LocalDateTime.of(2026, 8, 21, 17, 0));
        DeadlineTask nonEventTask = new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 8, 15, 18, 0));
        TaskList tasks = new TaskList(List.of(
                boundaryTask, insideRangeTask, startsBeforeRangeTask, endsAfterRangeTask, nonEventTask));

        TaskList filteredTasks = tasks.filterEventsWithinDateRange(
                LocalDate.of(2026, 8, 10), LocalDate.of(2026, 8, 20));

        assertIterableEquals(List.of(boundaryTask, insideRangeTask), filteredTasks.getTasks());
        assertEquals(5, tasks.getTaskCount());
    }

    @Test
    public void filterTasksByDescription_mixedTasks_returnsCaseInsensitiveSubstringMatches() {
        Task firstMatch = new ToDoTask("Read book");
        Task nonMatch = new ToDoTask("submit report");
        Task secondMatch = new DeadlineTask(
                "return BOOK", LocalDateTime.of(2026, 6, 6, 18, 0));
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        TaskList filteredTasks = tasks.filterTasksByDescription("book");

        assertIterableEquals(List.of(firstMatch, secondMatch), filteredTasks.getTasks());
        assertEquals(3, tasks.getTaskCount());
    }

    @Test
    public void countCompletedTasks_mixedCompletionStates_returnsCompletedCount() {
        Task completedTask = new ToDoTask("read book");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(List.of(completedTask, new ToDoTask("write notes")));

        long completedTaskCount = tasks.countCompletedTasks();

        assertEquals(1, completedTaskCount);
    }

    @Test
    public void countCompletedTasks_emptyTaskList_returnsZero() {
        assertEquals(0, new TaskList().countCompletedTasks());
    }

    @Test
    public void addTask_newTask_addsTaskAtEnd() {
        Task task1 = new ToDoTask("read book");
        Task task2 = new ToDoTask("return book");
        TaskList tasks = new TaskList(List.of(task1));

        tasks.addTask(task2);

        assertEquals(2, tasks.getTaskCount());
        assertSame(task2, tasks.getTask(1));
    }

    @Test
    public void addTask_duplicateTaskDetails_throwsIllegalArgumentException() {
        TaskList tasks = new TaskList(List.of(new ToDoTask("read book")));

        assertThrows(IllegalArgumentException.class, () ->
                tasks.addTask(new ToDoTask("read book")));
    }

    @Test
    public void addTask_nullTask_throwsNullPointerException() {
        TaskList tasks = new TaskList();

        assertThrows(NullPointerException.class, () -> tasks.addTask(null));
    }

    @Test
    public void containsTaskWithSameDetails_sameDetailsDifferentStatus_returnsTrue() {
        ToDoTask existingTask = new ToDoTask("read book");
        ToDoTask candidate = new ToDoTask("read book");
        existingTask.markAsDone();
        TaskList tasks = new TaskList(List.of(existingTask));

        assertTrue(tasks.containsTaskWithSameDetails(candidate));
    }

    @Test
    public void containsTaskWithSameDetailsExcept_excludedMatchingTask_returnsFalse() {
        ToDoTask task = new ToDoTask("read book");
        TaskList tasks = new TaskList(List.of(task));

        assertFalse(tasks.containsTaskWithSameDetailsExcept(new ToDoTask("read book"), 0));
    }

    @Test
    public void containsTaskWithSameDetails_nullCandidate_throwsNullPointerException() {
        TaskList tasks = new TaskList();

        assertThrows(NullPointerException.class, () -> tasks.containsTaskWithSameDetails(null));
    }

    @Test
    public void restoreSnapshot_mutatedList_restoresMembershipAndCompletionState() {
        ToDoTask originalTask = new ToDoTask("read book");
        TaskList tasks = new TaskList(List.of(originalTask));
        TaskList.Snapshot snapshot = tasks.createSnapshot();

        originalTask.markAsDone();
        tasks.addTask(new ToDoTask("write essay"));
        tasks.restoreSnapshot(snapshot);

        assertEquals(1, tasks.getTaskCount());
        assertSame(originalTask, tasks.getTask(0));
        assertEquals("[T][ ] read book", originalTask.toString());
    }

    @Test
    public void restoreSnapshot_nullSnapshot_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TaskList().restoreSnapshot(null));
    }

    @Test
    public void restoreSnapshot_originallyCompletedTask_restoresCompletedState() {
        Task task = new ToDoTask("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));
        TaskList.Snapshot snapshot = tasks.createSnapshot();
        task.markAsUndone();

        tasks.restoreSnapshot(snapshot);

        assertTrue(task.isDone());
    }

    @Test
    public void removeTask_validIndex_returnsRemovedTaskAndShiftsRemainingTasks() {
        Task task1 = new ToDoTask("read book");
        Task task2 = new ToDoTask("return book");
        Task task3 = new ToDoTask("buy book");
        TaskList tasks = new TaskList(List.of(task1, task2, task3));

        Task removedTask = tasks.removeTask(1);

        assertSame(task2, removedTask);
        assertEquals(2, tasks.getTaskCount());
        assertSame(task1, tasks.getTask(0));
        assertSame(task3, tasks.getTask(1));
    }

    @Test
    public void removeTask_indexOutsideTaskList_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.removeTask(0));
    }

    @Test
    public void getTask_validIndex_returnsTaskAtIndex() {
        Task task1 = new ToDoTask("read book");
        Task task2 = new ToDoTask("return book");
        TaskList tasks = new TaskList(List.of(task1, task2));

        Task result = tasks.getTask(1);

        assertSame(task2, result);
    }

    @Test
    public void getTask_indexOutsideTaskList_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList(List.of(new ToDoTask("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.getTask(1));
    }

    @Test
    public void replaceTask_validIndex_replacesOnlySelectedTask() {
        Task task1 = new ToDoTask("read book");
        Task task2 = new ToDoTask("return book");
        Task replacementTask = new ToDoTask("write essay");
        TaskList tasks = new TaskList(List.of(task1, task2));

        tasks.replaceTask(1, replacementTask);

        assertSame(task1, tasks.getTask(0));
        assertSame(replacementTask, tasks.getTask(1));
    }

    @Test
    public void replaceTask_duplicateOfOtherTask_throwsIllegalArgumentException() {
        ToDoTask firstTask = new ToDoTask("read book");
        ToDoTask secondTask = new ToDoTask("write essay");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        assertThrows(IllegalArgumentException.class, () ->
                tasks.replaceTask(1, new ToDoTask("read book")));
        assertSame(secondTask, tasks.getTask(1));
    }

    @Test
    public void replaceTask_indexOutsideTaskList_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () ->
                tasks.replaceTask(0, new ToDoTask("read book")));
    }

    @Test
    public void replaceTask_nullTask_throwsNullPointerExceptionWithoutReplacingTask() {
        Task originalTask = new ToDoTask("read book");
        TaskList tasks = new TaskList(List.of(originalTask));

        assertThrows(NullPointerException.class, () -> tasks.replaceTask(0, null));

        assertSame(originalTask, tasks.getTask(0));
    }

    @Test
    public void markTaskAsDone_validIndex_marksOnlySelectedTaskAsDone() {
        Task task1 = new ToDoTask("read book");
        Task task2 = new ToDoTask("return book");
        TaskList tasks = new TaskList(List.of(task1, task2));

        tasks.markTaskAsDone(1);

        assertEquals("[T][ ] read book", task1.toString());
        assertEquals("[T][X] return book", task2.toString());
    }

    @Test
    public void markTaskAsDone_indexOutsideTaskList_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markTaskAsDone(0));
    }

    @Test
    public void markTaskAsUndone_validIndex_marksOnlySelectedTaskAsUndone() {
        Task task1 = new ToDoTask("read book");
        Task task2 = new ToDoTask("return book");
        task1.markAsDone();
        task2.markAsDone();
        TaskList tasks = new TaskList(List.of(task1, task2));

        tasks.markTaskAsUndone(1);

        assertEquals("[T][X] read book", task1.toString());
        assertEquals("[T][ ] return book", task2.toString());
    }

    @Test
    public void markTaskAsUndone_indexOutsideTaskList_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markTaskAsUndone(0));
    }

    @Test
    public void formatTasks_multipleTasks_returnsOneBasedNumberedList() {
        Task task1 = new ToDoTask("read book");
        Task task2 = new ToDoTask("return book");
        task2.markAsDone();
        TaskList tasks = new TaskList(List.of(task1, task2));
        String expectedOutput = String.join(System.lineSeparator(),
                "\t1. [T][ ] read book",
                "\t2. [T][X] return book");

        String output = tasks.formatTasks();

        assertEquals(expectedOutput, output);
    }

    @Test
    public void formatTasks_emptyList_returnsEmptyString() {
        assertEquals("", new TaskList().formatTasks());
    }
}
