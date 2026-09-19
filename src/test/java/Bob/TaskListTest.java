package bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests task-list operations and command execution. */
public class TaskListTest {
    @Test
    @DisplayName("Adding a todo increases the task count and stores its description")
    public void addTodo_validDescription_taskIsAdded() throws BobException {
        TaskList taskList = new TaskList();

        Task task = taskList.addTodo("Read a book");

        assertEquals(1, taskList.size());
        assertEquals("Read a book", task.getItem());
        assertEquals(task, taskList.get(0));
    }

    @Test
    @DisplayName("Adding an empty todo throws an exception")
    public void addTodo_emptyDescription_exceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(BobException.class, () -> taskList.addTodo(""));
        assertEquals(0, taskList.size());
    }

    @Test
    @DisplayName("Deleting a task removes the requested one")
    public void deleteTask_validTaskNumber_taskRemoved() throws BobException {
        TaskList taskList = new TaskList();
        taskList.addTodo("First task");
        taskList.addTodo("Second task");

        Task deleted = taskList.deleteTask(1);

        assertEquals("First task", deleted.getItem());
        assertEquals(1, taskList.size());
        assertEquals("Second task", taskList.get(0).getItem());
    }

    @Test
    @DisplayName("Deleting an invalid task number throws an exception")
    public void deleteTask_invalidTaskNumber_exceptionThrown() throws BobException {
        TaskList taskList = new TaskList();
        taskList.addTodo("Only task");

        assertThrows(BobException.class, () -> taskList.deleteTask(2));
        assertEquals(1, taskList.size());
    }

    @Test
    @DisplayName("Finding tasks matches descriptions case-insensitively")
    public void findTasks_matchingKeyword_returnsMatchingTasks() throws BobException {
        TaskList taskList = new TaskList();
        taskList.addTodo("Read a book");
        taskList.addTodo("Return the BOOK");
        taskList.addTodo("Buy groceries");

        assertEquals(2, taskList.findTasks("book").size());
        assertEquals("Read a book", taskList.findTasks("book").get(0).getItem());
        assertEquals("Return the BOOK", taskList.findTasks("book").get(1).getItem());
    }

    @Test
    @DisplayName("Editing each task type updates its details and preserves completion")
    public void editTask_allTaskTypes_updatesDetails() throws BobException {
        TaskList taskList = new TaskList();
        taskList.addTodo("Old todo");
        taskList.addDeadline("Old deadline /by 2026-09-01");
        taskList.addEvent("Old event /from 2026-09-01 /to 2026-09-02");
        taskList.markTask(2);

        Task todo = taskList.editTask(1, "New todo");
        Task deadline = taskList.editTask(2, "New deadline /by 2026-10-01");
        Task event = taskList.editTask(3, "New event /from 2026-10-03 /to 2026-10-04");

        assertEquals("New todo", todo.getItem());
        assertEquals("[D][X] New deadline (by: Oct 01 2026)", deadline.toString());
        assertEquals("[E][ ] New event (from: Oct 03 2026 to: Oct 04 2026)", event.toString());
    }
}
