package james.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TaskList}.
 */
public class TaskListTest {

    private TaskList taskList;

    /**
     * Sets up a fresh TaskList instance before each test.
     */
    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
    }

    // ==========================================
    // Constructor & Initialization Tests
    // ==========================================

    /**
     * Tests that the default constructor creates an empty task list with size 0.
     */
    @Test
    public void constructor_default_createsEmptyList() {
        TaskList emptyList = new TaskList();
        assertEquals(0, emptyList.getSize());
        assertEquals(new ArrayList<>(), emptyList.getTasks());
    }

    /**
     * Tests that passing null to the constructor initializes an empty task list.
     */
    @Test
    public void constructor_nullList_createsEmptyList() {
        TaskList nullList = new TaskList(null);
        assertEquals(0, nullList.getSize());
        assertEquals(new ArrayList<>(), nullList.getTasks());
    }

    /**
     * Tests that passing an existing list of tasks to the constructor initializes the TaskList with those tasks.
     */
    @Test
    public void constructor_existingList_initializesWithTasks() {
        ArrayList<Task> initialList = new ArrayList<>();
        ToDo task1 = new ToDo("Task 1");
        Deadline task2 = new Deadline("Task 2", LocalDate.parse("2026-10-10"));
        initialList.add(task1);
        initialList.add(task2);

        TaskList initializedList = new TaskList(initialList);
        assertEquals(2, initializedList.getSize());
        assertEquals(task1, initializedList.getTask(0));
        assertEquals(task2, initializedList.getTask(1));
    }

    // ==========================================
    // Add & Get & Size Tests
    // ==========================================

    /**
     * Tests that size returns 0 for an empty task list.
     */
    @Test
    public void getSize_emptyList_returnsZero() {
        assertEquals(0, taskList.getSize());
    }

    /**
     * Tests that addTask correctly adds a task and increments the list size.
     */
    @Test
    public void addTask_validTask_taskAddedAndSizeIncremented() {
        ToDo task = new ToDo("Buy groceries");
        taskList.addTask(task);

        assertEquals(1, taskList.getSize());
        assertEquals(task, taskList.getTask(0));
    }

    /**
     * Tests that getTask returns the task at the specified index.
     */
    @Test
    public void getTask_validIndex_returnsCorrectTask() {
        ToDo task1 = new ToDo("Task 1");
        Deadline task2 = new Deadline("Task 2", LocalDate.parse("2026-12-01"));
        taskList.addTask(task1);
        taskList.addTask(task2);

        assertEquals(task1, taskList.getTask(0));
        assertEquals(task2, taskList.getTask(1));
    }

    /**
     * Tests that getTask with a negative index throws IndexOutOfBoundsException.
     */
    @Test
    public void getTask_negativeIndex_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.getTask(-1));
    }

    /**
     * Tests that getTask with an out-of-bounds index throws IndexOutOfBoundsException.
     */
    @Test
    public void getTask_outOfBoundsIndex_throwsIndexOutOfBoundsException() {
        taskList.addTask(new ToDo("Task 1"));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.getTask(1));
    }

    /**
     * Tests that getTasks returns the underlying list of tasks.
     */
    @Test
    public void getTasks_populatedList_returnsUnderlyingList() {
        ToDo task = new ToDo("Test task");
        taskList.addTask(task);

        ArrayList<Task> underlyingList = taskList.getTasks();
        assertEquals(1, underlyingList.size());
        assertEquals(task, underlyingList.get(0));
    }

    // ==========================================
    // Delete Task Tests
    // ==========================================

    /**
     * Tests that deleteTask by index removes and returns the task at that index.
     */
    @Test
    public void deleteTask_validIndex_removesAndReturnsTask() {
        ToDo task1 = new ToDo("Task 1");
        ToDo task2 = new ToDo("Task 2");
        taskList.addTask(task1);
        taskList.addTask(task2);

        Task removedTask = taskList.deleteTask(0);
        assertEquals(task1, removedTask);
        assertEquals(1, taskList.getSize());
        assertEquals(task2, taskList.getTask(0));
    }

    /**
     * Tests that deleteTask by index throws IndexOutOfBoundsException when index is out of bounds.
     */
    @Test
    public void deleteTask_outOfBoundsIndex_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.deleteTask(1));
    }

    /**
     * Tests that deleteTask by index throws IndexOutOfBoundsException when index is negative.
     */
    @Test
    public void deleteTask_negativeIndex_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.deleteTask(-1));
    }

    /**
     * Tests that deleteTask by Task object removes the task from the list.
     */
    @Test
    public void deleteTask_byTaskObject_removesTaskFromList() {
        ToDo task1 = new ToDo("Task 1");
        ToDo task2 = new ToDo("Task 2");
        taskList.addTask(task1);
        taskList.addTask(task2);

        taskList.deleteTask(task1);
        assertEquals(1, taskList.getSize());
        assertEquals(task2, taskList.getTask(0));
    }

    /**
     * Tests that deleteTask with a task not present in the list leaves the list unchanged.
     */
    @Test
    public void deleteTask_nonExistentTaskObject_listRemainsUnchanged() {
        ToDo task1 = new ToDo("Task 1");
        ToDo nonExistentTask = new ToDo("Non-existent Task");
        taskList.addTask(task1);

        taskList.deleteTask(nonExistentTask);
        assertEquals(1, taskList.getSize());
        assertEquals(task1, taskList.getTask(0));
    }

    // ==========================================
    // isTaskMatchingDate (Static) Tests
    // ==========================================

    /**
     * Tests that isTaskMatchingDate returns true when a Deadline occurs on the specified date.
     */
    @Test
    public void isTaskMatchingDate_deadlineMatchingDate_returnsTrue() {
        LocalDate deadlineDate = LocalDate.parse("2026-05-15");
        Deadline deadline = new Deadline("Submit assignment", deadlineDate);
        assertTrue(TaskList.isTaskMatchingDate(deadline, deadlineDate));
    }

    /**
     * Tests that isTaskMatchingDate returns false when a Deadline falls on a different date.
     */
    @Test
    public void isTaskMatchingDate_deadlineDifferentDate_returnsFalse() {
        LocalDate deadlineDate = LocalDate.parse("2026-05-15");
        LocalDate differentDate = LocalDate.parse("2026-05-16");
        Deadline deadline = new Deadline("Submit assignment", deadlineDate);
        assertFalse(TaskList.isTaskMatchingDate(deadline, differentDate));
    }

    /**
     * Tests that isTaskMatchingDate returns true when queried on the start date of an Event.
     */
    @Test
    public void isTaskMatchingDate_eventOnStartDate_returnsTrue() {
        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate endDate = LocalDate.parse("2026-06-05");
        Event event = new Event("Camp", startDate, endDate);
        assertTrue(TaskList.isTaskMatchingDate(event, startDate));
    }

    /**
     * Tests that isTaskMatchingDate returns true when queried on the end date of an Event.
     */
    @Test
    public void isTaskMatchingDate_eventOnEndDate_returnsTrue() {
        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate endDate = LocalDate.parse("2026-06-05");
        Event event = new Event("Camp", startDate, endDate);
        assertTrue(TaskList.isTaskMatchingDate(event, endDate));
    }

    /**
     * Tests that isTaskMatchingDate returns true when queried on a date between the start and end dates of an Event.
     */
    @Test
    public void isTaskMatchingDate_eventWithinDateRange_returnsTrue() {
        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate intermediateDate = LocalDate.parse("2026-06-03");
        LocalDate endDate = LocalDate.parse("2026-06-05");
        Event event = new Event("Camp", startDate, endDate);
        assertTrue(TaskList.isTaskMatchingDate(event, intermediateDate));
    }

    /**
     * Tests that isTaskMatchingDate returns false when queried on a date before the Event starts.
     */
    @Test
    public void isTaskMatchingDate_eventBeforeStartDate_returnsFalse() {
        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate endDate = LocalDate.parse("2026-06-05");
        LocalDate dateBefore = LocalDate.parse("2026-05-31");
        Event event = new Event("Camp", startDate, endDate);
        assertFalse(TaskList.isTaskMatchingDate(event, dateBefore));
    }

    /**
     * Tests that isTaskMatchingDate returns false when queried on a date after the Event ends.
     */
    @Test
    public void isTaskMatchingDate_eventAfterEndDate_returnsFalse() {
        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate endDate = LocalDate.parse("2026-06-05");
        LocalDate dateAfter = LocalDate.parse("2026-06-06");
        Event event = new Event("Camp", startDate, endDate);
        assertFalse(TaskList.isTaskMatchingDate(event, dateAfter));
    }

    /**
     * Tests that isTaskMatchingDate returns false for ToDo tasks since they do not have dates.
     */
    @Test
    public void isTaskMatchingDate_toDoTask_returnsFalse() {
        ToDo todo = new ToDo("Read book");
        LocalDate date = LocalDate.parse("2026-06-01");
        assertFalse(TaskList.isTaskMatchingDate(todo, date));
    }

    /**
     * Tests that isTaskMatchingDate returns false for generic Task instances without dates.
     */
    @Test
    public void isTaskMatchingDate_genericTask_returnsFalse() {
        Task genericTask = new Task("Generic task");
        LocalDate date = LocalDate.parse("2026-06-01");
        assertFalse(TaskList.isTaskMatchingDate(genericTask, date));
    }

    // ==========================================
    // getTasksOnDate Tests
    // ==========================================

    /**
     * Tests that getTasksOnDate returns an empty list when the task list contains only ToDo tasks.
     */
    @Test
    public void getTasksOnDate_toDoTask_returnsEmptyList() {
        taskList.addTask(new ToDo("Read book"));
        LocalDate date = LocalDate.parse("2026-11-11");
        ArrayList<Task> results = taskList.getTasksOnDate(date);
        assertEquals(0, results.size());
        assertEquals(new ArrayList<>(), results);
    }

    /**
     * Tests that getTasksOnDate returns the Event when queried date falls within its date range.
     */
    @Test
    public void getTasksOnDate_dateWithinEventRange_returnsEvent() {
        LocalDate targetDate = LocalDate.parse("2026-11-12");
        Event matchingEvent = new Event("Conference",
                LocalDate.parse("2026-11-11"),
                LocalDate.parse("2026-11-15"));
        taskList.addTask(matchingEvent);
        ArrayList<Task> results = taskList.getTasksOnDate(targetDate);
        assertEquals(1, results.size());
        assertEquals(matchingEvent, results.get(0));
    }

    /**
     * Tests that getTasksOnDate returns an empty list when the queried date is outside an Event's range.
     */
    @Test
    public void getTasksOnDate_dateOutsideEventRange_returnsEmptyList() {
        LocalDate targetDate = LocalDate.parse("2026-11-12");
        Event futureEvent = new Event("Workshop",
                LocalDate.parse("2026-11-13"),
                LocalDate.parse("2026-11-15"));
        taskList.addTask(futureEvent);
        ArrayList<Task> results = taskList.getTasksOnDate(targetDate);
        assertEquals(0, results.size());
    }

    /**
     * Tests that getTasksOnDate filters mixed task types and returns matching deadlines and events in insertion order.
     */
    @Test
    public void getTasksOnDate_mixedTasksWithMatches_returnsMatchingTasksInOrder() {
        LocalDate targetDate = LocalDate.parse("2026-10-15");

        ToDo todo = new ToDo("Read book");
        Deadline matchingDeadline = new Deadline("Submit report", LocalDate.parse("2026-10-15"));
        Deadline nonMatchingDeadline = new Deadline("Pay bills", LocalDate.parse("2026-10-20"));
        Event matchingEvent = new Event("Career Fair",
                LocalDate.parse("2026-10-14"),
                LocalDate.parse("2026-10-16"));
        Event nonMatchingEvent = new Event("Hackathon",
                LocalDate.parse("2026-10-17"),
                LocalDate.parse("2026-10-18"));

        taskList.addTask(todo);
        taskList.addTask(matchingDeadline);
        taskList.addTask(nonMatchingDeadline);
        taskList.addTask(matchingEvent);
        taskList.addTask(nonMatchingEvent);

        ArrayList<Task> results = taskList.getTasksOnDate(targetDate);
        assertEquals(2, results.size());
        assertEquals(matchingDeadline, results.get(0));
        assertEquals(matchingEvent, results.get(1));
    }

    /**
     * Tests that findTasks returns case-insensitive partial matches in insertion order.
     */
    @Test
    public void findTasks_caseInsensitivePartialMatch_returnsMatchingTasksInOrder() {
        ToDo readBook = new ToDo("read book");
        Deadline returnBook = new Deadline("return book", LocalDate.parse("2026-06-06"));
        ToDo unrelatedTask = new ToDo("buy groceries");
        taskList.addTask(readBook);
        taskList.addTask(returnBook);
        taskList.addTask(unrelatedTask);

        ArrayList<Task> results = taskList.findTasks("BOOK");

        assertEquals(new ArrayList<>(java.util.List.of(readBook, returnBook)), results);
    }

    /**
     * Tests that findTasks returns an empty list when no description matches.
     */
    @Test
    public void findTasks_noMatch_returnsEmptyList() {
        taskList.addTask(new ToDo("buy groceries"));

        assertTrue(taskList.findTasks("book").isEmpty());
    }

    // ==========================================
    // toString Tests
    // ==========================================

    /**
     * Tests that toString returns an empty string for an empty TaskList.
     */
    @Test
    public void toString_emptyList_returnsEmptyString() {
        assertEquals("", taskList.toString());
    }

    /**
     * Tests that toString returns a 1-indexed formatted numbered list of tasks.
     */
    @Test
    public void toString_multipleTasks_returnsNumberedString() {
        taskList.addTask(new ToDo("read book"));
        taskList.addTask(new Deadline("return book", LocalDate.parse("2026-06-06")));
        taskList.addTask(new Event("catch mouse",
                LocalDate.parse("2026-11-11"),
                LocalDate.parse("2026-11-11")));
        String expected = "1.[T][ ] read book\n" +
                "2.[D][ ] return book (by: Jun 06 2026)\n" +
                "3.[E][ ] catch mouse (from: Nov 11 2026 to: Nov 11 2026)\n";
        assertEquals(expected, taskList.toString());
    }
}
