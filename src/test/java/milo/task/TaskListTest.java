package milo.task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import milo.exception.MiloException;

class TaskListTest {
    @Test
    void constructor_noArgs_startsEmpty() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    void constructor_fromExistingList_preservesTasks() {
        ArrayList<Task> existing = new ArrayList<>();
        existing.add(new Todo("read book"));
        TaskList tasks = new TaskList(existing);
        assertEquals(1, tasks.size());
    }

    @Test
    void add_toEmptyList_increasesSizeToOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
        assertFalse(tasks.isEmpty());
    }

    @Test
    void get_validIndex_returnsTaskAtThatPosition() throws MiloException {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        tasks.add(todo);
        assertSame(todo, tasks.get(0));
    }

    @Test
    void get_negativeIndex_throwsMiloException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(MiloException.class, () -> tasks.get(-1));
    }

    @Test
    void get_indexEqualToSize_throwsMiloException() {
        // The classic off-by-one boundary: size() itself is one past the
        // last valid index.
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(MiloException.class, () -> tasks.get(1));
    }

    @Test
    void get_onEmptyList_throwsMiloException() {
        TaskList tasks = new TaskList();
        assertThrows(MiloException.class, () -> tasks.get(0));
    }

    @Test
    void delete_validIndex_removesAndReturnsTask() throws MiloException {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        tasks.add(todo);
        Task removed = tasks.delete(0);
        assertSame(todo, removed);
        assertEquals(0, tasks.size());
    }

    @Test
    void delete_middleTask_shiftsLaterTasksDown() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        tasks.add(new Todo("c"));
        tasks.delete(0);
        assertEquals("[T][ ] b", tasks.get(0).toString());
        assertEquals("[T][ ] c", tasks.get(1).toString());
        assertEquals(2, tasks.size());
    }

    @Test
    void delete_outOfRangeIndex_throwsMiloExceptionAndListUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(MiloException.class, () -> tasks.delete(5));
        assertEquals(1, tasks.size());
    }

    @Test
    void getDisplayLines_emptyList_returnsPlaceholderMessage() {
        TaskList tasks = new TaskList();
        assertArrayEquals(
                new String[] {"Your list is empty — nothing to chase yet."},
                tasks.getDisplayLines());
    }

    @Test
    void getDisplayLines_withTasks_returnsHeaderPlusNumberedLines() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        assertArrayEquals(
                new String[] {
                    "Here's everything on your list:",
                    "1.[T][ ] read book",
                    "2.[T][ ] return book"
                },
                tasks.getDisplayLines());
    }

    @Test
    void containsSameAs_identicalTask_isFound() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertTrue(tasks.containsSameAs(new Todo("read book")));
    }

    @Test
    void containsSameAs_differentDescription_isNotFound() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertFalse(tasks.containsSameAs(new Todo("read books")));
    }

    @Test
    void containsSameAs_emptyList_isNotFound() {
        assertFalse(new TaskList().containsSameAs(new Todo("read book")));
    }

    @Test
    void containsSameAs_ignoresWhetherTheTaskIsDone() throws MiloException {
        // A task already ticked off is still the same task, so adding it
        // again should still count as a duplicate.
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.get(0).markAsDone();
        assertTrue(tasks.containsSameAs(new Todo("read book")));
    }

    @Test
    void containsSameAs_doesNotChangeTheStoredTask() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.get(0).markAsDone();

        tasks.containsSameAs(new Todo("read book"));
        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    void restorePrevious_noSnapshotTaken_returnsFalse() {
        assertFalse(new TaskList().restorePrevious());
    }

    @Test
    void restorePrevious_afterSnapshot_undoesAnAddedTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.saveSnapshot();
        tasks.add(new Todo("b"));

        assertTrue(tasks.restorePrevious());
        assertEquals(1, tasks.size());
    }

    @Test
    void restorePrevious_snapshotIsIndependentOfLaterMarking() throws MiloException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.saveSnapshot();
        tasks.get(0).markAsDone();

        assertTrue(tasks.restorePrevious());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void restorePrevious_usedTwice_secondCallReturnsFalse() {
        TaskList tasks = new TaskList();
        tasks.saveSnapshot();
        tasks.add(new Todo("a"));

        assertTrue(tasks.restorePrevious());
        assertFalse(tasks.restorePrevious());
    }

    @Test
    void getMatchingDisplayLines_noTasksMatch_returnsPlaceholderMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertArrayEquals(
                new String[] {"I had a good sniff around and found nothing matching."},
                tasks.getMatchingDisplayLines("essay"));
    }

    @Test
    void getMatchingDisplayLines_someTasksMatch_returnsRenumberedFromOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        tasks.add(new Todo("return book"));
        assertArrayEquals(
                new String[] {
                    "Here's what I sniffed out:",
                    "1.[T][ ] read book",
                    "2.[T][ ] return book"
                },
                tasks.getMatchingDisplayLines("book"));
    }

    @Test
    void getMatchingDisplayLines_keywordIsCaseSensitive() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read Book"));
        assertArrayEquals(
                new String[] {"I had a good sniff around and found nothing matching."},
                tasks.getMatchingDisplayLines("book"));
    }

    @Test
    void asArrayList_reflectsTasksInInsertionOrder() {
        TaskList tasks = new TaskList();
        Task first = new Todo("a");
        Task second = new Todo("b");
        tasks.add(first);
        tasks.add(second);
        assertEquals(2, tasks.asArrayList().size());
        assertSame(first, tasks.asArrayList().get(0));
        assertSame(second, tasks.asArrayList().get(1));
    }
}
