package chris.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import chris.exception.ChrisException;

/**
 * Tests task-list behavior.
 */
public class TaskListTest {
    /**
     * Verifies that a unique task is added successfully.
     *
     * @throws ChrisException If the unique task is unexpectedly rejected.
     */
    @Test
    public void add_uniqueTask_addsTask() throws ChrisException {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    /**
     * Verifies that find returns matching tasks in their original order.
     */
    @Test
    public void find_matchingKeyword_returnsMatchingTasks() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2026, 8, 30)),
                new Todo("exercise")));

        List<Task> matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] read book", matchingTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Aug 30 2026)", matchingTasks.get(1).toString());
    }

    /**
     * Verifies that find returns an empty list when nothing matches.
     */
    @Test
    public void find_missingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("exercise")));

        assertTrue(tasks.find("book").isEmpty());
    }

    /**
     * Verifies that an identical todo cannot be added twice.
     */
    @Test
    public void add_duplicateTodo_throwsChrisException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        ChrisException exception = assertThrows(
                ChrisException.class, () -> tasks.add(new Todo("read book")));

        assertEquals("That task is already in your list.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that capitalization differences do not bypass duplicate detection.
     */
    @Test
    public void add_duplicateTodoWithDifferentCase_throwsChrisException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(ChrisException.class, () -> tasks.add(new Todo("Read Book")));

        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that deadlines are duplicates only when their due dates also match.
     */
    @Test
    public void add_deadlinesWithDifferentDates_addsBoth() throws ChrisException {
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", LocalDate.of(2026, 9, 20))));

        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 21)));

        assertEquals(2, tasks.size());
    }

    /**
     * Verifies that deadlines with matching descriptions and dates are duplicates.
     */
    @Test
    public void add_duplicateDeadline_throwsChrisException() {
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", LocalDate.of(2026, 9, 20))));

        assertThrows(ChrisException.class, () -> tasks.add(
                new Deadline("Submit Report", LocalDate.of(2026, 9, 20))));

        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that events with matching descriptions and timings are duplicates.
     */
    @Test
    public void add_duplicateEvent_throwsChrisException() {
        TaskList tasks = new TaskList(List.of(
                new Event("team meeting", "Monday 2pm", "Monday 4pm")));

        assertThrows(ChrisException.class, () -> tasks.add(
                new Event("Team Meeting", "monday 2PM", "monday 4PM")));

        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that events with different timings remain separate tasks.
     */
    @Test
    public void add_eventsWithDifferentTimings_addsBoth() throws ChrisException {
        TaskList tasks = new TaskList(List.of(
                new Event("team meeting", "Monday 2pm", "Monday 4pm")));

        tasks.add(new Event("team meeting", "Tuesday 2pm", "Tuesday 4pm"));

        assertEquals(2, tasks.size());
    }

    /**
     * Verifies that tasks of different types are not treated as duplicates.
     */
    @Test
    public void add_sameDescriptionWithDifferentType_addsBoth() throws ChrisException {
        TaskList tasks = new TaskList(List.of(new Todo("submit report")));

        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 20)));

        assertEquals(2, tasks.size());
    }

    /**
     * Verifies that marking and unmarking update the selected task.
     */
    @Test
    public void markAndUnmark_validIndex_updatesCompletionStatus() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        Task markedTask = tasks.mark(0);
        assertEquals("[T][X] read book", markedTask.toString());

        Task unmarkedTask = tasks.unmark(0);
        assertEquals("[T][ ] read book", unmarkedTask.toString());
    }

    /**
     * Verifies that deleting a task returns it and closes the list gap.
     */
    @Test
    public void delete_validIndex_removesAndReturnsTask() {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));

        Task deletedTask = tasks.delete(0);

        assertEquals("[T][ ] first", deletedTask.toString());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] second", tasks.get(0).toString());
    }

    /**
     * Verifies that callers cannot modify the task list through its public view.
     */
    @Test
    public void asList_returnedView_cannotBeModified() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.asList().add(new Todo("write notes")));
        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that an invalid internal task index violates the task-list precondition.
     */
    @Test
    public void delete_invalidIndex_throwsAssertionError() {
        TaskList tasks = new TaskList();

        AssertionError exception = assertThrows(AssertionError.class, () -> tasks.delete(0));

        assertEquals("Task index should be within the task list: 0", exception.getMessage());
    }
}
