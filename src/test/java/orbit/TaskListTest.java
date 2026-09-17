package orbit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void add_task_increasesSizeAndStoresTask() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
    }

    @Test
    public void remove_existingTask_returnsTaskAndRemovesIt() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        tasks.add(todo);

        Task removed = tasks.remove(0);

        assertEquals(todo, removed);
        assertTrue(tasks.asList().isEmpty());
    }

    @Test
    public void find_keyword_returnsCaseInsensitiveMatchesOnly() {
        TaskList tasks = new TaskList();
        Todo matchingTask = new Todo("Read Book");
        tasks.add(matchingTask);
        tasks.add(new Todo("buy groceries"));

        assertEquals(List.of(matchingTask), tasks.find("book"));
    }
}
