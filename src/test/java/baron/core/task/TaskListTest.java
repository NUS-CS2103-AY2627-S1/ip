package baron.core.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import baron.core.exception.BaronException;

/**
 * Tests for {@link TaskList}.
 */
class TaskListTest {

    /**
     * Verifies that an empty list has no tasks or formatted content.
     */
    @Test
    void emptyConstructor_emptyListCreated() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        assertTrue(taskList.getTasks().isEmpty());
        assertEquals("", taskList.toUuidString());
        assertEquals("", taskList.toFileString());
        assertEquals("", taskList.toString());
        assertEquals("", taskList.getNumberedTasks(taskList, false, false));
        assertEquals("", taskList.getNumberedTasks(taskList, true, true));
    }

    /**
     * Verifies that the collection constructor preserves task order and copies the collection.
     */
    @Test
    void collectionConstructor_tasksProvided_tasksCopiedInOrder() {
        Todo firstTask = todo("00000000-0000-0000-0000-000000000001", "first task");
        Todo secondTask = todo("00000000-0000-0000-0000-000000000002", "second task");
        List<Task> sourceTasks = new java.util.ArrayList<>(List.of(firstTask, secondTask));

        TaskList taskList = new TaskList(sourceTasks);
        sourceTasks.clear();

        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
    }

    /**
     * Verifies that the first and last positions in a non-empty list are valid.
     */
    @Test
    void checkTaskIndex_firstAndLastIndex_noExceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("first task"));
        taskList.addTask(new Todo("last task"));

        assertDoesNotThrow(() -> taskList.checkTaskIndex(0));
        assertDoesNotThrow(() -> taskList.checkTaskIndex(1));
    }

    /**
     * Verifies that negative indexes are rejected.
     */
    @Test
    void checkTaskIndex_negativeIndex_baronExceptionThrown() {
        TaskList taskList = new TaskList();

        Executable checkNegativeIndex = () -> taskList.checkTaskIndex(-1);
        BaronException exception = assertThrows(
                BaronException.class,
                checkNegativeIndex);

        assertEquals("Invalid task index", exception.getMessage());
    }

    /**
     * Verifies that an index immediately after the final task is rejected, including for an empty
     * list.
     */
    @Test
    void checkTaskIndex_indexAtListSize_baronExceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("only task"));

        Executable checkIndexAtListSize = () -> taskList.checkTaskIndex(taskList.size());
        BaronException exception = assertThrows(
                BaronException.class,
                checkIndexAtListSize);

        assertEquals("Invalid task index", exception.getMessage());
        assertThrows(BaronException.class, () -> new TaskList().checkTaskIndex(0));
    }

    /**
     * Verifies that a task can be found by identity and absent tasks return {@code -1}.
     */
    @Test
    void indexOf_taskPresentOrAbsent_expectedIndexReturned() {
        Todo firstTask = todo("00000000-0000-0000-0000-000000000001", "first task");
        Todo secondTask = todo("00000000-0000-0000-0000-000000000002", "second task");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        assertEquals(0, taskList.getTasks().indexOf(firstTask));
        assertEquals(1, taskList.getTasks().indexOf(secondTask));
        assertEquals(-1, taskList.getTasks().indexOf(
                todo("00000000-0000-0000-0000-000000000003", "missing")));
    }

    /**
     * Verifies that adding a task appends it and that returned task views cannot be changed.
     */
    @Test
    void addTask_taskProvided_taskAppendedAndSnapshotIsUnmodifiable() {
        TaskList taskList = new TaskList();
        Todo firstTask = todo("00000000-0000-0000-0000-000000000001", "first task");
        Todo secondTask = todo("00000000-0000-0000-0000-000000000002", "second task");

        taskList.addTask(firstTask);
        List<Task> snapshot = taskList.getTasks();
        taskList.addTask(secondTask);

        assertEquals(List.of(firstTask), snapshot);
        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(secondTask));
    }

    /**
     * Verifies that marking and unmarking returns and updates the requested task.
     */
    @Test
    void markAndUnmarkTask_validIndex_taskStatusUpdated() throws BaronException {
        Todo task = todo("00000000-0000-0000-0000-000000000001", "task");
        TaskList taskList = new TaskList(List.of(task));

        assertSame(task, taskList.markTask(0));
        assertTrue(task.isDone());
        assertSame(task, taskList.unmarkTask(0));
        assertFalse(task.isDone());
    }

    /**
     * Verifies that a task cannot be completed while one of its required tasks is incomplete.
     */
    @Test
    void markTask_requiredTaskIncomplete_taskUnchangedAfterRejection() throws BaronException {
        Todo requiredTask = todo("00000000-0000-0000-0000-000000000001", "required task");
        Todo unlockedTask = todo("00000000-0000-0000-0000-000000000002", "unlocked task");
        unlockedTask.setRequiredTasks(
                new TaskList(List.of(requiredTask, unlockedTask)), new TaskList(List.of(requiredTask)));
        TaskList taskList = new TaskList(List.of(requiredTask, unlockedTask));

        BaronException exception = assertThrows(BaronException.class, () -> taskList.markTask(1));

        assertTrue(exception.getMessage().contains("Cannot mark task"));
        assertFalse(unlockedTask.isDone());
    }

    /**
     * Verifies that a completed required task cannot be marked incomplete while an unlocked task
     * is complete.
     */
    @Test
    void unmarkTask_unlockedTaskCompleted_taskUnchangedAfterRejection() throws BaronException {
        Todo requiredTask = todo("00000000-0000-0000-0000-000000000001", "required task");
        Todo unlockedTask = todo("00000000-0000-0000-0000-000000000002", "unlocked task");
        unlockedTask.setRequiredTasks(
                new TaskList(List.of(requiredTask, unlockedTask)), new TaskList(List.of(requiredTask)));
        TaskList taskList = new TaskList(List.of(requiredTask, unlockedTask));
        taskList.markTask(0);
        taskList.markTask(1);

        BaronException exception = assertThrows(BaronException.class, () -> taskList.unmarkTask(0));

        assertTrue(exception.getMessage().contains("Cannot mark task"));
        assertTrue(requiredTask.isDone());
    }

    /**
     * Verifies that task operations reject invalid indexes.
     */
    @Test
    void taskOperations_invalidIndex_baronExceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(BaronException.class, () -> taskList.markTask(0));
        assertThrows(BaronException.class, () -> taskList.unmarkTask(-1));
        assertThrows(BaronException.class, () -> taskList.deleteTask(0));
    }

    /**
     * Verifies that deleting by task removes the requested task and ignores an absent task.
     */
    @Test
    void deleteTask_taskPresentOrAbsent_taskRemovedOnlyWhenPresent() {
        Todo firstTask = todo("00000000-0000-0000-0000-000000000001", "first task");
        Todo secondTask = todo("00000000-0000-0000-0000-000000000002", "second task");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        taskList.deleteTask(firstTask);
        taskList.deleteTask(todo("00000000-0000-0000-0000-000000000003", "missing"));

        assertEquals(List.of(secondTask), taskList.getTasks());
    }

    /**
     * Verifies that deleting by index returns the removed task and keeps the remaining order.
     */
    @Test
    void deleteTask_validIndex_removedTaskReturned() throws BaronException {
        Todo firstTask = todo("00000000-0000-0000-0000-000000000001", "first task");
        Todo secondTask = todo("00000000-0000-0000-0000-000000000002", "second task");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        Task deletedTask = taskList.deleteTask(0);

        assertSame(firstTask, deletedTask);
        assertEquals(List.of(secondTask), taskList.getTasks());
    }

    /**
     * Verifies that deleting a required task clears its relationship with an unlocked task.
     */
    @Test
    void deleteTask_requiredTaskWithUnlockedTask_relationshipCleared() throws BaronException {
        Todo requiredTask = todo("00000000-0000-0000-0000-000000000001", "required task");
        Todo unlockedTask = todo("00000000-0000-0000-0000-000000000002", "unlocked task");
        unlockedTask.setRequiredTasks(
                new TaskList(List.of(requiredTask, unlockedTask)), new TaskList(List.of(requiredTask)));
        TaskList taskList = new TaskList(List.of(requiredTask, unlockedTask));

        taskList.deleteTask(0);

        assertDoesNotThrow(unlockedTask::markAsDone);
        assertTrue(unlockedTask.isDone());
    }

    /**
     * Verifies that searching is case-sensitive and preserves the original task order.
     */
    @Test
    void findTasks_matchingAndNonMatchingKeywords_expectedTasksReturned() {
        Todo firstTask = todo("00000000-0000-0000-0000-000000000001", "read book");
        Todo secondTask = todo("00000000-0000-0000-0000-000000000002", "buy bookcase");
        Todo thirdTask = todo("00000000-0000-0000-0000-000000000003", "Read notes");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask, thirdTask));

        assertEquals(List.of(firstTask, secondTask), taskList.findTasks("book").getTasks());
        assertTrue(taskList.findTasks("BOOK").getTasks().isEmpty());
        assertEquals(List.of(firstTask, secondTask, thirdTask), taskList.findTasks("").getTasks());
    }

    /**
     * Verifies that file and display formats join task representations with newlines.
     */
    @Test
    void formatting_multipleTasks_taskRepresentationsJoinedWithNewlines() {
        Todo firstTask = todo("00000000-0000-0000-0000-000000000001", "first task");
        Todo secondTask = todo("00000000-0000-0000-0000-000000000002", "second task");
        TaskList taskList = new TaskList(List.of(firstTask, secondTask));

        assertEquals(
                "00000000-0000-0000-0000-000000000001, 00000000-0000-0000-0000-000000000002",
                taskList.toUuidString());
        assertEquals(
                firstTask.toFileString() + "\n" + secondTask.toFileString(),
                taskList.toFileString());
        assertEquals(firstTask + "\n" + secondTask, taskList.toString());
    }

    /**
     * Creates a to-do task with a predictable identifier for test assertions.
     *
     * @param uuid The task identifier.
     * @param description The task description.
     * @return The created task.
     */
    private Todo todo(String uuid, String description) {
        return new Todo(UUID.fromString(uuid), description);
    }
}
