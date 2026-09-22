package panda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import panda.exception.InvalidDateException;
import panda.exception.InvalidTaskNumberException;

/**
 * Tests core list mutations, defensive copies, and shared task-number validation.
 */
class TaskListCoreTest {

    @Test
    void constructorsAndAdd_initialCollectionsRemainIndependent() {
        ArrayList<Task> initialTasks = new ArrayList<>(List.of(new Todo("one")));
        TaskList tasks = new TaskList(initialTasks);
        initialTasks.add(new Todo("outside change"));

        tasks.add(new Todo("two"));

        assertEquals(2, tasks.getTaskCount());
        assertEquals("one", tasks.getTasks().get(0).task().getName());
        assertEquals("two", tasks.getTasks().get(1).task().getName());
        assertEquals(1, tasks.getTasks().get(0).number());
        assertEquals(2, tasks.getTasks().get(1).number());
        assertEquals(0, new TaskList().getTaskCount());
    }

    @Test
    void markAndUnmark_validNumber_mutatesAndReturnsSameTask()
            throws InvalidTaskNumberException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));

        assertSame(todo, tasks.mark(1));
        assertTrue(todo.isDone());
        assertSame(todo, tasks.unmark(1));
        assertFalse(todo.isDone());
    }

    @Test
    void numberedOperations_invalidNumbers_throwInvalidTaskNumberException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(InvalidTaskNumberException.class, () -> tasks.mark(0));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.mark(2));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.unmark(-1));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.rename(2, "new name"));
        assertThrows(InvalidTaskNumberException.class, () ->
                tasks.rescheduleDeadline(2, "2026-09-14 10:00"));
        assertThrows(InvalidTaskNumberException.class, () ->
                tasks.rescheduleEvent(2,
                        "2026-09-14 10:00", "2026-09-14 11:00"));
    }

    @Test
    void delete_firstTask_renumbersRemainingTasks() throws InvalidTaskNumberException {
        Todo first = new Todo("one");
        Todo second = new Todo("two");
        Todo third = new Todo("three");
        TaskList tasks = new TaskList(List.of(first, second, third));

        tasks.delete(1);

        assertEquals(2, tasks.getTaskCount());
        assertEquals(1, tasks.getTasks().get(0).number());
        assertSame(second, tasks.getTasks().get(0).task());
        assertEquals(2, tasks.getTasks().get(1).number());
        assertSame(third, tasks.getTasks().get(1).task());
    }

    @Test
    void returnedLists_modificationAttempts_throwUnsupportedOperationException() {
        TaskList tasks = new TaskList(List.of(new Todo("one")));

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.getTasks().add(new TaskList.NumberedTask(2, new Todo("two"))));
        assertThrows(UnsupportedOperationException.class, () ->
                tasks.getTaskSnapshot().add(new Todo("two")));
        assertThrows(UnsupportedOperationException.class, () ->
                tasks.getTasksMatching("one").clear());
    }

    @Test
    void returnedSnapshot_laterListChanges_doNotChangeSnapshot() {
        TaskList tasks = new TaskList(List.of(new Todo("one")));
        List<Task> snapshot = tasks.getTaskSnapshot();

        tasks.add(new Todo("two"));

        assertEquals(1, snapshot.size());
        assertEquals(2, tasks.getTaskCount());
    }

    @Test
    void rescheduleDeadline_invalidDate_keepsOriginalDeadline()
            throws InvalidDateException {
        Deadline deadline = new Deadline("report", "2026-09-14 10:00");
        TaskList tasks = new TaskList(List.of(deadline));

        assertThrows(InvalidDateException.class, () ->
                tasks.rescheduleDeadline(1, "invalid"));

        assertEquals("report (by: Sep 14 2026 10:00)", deadline.getDisplayText());
    }

    @Test
    void renameAndReschedule_emptyList_throwBeforeChangingAnything() {
        TaskList tasks = new TaskList();

        assertThrows(InvalidTaskNumberException.class, () -> tasks.rename(1, "new"));
        assertThrows(InvalidTaskNumberException.class, () ->
                tasks.rescheduleDeadline(1, "2026-09-14 10:00"));
        assertThrows(InvalidTaskNumberException.class, () ->
                tasks.rescheduleEvent(1,
                        "2026-09-14 10:00", "2026-09-14 11:00"));
        assertEquals(0, tasks.getTaskCount());
    }
}
