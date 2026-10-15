package coco;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void find_keywordMatchesMultipleTasks_returnsAllInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy groceries"));
        tasks.add(new Todo("return book"));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[T][ ] return book", matches.get(1).toString());
    }

    @Test
    public void find_keywordDifferentCaseFromDescription_matchesCaseInsensitively() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> matches = tasks.find("BOOK");

        assertEquals(1, matches.size());
    }

    @Test
    public void find_noTaskContainsKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> matches = tasks.find("laptop");

        assertTrue(matches.isEmpty());
    }

    @Test
    public void find_emptyTaskList_returnsEmptyList() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.find("anything").isEmpty());
    }
}
