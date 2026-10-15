package ip.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ip.model.Task;
import ip.model.Todo;

class TaskListTest {
    @Test
    void find_matchingKeyword_returnsMatchingTasksIgnoringCase() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read book"));
        tasks.add(new Todo("Buy groceries"));
        tasks.add(new Todo("Return BOOK"));

        List<Task> matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("Read book", matchingTasks.get(0).getDescription());
        assertEquals("Return BOOK", matchingTasks.get(1).getDescription());
    }

    @Test
    void find_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read book"));

        assertEquals(0, tasks.find("movie").size());
    }
}
