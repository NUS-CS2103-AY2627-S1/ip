package zeus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import zeus.exception.ZeusException;

/** Tests state changes and one-based task-number validation in {@link TaskList}. */
public class TaskListTest {
    @Test
    public void constructorAndGetTasks_externalListChanges_doNotChangeTaskList() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        List<Task> initialTasks = new ArrayList<>(List.of(first, second));
        TaskList tasks = new TaskList(initialTasks);

        initialTasks.clear();
        List<Task> snapshot = tasks.getTasks();
        tasks.add(new Todo("third"));

        assertEquals(List.of(first, second), snapshot);
        assertEquals(3, tasks.size());
        assertThrows(UnsupportedOperationException.class, () ->
                snapshot.add(new Todo("not allowed")));
    }

    @Test
    public void markAndUnmark_validTaskNumber_updatesAndReturnsSelectedTask()
            throws ZeusException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        Task markedTask = tasks.mark(2);

        assertSame(second, markedTask);
        assertEquals(" ", first.getStatusIcon());
        assertEquals("X", second.getStatusIcon());

        Task unmarkedTask = tasks.unmark(2);

        assertSame(second, unmarkedTask);
        assertEquals(" ", second.getStatusIcon());
    }

    @Test
    public void delete_validTaskNumber_removesAndReturnsSelectedTask() throws ZeusException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        Task third = new Todo("third");
        TaskList tasks = new TaskList(List.of(first, second, third));

        Task deletedTask = tasks.delete(2);

        assertSame(second, deletedTask);
        assertEquals(List.of(first, third), tasks.getTasks());
        assertEquals(2, tasks.size());
    }

    @Test
    public void find_keywordInDescription_returnsMatchingTasksInOriginalOrder() {
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Todo("buy bread");
        Task secondMatch = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matchingTasks = tasks.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
        assertEquals(List.of(), tasks.find("missing"));
    }

    @Test
    public void numberedOperations_emptyList_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertZeusException("Your task list is empty.", () -> tasks.mark(1));
        assertZeusException("Your task list is empty.", () -> tasks.unmark(1));
        assertZeusException("Your task list is empty.", () -> tasks.delete(1));
    }

    @Test
    public void numberedOperations_outOfRange_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));

        assertZeusException("There is no task number 0. Choose a number from 1 to 1.", () ->
                tasks.mark(0));
        assertZeusException("There is no task number 2. Choose a number from 1 to 1.", () ->
                tasks.unmark(2));
        assertZeusException("There is no task number -1. Choose a number from 1 to 1.", () ->
                tasks.delete(-1));
    }

    private static void assertZeusException(String expectedMessage, Executable operation) {
        ZeusException exception = assertThrows(ZeusException.class, operation);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
