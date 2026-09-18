package habi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list searches. */
public class TaskListTest {
    @Test
    public void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void constructor_nullTaskCollection_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    public void find_keywordInDescriptions_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2026-09-15"));
        tasks.add(new Event("project meeting", "Mon", "Tue"));

        List<Task> matches = tasks.find("book");

        assertEquals(List.of("read book", "return book"),
                matches.stream().map(Task::getDescription).toList());
        assertTrue(tasks.find("Sep").isEmpty());
    }
}
