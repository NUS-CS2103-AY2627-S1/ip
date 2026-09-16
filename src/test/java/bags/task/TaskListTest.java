package bags.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import bags.exception.BagsException;

/**
 * Tests the {@link TaskList} class for task-related methods.
 *
 * <p>
 * These tests verify that tasks are correctly added to the task list,
 * marked as done or undone, deleted, and that invalid task numbers or
 * missing task numbers are handled correctly.
 * </p>
 */
class TaskListTest {

    private TaskList createTaskList() throws BagsException {
        TaskList taskList = new TaskList();

        taskList.add(new Todo("Read book"));
        taskList.add(new Todo("Do homework"));

        return taskList;
    }

    @Test
    void addMethod_emptyList_successfullyAddsTask() throws BagsException {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.getSize());

        Todo todo = new Todo("Read book");
        taskList.add(todo);

        List<Task> tasks = taskList.getTasks();
        List<String> readingFileRecords = taskList.saveRecords();

        assertEquals(1, taskList.getSize());
        assertEquals(todo, tasks.get(0));
        assertEquals(todo.parseEvent(), readingFileRecords.get(0));
    }

    @Test
    void getTask_existingTask_returnsSelectedTask() throws BagsException {
        TaskList taskList = createTaskList();

        Task task = taskList.getTask("get 2");

        assertEquals("Do homework", task.getDescription());
    }

    @Test
    void saveRecords_multipleTaskTypes_returnsAllFormattedRecords()
            throws BagsException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read book"));
        taskList.add(new Deadline("Submit report", "2026-09-12 23:59"));
        taskList.add(new Event("Project meeting", "2026-09-13 10:00", "2026-09-13 11:00"));
        taskList.markDone("done 2");

        List<String> records = taskList.saveRecords();

        assertEquals(List.of(
                "T | [ ] | Read book",
                "D | [X] | Submit report | 2026-09-12 23:59",
                "E | [ ] | Project meeting | 2026-09-13 10:00 | 2026-09-13 11:00"
        ), records);
    }

    @Test
    void search_caseInsensitiveKeyword_returnsMatchingTasks() throws BagsException {
        TaskList taskList = createTaskList();

        String result = taskList.search("search BOOK");

        assertEquals("🔎 I rummaged through your bag and found these:" + System.lineSeparator()
                + "1.[T][ ] Read book", result);
    }

    @Test
    void search_noMatchingKeyword_returnsNoMatchesMessage() throws BagsException {
        TaskList taskList = createTaskList();

        String result = taskList.search("search groceries");

        assertEquals("🔎 Oops! I can't find any matching tasks in the bag. Wanna add a task?", result);
    }

    @Test
    void search_missingKeyword_throwsException() throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.search("search")
        );

        assertTrue(exception.getMessage().contains("keyword"));
    }

    @Test
    void edit_completedTask_preservesCompletionStatus() throws BagsException {
        TaskList taskList = createTaskList();
        taskList.markDone("done 1");
        Todo replacementTask = new Todo("Read chapter");

        Task updatedTask = taskList.edit("edit 1", replacementTask);

        assertSame(replacementTask, updatedTask);
        assertEquals("Read chapter", taskList.getTask("get 1").getDescription());
        assertTrue(updatedTask.isDone());
    }

    @Test
    void edit_differentTaskType_throwsException() throws BagsException {
        TaskList taskList = createTaskList();
        Deadline replacementTask = new Deadline("Submit report", "2026-09-12 23:59");

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.edit("edit 1", replacementTask)
        );

        assertTrue(exception.getMessage().contains("same type"));
    }

    @Test
    void markDone_existingTask_marksCorrectTaskAsDone()
            throws BagsException {
        TaskList taskList = createTaskList();

        Task task = taskList.markDone("done 1");

        assertTrue(task.isDone());
    }

    @Test
    void markDone_missingTaskNumber_exceptionThrown()
            throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.markDone("done")
        );

        assertTrue(exception.getMessage().contains("forget a task number"));
    }

    @Test
    void markDone_taskZero_rejectsInput() throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.markDone("done 0")
        );

        assertTrue(exception.getMessage().contains("Task does not exist"));
    }

    @Test
    void markDone_taskNumberGreaterThanListSize_rejectsInput()
            throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.markDone("done 3")
        );

        assertTrue(exception.getMessage().contains("Task does not exist"));
    }

    @Test
    void markDone_nonNumericInput_rejectsInput()
            throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.markDone("done abc")
        );

        assertTrue(exception.getMessage().contains("Invalid task number"));
    }

    @Test
    void markUndone_addedTask_marksCorrectTaskUndone()
            throws BagsException {
        TaskList taskList = createTaskList();

        taskList.markDone("done 1");

        Task task = taskList.markUndone("undone 1");
        List<String> readingFileRecords = taskList.saveRecords();

        assertFalse(task.isDone());
        assertEquals(task.parseEvent(), readingFileRecords.get(0));
    }

    @Test
    void markUndone_missingTaskNumber_exceptionThrown()
            throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.markUndone("undone")
        );

        assertTrue(exception.getMessage().contains("forget a task number"));
    }

    @Test
    void markUndone_taskZero_rejectsInput() throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.markUndone("undone 0")
        );

        assertTrue(exception.getMessage().contains("Task does not exist"));
    }

    @Test
    void markUndone_taskNumberGreaterThanListSize_rejectsInput()
            throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.markUndone("undone 3")
        );

        assertTrue(exception.getMessage().contains("Task does not exist"));
    }

    @Test
    void markUndone_nonNumericTaskNumber_rejectsInput()
            throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.markUndone("undone abc")
        );

        assertTrue(exception.getMessage().contains("Invalid task number"));
    }

    @Test
    void deleteMethod_deleteOne_removesCorrectTask()
            throws BagsException {
        TaskList taskList = createTaskList();

        Task deleted = taskList.delete("delete 1");

        assertEquals("Read book", deleted.getDescription());
        assertEquals(1, taskList.getSize());
        assertEquals(
                "Do homework",
                taskList.getTasks().get(0).getDescription()
        );
    }

    @Test
    void deleteMethod_missingTaskNumber_exceptionThrown()
            throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.delete("delete")
        );

        assertTrue(exception.getMessage().contains("forget a task number"));
    }

    @Test
    void deleteMethod_taskZero_rejectsInput()
            throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.delete("delete 0")
        );

        assertTrue(exception.getMessage().contains("Task does not exist"));
    }

    @Test
    void deleteMethod_taskNumberGreaterThanListSize_rejectsInput()
            throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.delete("delete 3")
        );

        assertTrue(exception.getMessage().contains("Task does not exist"));
    }

    @Test
    void deleteMethod_nonNumericTaskInput_rejectsInput()
            throws BagsException {
        TaskList taskList = createTaskList();

        BagsException exception = assertThrows(
                BagsException.class, () -> taskList.delete("delete abc")
        );

        assertTrue(exception.getMessage().contains("Invalid task number"));
    }
}
