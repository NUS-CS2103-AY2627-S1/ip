package neil.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import neil.exception.NeilException;

/**
 * Tests for managing tasks in a {@link ToDoList}.
 */
class ToDoListTest {

    @Test
    void add_twoTasks_tasksAreNumberedInInsertionOrder() {
        ToDoList taskList = new ToDoList();
        taskList.add(new ToDoTask("read book"));
        taskList.add(new ToDoTask("write report"));

        assertEquals(2, taskList.size());
        assertEquals("1.[T][ ] read book\n2.[T][ ] write report\n", taskList.toString());
    }

    @Test
    void add_nullTask_assertionErrorThrown() {
        ToDoList taskList = new ToDoList();

        assertThrows(AssertionError.class, () -> taskList.add(null));
    }

    @Test
    void remove_existingTask_taskRemovedAndReturned() throws NeilException {
        ToDoList taskList = new ToDoList();
        Task firstTask = new ToDoTask("read book");
        Task secondTask = new ToDoTask("write report");
        taskList.add(firstTask);
        taskList.add(secondTask);

        Task removedTask = taskList.remove(1);

        assertSame(firstTask, removedTask);
        assertEquals(1, taskList.size());
        assertEquals("1.[T][ ] write report\n", taskList.toString());
    }

    @Test
    void remove_invalidTaskNumber_exceptionThrown() {
        ToDoList taskList = new ToDoList();
        taskList.add(new ToDoTask("read book"));

        assertTaskDoesNotExist(() -> taskList.remove(0));
        assertTaskDoesNotExist(() -> taskList.remove(2));
    }

    @Test
    void markTaskAsDone_existingTask_taskMarkedAndReturned() throws NeilException {
        ToDoList taskList = new ToDoList();
        Task task = new ToDoTask("read book");
        taskList.add(task);

        Task markedTask = taskList.markTaskAsDone(1);

        assertSame(task, markedTask);
        assertEquals("[T][X] read book", markedTask.toString());
    }

    @Test
    void markTaskAsDone_invalidTaskNumber_exceptionThrown() {
        ToDoList taskList = new ToDoList();

        assertTaskDoesNotExist(() -> taskList.markTaskAsDone(1));
    }

    @Test
    void unmarkTask_existingDoneTask_taskUnmarkedAndReturned() throws NeilException {
        ToDoList taskList = new ToDoList();
        Task task = new ToDoTask("read book");
        task.markAsDone();
        taskList.add(task);

        Task unmarkedTask = taskList.unmarkTask(1);

        assertSame(task, unmarkedTask);
        assertEquals("[T][ ] read book", unmarkedTask.toString());
    }

    @Test
    void unmarkTask_invalidTaskNumber_exceptionThrown() {
        ToDoList taskList = new ToDoList();

        assertTaskDoesNotExist(() -> taskList.unmarkTask(1));
    }

    @Test
    void getTasks_populatedList_returnsUnmodifiableSnapshot() {
        ToDoList taskList = new ToDoList();
        Task task = new ToDoTask("read book");
        taskList.add(task);

        List<Task> tasks = taskList.getTasks();

        assertEquals(List.of(task), tasks);
        assertThrows(UnsupportedOperationException.class, () -> tasks.add(new ToDoTask("write report")));
    }

    @Test
    void findTasks_multipleMatchingDescriptions_matchesReturnedInOrder() {
        ToDoList taskList = new ToDoList();
        Task firstMatch = new ToDoTask("read book");
        Task nonMatch = new EventTask(
                "attend meeting",
                "2026-08-25 14:00",
                "2026-08-25 16:00");
        Task secondMatch = new DeadlineTask("return BOOK", LocalDate.of(2026, 8, 30));
        taskList.add(firstMatch);
        taskList.add(nonMatch);
        taskList.add(secondMatch);

        List<Task> matchingTasks = taskList.findTasks("book");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
        assertThrows(UnsupportedOperationException.class, () -> matchingTasks.add(nonMatch));
    }

    @Test
    void findTasks_noMatchingDescription_emptyListReturned() {
        ToDoList taskList = new ToDoList();
        taskList.add(new ToDoTask("read book"));

        assertEquals(List.of(), taskList.findTasks("report"));
    }

    /**
     * Verifies that an operation rejects a task number that is not in the list.
     *
     * @param operation operation that uses a task number.
     */
    private void assertTaskDoesNotExist(TaskListOperation operation) {
        assertThrows(NeilException.class, operation::perform);
    }

    /**
     * Represents an operation on a task list that can report a domain-specific error.
     */
    @FunctionalInterface
    private interface TaskListOperation {
        void perform() throws NeilException;
    }
}
