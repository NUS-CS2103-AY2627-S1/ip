package es;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void addGetRemove_updatesCollection() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Deadline second = assertDeadline("second", "2026-08-28");

        tasks.add(first);
        tasks.add(second);
        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.remove(1));
        assertEquals(1, tasks.size());
    }

    @Test
    void find_emptyList_returnsNoMatches() {
        TaskList tasks = new TaskList();

        assertEquals(new ArrayList<Integer>(), tasks.find("anything"));
    }

    private Deadline assertDeadline(String description, String date) {
        try {
            return new Deadline(description, date);
        } catch (EsException e) {
            throw new AssertionError(e);
        }
    }
}
