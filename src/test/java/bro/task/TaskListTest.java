package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import bro.exception.BroException;

/**
 * Unit tests for {@link TaskList}.
 */
public class TaskListTest {

    // -------------------------------------------------------------------------
    // Constructor tests
    // -------------------------------------------------------------------------

    @Test
    public void constructor_noArguments_emptyListCreated() {
        TaskList taskList = new TaskList();
        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
        assertNotNull(taskList.getAllTasks());
        assertEquals(0, taskList.getAllTasks().size());
    }

    @Test
    public void constructor_existingTasks_taskListCreated() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("read book"));
        initialTasks.add(new Todo("write tests"));

        TaskList taskList = new TaskList(initialTasks);
        assertFalse(taskList.isEmpty());
        assertEquals(2, taskList.size());
        assertEquals("read book", taskList.get(0).getTask());
        assertEquals("write tests", taskList.get(1).getTask());
    }

    @Test
    public void constructor_nullTasks_emptyListCreated() {
        TaskList taskList = new TaskList(null);
        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
        assertNotNull(taskList.getAllTasks());
    }

    // -------------------------------------------------------------------------
    // add, get, size, isEmpty tests
    // -------------------------------------------------------------------------

    @Test
    public void addAndGet_validTasks_tasksAddedAndRetrieved() {
        TaskList taskList = new TaskList();
        Todo todo1 = new Todo("buy groceries");
        Todo todo2 = new Todo("clean room");

        taskList.add(todo1);
        assertEquals(1, taskList.size());
        assertFalse(taskList.isEmpty());
        assertEquals(todo1, taskList.get(0));

        taskList.add(todo2);
        assertEquals(2, taskList.size());
        assertEquals(todo2, taskList.get(1));
    }

    @Test
    public void get_outOfBoundsIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));

        taskList.add(new Todo("task 1"));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(1));
    }

    // -------------------------------------------------------------------------
    // delete tests
    // -------------------------------------------------------------------------

    @Test
    public void delete_validIndex_taskDeletedAndReturned() {
        TaskList taskList = new TaskList();
        Todo todo1 = new Todo("task 1");
        Todo todo2 = new Todo("task 2");
        Todo todo3 = new Todo("task 3");

        taskList.add(todo1);
        taskList.add(todo2);
        taskList.add(todo3);

        Task removed = taskList.delete(1);
        assertEquals(todo2, removed);
        assertEquals(2, taskList.size());
        assertEquals(todo1, taskList.get(0));
        assertEquals(todo3, taskList.get(1));
    }

    @Test
    public void delete_outOfBoundsIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(0));

        taskList.add(new Todo("task 1"));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(1));
    }

    // -------------------------------------------------------------------------
    // findTasksOnDate tests
    // -------------------------------------------------------------------------

    @Test
    public void findTasksOnDate_mixedTasks_matchingTasksReturned() throws BroException {
        TaskList taskList = new TaskList();
        LocalDate targetDate = LocalDate.of(2026, 8, 28);

        // 1. Uncompleted Todo (should be included)
        Todo uncompletedTodo = new Todo("uncompleted todo");
        taskList.add(uncompletedTodo);

        // 2. Completed Todo (should be excluded)
        Todo completedTodo = new Todo("completed todo");
        completedTodo.markDone();
        taskList.add(completedTodo);

        // 3. Deadline on target date (should be included)
        Deadline matchingDeadline = new Deadline("submit assignment", "28/8/2026 2359");
        taskList.add(matchingDeadline);

        // 4. Deadline on different date (should be excluded)
        Deadline nonMatchingDeadline = new Deadline("pay bills", "29/8/2026 2359");
        taskList.add(nonMatchingDeadline);

        // 5. Event spanning across target date (should be included)
        Event matchingEvent = new Event("hackathon", "27/8/2026 0900", "29/8/2026 1800");
        taskList.add(matchingEvent);

        // 6. Event outside target date (should be excluded)
        Event pastEvent = new Event("workshop", "20/8/2026 1000", "21/8/2026 1200");
        taskList.add(pastEvent);

        ArrayList<Task> results = taskList.findTasksOnDate(targetDate);
        assertEquals(3, results.size());
        assertTrue(results.contains(uncompletedTodo));
        assertTrue(results.contains(matchingDeadline));
        assertTrue(results.contains(matchingEvent));
        assertFalse(results.contains(completedTodo));
        assertFalse(results.contains(nonMatchingDeadline));
        assertFalse(results.contains(pastEvent));
    }

    @Test
    public void findTasksOnDate_emptyList_emptyListReturned() {
        TaskList taskList = new TaskList();
        LocalDate targetDate = LocalDate.of(2026, 8, 28);
        ArrayList<Task> results = taskList.findTasksOnDate(targetDate);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    // -------------------------------------------------------------------------
    // findTasksByKeywords tests
    // -------------------------------------------------------------------------

    @Test
    public void findTasksByKeywords_matchingKeyword_matchingTasksReturned() throws BroException {
        TaskList taskList = new TaskList();
        Todo todo1 = new Todo("read book");
        Deadline deadline = new Deadline("return book to library", "28/8/2026 2359");
        Event event = new Event("book club meeting", "28/8/2026 1400", "28/8/2026 1600");
        Todo todo2 = new Todo("buy groceries");

        taskList.add(todo1);
        taskList.add(deadline);
        taskList.add(event);
        taskList.add(todo2);

        ArrayList<Task> matchingTasks = taskList.findTasksByKeywords("book");
        assertEquals(3, matchingTasks.size());
        assertTrue(matchingTasks.contains(todo1));
        assertTrue(matchingTasks.contains(deadline));
        assertTrue(matchingTasks.contains(event));
        assertFalse(matchingTasks.contains(todo2));
    }

    @Test
    public void findTasksByKeywords_mixedCaseQuery_matchingTasksReturned() throws BroException {
        TaskList taskList = new TaskList();
        Todo todo1 = new Todo("Read Book");
        Deadline deadline = new Deadline("RETURN BOOK TO LIBRARY", "28/8/2026 2359");
        Event event = new Event("book club meeting", "28/8/2026 1400", "28/8/2026 1600");
        Todo todo2 = new Todo("buy groceries");

        taskList.add(todo1);
        taskList.add(deadline);
        taskList.add(event);
        taskList.add(todo2);

        // Search with uppercase query
        ArrayList<Task> uppercaseResults = taskList.findTasksByKeywords("BOOK");
        assertEquals(3, uppercaseResults.size());
        assertTrue(uppercaseResults.contains(todo1));
        assertTrue(uppercaseResults.contains(deadline));
        assertTrue(uppercaseResults.contains(event));

        // Search with mixed-case query
        ArrayList<Task> mixedCaseResults = taskList.findTasksByKeywords("bOoK");
        assertEquals(3, mixedCaseResults.size());
        assertTrue(mixedCaseResults.contains(todo1));
        assertTrue(mixedCaseResults.contains(deadline));
        assertTrue(mixedCaseResults.contains(event));

        // Search with lowercase query on mixed-case task descriptions
        ArrayList<Task> lowercaseResults = taskList.findTasksByKeywords("book");
        assertEquals(3, lowercaseResults.size());
        assertTrue(lowercaseResults.contains(todo1));
        assertTrue(lowercaseResults.contains(deadline));
        assertTrue(lowercaseResults.contains(event));
    }

    @Test
    public void findTasksByKeywords_noMatch_emptyListReturned() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("buy groceries"));

        ArrayList<Task> matchingTasks = taskList.findTasksByKeywords("gym");
        assertNotNull(matchingTasks);
        assertTrue(matchingTasks.isEmpty());
    }

    @Test
    public void findTasksByKeywords_emptyList_emptyListReturned() {
        TaskList taskList = new TaskList();
        ArrayList<Task> matchingTasks = taskList.findTasksByKeywords("book");

        assertNotNull(matchingTasks);
        assertTrue(matchingTasks.isEmpty());
    }

    @Test
    public void findTasksByKeywords_phraseMatch_matchingTasksReturned() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("clean Living Room");
        taskList.add(todo);
        taskList.add(new Todo("clean kitchen"));

        ArrayList<Task> matchingTasks = taskList.findTasksByKeywords("living ROOM");
        assertEquals(1, matchingTasks.size());
        assertEquals(todo, matchingTasks.get(0));
    }

    @Test
    public void findTasksByKeywords_multipleKeywords_matchingTasksReturned() {
        TaskList taskList = new TaskList();
        Todo todo1 = new Todo("read book");
        Todo todo2 = new Todo("prepare for test");
        Todo todo3 = new Todo("cook dinner");
        taskList.add(todo1);
        taskList.add(todo2);
        taskList.add(todo3);

        ArrayList<Task> matchingTasks = taskList.findTasksByKeywords("book", "test");
        assertEquals(2, matchingTasks.size());
        assertTrue(matchingTasks.contains(todo1));
        assertTrue(matchingTasks.contains(todo2));
        assertFalse(matchingTasks.contains(todo3));
    }

    @Test
    public void findTasksByKeywords_nullOrEmptyVarargs_emptyListReturned() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        ArrayList<Task> nullKeywords = taskList.findTasksByKeywords((String[]) null);
        assertNotNull(nullKeywords);
        assertTrue(nullKeywords.isEmpty());

        ArrayList<Task> emptyVarargs = taskList.findTasksByKeywords();
        assertNotNull(emptyVarargs);
        assertTrue(emptyVarargs.isEmpty());
    }

    // -------------------------------------------------------------------------
    // getAllTasks tests
    // -------------------------------------------------------------------------

    @Test
    public void getAllTasks_existingTasks_underlyingListReturned() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("buy milk");
        taskList.add(todo);

        ArrayList<Task> allTasks = taskList.getAllTasks();
        assertEquals(1, allTasks.size());
        assertEquals(todo, allTasks.get(0));
    }

    // -------------------------------------------------------------------------
    // Duplicate detection tests
    // -------------------------------------------------------------------------

    @Test
    public void hasDuplicateAndGetDuplicate_matchingAndDifferentTasks_behaveCorrectly() throws BroException {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("buy groceries");
        Deadline deadline = new Deadline("submit project", "2026-10-15 1800");
        taskList.add(todo);
        taskList.add(deadline);

        Todo identicalTodo = new Todo("BUY GROCERIES");
        assertTrue(taskList.hasDuplicate(identicalTodo));
        assertEquals(todo, taskList.getDuplicate(identicalTodo));

        Deadline identicalDeadline = new Deadline("submit project", "2026-10-15 1800");
        assertTrue(taskList.hasDuplicate(identicalDeadline));
        assertEquals(deadline, taskList.getDuplicate(identicalDeadline));

        Todo differentTodo = new Todo("read book");
        assertFalse(taskList.hasDuplicate(differentTodo));
        assertNull(taskList.getDuplicate(differentTodo));

        assertFalse(taskList.hasDuplicate(null));
        assertNull(taskList.getDuplicate(null));
    }
}
