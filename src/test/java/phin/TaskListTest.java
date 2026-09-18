package phin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Checks task ordering, status updates, and collection ownership.
 */
class TaskListTest {
    /**
     * Checks substring matching and preserves task identity, type, status, and order.
     */
    @Test
    void find_matchingDescriptions_preservesTasksAndOrder() {
        Task first = new Todo("read book");
        Task second = new Deadline("return book", "2024-03-01");
        Task third = new Event("bookshelf", "2024-03-01", "2024-03-02");
        second.markAsDone();
        TaskList tasks = new TaskList(List.of(first, new Todo("unrelated"), second, third));
        assertEquals(List.of(first, second, third), tasks.find("book"));
        assertEquals(List.of(first), tasks.find("read book"));
        assertTrue(second.isDone);
        assertFalse(first.isDone);
        assertFalse(third.isDone);
        assertEquals(4, tasks.size());
    }

    /**
     * Checks that case, dates, type icons, and status icons do not produce false matches.
     */
    @Test
    void find_absentOrNonDescriptionText_returnsEmpty() {
        TaskList tasks = new TaskList(List.of(new Deadline("read book", "2024-03-01")));
        for (String keyword : new String[] {"Book", "missing", "Mar", "2024", "[D]", "[ ]"}) {
            assertTrue(tasks.find(keyword).isEmpty(), keyword);
        }
        assertTrue(new TaskList().find("book").isEmpty());
    }

    /**
     * Checks that search snapshots cannot structurally edit the stored list.
     */
    @Test
    void find_resultCollection_cannotChangeStoredTasks() {
        Task first = new Todo("book");
        TaskList tasks = new TaskList(List.of(first));
        List<Task> matches = tasks.find("book");
        assertThrows(UnsupportedOperationException.class, matches::clear);
        assertThrows(UnsupportedOperationException.class, () -> matches.add(new Todo("external")));
        assertEquals(List.of(first), tasks.asList());
        tasks.add(new Todo("second book"));
        assertEquals(List.of(first), matches);
        assertEquals(2, tasks.size());
    }

    @Test
    void constructor_loadedList_copiesCollectionButPreservesTasks() {
        Task task = new Todo("saved");
        task.markAsDone();
        List<Task> loaded = new ArrayList<>(List.of(task));
        TaskList tasks = new TaskList(loaded);
        loaded.clear();
        assertEquals(1, tasks.size());
        assertSame(task, tasks.asList().get(0));
        assertTrue(tasks.asList().get(0).isDone);
    }

    @Test
    void delete_middleAndRemainingTasks_preservesOrderAndBecomesEmpty() {
        Task first = new Todo("first");
        Task middle = new Deadline("middle", "2024-03-01");
        Task last = new Event("last", "2024-03-01", "2024-03-02");
        TaskList tasks = new TaskList();
        tasks.add(first);
        tasks.add(middle);
        tasks.add(last);
        assertSame(middle, tasks.delete(1));
        assertEquals(List.of(first, last), tasks.asList());
        assertSame(first, tasks.delete(0));
        assertSame(last, tasks.delete(0));
        assertEquals(0, tasks.size());
    }

    @Test
    void setDone_markAndUnmark_changesOnlySelectedTask() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        assertSame(second, tasks.setDone(1, true));
        tasks.setDone(1, true);
        assertTrue(second.isDone);
        assertFalse(first.isDone);
        assertSame(second, tasks.setDone(1, false));
        tasks.setDone(1, false);
        assertFalse(second.isDone);
    }

    @Test
    void asList_snapshot_disallowsCollectionEditsAndKeepsOldOrdering() {
        Task first = new Todo("first");
        TaskList tasks = new TaskList(List.of(first));
        List<Task> snapshot = tasks.asList();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("external")));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        tasks.add(new Todo("second"));
        assertEquals(List.of(first), snapshot);
        assertEquals(2, tasks.size());
        tasks.setDone(0, true);
        assertTrue(snapshot.get(0).isDone); // Snapshots intentionally share task objects.
    }

    @Test
    void mutations_invalidIndices_leaveListUnchanged() {
        Task task = new Todo("keep");
        TaskList tasks = new TaskList(List.of(task));
        for (int index : new int[] {-1, 1}) {
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.setDone(index, true));
        }
        assertEquals(List.of(task), tasks.asList());
        assertFalse(task.isDone);
    }

    @Test
    void update_middleTask_replacesOnlySelectedTask() {
        Task first = new Todo("first");
        Task original = new Deadline("old", "2024-03-01");
        Task replacement = new Deadline("new", "2024-03-02");
        TaskList tasks = new TaskList(List.of(first, original));

        assertSame(replacement, tasks.update(1, replacement));
        assertEquals(List.of(first, replacement), tasks.asList());
    }

    @Test
    void containsEquivalent_sameDetailsIgnoresStatusButRespectsTypeAndDates() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit", "2024-03-01");
        TaskList tasks = new TaskList(List.of(todo, deadline));
        Todo completedCopy = new Todo("read book");
        completedCopy.markAsDone();

        assertTrue(tasks.containsEquivalent(completedCopy));
        assertTrue(tasks.containsEquivalent(new Deadline("submit", "2024-03-01")));
        assertFalse(tasks.containsEquivalent(new Deadline("submit", "2024-03-02")));
        assertFalse(tasks.containsEquivalent(new Deadline("read book", "2024-03-01")));
    }
}
