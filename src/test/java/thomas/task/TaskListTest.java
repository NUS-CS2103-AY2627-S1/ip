package thomas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;

class TaskListTest {
    @Test
    void addGetDeleteAndSize_maintainTaskOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(first, tasks.delete(0));
        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
    }

    @Test
    void mark_incompleteTask_marksItDone() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("finish"));

        tasks.mark(0);

        assertTrue(tasks.get(0).isDone());
    }

    @Test
    void mark_completedTask_throwsException() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("finish"));
        tasks.mark(0);

        assertThrows(ThomasException.class, () -> tasks.mark(0));
    }

    @Test
    void unmark_completedTask_marksItIncomplete() throws ThomasException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("finish"));
        tasks.mark(0);

        tasks.unmark(0);

        assertFalse(tasks.get(0).isDone());
    }

    @Test
    void unmark_incompleteTask_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("finish"));

        assertThrows(ThomasException.class, () -> tasks.unmark(0));
    }

    @Test
    void containsEquivalent_sameDetailsWithDifferentStatus_returnsTrue() {
        Task existing = new Todo("finish");
        existing.markAsDone();
        TaskList tasks = new TaskList();
        tasks.add(existing);

        assertTrue(tasks.containsEquivalent(new Todo("finish")));
        assertFalse(tasks.containsEquivalent(new Todo("different")));
    }
}
