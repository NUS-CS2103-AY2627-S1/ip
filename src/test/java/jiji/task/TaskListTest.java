package jiji.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
    }

    @Test
    public void add_singleTask_sizeIncrements() {
        assertTrue(taskList.isEmpty());
        taskList.add(new Todo("read book"));
        assertEquals(1, taskList.size());
        assertFalse(taskList.isEmpty());
    }

    @Test
    public void add_nullTask_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> taskList.add(null));
    }

    @Test
    public void delete_validIndex_taskRemovedAndReturned() {
        Task todo = new Todo("read book");
        Task deadline = new Deadline("submit homework", "tonight");
        taskList.add(todo);
        taskList.add(deadline);

        Task removed = taskList.delete(0);
        assertEquals(todo, removed);
        assertEquals(1, taskList.size());
        assertEquals(deadline, taskList.get(0));
    }

    @Test
    public void delete_outOfBoundsIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(0));
        taskList.add(new Todo("read book"));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(5));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(-1));
    }

    @Test
    public void get_validIndex_returnsTask() {
        Task todo = new Todo("borrow novel");
        taskList.add(todo);
        assertEquals(todo, taskList.get(0));
    }

    @Test
    public void get_outOfBoundsIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
    }

    @Test
    public void findTasks_matchingKeyword_returnsMatchingTasks() {
        Task t1 = new Todo("read book");
        Task t2 = new Deadline("return book", "Sunday");
        Task t3 = new Todo("eat lunch");
        taskList.add(t1);
        taskList.add(t2);
        taskList.add(t3);

        assertEquals(2, taskList.findTasks("book").size());
        assertEquals(t1, taskList.findTasks("book").get(0));
        assertEquals(t2, taskList.findTasks("book").get(1));
    }

    @Test
    public void findTasks_caseInsensitive_returnsMatchingTasks() {
        Task t1 = new Todo("Read Book");
        taskList.add(t1);

        assertEquals(1, taskList.findTasks("read").size());
        assertEquals(1, taskList.findTasks("BOOK").size());
    }

    @Test
    public void findTasks_noMatchOrEmptyKeyword_returnsEmptyList() {
        taskList.add(new Todo("read book"));
        assertTrue(taskList.findTasks("swimming").isEmpty());
        assertTrue(taskList.findTasks("").isEmpty());
        assertTrue(taskList.findTasks(null).isEmpty());
    }

    @Test
    public void addAll_multipleTasks_allAdded() {
        Task t1 = new Todo("task 1");
        Task t2 = new Todo("task 2");
        Task t3 = new Todo("task 3");

        taskList.addAll(t1, t2, t3);
        assertEquals(3, taskList.size());
        assertEquals(t1, taskList.get(0));
        assertEquals(t2, taskList.get(1));
        assertEquals(t3, taskList.get(2));

        taskList.addAll();
        assertEquals(3, taskList.size());
    }

    @Test
    public void findDuplicate_matchingTask_returnsTask() {
        Task t1 = new Todo("read book");
        Task d1 = new Deadline("return book", "2026-08-30");
        Task e1 = new Event("camp", "2026-08-29", "2026-08-31");
        taskList.addAll(t1, d1, e1);

        assertEquals(t1, taskList.findDuplicate(new Todo("READ BOOK")));
        assertEquals(d1, taskList.findDuplicate(new Deadline("Return Book", "2026-08-30")));
        assertEquals(e1, taskList.findDuplicate(new Event("CAMP", "2026-08-29", "2026-08-31")));
    }

    @Test
    public void findDuplicate_nonMatchingTaskOrNull_returnsNull() {
        Task t1 = new Todo("read book");
        taskList.add(t1);

        assertNull(taskList.findDuplicate(new Todo("read magazine")));
        assertNull(taskList.findDuplicate(new Deadline("read book", "tomorrow")));
        assertNull(taskList.findDuplicate(null));
    }
}
