package jimbo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskList}. Only {@link TaskList#find} is covered, since the
 * rest of TaskList's methods are one-line delegates to the underlying
 * {@link ArrayList} with no logic of their own to break.
 */
public class TaskListTest {
    @Test
    public void find_matchingKeyword_returnsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("buy milk"));

        ArrayList<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void find_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        ArrayList<Task> matches = tasks.find("milk");

        assertTrue(matches.isEmpty());
    }

    @Test
    public void find_keywordDifferentCaseFromDescription_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        ArrayList<Task> matches = tasks.find("book");

        assertEquals(1, matches.size());
        assertEquals("Read Book", matches.get(0).getDescription());
    }

    @Test
    public void find_emptyList_returnsEmptyList() {
        TaskList tasks = new TaskList();

        ArrayList<Task> matches = tasks.find("book");

        assertTrue(matches.isEmpty());
    }
}
