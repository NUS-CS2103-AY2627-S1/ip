package chatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests ordered task-list operations and type-aware duplicate detection. */
class TaskListTest {

    @Test
    void list_addGetRemoveAndIterateInOrder() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(first, tasks.iterator().next());
        tasks.remove(0);
        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    @Test
    void isDuplicate_matchesOnlySameConcreteTypeAndDetails() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("same description"));
        tasks.add(new Deadline("same description", "2026-09-01 1800"));
        tasks.add(new Event("meeting", "2026-09-02 1000", "2026-09-02 1100"));

        assertTrue(tasks.isDuplicate(new Todo("same description")));
        assertTrue(tasks.isDuplicate(new Deadline("same description", "2026-09-01 1800")));
        assertTrue(tasks.isDuplicate(new Event("meeting", "2026-09-02 1000", "2026-09-02 1100")));
        assertFalse(tasks.isDuplicate(new Deadline("same description", "2026-09-01 1900")));
        assertFalse(tasks.isDuplicate(new Todo("meeting")));
    }
}
