package duck.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the public operations provided by {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void constructor_noArguments_createsEmptyTaskList() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void constructor_initialTasks_preservesTaskOrder() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");

        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        assertEquals(2, tasks.getTaskCount());
        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    public void constructor_sourceListChanged_taskListRemainsUnchanged() {
        Task firstTask = new Todo("first");
        ArrayList<Task> sourceTasks = new ArrayList<>(List.of(firstTask));
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.add(new Todo("second"));

        assertEquals(1, tasks.getTaskCount());
        assertSame(firstTask, tasks.get(0));
    }

    @Test
    public void constructor_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TaskList(null));
    }

    @Test
    public void getTaskCount_tasksAddedAndDeleted_returnsCurrentCount() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        assertEquals(2, tasks.getTaskCount());

        tasks.delete(0);
        assertEquals(1, tasks.getTaskCount());
    }

    @Test
    public void get_validIndexes_returnsSelectedTasks() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    public void get_invalidIndexes_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
    }

    @Test
    public void add_task_appendsTask() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList tasks = new TaskList(List.of(firstTask));

        tasks.add(secondTask);

        assertEquals(2, tasks.getTaskCount());
        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    public void add_nullTask_throwsNullPointerException() {
        TaskList tasks = new TaskList();

        assertThrows(NullPointerException.class, () -> tasks.add(null));
        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void addAtIndex_validIndexes_insertsAtRequestedPositions() {
        Task middleTask = new Todo("middle");
        TaskList tasks = new TaskList(List.of(middleTask));
        Task firstTask = new Todo("first");
        Task lastTask = new Todo("last");

        tasks.add(0, firstTask);
        tasks.add(tasks.getTaskCount(), lastTask);

        assertEquals(List.of(firstTask, middleTask, lastTask), tasks.getTasksSnapshot());
    }

    @Test
    public void addAtIndex_invalidIndexes_throwsIndexOutOfBoundsException() {
        Task originalTask = new Todo("original");
        TaskList tasks = new TaskList(List.of(originalTask));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(-1, new Todo("negative index")));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(2, new Todo("past the end")));
        assertEquals(List.of(originalTask), tasks.getTasksSnapshot());
    }

    @Test
    public void addAtIndex_nullTask_throwsNullPointerException() {
        Task originalTask = new Todo("original");
        TaskList tasks = new TaskList(List.of(originalTask));

        assertThrows(NullPointerException.class, () -> tasks.add(0, null));
        assertEquals(List.of(originalTask), tasks.getTasksSnapshot());
    }

    @Test
    public void delete_validIndexes_returnsDeletedTasksAndPreservesOrder() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        Task thirdTask = new Todo("third");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask, thirdTask));

        assertSame(secondTask, tasks.delete(1));
        assertEquals(List.of(firstTask, thirdTask), tasks.getTasksSnapshot());
        assertSame(firstTask, tasks.delete(0));
        assertSame(thirdTask, tasks.delete(0));
        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void delete_invalidIndexes_throwsIndexOutOfBoundsException() {
        Task originalTask = new Todo("original");
        TaskList tasks = new TaskList(List.of(originalTask));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(1));
        assertEquals(List.of(originalTask), tasks.getTasksSnapshot());
    }

    @Test
    public void markAsDone_validIndex_marksTaskAsDone() {
        Task task = new Todo("task");
        TaskList tasks = new TaskList(List.of(task));

        tasks.markAsDone(0);
        tasks.markAsDone(0);

        assertTrue(task.isDone());
    }

    @Test
    public void markAsDone_invalidIndexes_throwsIndexOutOfBoundsException() {
        Task task = new Todo("task");
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsDone(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsDone(1));
        assertFalse(task.isDone());
    }

    @Test
    public void markAsUndone_validIndex_marksTaskAsUndone() {
        Task task = new Todo("task");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        tasks.markAsUndone(0);
        tasks.markAsUndone(0);

        assertFalse(task.isDone());
    }

    @Test
    public void markAsUndone_invalidIndexes_throwsIndexOutOfBoundsException() {
        Task task = new Todo("task");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsUndone(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsUndone(1));
        assertTrue(task.isDone());
    }

    @Test
    public void find_matchingKeyword_returnsMatchesInOriginalOrderIgnoringCase() {
        Task firstMatch = new Todo("Read BOOK");
        Task nonMatch = new Todo("submit assignment");
        Task secondMatch = new Todo("return book to library");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matches = tasks.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matches);
    }

    @Test
    public void find_partialAndMultiwordKeywords_returnsMatchingTasks() {
        Task partialMatch = new Todo("read textbook");
        Task phraseMatch = new Todo("attend project meeting");
        TaskList tasks = new TaskList(List.of(partialMatch, phraseMatch));

        assertEquals(List.of(partialMatch), tasks.find("text"));
        assertEquals(List.of(phraseMatch), tasks.find("project meeting"));
    }

    @Test
    public void find_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("assignment"));
    }

    @Test
    public void find_invalidKeyword_throwsRelevantException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(NullPointerException.class, () -> tasks.find(null));
        assertThrows(IllegalArgumentException.class, () -> tasks.find(""));
        assertThrows(IllegalArgumentException.class, () -> tasks.find("   "));
    }

    @Test
    public void find_attemptToModifyMatches_throwsException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<Task> matches = tasks.find("book");

        assertThrows(UnsupportedOperationException.class, () -> matches.add(new Todo("another book")));
    }

    @Test
    public void indexOfTaskWithSameDetails_exactTodo_returnsMatchingIndex() {
        Task completedTask = new Todo("read book");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(List.of(new Todo("exercise"), completedTask));

        assertEquals(1, tasks.indexOfTaskWithSameDetails(new Todo("read book")));
    }

    @Test
    public void indexOfTaskWithSameDetails_differentTaskDetails_returnsNegativeOne() {
        TaskList tasks = new TaskList(List.of(
                new Todo("submit report"),
                new Deadline("submit report", LocalDate.of(2026, 10, 15)),
                new Event("meeting", "Monday 2pm", "Monday 4pm")));

        assertEquals(-1, tasks.indexOfTaskWithSameDetails(
                new Deadline("submit report", LocalDate.of(2026, 10, 16))));
        assertEquals(-1, tasks.indexOfTaskWithSameDetails(
                new Event("meeting", "Monday 2pm", "Monday 5pm")));
    }

    @Test
    public void indexOfTaskWithSameDetails_matchingDeadlineAndEvent_returnsFirstMatch() {
        Task deadline = new Deadline("submit report", LocalDate.of(2026, 10, 15));
        Task event = new Event("meeting", " Monday 2pm ", " Monday 4pm ");
        TaskList tasks = new TaskList(List.of(deadline, event));

        assertEquals(0, tasks.indexOfTaskWithSameDetails(
                new Deadline("submit report", LocalDate.of(2026, 10, 15))));
        assertEquals(1, tasks.indexOfTaskWithSameDetails(
                new Event("meeting", "Monday 2pm", "Monday 4pm")));
    }

    @Test
    public void indexOfTaskWithSameDetails_nullTask_throwsNullPointerException() {
        TaskList tasks = new TaskList();

        assertThrows(NullPointerException.class, () -> tasks.indexOfTaskWithSameDetails(null));
    }

    @Test
    public void getTasksSnapshot_emptyTaskList_returnsEmptyList() {
        TaskList tasks = new TaskList();

        assertEquals(List.of(), tasks.getTasksSnapshot());
    }

    @Test
    public void getTasksSnapshot_taskListWithTasks_preservesTaskOrder() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        List<Task> snapshotTasks = tasks.getTasksSnapshot();

        assertEquals(2, snapshotTasks.size());
        assertSame(firstTask, snapshotTasks.get(0));
        assertSame(secondTask, snapshotTasks.get(1));
    }

    @Test
    public void getTasksSnapshot_attemptToModifyReturnedList_throwsException() {
        TaskList tasks = new TaskList(List.of(new Todo("original")));
        List<Task> snapshotTasks = tasks.getTasksSnapshot();

        assertThrows(UnsupportedOperationException.class, () -> snapshotTasks.add(new Todo("new task")));
    }

    @Test
    public void getTasksSnapshot_taskAddedAfterSnapshot_snapshotRemainsUnchanged() {
        Task firstTask = new Todo("first");
        TaskList tasks = new TaskList(List.of(firstTask));
        List<Task> snapshotTasks = tasks.getTasksSnapshot();

        tasks.add(new Todo("second"));

        assertEquals(1, snapshotTasks.size());
        assertSame(firstTask, snapshotTasks.get(0));
        assertEquals(2, tasks.getTaskCount());
    }
}
