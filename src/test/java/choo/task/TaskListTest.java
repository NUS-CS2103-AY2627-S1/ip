package choo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import choo.exception.ChooException;

/**
 * Checks task collection operations and one-based position validation.
 */
public class TaskListTest {
    @Test
    void constructor_nullTask_throwsAssertionError() {
        List<Task> tasksWithNull = new ArrayList<>();
        tasksWithNull.add(new Todo("valid"));
        tasksWithNull.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(
                new Todo("valid"), null));
        assertThrows(AssertionError.class, () -> new TaskList(tasksWithNull));
    }

    @Test
    void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add((Task) null));
        assertThrows(AssertionError.class, () -> tasks.add(1, null));
    }

    @Test
    void varargsConstructor_multipleTasks_preservesOrder() throws ChooException {
        TaskList tasks = new TaskList(
                new Todo("first"), new Todo("second"), new Todo("third"));

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] first", tasks.get(1).toString());
        assertEquals("[T][ ] second", tasks.get(2).toString());
        assertEquals("[T][ ] third", tasks.get(3).toString());
    }

    @Test
    void constructor_sourceListChanges_doesNotChangeTaskList() throws ChooException {
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("first"));
        TaskList tasks = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] first", tasks.get(1).toString());
    }

    @Test
    void add_validPositions_insertsAtOneBasedPosition() throws ChooException {
        TaskList tasks = new TaskList(List.of(new Todo("first")));
        tasks.add(new Todo("third"));
        tasks.add(2, new Todo("second"));

        assertEquals("[T][ ] second", tasks.get(2).toString());
        assertEquals("[T][ ] third", tasks.get(3).toString());
    }

    @Test
    void remove_validPosition_removesAndReturnsTask() throws ChooException {
        TaskList tasks = new TaskList(List.of(
                new Todo("first"), new Todo("second"), new Todo("third")));
        Task removed = tasks.remove(2);

        assertEquals("[T][ ] second", removed.toString());
        assertEquals("[T][ ] third", tasks.get(2).toString());
        assertEquals(2, tasks.asList().size());
    }

    @Test
    void positionOperations_outsideList_throwSpecificError() {
        TaskList tasks = new TaskList(List.of(new Todo("only")));
        assertInvalidPosition(tasks, 0);
        assertInvalidPosition(tasks, 2);
        ChooException addException = assertThrows(ChooException.class, () -> tasks.add(3, new Todo("invalid")));
        assertEquals("Task number 3 is outside the list.", addException.getMessage());
    }

    @Test
    void find_keyword_matchesDescriptionsInOriginalOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", "2019-12-06"),
                new Event("book club", "Monday", "Tuesday"),
                new Todo("BOOK notes")));

        List<Task> matchingTasks = tasks.find("book");

        assertEquals(3, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return book", matchingTasks.get(1).getDescription());
        assertEquals("book club", matchingTasks.get(2).getDescription());
    }

    @Test
    void find_caseMismatchOrScheduleOnlyMatch_returnsNoTasks() {
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", "2019-12-06"),
                new Event("orientation", "book room", "Tuesday"),
                new Todo("BOOK notes")));

        assertEquals(List.of(), tasks.find("book"));
        assertEquals(List.of(), tasks.find("2019"));
    }

    @Test
    void sortedByDeadline_mixedTasks_ordersDeadlinesAndPreservesOtherOrder() throws ChooException {
        TaskList tasks = new TaskList(List.of(
                new Todo("first undated"),
                new Deadline("later", "2026-12-31"),
                new Event("second undated", "Monday", "Tuesday"),
                new Deadline("earlier first", "2026-01-15"),
                new Deadline("earlier second", "2026-01-15")));

        TaskList sortedTasks = tasks.sortedByDeadline();

        assertEquals("first undated", tasks.get(1).getDescription());
        assertEquals("earlier first", sortedTasks.get(1).getDescription());
        assertEquals("earlier second", sortedTasks.get(2).getDescription());
        assertEquals("later", sortedTasks.get(3).getDescription());
        assertEquals("first undated", sortedTasks.get(4).getDescription());
        assertEquals("second undated", sortedTasks.get(5).getDescription());
    }

    private static void assertInvalidPosition(TaskList tasks, int taskNumber) {
        ChooException exception = assertThrows(ChooException.class, () -> tasks.get(taskNumber));
        assertEquals("Task number " + taskNumber + " is outside the list.",
                exception.getMessage());
    }
}
