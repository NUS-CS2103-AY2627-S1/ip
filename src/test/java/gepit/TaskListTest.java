package gepit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import gepit.task.Task;
import gepit.task.Todo;

public class TaskListTest {

    @Test
    public void find_matchingKeyword_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy bread"));
        tasks.add(new Todo("return book"));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void find_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> matches = tasks.find("bread");

        assertEquals(0, matches.size());
    }

    @Test
    public void find_differentLetterCase_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        List<Task> matches = tasks.find("book");

        assertEquals(1, matches.size());
    }

    @Test
    void updateDescription_markedTask_preservesMarkedStatus() {
        Task task = new Todo("old description");
        task.markDone();

        task.updateDescription("new description");

        assertEquals("[T][X] new description", task.toString());
    }
}
