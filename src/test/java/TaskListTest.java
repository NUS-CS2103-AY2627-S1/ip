package sioet.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void find_returnsMatchingTasks() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("read book"));
        initialTasks.add(new Todo("go shopping"));
        initialTasks.add(new Todo("return library book"));

        TaskList taskList = new TaskList(initialTasks);

        List<Task> matches = taskList.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return library book", matches.get(1).getDescription());
    }

    @Test
    public void find_isCaseInsensitive() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("Read Book"));
        initialTasks.add(new Todo("go shopping"));

        TaskList taskList = new TaskList(initialTasks);

        List<Task> matches = taskList.find("book");

        assertEquals(1, matches.size());
        assertEquals("Read Book", matches.get(0).getDescription());
    }

    @Test
    public void find_returnsEmptyListWhenNoMatch() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("read book"));
        initialTasks.add(new Todo("go shopping"));

        TaskList taskList = new TaskList(initialTasks);

        List<Task> matches = taskList.find("exam");

        assertEquals(0, matches.size());
    }
}
