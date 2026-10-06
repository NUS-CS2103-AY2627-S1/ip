package ducky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests operations on {@link TaskList}.
 */
class TaskListTest {
    @Test
    void addAndDelete_taskListUpdated() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("read book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
        assertEquals(task, tasks.delete(0));
        assertEquals(0, tasks.size());
    }

    @Test
    void getTasks_returnsDefensiveCopy() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));
        List<Task> copy = tasks.getTasks();

        copy.clear();

        assertEquals(1, tasks.size());
    }

    @Test
    void containsEquivalent_matchesTaskDetailsButNotTaskType() {
        TaskList tasks = new TaskList(List.of(
                new ToDo("read book", List.of("school")),
                new Deadline("read book", java.time.LocalDate.of(2026, 9, 1), List.of("school"))));

        assertTrue(tasks.containsEquivalent(new ToDo("read book", List.of("school"))));
        assertTrue(tasks.containsEquivalent(
                new Deadline("read book", java.time.LocalDate.of(2026, 9, 1), List.of("school"))));
        assertFalse(tasks.containsEquivalent(new ToDo("read book", List.of("fun"))));
    }
}
