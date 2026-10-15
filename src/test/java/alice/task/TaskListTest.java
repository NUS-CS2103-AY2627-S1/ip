package alice.task;

// Used Codex to create these tests.
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests date-based task retrieval in {@link TaskList}.
 */
class TaskListTest {
    private static final LocalDate DATE = LocalDate.of(2019, 12, 2);

    /** Verifies that the default constructor creates an empty task list. */
    @Test
    void constructor_emptyList_createsEmptyTaskList() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
        assertEquals(List.of(), tasks.asList());
    }

    /** Verifies that the constructor copies, rather than shares, the supplied task list. */
    @Test
    void constructor_suppliedTasks_copiesSuppliedTasks() {
        ToDo todo = new ToDo("read book");
        List<Task> initialTasks = new ArrayList<>(List.of(todo));

        TaskList tasks = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
    }

    /** Verifies that an added task can be retrieved at its inserted position. */
    @Test
    void addAndGet_newTask_returnsTaskAtAddedPosition() {
        TaskList tasks = new TaskList();
        ToDo todo = new ToDo("read book");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
    }

    /** Verifies that removing a task returns it and closes the resulting list gap. */
    @Test
    void remove_taskAtIndex_returnsAndRemovesTask() {
        ToDo firstTask = new ToDo("read book");
        ToDo secondTask = new ToDo("buy milk");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task removedTask = tasks.remove(0);

        assertEquals(firstTask, removedTask);
        assertEquals(1, tasks.size());
        assertEquals(secondTask, tasks.get(0));
    }

    /** Verifies that a task snapshot cannot be modified or changed by later additions. */
    @Test
    void asList_tasksAdded_returnsImmutableSnapshotOfTasks() {
        ToDo firstTask = new ToDo("read book");
        ToDo secondTask = new ToDo("buy milk");
        TaskList tasks = new TaskList(List.of(firstTask));

        List<Task> savedTasks = tasks.asList();
        tasks.add(secondTask);

        assertEquals(List.of(firstTask), savedTasks);
        assertThrows(UnsupportedOperationException.class, () -> savedTasks.add(secondTask));
    }

    /** Verifies that task lookup returns the task position or {@code -1} when absent. */
    @Test
    void indexOf_presentAndAbsentTasks_returnsIndexOrMinusOne() {
        ToDo firstTask = new ToDo("read book");
        ToDo secondTask = new ToDo("buy milk");
        TaskList tasks = new TaskList(List.of(firstTask));

        assertEquals(0, tasks.indexOf(firstTask));
        assertEquals(-1, tasks.indexOf(secondTask));
    }

    /** Verifies that a deadline is returned when its due date matches the requested date. */
    @Test
    void getTasksOnDate_deadlineOnRequestedDate_returnsDeadline() {
        Deadline deadline = new Deadline("return book", DATE);
        TaskList tasks = new TaskList(List.of(deadline));

        assertEquals(List.of(deadline), tasks.getTasksOnDate(DATE));
    }

    /** Verifies that an event covering the requested date is returned. */
    @Test
    void getTasksOnDate_eventSpanningRequestedDate_returnsEvent() {
        Event event = new Event("overnight trip",
                LocalDateTime.of(2019, 12, 1, 22, 0),
                LocalDateTime.of(2019, 12, 3, 9, 0));
        TaskList tasks = new TaskList(List.of(event));

        assertEquals(List.of(event), tasks.getTasksOnDate(DATE));
    }

    /** Verifies that events beginning or ending on the requested date are returned. */
    @Test
    void getTasksOnDate_eventStartsOrEndsOnRequestedDate_returnsEvent() {
        Event startsOnDate = new Event("morning meeting",
                LocalDateTime.of(2019, 12, 2, 9, 0),
                LocalDateTime.of(2019, 12, 2, 10, 0));
        Event endsOnDate = new Event("late trip",
                LocalDateTime.of(2019, 12, 1, 23, 0),
                LocalDateTime.of(2019, 12, 2, 1, 0));
        TaskList tasks = new TaskList(List.of(startsOnDate, endsOnDate));

        assertEquals(List.of(startsOnDate, endsOnDate), tasks.getTasksOnDate(DATE));
    }

    /** Verifies that undated tasks and tasks on other dates are excluded from the result. */
    @Test
    void getTasksOnDate_todoAndTasksOnOtherDates_returnsEmptyList() {
        ToDo todo = new ToDo("read book");
        Deadline otherDeadline = new Deadline("pay bill", DATE.plusDays(1));
        TaskList tasks = new TaskList(List.of(todo, otherDeadline));

        assertEquals(List.of(), tasks.getTasksOnDate(DATE));
    }

    @Test
    void getTasksWithKeyword_matchingDescriptionsIgnoringCase_returnsMatchingTasksInOrder() {
        ToDo firstMatch = new ToDo("read book");
        Deadline secondMatch = new Deadline("return BOOK", DATE);
        ToDo nonMatchingTask = new ToDo("buy milk");
        TaskList tasks = new TaskList(List.of(firstMatch, secondMatch, nonMatchingTask));

        assertEquals(List.of(firstMatch, secondMatch), tasks.getTasksWithKeyword("book"));
    }

    @Test
    void getTasksWithKeyword_noMatchingDescriptions_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));

        assertEquals(List.of(), tasks.getTasksWithKeyword("milk"));
    }
}
