package pixel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests the task-list operations that change or validate application state. */
class TaskListTest {

    @Test
    void add_tasks_preservesInsertionOrderAndSize() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    @Test
    void isValidIndex_emptyAndBoundaryIndexes_returnsExpectedResults() {
        TaskList emptyTasks = new TaskList();
        assertFalse(emptyTasks.isValidIndex(0));
        assertFalse(emptyTasks.isValidIndex(-1));

        emptyTasks.add(new Todo("only task"));
        assertTrue(emptyTasks.isValidIndex(0));
        assertFalse(emptyTasks.isValidIndex(1));
        assertFalse(emptyTasks.isValidIndex(-1));
    }

    @Test
    void delete_middleTask_returnsRemovedTaskAndShiftsFollowingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        Task removed = new Todo("second");
        tasks.add(removed);
        Task following = new Todo("third");
        tasks.add(following);

        assertEquals(removed, tasks.delete(1));
        assertEquals(2, tasks.size());
        assertEquals(following, tasks.get(1));
    }

    @Test
    void markAndUnmark_task_updatesCompletionState() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.markAsDone(0);
        assertEquals("[T][X] read book", tasks.get(0).toString());

        tasks.markAsNotDone(0);
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void replace_middleTask_preservesListOrderAndSize() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task replacement = new Todo("updated second");
        Task third = new Todo("third");
        tasks.add(first);
        tasks.add(new Todo("second"));
        tasks.add(third);

        tasks.replace(1, replacement);

        assertEquals(3, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(replacement, tasks.get(1));
        assertEquals(third, tasks.get(2));
    }

    @Test
    void containsSameDetails_sameAndDifferentTasks_returnsExpectedResults() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", java.time.LocalDate.of(2026, 9, 12)));

        assertTrue(tasks.containsSameDetails(new Todo("read book")));
        assertFalse(tasks.containsSameDetails(new Todo("Read book")));
        assertFalse(tasks.containsSameDetails(
                new Deadline("submit report", java.time.LocalDate.of(2026, 9, 13))));
        assertFalse(tasks.containsSameDetailsExcept(new Todo("read book"), 0));
    }

    @Test
    void getAndDelete_invalidIndex_throwIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsDone(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.markAsNotDone(-1));
        assertThrows(IndexOutOfBoundsException.class, () ->
                tasks.replace(1, new Todo("replacement")));
    }
}
