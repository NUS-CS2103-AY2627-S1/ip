package milo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the behavior of {@link TaskList}.
 */
public class TaskListTest {
    private final Task firstTask = new Todo("first task");
    private final Task secondTask = new Todo("second task");
    private final Task thirdTask = new Todo("third task");
    private final Task insertedTask = new Todo("inserted task");

    @Test
    public void constructor_noTasks_createsEmptyList() {
        TaskList tasks = new TaskList();

        assertListContainsTasks(tasks);
    }

    @Test
    public void constructor_tasks_defensivelyCopiesSourceListInOrder() {
        List<Task> sourceTasks = new ArrayList<>(List.of(firstTask, secondTask));

        TaskList tasks = new TaskList(sourceTasks);
        sourceTasks.clear();

        assertListContainsTasks(tasks, firstTask, secondTask);
    }

    @Test
    public void add_task_appendsToEnd() {
        TaskList tasks = new TaskList();

        tasks.add(firstTask);
        tasks.add(secondTask);

        assertListContainsTasks(tasks, firstTask, secondTask);
    }

    @Test
    public void addAtIndex_beginning_insertsBeforeExistingTasks() {
        TaskList tasks = createThreeTaskList();

        tasks.add(0, insertedTask);

        assertListContainsTasks(tasks, insertedTask, firstTask, secondTask, thirdTask);
    }

    @Test
    public void addAtIndex_middle_insertsBetweenExistingTasks() {
        TaskList tasks = createThreeTaskList();

        tasks.add(1, insertedTask);

        assertListContainsTasks(tasks, firstTask, insertedTask, secondTask, thirdTask);
    }

    @Test
    public void addAtIndex_end_appendsAfterExistingTasks() {
        TaskList tasks = createThreeTaskList();

        tasks.add(3, insertedTask);

        assertListContainsTasks(tasks, firstTask, secondTask, thirdTask, insertedTask);
    }

    @Test
    public void addAtIndex_invalidIndices_exceptionsThrownAndListUnchanged() {
        TaskList tasks = createThreeTaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(-1, insertedTask));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(4, insertedTask));
        assertListContainsTasks(tasks, firstTask, secondTask, thirdTask);
    }

    @Test
    public void delete_onlyTask_returnsTaskAndEmptiesList() {
        TaskList tasks = new TaskList(List.of(firstTask));

        Task deletedTask = tasks.delete(0);

        assertSame(firstTask, deletedTask);
        assertListContainsTasks(tasks);
    }

    @Test
    public void delete_firstTask_returnsTaskAndShiftsRemainingTasks() {
        TaskList tasks = createThreeTaskList();

        Task deletedTask = tasks.delete(0);

        assertSame(firstTask, deletedTask);
        assertListContainsTasks(tasks, secondTask, thirdTask);
    }

    @Test
    public void delete_middleTask_returnsTaskAndPreservesRemainingOrder() {
        TaskList tasks = createThreeTaskList();

        Task deletedTask = tasks.delete(1);

        assertSame(secondTask, deletedTask);
        assertListContainsTasks(tasks, firstTask, thirdTask);
    }

    @Test
    public void delete_lastTask_returnsTaskAndRetainsEarlierTasks() {
        TaskList tasks = createThreeTaskList();

        Task deletedTask = tasks.delete(2);

        assertSame(thirdTask, deletedTask);
        assertListContainsTasks(tasks, firstTask, secondTask);
    }

    @Test
    public void delete_negativeIndex_exceptionThrownAndListUnchanged() {
        TaskList tasks = createThreeTaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(-1));
        assertListContainsTasks(tasks, firstTask, secondTask, thirdTask);
    }

    @Test
    public void delete_indexEqualToSize_exceptionThrownAndListUnchanged() {
        TaskList tasks = createThreeTaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(3));
        assertListContainsTasks(tasks, firstTask, secondTask, thirdTask);
    }

    @Test
    public void delete_emptyList_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(0));
        assertListContainsTasks(tasks);
    }

    @Test
    public void get_validIndices_returnsSelectedTasks() {
        TaskList tasks = createThreeTaskList();

        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
        assertSame(thirdTask, tasks.get(2));
    }

    @Test
    public void get_invalidIndices_exceptionsThrownAndListUnchanged() {
        TaskList tasks = createThreeTaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(3));
        assertListContainsTasks(tasks, firstTask, secondTask, thirdTask);
    }

    @Test
    public void mark_incompleteTask_returnsAndMarksSelectedTask() {
        TaskList tasks = createThreeTaskList();

        Task markedTask = tasks.mark(1);

        assertSame(secondTask, markedTask);
        assertEquals(" ", firstTask.getStatusIcon());
        assertEquals("X", secondTask.getStatusIcon());
        assertEquals(" ", thirdTask.getStatusIcon());
    }

    @Test
    public void mark_completedTask_keepsTaskMarked() {
        secondTask.markAsDone();
        TaskList tasks = createThreeTaskList();

        Task markedTask = tasks.mark(1);

        assertSame(secondTask, markedTask);
        assertEquals("X", secondTask.getStatusIcon());
    }

    @Test
    public void mark_invalidIndices_exceptionsThrownAndListUnchanged() {
        TaskList tasks = createThreeTaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.mark(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.mark(3));
        assertListContainsTasks(tasks, firstTask, secondTask, thirdTask);
        assertAllTasksNotDone();
    }

    @Test
    public void unmark_completedTask_returnsAndUnmarksSelectedTask() {
        secondTask.markAsDone();
        TaskList tasks = createThreeTaskList();

        Task unmarkedTask = tasks.unmark(1);

        assertSame(secondTask, unmarkedTask);
        assertEquals(" ", firstTask.getStatusIcon());
        assertEquals(" ", secondTask.getStatusIcon());
        assertEquals(" ", thirdTask.getStatusIcon());
    }

    @Test
    public void unmark_incompleteTask_keepsTaskUnmarked() {
        TaskList tasks = createThreeTaskList();

        Task unmarkedTask = tasks.unmark(1);

        assertSame(secondTask, unmarkedTask);
        assertEquals(" ", secondTask.getStatusIcon());
    }

    @Test
    public void unmark_invalidIndices_exceptionsThrownAndListUnchanged() {
        secondTask.markAsDone();
        TaskList tasks = createThreeTaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.unmark(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.unmark(3));
        assertListContainsTasks(tasks, firstTask, secondTask, thirdTask);
        assertEquals("X", secondTask.getStatusIcon());
    }

    @Test
    public void find_matchingSubstring_returnsMatchesInOriginalOrder() {
        Task firstMatch = new Todo("borrow book");
        Task nonMatch = new Todo("project meeting");
        Task secondMatch = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        TaskList matchingTasks = tasks.find("book");

        assertListContainsTasks(matchingTasks, firstMatch, secondMatch);
        assertListContainsTasks(tasks, firstMatch, nonMatch, secondMatch);
    }

    @Test
    public void find_mixedCaseKeyword_matchesIgnoringCase() {
        Task upperCaseTask = new Todo("RETURN BOOK");
        TaskList tasks = new TaskList(List.of(upperCaseTask));

        TaskList matchingTasks = tasks.find("book");

        assertListContainsTasks(matchingTasks, upperCaseTask);
    }

    @Test
    public void find_noMatchingTasks_returnsEmptyList() {
        TaskList tasks = createThreeTaskList();

        TaskList matchingTasks = tasks.find("missing");

        assertListContainsTasks(matchingTasks);
    }

    @Test
    public void find_emptyList_returnsEmptyList() {
        TaskList tasks = new TaskList();

        TaskList matchingTasks = tasks.find("task");

        assertListContainsTasks(matchingTasks);
    }

    @Test
    public void find_resultModified_originalListUnchanged() {
        TaskList tasks = createThreeTaskList();
        TaskList matchingTasks = tasks.find("task");

        matchingTasks.delete(0);

        assertListContainsTasks(tasks, firstTask, secondTask, thirdTask);
        assertListContainsTasks(matchingTasks, secondTask, thirdTask);
    }

    @Test
    public void size_listMutations_returnsCurrentTaskCount() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
        tasks.add(firstTask);
        assertEquals(1, tasks.size());
        tasks.add(secondTask);
        assertEquals(2, tasks.size());
        tasks.delete(0);
        assertEquals(1, tasks.size());
        tasks.clear();
        assertEquals(0, tasks.size());
    }

    @Test
    public void getTasks_currentTasks_returnsOrderedSnapshot() {
        TaskList tasks = createThreeTaskList();

        List<Task> taskSnapshot = tasks.getTasks();
        tasks.delete(0);

        assertEquals(List.of(firstTask, secondTask, thirdTask), taskSnapshot);
    }

    @Test
    public void getTasks_returnedList_modificationRejected() {
        TaskList tasks = createThreeTaskList();
        List<Task> taskSnapshot = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class,
                () -> taskSnapshot.add(insertedTask));
        assertListContainsTasks(tasks, firstTask, secondTask, thirdTask);
    }

    @Test
    public void clear_nonEmptyAndEmptyList_leavesListEmpty() {
        TaskList tasks = createThreeTaskList();

        tasks.clear();
        assertListContainsTasks(tasks);
        tasks.clear();
        assertListContainsTasks(tasks);
    }

    private TaskList createThreeTaskList() {
        return new TaskList(List.of(firstTask, secondTask, thirdTask));
    }

    private void assertListContainsTasks(TaskList tasks, Task... expectedTasks) {
        assertEquals(expectedTasks.length, tasks.size());
        assertEquals(List.of(expectedTasks), tasks.getTasks());
    }

    private void assertAllTasksNotDone() {
        assertEquals(" ", firstTask.getStatusIcon());
        assertEquals(" ", secondTask.getStatusIcon());
        assertEquals(" ", thirdTask.getStatusIcon());
    }
}
