package vega;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void add_validTask_increasesSizeAndStoresTask() throws VegaException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertSame(todo, tasks.get(1));
    }

    @Test
    public void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void delete_existingTask_removesAndReturnsTask() throws VegaException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        tasks.add(todo);

        Task deletedTask = tasks.delete(1);

        assertSame(todo, deletedTask);
        assertEquals(0, tasks.size());
    }

    @Test
    public void get_outOfRangeTaskNumber_throwsVegaException() {
        TaskList tasks = new TaskList();

        assertThrows(VegaException.class, () -> tasks.get(1));
    }

    @Test
    public void find_matchingKeyword_returnsOnlyMatchingTasks() {
        TaskList tasks = new TaskList();
        Todo matchingTodo = new Todo("Read Book");
        tasks.add(matchingTodo);
        tasks.add(new Todo("buy milk"));

        assertEquals(1, tasks.find("book").size());
        assertSame(matchingTodo, tasks.find("book").get(0));
    }

    @Test
    public void find_keywordWithDifferentCase_matchesCaseInsensitively() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Submit Report"));

        assertEquals(1, tasks.find("REPORT").size());
    }
}
