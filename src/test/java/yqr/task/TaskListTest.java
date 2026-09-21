package yqr.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import yqr.exception.YqrException;

/**
 * Tests the public behaviors of {@link TaskList}.
 */
class TaskListTest {
    private static final String INVALID_TASK_NUMBER_MESSAGE = "Please input a valid task number";

    @Test
    void addTask_emptyList_taskAdded() throws YqrException {
        Task task = new Todo("read book");
        TaskList taskList = new TaskList();

        taskList.addTask(task);

        assertEquals(1, taskList.getTaskCount());
        assertIterableEquals(List.of(task), taskList.getTasks());
    }

    @Test
    void addTask_nonEmptyList_taskAppendedAtEnd() throws YqrException {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList taskList = new TaskList(List.of(firstTask));

        taskList.addTask(secondTask);

        assertIterableEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    @Test
    void addTask_duplicateTodoWithDifferentStatus_duplicateRejected() {
        Todo existingTask = new Todo("read book");
        existingTask.markAsDone();
        TaskList taskList = new TaskList(List.of(existingTask));

        YqrException exception = assertThrows(
                YqrException.class, () -> taskList.addTask(new Todo("read book")));

        assertEquals("This task already exists in the list", exception.getMessage());
        assertEquals(1, taskList.getTaskCount());
    }

    @Test
    void addTask_sameDescriptionWithDifferentType_bothTasksStored() throws YqrException {
        TaskList taskList = new TaskList(List.of(new Todo("submit report")));

        taskList.addTask(new Deadline("submit report", LocalDate.of(2026, 9, 30)));

        assertEquals(2, taskList.getTaskCount());
    }

    @Test
    void addTask_nullTask_nullRejected() {
        TaskList taskList = new TaskList();

        YqrException exception = assertThrows(YqrException.class, () -> taskList.addTask(null));

        assertEquals("Task cannot be empty", exception.getMessage());
    }

    @Test
    void getTaskCount_emptyAndPopulatedLists_correctCountsReturned() {
        TaskList emptyTaskList = new TaskList();
        TaskList populatedTaskList = new TaskList(List.of(
                new Todo("first"), new Todo("second"), new Todo("third")));

        assertEquals(0, emptyTaskList.getTaskCount());
        assertEquals(3, populatedTaskList.getTaskCount());
    }

    @Test
    void getTasks_listChangesAfterCall_returnedSnapshotIsUnchangedAndUnmodifiable() throws YqrException {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList taskList = new TaskList(List.of(firstTask));
        List<Task> snapshot = taskList.getTasks();

        taskList.addTask(secondTask);

        assertIterableEquals(List.of(firstTask), snapshot);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(secondTask));
        assertIterableEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    @Test
    void findTasks_keywordInMultipleDescriptions_matchingTasksReturnedInOriginalOrder() {
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Todo("buy groceries");
        Task secondMatch = new Deadline("return book", LocalDate.of(2026, 9, 1));
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matchingTasks = taskList.findTasks("book");

        assertIterableEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void findTasks_keywordIsPartialWord_matchingTaskReturned() {
        Task matchingTask = new Todo("read textbook");
        TaskList taskList = new TaskList(List.of(matchingTask, new Todo("write notes")));

        List<Task> matchingTasks = taskList.findTasks("text");

        assertIterableEquals(List.of(matchingTask), matchingTasks);
    }

    @Test
    void findTasks_keywordOnlyInTaskMetadata_noTasksReturned() {
        Task deadline = new Deadline("return item", LocalDate.of(2026, 9, 1));
        Task event = new Event("team meeting", "book room", "leave room");
        TaskList taskList = new TaskList(List.of(deadline, event));

        List<Task> matchingTasks = taskList.findTasks("book");

        assertIterableEquals(List.of(), matchingTasks);
    }

    @Test
    void findTasks_noMatchingDescriptions_emptyListReturned() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        List<Task> matchingTasks = taskList.findTasks("groceries");

        assertIterableEquals(List.of(), matchingTasks);
    }

    @Test
    void markTaskAsDone_validTask_taskMarkedAndReturned() throws YqrException {
        Task firstTask = new Todo("first");
        Task targetTask = new Todo("target");
        Task thirdTask = new Todo("third");
        TaskList taskList = new TaskList(List.of(firstTask, targetTask, thirdTask));

        Task markedTask = taskList.markTaskAsDone(2);

        assertSame(targetTask, markedTask);
        assertTrue(targetTask.isDone());
        assertFalse(firstTask.isDone());
        assertFalse(thirdTask.isDone());
        assertIterableEquals(List.of(firstTask, targetTask, thirdTask), taskList.getTasks());
    }

    @Test
    void markTaskAsDone_alreadyDoneTask_taskRemainsDone() throws YqrException {
        Task task = new Todo("task");
        task.markAsDone();
        TaskList taskList = new TaskList(List.of(task));

        Task markedTask = taskList.markTaskAsDone(1);

        assertSame(task, markedTask);
        assertTrue(task.isDone());
    }

    @Test
    void markTaskAsDone_invalidTaskNumbers_exceptionsThrown() {
        TaskList emptyTaskList = new TaskList();
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        assertInvalidTaskNumber(() -> emptyTaskList.markTaskAsDone(1));
        assertInvalidTaskNumber(() -> taskList.markTaskAsDone(-1));
        assertInvalidTaskNumber(() -> taskList.markTaskAsDone(0));
        assertInvalidTaskNumber(() -> taskList.markTaskAsDone(2));
    }

    @Test
    void markTaskAsNotDone_doneTask_taskUnmarkedAndReturned() throws YqrException {
        Task firstTask = new Todo("first");
        Task targetTask = new Todo("target");
        Task thirdTask = new Todo("third");
        targetTask.markAsDone();
        TaskList taskList = new TaskList(List.of(firstTask, targetTask, thirdTask));

        Task unmarkedTask = taskList.markTaskAsNotDone(2);

        assertSame(targetTask, unmarkedTask);
        assertFalse(targetTask.isDone());
        assertFalse(firstTask.isDone());
        assertFalse(thirdTask.isDone());
        assertIterableEquals(List.of(firstTask, targetTask, thirdTask), taskList.getTasks());
    }

    @Test
    void markTaskAsNotDone_alreadyNotDoneTask_taskRemainsNotDone() throws YqrException {
        Task task = new Todo("task");
        TaskList taskList = new TaskList(List.of(task));

        Task unmarkedTask = taskList.markTaskAsNotDone(1);

        assertSame(task, unmarkedTask);
        assertFalse(task.isDone());
    }

    @Test
    void markTaskAsNotDone_invalidTaskNumbers_exceptionsThrown() {
        TaskList emptyTaskList = new TaskList();
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        assertInvalidTaskNumber(() -> emptyTaskList.markTaskAsNotDone(1));
        assertInvalidTaskNumber(() -> taskList.markTaskAsNotDone(-1));
        assertInvalidTaskNumber(() -> taskList.markTaskAsNotDone(0));
        assertInvalidTaskNumber(() -> taskList.markTaskAsNotDone(2));
    }

    @Test
    void deleteTask_onlyTask_taskRemovedAndReturned() throws YqrException {
        Task onlyTask = new Todo("read book");
        TaskList taskList = new TaskList(List.of(onlyTask));

        Task deletedTask = taskList.deleteTask(1);

        assertSame(onlyTask, deletedTask);
        assertEquals(0, taskList.getTaskCount());
        assertIterableEquals(List.of(), taskList.getTasks());
    }

    @Test
    void deleteTask_firstTask_firstTaskRemoved() throws YqrException {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        Task thirdTask = new Todo("third");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask, thirdTask));

        Task deletedTask = taskList.deleteTask(1);

        assertSame(firstTask, deletedTask);
        assertIterableEquals(List.of(secondTask, thirdTask), taskList.getTasks());
    }

    @Test
    void deleteTask_middleTask_middleTaskRemoved() throws YqrException {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        Task thirdTask = new Todo("third");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask, thirdTask));

        Task deletedTask = taskList.deleteTask(2);

        assertSame(secondTask, deletedTask);
        assertIterableEquals(List.of(firstTask, thirdTask), taskList.getTasks());
    }

    @Test
    void deleteTask_lastTask_lastTaskRemoved() throws YqrException {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        Task thirdTask = new Todo("third");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask, thirdTask));

        Task deletedTask = taskList.deleteTask(3);

        assertSame(thirdTask, deletedTask);
        assertIterableEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    @Test
    void deleteTask_emptyList_exceptionThrown() {
        TaskList taskList = new TaskList();

        YqrException exception = assertThrows(YqrException.class, () -> taskList.deleteTask(1));

        assertEquals(INVALID_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    @Test
    void deleteTask_zeroTaskNumber_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        YqrException exception = assertThrows(YqrException.class, () -> taskList.deleteTask(0));

        assertEquals(INVALID_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    @Test
    void deleteTask_negativeTaskNumber_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        YqrException exception = assertThrows(YqrException.class, () -> taskList.deleteTask(-1));

        assertEquals(INVALID_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    @Test
    void deleteTask_taskNumberAboveRange_exceptionThrown() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        YqrException exception = assertThrows(YqrException.class, () -> taskList.deleteTask(2));

        assertEquals(INVALID_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    @Test
    void undo_addedTask_previousTaskListRestored() throws YqrException {
        Todo originalTask = new Todo("original");
        TaskList taskList = new TaskList(List.of(originalTask));
        taskList.addTask(new Todo("added"));

        taskList.undo();

        assertEquals(1, taskList.getTaskCount());
        assertEquals(originalTask.toString(), taskList.getTasks().get(0).toString());
    }

    @Test
    void undo_deletedDoneTask_taskAndStatusRestored() throws YqrException {
        Todo deletedTask = new Todo("completed task");
        deletedTask.markAsDone();
        TaskList taskList = new TaskList(List.of(deletedTask, new Todo("remaining")));
        taskList.deleteTask(1);

        taskList.undo();

        assertEquals(2, taskList.getTaskCount());
        assertEquals("[T][X] completed task", taskList.getTasks().get(0).toString());
    }

    @Test
    void undo_deletedDatedTasks_taskTypesAndDetailsRestored() throws YqrException {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 8));
        Event event = new Event("project meeting", "2pm", "3pm");
        TaskList taskList = new TaskList(List.of(deadline, event));
        taskList.deleteTask(1);

        taskList.undo();

        assertEquals(deadline.toString(), taskList.getTasks().get(0).toString());
        assertEquals(event.toString(), taskList.getTasks().get(1).toString());
    }

    @Test
    void undo_markedTask_incompleteStatusRestored() throws YqrException {
        TaskList taskList = new TaskList(List.of(new Todo("task")));
        taskList.markTaskAsDone(1);

        taskList.undo();

        assertFalse(taskList.getTasks().get(0).isDone());
    }

    @Test
    void undo_unmarkedTask_completedStatusRestored() throws YqrException {
        Todo task = new Todo("task");
        task.markAsDone();
        TaskList taskList = new TaskList(List.of(task));
        taskList.markTaskAsNotDone(1);

        taskList.undo();

        assertTrue(taskList.getTasks().get(0).isDone());
    }

    @Test
    void undo_twice_secondUndoRejected() throws YqrException {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("task"));
        taskList.undo();

        YqrException exception = assertThrows(YqrException.class, taskList::undo);

        assertEquals("There is no command to undo", exception.getMessage());
    }

    /**
     * Asserts that an operation fails because its task number is outside the list.
     *
     * @param operation operation expected to reject its task number.
     */
    private static void assertInvalidTaskNumber(TaskListOperation operation) {
        YqrException exception = assertThrows(YqrException.class, operation::execute);
        assertEquals(INVALID_TASK_NUMBER_MESSAGE, exception.getMessage());
    }

    /**
     * Represents a task-list operation that may reject an invalid task number.
     */
    @FunctionalInterface
    private interface TaskListOperation {
        void execute() throws YqrException;
    }
}
