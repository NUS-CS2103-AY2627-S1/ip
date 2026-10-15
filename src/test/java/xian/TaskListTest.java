package xian;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void find_matchingKeyword_returnsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        tasks.add(new Todo("return book"));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.getSize());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return book", matchingTasks.get(1).getDescription());
    }

    @Test
    public void find_upperCaseKeyword_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        TaskList matchingTasks = tasks.find("BOOK");

        assertEquals(1, matchingTasks.getSize());
        assertEquals("read book", matchingTasks.get(0).getDescription());
    }

    @Test
    public void find_noMatchingTask_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        TaskList matchingTasks = tasks.find("laptop");

        assertEquals(0, matchingTasks.getSize());
    }

    @Test
    public void find_matchingKeyword_doesNotModifyOriginalTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));

        tasks.find("book");

        assertEquals(2, tasks.getSize());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("buy milk", tasks.get(1).getDescription());
    }
}
