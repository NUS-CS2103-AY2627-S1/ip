package cooper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import cooper.exception.CooperException;

/** Tests the one-based task-list operations used by Cooper commands. */
public class TaskListTest {
    @Test
    public void addAndGet_multipleTasks_usesOneBasedIndexes() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(1));
        assertEquals(second, tasks.get(2));
    }

    @Test
    public void get_indexOutsideList_throwsCooperException() {
        TaskList tasks = new TaskList(List.of(new ToDo("only task")));

        assertInvalidIndex(() -> tasks.get(0));
        assertInvalidIndex(() -> tasks.get(2));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        Task deletedTask = tasks.delete(1);

        assertEquals(first, deletedTask);
        assertEquals(List.of(second), tasks.asList());
    }

    @Test
    public void delete_indexOutsideList_throwsCooperException() {
        TaskList tasks = new TaskList(List.of(new ToDo("only task")));

        assertInvalidIndex(() -> tasks.delete(-1));
        assertInvalidIndex(() -> tasks.delete(2));
    }

    @Test
    public void asList_returnedSnapshot_cannotMutateTaskList() {
        TaskList tasks = new TaskList(List.of(new ToDo("original")));
        List<Task> snapshot = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new ToDo("new")));
        tasks.add(new ToDo("later"));

        assertEquals(1, snapshot.size());
        assertEquals(2, tasks.size());
    }

    @Test
    public void find_keywordMatchesSomeDescriptions_returnsMatchesInOriginalOrder() {
        Task firstMatch = new ToDo("read book");
        Task nonMatch = new ToDo("buy groceries");
        Task secondMatch = new ToDo("return book");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        assertEquals(List.of(firstMatch, secondMatch), tasks.find("book"));
    }

    @Test
    public void find_keywordDoesNotMatch_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));

        assertEquals(List.of(), tasks.find("movie"));
    }

    private static void assertInvalidIndex(Runnable operation) {
        CooperException exception = assertThrows(CooperException.class, operation::run);
        assertEquals("I couldn't find a task with that index :(", exception.getMessage());
    }
}
