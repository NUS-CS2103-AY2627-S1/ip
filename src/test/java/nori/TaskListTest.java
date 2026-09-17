package nori;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nori.task.Todo;

class TaskListTest {
    @Test
    void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    void find_keywordWithDifferentCase_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        tasks.add(new Todo("buy groceries"));

        TaskList matches = tasks.find("book");

        assertEquals(1, matches.size());
        assertEquals("[T][ ] Read Book", matches.get(0).toString());
    }

    @Test
    void find_missingKeyword_returnsEmptyIndependentList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        TaskList matches = tasks.find("groceries");

        assertEquals(0, matches.size());
        assertEquals(1, tasks.size());
    }
}
