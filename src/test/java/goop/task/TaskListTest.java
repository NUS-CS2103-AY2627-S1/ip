package goop.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests ordering, state changes, and encapsulation of {@link TaskList}.
 */
class TaskListTest {
    @Test
    void constructorAndGetTasks_externalListChangesCannotMutateTaskList() {
        Todo firstTask = new Todo("first");
        List<Task> sourceTasks = new ArrayList<>(List.of(firstTask));
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.clear();
        List<Task> snapshotTasks = tasks.getTasks();
        tasks.add(new Todo("second"));

        assertEquals(2, tasks.size());
        assertEquals(1, snapshotTasks.size());
        assertSame(firstTask, snapshotTasks.get(0));
        assertThrows(UnsupportedOperationException.class, () ->
                snapshotTasks.add(new Todo("cannot add")));
    }

    @Test
    void setDone_trueThenFalse_updatesSelectedTaskOnly() {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));

        tasks.setDone(1, true);
        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());

        tasks.setDone(1, false);
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    void deleteThenIndexedAdd_restoresOriginalOrder() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        Todo third = new Todo("third");
        TaskList tasks = new TaskList(List.of(first, second, third));

        Task deleted = tasks.delete(1);
        tasks.add(1, deleted);

        assertEquals(List.of(first, second, third), tasks.getTasks());
    }

    @Test
    void find_keywordInSomeDescriptions_returnsMatchingTasksInOrder() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("write essay");
        Todo secondMatch = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matches = tasks.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matches);
        assertThrows(UnsupportedOperationException.class, () ->
                matches.add(new Todo("another book")));
    }
}
