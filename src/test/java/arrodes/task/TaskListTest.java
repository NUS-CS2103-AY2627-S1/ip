package arrodes.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import arrodes.exception.ArrodesException;



/** Tests task-list capacity, mutation, indexing, and retrieval behavior. */
class TaskListTest {

    private TaskList taskList;

    /** Creates a fresh default-capacity list for each test. */
    @BeforeEach
    void setUp() {
        taskList = new TaskList(100);
    }


    /** Rejects a negative list capacity. */
    @Test
    void constructor_negativeCapacity_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskList(-1));
    }

    /** Allows a zero-capacity list and reports it as full. */
    @Test
    void constructor_zeroCapacity_createsEmptyFullList() {
        TaskList zeroList = new TaskList(0);
        assertTrue(zeroList.isFull());
        assertEquals(0, zeroList.getSize());
    }


    /** Returns zero for a new list. */
    @Test
    void getSize_emptyList_returnsZero() {
        assertEquals(0, taskList.getSize());
    }

    /** Reports that a new default list is not full. */
    @Test
    void isFull_emptyList_returnsFalse() {
        assertFalse(taskList.isFull());
    }

    /** Reports full when a list reaches its capacity. */
    @Test
    void isFull_listAtCapacity_returnsTrue() throws ArrodesException {
        TaskList small = new TaskList(1);
        small.insert(new Todo("only task"));
        assertTrue(small.isFull());
    }


    /** Inserts a valid task and increases the size. */
    @Test
    void insert_validTask_increasesSize() throws ArrodesException {
        taskList.insert(new Todo("walk the dog"));
        assertEquals(1, taskList.getSize());
    }

    /** Rejects a null task. */
    @Test
    void insert_nullTask_throwsArrodesException() {
        assertThrows(ArrodesException.class, () -> taskList.insert(null));
    }

    /** Rejects a task with a blank description. */
    @Test
    void construct_taskWithBlankDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Todo("   "));
    }

    /** Rejects insertion after the list reaches capacity. */
    @Test
    void insert_whenFull_throwsArrodesException() throws ArrodesException {
        TaskList small = new TaskList(1);
        small.insert(new Todo("first task"));
        assertThrows(ArrodesException.class, () -> small.insert(new Todo("second task")));
    }

    /** Inserts several valid tasks in order. */
    @Test
    void insert_multipleValidTasks_allInserted() throws ArrodesException {
        taskList.insert(new Todo("task one"));
        taskList.insert(new Todo("task two"));
        taskList.insert(new Todo("task three"));
        assertEquals(3, taskList.getSize());
    }

    /** Rejects a task with the same meaningful details as an existing task. */
    @Test
    void insert_duplicateTask_throwsArrodesException() throws ArrodesException {
        taskList.insert(new Todo("same task"));
        assertThrows(ArrodesException.class, () -> taskList.insert(new Todo("same task")));
    }


    /** Deletes a valid task and decreases the size. */
    @Test
    void delete_validItemNumber_decreasesSize() throws ArrodesException {
        taskList.insert(new Todo("delete me"));
        taskList.delete(1);
        assertEquals(0, taskList.getSize());
    }

    /** Rejects display number zero. */
    @Test
    void delete_itemNumberZero_throwsArrodesException() throws ArrodesException {
        taskList.insert(new Todo("a task"));
        assertThrows(ArrodesException.class, () -> taskList.delete(0));
    }

    /** Rejects a display number beyond the list size. */
    @Test
    void delete_itemNumberBeyondSize_throwsArrodesException() throws ArrodesException {
        taskList.insert(new Todo("a task"));
        assertThrows(ArrodesException.class, () -> taskList.delete(2));
    }

    /** Leaves an empty list after deleting its only task. */
    @Test
    void delete_onlyTask_listBecomesEmpty() throws ArrodesException {
        taskList.insert(new Todo("sole task"));
        taskList.delete(1);
        assertEquals(0, taskList.getSize());
    }

    /** Shifts later tasks after deleting a middle task. */
    @Test
    void delete_middleTask_remainingTasksShiftCorrectly() throws ArrodesException {
        taskList.insert(new Todo("first"));
        taskList.insert(new Todo("second"));
        taskList.insert(new Todo("third"));
        taskList.delete(2); // removes "second"
        assertEquals(2, taskList.getSize());
        assertEquals("first", taskList.getTaskByNumber(1).getDescription());
        assertEquals("third", taskList.getTaskByNumber(2).getDescription());
    }


    /** Retrieves a task by zero-based index. */
    @Test
    void getTaskByIndex_validIndex_returnsCorrectTask() throws ArrodesException {
        taskList.insert(new Todo("alpha"));
        taskList.insert(new Todo("beta"));
        assertEquals("beta", taskList.getTaskByIndex(1).getDescription());
    }

    /** Rejects a negative zero-based index. */
    @Test
    void getTaskByIndex_negativeIndex_throwsArrodesException() throws ArrodesException {
        taskList.insert(new Todo("a task"));
        assertThrows(ArrodesException.class, () -> taskList.getTaskByIndex(-1));
    }

    /** Rejects an index equal to the list size. */
    @Test
    void getTaskByIndex_indexEqualToSize_throwsArrodesException() throws ArrodesException {
        taskList.insert(new Todo("a task"));
        assertThrows(ArrodesException.class, () -> taskList.getTaskByIndex(1));
    }


    /** Retrieves tasks by one-based display number. */
    @Test
    void getTaskByNumber_validNumber_returnsCorrectTask() throws ArrodesException {
        taskList.insert(new Todo("alpha"));
        taskList.insert(new Todo("beta"));
        assertEquals("alpha", taskList.getTaskByNumber(1).getDescription());
        assertEquals("beta", taskList.getTaskByNumber(2).getDescription());
    }

    /** Rejects a display number beyond the list size. */
    @Test
    void getTaskByNumber_numberLargerThanSize_throwsArrodesException() throws ArrodesException {
        taskList.insert(new Todo("a task"));
        assertThrows(ArrodesException.class, () -> taskList.getTaskByNumber(2));
    }

    /** Rejects display number zero. */
    @Test
    void getTaskByNumber_numberZero_throwsArrodesException() {
        assertThrows(ArrodesException.class, () -> taskList.getTaskByNumber(0));
    }


    /** Preserves todo, deadline, and event types in one list. */
    @Test
    void insert_mixedTaskTypes_allStoredCorrectly() throws ArrodesException {
        LocalDateTime due = LocalDateTime.of(2025, 12, 1, 0, 0);
        LocalDateTime start = LocalDateTime.of(2025, 12, 5, 9, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 5, 17, 0);

        taskList.insert(new Todo("todo task"));
        taskList.insert(new Deadline("deadline task", due));
        taskList.insert(new Event("event task", start, end));

        assertEquals(3, taskList.getSize());
        assertInstanceOf(Todo.class, taskList.getTaskByNumber(1));
        assertInstanceOf(Deadline.class, taskList.getTaskByNumber(2));
        assertInstanceOf(Event.class, taskList.getTaskByNumber(3));
    }

    /** Restores a deleted task at the requested zero-based position. */
    @Test
    void restore_validPosition_reinsertsTaskInOrder() throws ArrodesException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        taskList.insert(first);
        taskList.restore(1, second);

        assertEquals(2, taskList.getSize());
        assertEquals("second", taskList.getTaskByNumber(2).getDescription());
    }

    /** Rejects invalid restore inputs without changing the task list. */
    @Test
    void restore_invalidPositionOrTask_throwsArrodesException() throws ArrodesException {
        taskList.insert(new Todo("first"));

        assertThrows(ArrodesException.class, () -> taskList.restore(-1, new Todo("second")));
        assertThrows(ArrodesException.class, () -> taskList.restore(0, null));
        assertEquals(1, taskList.getSize());
    }
}
