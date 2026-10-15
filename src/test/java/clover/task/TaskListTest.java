package clover.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests assumptions enforced by {@link TaskList}.
 */
class TaskListTest {

    @Test
    void constructor_nullBackingCollection_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    void add_nullTask_assertionErrorThrown() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertThrows(AssertionError.class, () -> taskList.add(null));
    }

    @Test
    void getLast_emptyTaskList_assertionErrorThrown() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, taskList::getLast);
    }

    @Test
    void constructor_sourceListChanged_taskListIsUnchanged() {
        List<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new ToDo("read book"));
        TaskList taskList = new TaskList(sourceTasks);

        sourceTasks.add(new ToDo("buy groceries"));

        assertEquals(1, taskList.size());
    }

    @Test
    void asList_taskAddedToReturnedList_unsupportedOperationExceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(UnsupportedOperationException.class, () -> taskList.asList().add(new ToDo("read book")));
    }

    @Test
    void containsEquivalent_sameTypeAndDetails_trueReturnedRegardlessOfCompletionStatus() {
        TaskList taskList = new TaskList();
        ToDo existingTask = new ToDo("read book", "Alice");
        existingTask.markAsDone();
        taskList.add(existingTask);

        assertTrue(taskList.containsEquivalent(new ToDo("  READ  BOOK  ", " alice ")));
        assertFalse(taskList.containsEquivalent(new Deadline("read book", LocalDate.of(2026, 9, 1), "Alice")));
    }

    @Test
    void addAndRemove_indexedOperations_preserveExpectedOrder() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("first"));
        taskList.add(0, new ToDo("zeroth"));

        assertEquals("zeroth", taskList.remove(0).getDescription());
        assertEquals("first", taskList.get(0).getDescription());
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(1));
    }

    @Test
    void containsEquivalent_eventWithChangedDatesOrTutoree_falseReturned() {
        TaskList taskList = new TaskList();
        taskList.add(new Event("lesson", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2), "Alice"));

        assertFalse(taskList.containsEquivalent(
                new Event("lesson", LocalDate.of(2026, 9, 3), LocalDate.of(2026, 9, 4), "Alice")));
        assertFalse(taskList.containsEquivalent(
                new Event("lesson", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 3), "Alice")));
        assertTrue(taskList.containsEquivalent(
                new Event(" lesson ", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2), " alice ")));
    }
}
