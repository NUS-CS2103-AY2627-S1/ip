package alzara.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskList}: the thin wrapper around an {@code ArrayList<Task>} that
 * every command operates on. The "is this the task I added?" checks below use
 * assertSame (reference identity) rather than assertEquals, since Task's own
 * equals() (tested separately via hasDuplicate() below) compares details
 * rather than identity, and these tests care about which instance came back.
 */
class TaskListTest {

    // A TaskList built from an empty ArrayList starts out empty.
    @Test
    void size_emptyList_returnsZero() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertEquals(0, taskList.size());
    }

    // add() should increase size() by one.
    @Test
    void add_singleTask_sizeIncreases() {
        TaskList taskList = new TaskList(new ArrayList<>());

        taskList.add(new Task("read book"));

        assertEquals(1, taskList.size());
    }

    // get() should return the exact Task instance that was added at that index.
    @Test
    void get_afterAdd_returnsSameTaskInstance() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Task task = new Task("read book");

        taskList.add(task);

        assertSame(task, taskList.get(0));
    }

    // Tasks should come back out in the order they were added.
    @Test
    void add_multipleTasks_maintainsInsertionOrder() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Task first = new Task("read book");
        Task second = new Task("borrow book");

        taskList.add(first);
        taskList.add(second);

        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
    }

    // delete() should return the removed task and shrink the list, shifting
    // later tasks down by one index.
    @Test
    void delete_existingIndex_returnsRemovedTaskAndShiftsRemaining() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Task first = new Task("read book");
        Task second = new Task("borrow book");
        taskList.add(first);
        taskList.add(second);

        Task removed = taskList.delete(0);

        assertSame(first, removed);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    // get() on an empty (or otherwise out-of-range) list isn't bounds-checked
    // with a thrown exception - it's guarded by an assert instead, since a
    // caller reaching this with an invalid index is a programming bug, not
    // something a well-behaved caller should ever trigger. Callers (see
    // Command subclasses) are responsible for validating the index first.
    @Test
    void get_indexOutOfBounds_assertionErrorThrown() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertThrows(AssertionError.class, () -> taskList.get(0));
    }

    // Same as above, but for delete().
    @Test
    void delete_indexOutOfBounds_assertionErrorThrown() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertThrows(AssertionError.class, () -> taskList.delete(0));
    }

    // getTasks() exposes the live underlying list, not a defensive copy - a
    // reference obtained before an add() should still reflect that add()
    // afterwards. Storage.save() and Ui.showTaskList() both depend on this.
    @Test
    void getTasks_afterSubsequentAdd_reflectsTheAddedTask() {
        TaskList taskList = new TaskList(new ArrayList<>());
        ArrayList<Task> tasksView = taskList.getTasks();
        Task task = new Task("read book");

        taskList.add(task);

        assertEquals(1, tasksView.size());
        assertSame(task, tasksView.get(0));
    }

    // --- hasDuplicate() ---

    // An empty list has no duplicate of anything.
    @Test
    void hasDuplicate_emptyList_returnsFalse() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertFalse(taskList.hasDuplicate(new ToDo("read book")));
    }

    // Two ToDos with the same description (same case) are duplicates.
    @Test
    void hasDuplicate_toDoSameDescription_returnsTrue() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new ToDo("read book"));

        assertTrue(taskList.hasDuplicate(new ToDo("read book")));
    }

    // The description comparison ignores case.
    @Test
    void hasDuplicate_toDoDifferentCase_returnsTrue() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new ToDo("read book"));

        assertTrue(taskList.hasDuplicate(new ToDo("READ BOOK")));
    }

    // A different description is not a duplicate.
    @Test
    void hasDuplicate_toDoDifferentDescription_returnsFalse() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new ToDo("read book"));

        assertFalse(taskList.hasDuplicate(new ToDo("borrow book")));
    }

    // Same description but a different task type (ToDo vs Deadline) is not a duplicate.
    @Test
    void hasDuplicate_sameDescriptionDifferentType_returnsFalse() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new ToDo("read book"));

        assertFalse(taskList.hasDuplicate(new Deadline("read book", LocalDate.of(2019, 10, 15))));
    }

    // Two Deadlines are duplicates only when both the description and the due date match.
    @Test
    void hasDuplicate_deadlineSameDescriptionAndDate_returnsTrue() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));

        assertTrue(taskList.hasDuplicate(new Deadline("return book", LocalDate.of(2019, 10, 15))));
    }

    @Test
    void hasDuplicate_deadlineSameDescriptionDifferentDate_returnsFalse() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));

        assertFalse(taskList.hasDuplicate(new Deadline("return book", LocalDate.of(2019, 10, 16))));
    }

    // Two Events are duplicates only when the description and both dates match.
    @Test
    void hasDuplicate_eventSameDescriptionAndDates_returnsTrue() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new Event("trip", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16)));

        assertTrue(taskList.hasDuplicate(
                new Event("trip", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16))));
    }

    @Test
    void hasDuplicate_eventSameDescriptionDifferentDates_returnsFalse() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new Event("trip", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16)));

        assertFalse(taskList.hasDuplicate(
                new Event("trip", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 17))));
    }

    // A completed task is still a duplicate of an equivalent not-yet-done task -
    // completion state is not part of what makes two tasks "the same details".
    @Test
    void hasDuplicate_matchIsAlreadyMarkedDone_stillReturnsTrue() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Task doneTask = new ToDo("read book");
        doneTask.mark();
        taskList.add(doneTask);

        assertTrue(taskList.hasDuplicate(new ToDo("read book")));
    }
}
