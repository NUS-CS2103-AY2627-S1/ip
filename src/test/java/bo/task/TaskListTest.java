package bo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskList}.
 *
 * Focuses on:
 *  - add/remove/get and how the list's size and ordering change
 *  - isValidTaskNumber, which is off-by-one-prone (1-based task numbers vs 0-based list index)
 *  - asList returning an independent snapshot (should not let outside code mutate internal state)
 */
public class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
    }

    // ---------- add(Task) ----------

    @Test
    public void add_singleTask_sizeIncreasesAndTaskRetrievable() {
        Todo todo = new Todo("read book");
        taskList.add(todo);

        assertEquals(1, taskList.size());
        assertEquals(todo, taskList.get(0));
    }

    @Test
    public void add_multipleTasks_preservesInsertionOrder() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        taskList.add(first);
        taskList.add(second);

        assertEquals(first, taskList.get(0));
        assertEquals(second, taskList.get(1));
        assertEquals(2, taskList.size());
    }

    // ---------- add(int, Task) : insert at index ----------

    @Test
    public void add_atIndex_insertsWithoutOverwriting() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        taskList.add(first);
        taskList.add(second);

        Todo inserted = new Todo("inserted");
        taskList.add(1, inserted); // between first and second

        assertEquals(3, taskList.size());
        assertEquals(first, taskList.get(0));
        assertEquals(inserted, taskList.get(1));
        assertEquals(second, taskList.get(2));
    }

    // ---------- remove(int) ----------

    @Test
    public void remove_middleIndex_returnsRemovedTaskAndShiftsRest() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        Todo third = new Todo("third");
        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        Task removed = taskList.remove(1);

        assertEquals(second, removed);
        assertEquals(2, taskList.size());
        assertEquals(first, taskList.get(0));
        assertEquals(third, taskList.get(1)); // shifted down after removal
    }

    // ---------- isValidTaskNumber(int) ----------
    // This method is the most bug-prone: task numbers shown to the user are 1-based,
    // but the internal list is 0-based, so boundary values are exactly where bugs hide.

    @Test
    public void isValidTaskNumber_withinRange_returnsTrue() {
        taskList.add(new Todo("only task"));

        assertTrue(taskList.isValidTaskNumber(1)); // lowest valid number
    }

    @Test
    public void isValidTaskNumber_zero_returnsFalse() {
        taskList.add(new Todo("only task"));

        assertFalse(taskList.isValidTaskNumber(0)); // one below the valid range
    }

    @Test
    public void isValidTaskNumber_negative_returnsFalse() {
        taskList.add(new Todo("only task"));

        assertFalse(taskList.isValidTaskNumber(-5));
    }

    @Test
    public void isValidTaskNumber_oneAboveSize_returnsFalse() {
        taskList.add(new Todo("only task"));

        assertFalse(taskList.isValidTaskNumber(2)); // one above the valid range
    }

    @Test
    public void isValidTaskNumber_emptyList_returnsFalseEvenForOne() {
        assertFalse(taskList.isValidTaskNumber(1));
    }

    // ---------- asList() ----------

    @Test
    public void asList_returnsTasksInOrder() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        taskList.add(first);
        taskList.add(second);

        List<Task> snapshot = taskList.asList();

        assertEquals(List.of(first, second), snapshot);
    }

    @Test
    public void asList_isIndependentSnapshot_doesNotAffectInternalList() {
        taskList.add(new Todo("first"));
        List<Task> snapshot = taskList.asList();

        taskList.add(new Todo("second")); // mutate the taskList after taking snapshot

        assertEquals(1, snapshot.size(), "snapshot should not reflect changes made after it was taken");
        assertEquals(2, taskList.size());
    }
}
