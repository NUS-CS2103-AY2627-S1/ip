package atlas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/** Tests adding, retrieving, and removing tasks from a task list. */
public class TaskListTest {

    @Test
    void newTaskListIsEmpty() {
        TaskList taskList = new TaskList();

        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
    }

    @Test
    void addingTasksPreservesTheirOrder() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    @Test
    void removingTaskReturnsItAndClosesTheGap() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");
        taskList.add(first);
        taskList.add(second);

        Task removed = taskList.remove(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    void findingTasksMatchesDescriptionsInOrder() {
        TaskList taskList = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("visit museum");
        Task third = new Todo("return book");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        ArrayList<Task> matches = taskList.find("book");

        assertEquals(2, matches.size());
        assertSame(first, matches.get(0));
        assertSame(third, matches.get(1));
        assertTrue(taskList.find("BOOK").isEmpty());
    }

    @Test
    void findingWithMultipleKeywordsMatchesAnyOfThem() {
        TaskList taskList = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("visit museum");
        Task third = new Todo("return book");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        ArrayList<Task> matches = taskList.find("museum", "book");

        assertEquals(3, matches.size());
        assertSame(first, matches.get(0));
        assertSame(second, matches.get(1));
        assertSame(third, matches.get(2));
        assertTrue(taskList.find("nonsense", "missing").isEmpty());
    }
}
