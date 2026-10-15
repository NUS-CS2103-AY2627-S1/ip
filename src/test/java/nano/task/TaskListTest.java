package nano.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import nano.NanoException;

public class TaskListTest {

    @Test
    void findReturnsMatchingTasks() throws NanoException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("go shopping"));
        tasks.add(new Todo("return book"));

        TaskList result = tasks.find("book");

        assertEquals(2, result.size());
    }

    @Test
    void findIsCaseInsensitive() throws NanoException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        TaskList result = tasks.find("book");

        assertEquals(1, result.size());
    }

    @Test
    void findReturnsEmptyListWhenNoMatch() throws NanoException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        TaskList result = tasks.find("movie");

        assertEquals(0, result.size());
    }
}
