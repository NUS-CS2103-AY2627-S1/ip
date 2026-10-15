package yapbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import yapbot.exception.YapBotException;

public class TaskListTest {

    @Test
    public void constructor_default_createsEmptyList() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        assertFalse(taskList.isFull());
    }

    @Test
    public void constructor_fromLoadedArray_copiesOnlyLoadedCountTasks() {
        Task first = new Todo("read book");
        Task second = new Todo("write code");
        Task third = new Todo("should not be loaded");
        Task[] loadedTasks = new Task[]{first, second, third};

        TaskList taskList = new TaskList(loadedTasks, 2);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
        assertNull(taskList.toArray()[2]);
    }

    @Test
    public void size_emptyList_returnsZero() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
    }

    @Test
    public void size_afterAddingTasks_returnsTaskCount() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("write code"));

        assertEquals(2, taskList.size());
    }

    @Test
    public void isFull_emptyList_returnsFalse() {
        TaskList taskList = new TaskList();

        assertFalse(taskList.isFull());
    }

    @Test
    public void isFull_belowMaxCapacity_returnsFalse() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertFalse(taskList.isFull());
    }

    @Test
    public void isFull_atMaxCapacity_returnsTrue() throws YapBotException {
        TaskList taskList = fillToCapacity();

        assertTrue(taskList.isFull());
    }

    @Test
    public void get_validIndex_returnsExpectedTask() throws YapBotException {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");
        taskList.add(task);

        assertSame(task, taskList.get(0));
    }

    @Test
    public void toArray_afterAddingTasks_containsAddedTasksInOrderWithBackingCapacity() throws YapBotException {
        TaskList taskList = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("write code");
        taskList.add(first);
        taskList.add(second);

        Task[] tasks = taskList.toArray();

        assertEquals(TaskList.MAX_CAPACITY, tasks.length);
        assertSame(first, tasks[0]);
        assertSame(second, tasks[1]);
        assertNull(tasks[2]);
    }

    @Test
    public void add_task_increasesSizeAndAppendsAtEnd() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        Task second = new Todo("write code");

        taskList.add(second);

        assertEquals(2, taskList.size());
        assertSame(second, taskList.get(1));
    }

    @Test
    public void add_whenListFull_exceptionThrown() throws YapBotException {
        TaskList taskList = fillToCapacity();

        assertThrows(YapBotException.class, () -> taskList.add(new Todo("one too many")));
    }

    @Test
    public void add_duplicateOfExistingTask_exceptionThrown() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(YapBotException.class, () -> taskList.add(new Todo("read book")));
    }

    @Test
    public void add_duplicateDifferingOnlyInCase_exceptionThrown() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(YapBotException.class, () -> taskList.add(new Todo("Read Book")));
    }

    @Test
    public void add_sameDescriptionDifferentTaskType_doesNotThrow() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        taskList.add(new Deadline("read book", LocalDate.of(2019, 10, 15)));

        assertEquals(2, taskList.size());
    }

    @Test
    public void delete_middleIndex_shiftsRemainingTasksAndReturnsRemovedTask() throws YapBotException {
        TaskList taskList = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("write code");
        Task third = new Todo("walk dog");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        Task removed = taskList.delete(0);

        assertSame(first, removed);
        assertEquals(2, taskList.size());
        assertSame(second, taskList.get(0));
        assertSame(third, taskList.get(1));
    }

    @Test
    public void delete_onlyTask_listBecomesEmpty() throws YapBotException {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");
        taskList.add(task);

        Task removed = taskList.delete(0);

        assertSame(task, removed);
        assertEquals(0, taskList.size());
        assertNull(taskList.toArray()[0]);
    }

    @Test
    public void markAsDone_validIndex_taskStatusBecomesDone() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        taskList.markAsDone(0);

        assertEquals("1", taskList.get(0).getStatusValue());
    }

    @Test
    public void markAsNotDone_previouslyDoneTask_taskStatusBecomesNotDone() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.markAsDone(0);

        taskList.markAsNotDone(0);

        assertEquals("0", taskList.get(0).getStatusValue());
    }

    private TaskList fillToCapacity() throws YapBotException {
        TaskList taskList = new TaskList();
        for (int i = 0; i < TaskList.MAX_CAPACITY; i++) {
            taskList.add(new Todo("task " + i));
        }
        return taskList;
    }

    @Test
    public void findMatching_keywordPresentInSomeDescriptions_returnsOnlyMatchesInOrder() throws YapBotException {
        TaskList taskList = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("write code");
        Task third = new Todo("return book");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        Task[] matches = taskList.findMatching("book");

        assertEquals(2, matches.length);
        assertSame(first, matches[0]);
        assertSame(third, matches[1]);
    }

    @Test
    public void findMatching_noDescriptionContainsKeyword_returnsEmptyArray() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        Task[] matches = taskList.findMatching("nomatch");

        assertEquals(0, matches.length);
    }

    @Test
    public void findMatching_emptyList_returnsEmptyArray() {
        TaskList taskList = new TaskList();

        Task[] matches = taskList.findMatching("book");

        assertEquals(0, matches.length);
    }

    @Test
    public void findMatching_keywordDifferentCase_doesNotMatch() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        Task[] matches = taskList.findMatching("BOOK");

        assertEquals(0, matches.length);
    }

    @Test
    public void validateIndex_validIndexWithinRange_returnsZeroBasedIndex() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("write code"));

        int index = taskList.validateIndex("mark 2", "mark");

        assertEquals(1, index);
    }

    @Test
    public void validateIndex_firstTask_returnsIndexZero() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        int index = taskList.validateIndex("delete 1", "delete");

        assertEquals(0, index);
    }

    @Test
    public void validateIndex_extraWhitespaceAroundNumber_trimmedAndParsed() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("write code"));

        int index = taskList.validateIndex("mark   2   ", "mark");

        assertEquals(1, index);
    }

    @Test
    public void validateIndex_noArgumentGiven_exceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(YapBotException.class, () -> taskList.validateIndex("mark", "mark"));
    }

    @Test
    public void validateIndex_blankArgumentGiven_exceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(YapBotException.class, () -> taskList.validateIndex("mark   ", "mark"));
    }

    @Test
    public void validateIndex_nonNumericArgument_exceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(YapBotException.class, () -> taskList.validateIndex("mark two", "mark"));
    }

    @Test
    public void validateIndex_emptyTaskList_exceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(YapBotException.class, () -> taskList.validateIndex("mark 1", "mark"));
    }

    @Test
    public void validateIndex_indexZero_exceptionThrown() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(YapBotException.class, () -> taskList.validateIndex("mark 0", "mark"));
    }

    @Test
    public void validateIndex_negativeIndex_exceptionThrown() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(YapBotException.class, () -> taskList.validateIndex("mark -1", "mark"));
    }

    @Test
    public void validateIndex_indexBeyondListSize_exceptionThrown() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(YapBotException.class, () -> taskList.validateIndex("mark 2", "mark"));
    }
}
