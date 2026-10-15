package dave.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dave.exception.DaveCommandException;

/**
 * Unit tests for the {@link TaskList} class.
 */
public class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        this.taskList = new TaskList();
    }

    @Test
    public void add_singleTask_increasesSizeAndRetrievesCorrectly() {
        Task todo = new Todo("borrow book");
        this.taskList.add(todo);

        assertEquals(1, this.taskList.size());
        assertEquals(todo, this.taskList.get(0));
    }

    @Test
    public void hasDuplicate_duplicatePresent_returnsTrue() {
        this.taskList.add(new Todo("read book"));
        assertTrue(this.taskList.hasDuplicate(new Todo("read book")));
        assertTrue(this.taskList.hasDuplicate(new Todo("READ BOOK")));
    }

    @Test
    public void hasDuplicate_duplicateAbsent_returnsFalse() {
        this.taskList.add(new Todo("read book"));
        assertFalse(this.taskList.hasDuplicate(new Todo("write code")));
        assertFalse(this.taskList.hasDuplicate(null));
    }

    @Test
    public void findDuplicate_duplicatePresent_returnsTask() {
        Deadline deadline = new Deadline("submit essay", LocalDateTime.of(2026, 10, 1, 12, 0));
        this.taskList.add(deadline);

        Task found = this.taskList.findDuplicate(
                new Deadline("submit essay", LocalDateTime.of(2026, 10, 1, 12, 0)));
        assertEquals(deadline, found);
    }

    @Test
    public void findDuplicate_nullTask_returnsNull() {
        assertNull(this.taskList.findDuplicate(null));
    }

    @Test
    public void get_invalidIndex_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> this.taskList.get(0));

        this.taskList.add(new Todo("test task"));
        assertThrows(DaveCommandException.class, () -> this.taskList.get(-1));
        assertThrows(DaveCommandException.class, () -> this.taskList.get(1));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        this.taskList.add(first);
        this.taskList.add(second);

        Task removed = this.taskList.delete(0);
        assertEquals(first, removed);
        assertEquals(1, this.taskList.size());
        assertEquals(second, this.taskList.get(0));
    }

    @Test
    public void delete_invalidIndex_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> this.taskList.delete(0));

        this.taskList.add(new Todo("task"));
        assertThrows(DaveCommandException.class, () -> this.taskList.delete(-1));
        assertThrows(DaveCommandException.class, () -> this.taskList.delete(2));
    }

    @Test
    public void setDone_validIndex_updatesTaskStatus() {
        Task task = new Todo("task to complete");
        this.taskList.add(task);

        this.taskList.setDone(0, true);
        assertTrue(this.taskList.get(0).isDone());

        this.taskList.setDone(0, false);
        assertFalse(this.taskList.get(0).isDone());
    }

    @Test
    public void setDone_invalidIndex_throwsDaveCommandException() {
        assertThrows(DaveCommandException.class, () -> this.taskList.setDone(0, true));
    }

    @Test
    public void find_matchingKeyword_returnsMatchingTasks() {
        Task bookTask = new Todo("read book");
        Task otherTask = new Todo("eat lunch");
        this.taskList.add(bookTask);
        this.taskList.add(otherTask);

        ArrayList<IndexedTask> results = this.taskList.find("book");
        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getOriginalIndex());
        assertEquals(bookTask, results.get(0).getTask());
    }

    @Test
    public void find_noMatchingKeyword_returnsEmptyList() {
        this.taskList.add(new Todo("read book"));
        this.taskList.add(new Todo("write code"));

        ArrayList<IndexedTask> results = this.taskList.find("dinner");
        assertTrue(results.isEmpty());
    }

    @Test
    public void find_multipleMatchesAcrossTasks_returnsAllMatchingTasks() {
        Task first = new Todo("eat breakfast");
        Task readBook = new Todo("read book");
        Task cookDinner = new Todo("cook dinner");
        Task returnBook = new Todo("return book");
        this.taskList.add(first);
        this.taskList.add(readBook);
        this.taskList.add(cookDinner);
        this.taskList.add(returnBook);

        ArrayList<IndexedTask> results = this.taskList.find("book");
        assertEquals(2, results.size());
        assertEquals(2, results.get(0).getOriginalIndex());
        assertEquals(readBook, results.get(0).getTask());
        assertEquals(4, results.get(1).getOriginalIndex());
        assertEquals(returnBook, results.get(1).getTask());
    }
}
