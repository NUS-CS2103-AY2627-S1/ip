package luna.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link TaskList#mark(int)} method.
 */
public class TaskListTest {
    @Test
    public void mark_taskAtValidIndex_marksTaskDoneAndReturnsSameTask() {
        Todo todo = new Todo("submit quiz");
        TaskList taskList = new TaskList(todo);

        Task markedTask = taskList.mark(0);

        assertSame(todo, markedTask);
        assertTrue(markedTask.isDone());
        assertEquals("[X]", markedTask.getStatusIcon());
    }

    @Test
    public void mark_taskAlreadyDone_keepsTaskDoneAndReturnsSameTask() {
        Todo todo = new Todo("submit quiz");
        todo.markAsDone();
        TaskList taskList = new TaskList(todo);

        Task markedTask = taskList.mark(0);

        assertSame(todo, markedTask);
        assertTrue(markedTask.isDone());
        assertEquals("[X]", markedTask.getStatusIcon());
    }

    @Test
    public void mark_negativeIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(new Todo("submit quiz"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(-1));
    }

    @Test
    public void mark_indexEqualToSize_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(new Todo("submit quiz"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(1));
    }

    @Test
    public void unmark_doneTask_marksTaskNotDoneAndReturnsSameTask() {
        Todo todo = new Todo("submit quiz");
        todo.markAsDone();
        TaskList taskList = new TaskList(todo);

        Task unmarkedTask = taskList.unmark(0);

        assertSame(todo, unmarkedTask);
        assertFalse(unmarkedTask.isDone());
        assertEquals("[ ]", unmarkedTask.getStatusIcon());
    }

    @Test
    public void clear_tasksPresent_removesEveryTask() {
        TaskList taskList = new TaskList(new Todo("read book"), new Todo("submit quiz"));

        taskList.clear();

        assertEquals(0, taskList.size());
    }

    @Test
    public void constructor_emptyList_createsEmptyTaskList() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        assertTrue(taskList.asList().isEmpty());
    }

    @Test
    public void constructor_listProvided_copiesSourceList() {
        List<Task> sourceTasks = new ArrayList<>(List.of(new Todo("read book")));
        TaskList taskList = new TaskList(sourceTasks);

        sourceTasks.clear();

        assertEquals(1, taskList.size());
    }

    @Test
    public void addAndRemove_validTask_updatesListAndReturnsTask() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book");

        taskList.add(todo);
        Task removedTask = taskList.remove(0);

        assertSame(todo, removedTask);
        assertEquals(0, taskList.size());
    }

    @Test
    public void add_nullTask_throwsIllegalArgumentException() {
        TaskList taskList = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> taskList.add(null));
    }

    @Test
    public void asList_returnedListIsModified_keepsOriginalListUnchanged() {
        TaskList taskList = new TaskList(new Todo("read book"));
        List<Task> copiedTasks = taskList.asList();

        copiedTasks.clear();

        assertEquals(1, taskList.size());
    }

    @Test
    public void constructor_nullTask_assertsTaskListInvariant() {
        assertThrows(AssertionError.class, () -> new TaskList((Task) null));
    }

    @Test
    public void findTaskIndices_keywordMatchesMultipleTasks_returnsOriginalIndices() {
        TaskList taskList = new TaskList(
                new Todo("read book"),
                new Todo("submit quiz"),
                new Todo("return book"));

        List<Integer> matchingTaskIndices = taskList.findTaskIndices("book");

        assertIterableEquals(List.of(0, 2), matchingTaskIndices);
    }

    @Test
    public void findTaskIndices_keywordUsesCaseInsensitiveMatching_returnsOriginalIndex() {
        TaskList taskList = new TaskList(new Todo("Read Book"), new Todo("write summary"));

        List<Integer> matchingTaskIndices = taskList.findTaskIndices("book");

        assertIterableEquals(List.of(0), matchingTaskIndices);
    }

    @Test
    public void findTaskIndices_keywordMatchesNoTasks_returnsEmptyList() {
        TaskList taskList = new TaskList(new Todo("read book"), new Todo("submit quiz"));

        List<Integer> matchingTaskIndices = taskList.findTaskIndices("meeting");

        assertTrue(matchingTaskIndices.isEmpty());
    }

    @Test
    public void containsTaskWithSameDetails_caseAndStatusDiffer_returnsTrue() {
        Todo existingTask = new Todo("Read Book");
        existingTask.markAsDone();
        TaskList taskList = new TaskList(existingTask);

        boolean containsDuplicate = taskList.containsTaskWithSameDetails(new Todo("read   book"));

        assertTrue(containsDuplicate);
    }

    @Test
    public void containsTaskWithSameDetails_eventTimeDiffers_returnsFalse() {
        Event existingEvent = new Event("meeting",
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 11, 0));
        Event laterEvent = new Event("meeting",
                LocalDateTime.of(2026, 9, 15, 11, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0));
        TaskList taskList = new TaskList(existingEvent);

        assertFalse(taskList.containsTaskWithSameDetails(laterEvent));
    }

    @Test
    public void containsTaskWithSameDetails_nullCandidate_returnsFalse() {
        TaskList taskList = new TaskList(new Todo("read book"));

        assertFalse(taskList.containsTaskWithSameDetails(null));
    }

    @Test
    public void unmark_indexOutsideList_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(new Todo("read book"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(1));
    }
}
