package gunna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gunna.task.Deadline;
import gunna.task.Event;
import gunna.task.Task;
import gunna.task.Todo;

/**
 * JUnit test class for testing the TaskList class.
 * Tests cover adding, deleting, marking, unmarking, and searching tasks.
 */
public class TaskListTest {

    private TaskList taskList;
    private Task todo;
    private Task deadline;
    private Task event;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
        todo = new Todo("Buy milk");
        deadline = new Deadline("Submit report", LocalDate.of(2024, 6, 15));
        event = new Event("Team meeting", "2pm", "4pm");
    }

    @Test
    public void constructor_emptyConstructor_createsEmptyList() {
        TaskList newList = new TaskList();
        assertEquals(0, newList.size(), "New TaskList should be empty");
    }

    @Test
    public void constructor_withArrayList_createsListWithTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(todo);
        tasks.add(deadline);
        TaskList newList = new TaskList(tasks);
        assertEquals(2, newList.size(), "TaskList should have 2 tasks");
    }

    @Test
    public void add_singleTask_increasesSize() {
        assertEquals(0, taskList.size(), "Initial size should be 0");
        taskList.add(todo);
        assertEquals(1, taskList.size(), "Size should be 1 after adding one task");
    }

    @Test
    public void add_multipleTasks_increasesSize() {
        taskList.add(todo);
        taskList.add(deadline);
        taskList.add(event);
        assertEquals(3, taskList.size(), "Size should be 3 after adding three tasks");
    }

    @Test
    public void add_task_canBeRetrieved() {
        taskList.add(todo);
        assertEquals(todo, taskList.get(0), "Added task should be retrievable at index 0");
    }

    @Test
    public void get_validIndex_returnsCorrectTask() {
        taskList.add(todo);
        taskList.add(deadline);
        taskList.add(event);
        assertEquals(todo, taskList.get(0), "get(0) should return first task");
        assertEquals(deadline, taskList.get(1), "get(1) should return second task");
        assertEquals(event, taskList.get(2), "get(2) should return third task");
    }

    @Test
    public void get_invalidIndex_throwsException() {
        taskList.add(todo);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            taskList.get(1);
        }, "Getting task at invalid index should throw IndexOutOfBoundsException");
    }

    @Test
    public void get_negativeIndex_throwsException() {
        taskList.add(todo);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            taskList.get(-1);
        }, "Getting task at negative index should throw IndexOutOfBoundsException");
    }

    @Test
    public void delete_validIndex_removesTask() {
        taskList.add(todo);
        taskList.add(deadline);
        assertEquals(2, taskList.size(), "Size should be 2 before deletion");
        taskList.delete(0);
        assertEquals(1, taskList.size(), "Size should be 1 after deletion");
    }

    @Test
    public void delete_validIndex_returnsDeletedTask() {
        taskList.add(todo);
        taskList.add(deadline);
        Task deleted = taskList.delete(0);
        assertEquals(todo, deleted, "delete should return the deleted task");
    }

    @Test
    public void delete_invalidIndex_throwsException() {
        taskList.add(todo);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            taskList.delete(5);
        }, "Deleting at invalid index should throw IndexOutOfBoundsException");
    }

    @Test
    public void delete_fromEmptyList_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            taskList.delete(0);
        }, "Deleting from empty list should throw IndexOutOfBoundsException");
    }

    @Test
    public void mark_validIndex_marksTaskAsDone() {
        taskList.add(todo);
        assertFalse(taskList.get(0).isDone(), "Task should initially not be done");
        taskList.mark(0);
        assertTrue(taskList.get(0).isDone(), "Task should be marked as done");
    }

    @Test
    public void mark_invalidIndex_throwsException() {
        taskList.add(todo);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            taskList.mark(5);
        }, "Marking at invalid index should throw IndexOutOfBoundsException");
    }

    @Test
    public void unmark_validIndex_unmarksTask() {
        taskList.add(todo);
        taskList.mark(0);
        assertTrue(taskList.get(0).isDone(), "Task should be marked as done");
        taskList.unmark(0);
        assertFalse(taskList.get(0).isDone(), "Task should be unmarked");
    }

    @Test
    public void unmark_invalidIndex_throwsException() {
        taskList.add(todo);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            taskList.unmark(5);
        }, "Unmarking at invalid index should throw IndexOutOfBoundsException");
    }

    @Test
    public void size_emptyList_returnsZero() {
        assertEquals(0, taskList.size(), "Empty list should have size 0");
    }

    @Test
    public void size_afterAddingTasks_returnsCorrectSize() {
        taskList.add(todo);
        assertEquals(1, taskList.size());
        taskList.add(deadline);
        assertEquals(2, taskList.size());
        taskList.add(event);
        assertEquals(3, taskList.size());
    }

    @Test
    public void size_afterDeletingTasks_returnsCorrectSize() {
        taskList.add(todo);
        taskList.add(deadline);
        taskList.add(event);
        assertEquals(3, taskList.size());
        taskList.delete(1);
        assertEquals(2, taskList.size());
        taskList.delete(0);
        assertEquals(1, taskList.size());
    }

    @Test
    public void getTasks_emptyList_returnsEmptyArrayList() {
        ArrayList<Task> tasks = taskList.getTasks();
        assertEquals(0, tasks.size(), "getTasks on empty list should return empty ArrayList");
    }

    @Test
    public void getTasks_withTasks_returnsAllTasks() {
        taskList.add(todo);
        taskList.add(deadline);
        ArrayList<Task> tasks = taskList.getTasks();
        assertEquals(2, tasks.size(), "getTasks should return all tasks");
        assertEquals(todo, tasks.get(0));
        assertEquals(deadline, tasks.get(1));
    }

    @Test
    public void getTasksOnDate_noDeadlines_returnsEmptyList() {
        taskList.add(todo);
        taskList.add(event);
        LocalDate searchDate = LocalDate.of(2024, 6, 15);
        ArrayList<Task> found = taskList.getTasksOnDate(searchDate);
        assertEquals(0, found.size(), "Should return empty list when no deadlines match");
    }

    @Test
    public void getTasksOnDate_matchingDeadline_returnsDeadline() {
        LocalDate searchDate = LocalDate.of(2024, 6, 15);
        Deadline matchingDeadline = new Deadline("Task on date", searchDate);
        taskList.add(todo);
        taskList.add(matchingDeadline);
        taskList.add(event);

        ArrayList<Task> found = taskList.getTasksOnDate(searchDate);
        assertEquals(1, found.size(), "Should find one matching deadline");
        assertEquals(matchingDeadline, found.get(0));
    }

    @Test
    public void getTasksOnDate_multipleMatchingDeadlines_returnsAll() {
        LocalDate searchDate = LocalDate.of(2024, 6, 15);
        Deadline deadline1 = new Deadline("Task 1", searchDate);
        Deadline deadline2 = new Deadline("Task 2", searchDate);
        Deadline deadline3 = new Deadline("Task 3", LocalDate.of(2024, 6, 16));

        taskList.add(deadline1);
        taskList.add(deadline2);
        taskList.add(deadline3);

        ArrayList<Task> found = taskList.getTasksOnDate(searchDate);
        assertEquals(2, found.size(), "Should find two matching deadlines");
        assertTrue(found.contains(deadline1));
        assertTrue(found.contains(deadline2));
        assertFalse(found.contains(deadline3));
    }

    @Test
    public void getTasksOnDate_differentDate_returnsEmptyList() {
        LocalDate taskDate = LocalDate.of(2024, 6, 15);
        LocalDate searchDate = LocalDate.of(2024, 6, 16);
        Deadline deadline = new Deadline("Task", taskDate);
        taskList.add(deadline);

        ArrayList<Task> found = taskList.getTasksOnDate(searchDate);
        assertEquals(0, found.size(), "Should not find deadline with different date");
    }

    @Test
    public void add_afterDelete_maintainsCorrectOrder() {
        taskList.add(todo);
        taskList.add(deadline);
        taskList.add(event);
        taskList.delete(1); // Delete deadline

        Task newTodo = new Todo("New task");
        taskList.add(newTodo);

        assertEquals(3, taskList.size());
        assertEquals(todo, taskList.get(0));
        assertEquals(event, taskList.get(1));
        assertEquals(newTodo, taskList.get(2));
    }

    @Test
    public void mark_multipleTimesOnSameTask_taskStaysDone() {
        taskList.add(todo);
        taskList.mark(0);
        taskList.mark(0);
        taskList.mark(0);
        assertTrue(taskList.get(0).isDone(), "Task should remain done after multiple marks");
    }

    @Test
    public void unmark_multipleTimesOnSameTask_taskStaysNotDone() {
        taskList.add(todo);
        taskList.unmark(0);
        taskList.unmark(0);
        taskList.unmark(0);
        assertFalse(taskList.get(0).isDone(), "Task should remain not done after multiple unmarks");
    }

    @Test
    public void findTasksByKeyword_noMatches_returnsEmptyList() {
        taskList.add(todo);
        taskList.add(deadline);
        ArrayList<Task> found = taskList.findTasksByKeyword("xyz");
        assertEquals(0, found.size(), "Should return empty list when no tasks match");
    }

    @Test
    public void findTasksByKeyword_singleMatch_returnsMatchingTask() {
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("buy groceries"));
        ArrayList<Task> found = taskList.findTasksByKeyword("book");
        assertEquals(1, found.size(), "Should find one matching task");
        assertTrue(found.get(0).getDescription().contains("book"));
    }

    @Test
    public void findTasksByKeyword_multipleMatches_returnsAllMatches() {
        taskList.add(new Todo("read book"));
        taskList.add(new Deadline("return book", LocalDate.of(2024, 6, 15)));
        taskList.add(new Todo("buy groceries"));
        ArrayList<Task> found = taskList.findTasksByKeyword("book");
        assertEquals(2, found.size(), "Should find two matching tasks");
    }

    @Test
    public void findTasksByKeyword_caseInsensitive_findsMatch() {
        taskList.add(new Todo("Read Book"));
        ArrayList<Task> found = taskList.findTasksByKeyword("book");
        assertEquals(1, found.size(), "Should find match regardless of case");
    }

    @Test
    public void findTasksByKeyword_partialMatch_findsMatch() {
        taskList.add(new Todo("reading books at library"));
        ArrayList<Task> found = taskList.findTasksByKeyword("book");
        assertEquals(1, found.size(), "Should find partial match");
    }

    @Test
    public void findTasksByKeyword_emptyList_returnsEmptyList() {
        ArrayList<Task> found = taskList.findTasksByKeyword("book");
        assertEquals(0, found.size(), "Should return empty list when task list is empty");
    }

    @Test
    public void findTasksByKeyword_emptyKeyword_returnsAllTasks() {
        taskList.add(todo);
        taskList.add(deadline);
        ArrayList<Task> found = taskList.findTasksByKeyword("");
        assertEquals(2, found.size(), "Empty keyword should match all tasks");
    }
}
