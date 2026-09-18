package cookie.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests ordered task-list operations and iteration. */
public class TaskListTest {
    @Test
    public void taskList_invalidInternalState_triggersAssertionErrors() {
        Todo task = new Todo("valid task");
        TaskList tasks = new TaskList(task);
        List<Task> tasksWithNull = new ArrayList<>();
        tasksWithNull.add(task);
        tasksWithNull.add(null);

        assertThrows(AssertionError.class, () -> new TaskList((List<Task>) null));
        assertThrows(AssertionError.class, () -> new TaskList(tasksWithNull));
        assertThrows(AssertionError.class, () -> tasks.add(null));
        assertThrows(AssertionError.class, () -> tasks.add(0, null));
        assertThrows(AssertionError.class, () -> new TaskList.IndexedTask(0, task));
        assertThrows(AssertionError.class, () -> new TaskList.IndexedTask(1, null));
    }

    @Test
    public void taskList_addInsertDeleteAndGet_maintainsOrder() {
        Todo first = new Todo("first");
        Todo inserted = new Todo("inserted");
        Todo last = new Todo("last");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(last);
        tasks.add(1, inserted);

        assertEquals(3, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(inserted, tasks.get(1));
        assertEquals(inserted, tasks.delete(1));
        assertEquals(List.of(first, last), toList(tasks));
    }

    @Test
    public void taskList_markAndUnmark_delegatesToSelectedTask() {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"));

        tasks.mark(1);
        assertTrue(tasks.get(1).isDone());
        assertFalse(tasks.get(0).isDone());
        tasks.unmark(1);
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    public void containsSameDetails_matchingAndDifferentTasks_returnsExpectedResult() {
        TaskList tasks = new TaskList(new Todo("buy milk"));

        assertTrue(tasks.containsSameDetails(new Todo("buy milk")));
        assertFalse(tasks.containsSameDetails(new Todo("buy bread")));
        assertThrows(AssertionError.class, () -> tasks.containsSameDetails(null));
    }

    @Test
    public void iterator_returnsTasksInStoredOrder() {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"));

        assertEquals(List.of("first", "second"), toList(tasks).stream()
                .map(Task::getDescription)
                .toList());
    }

    @Test
    public void findAndFindOn_matchingTasks_preserveOriginalTaskNumbers() {
        LocalDate queriedDate = LocalDate.of(2026, 8, 27);
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Todo("buy groceries"),
                new Deadline("return book", new DateTimeValue(queriedDate, null)),
                new Event("conference",
                        new DateTimeValue(queriedDate.minusDays(1), LocalTime.of(9, 0)),
                        new DateTimeValue(queriedDate.plusDays(1), LocalTime.of(17, 0))));

        assertEquals(List.of(1, 3), tasks.find("book").stream()
                .map(TaskList.IndexedTask::taskNumber)
                .toList());
        assertEquals(List.of(3, 4), tasks.findOn(queriedDate).stream()
                .map(TaskList.IndexedTask::taskNumber)
                .toList());
    }

    @Test
    public void getSortedView_descriptionSort_preservesNumbersTiesAndStoredOrder() {
        TaskList tasks = new TaskList(
                new Todo("zebra"),
                new Todo("apple"),
                new Todo("Apple"),
                new Todo("banana"));

        List<TaskList.IndexedTask> ascending = tasks.getSortedView(
                SortCriterion.DESCRIPTION, SortDirection.ASCENDING);
        List<TaskList.IndexedTask> descending = tasks.getSortedView(
                SortCriterion.DESCRIPTION, SortDirection.DESCENDING);

        assertEquals(List.of(2, 3, 4, 1), taskNumbersOf(ascending));
        assertEquals(List.of(1, 4, 2, 3), taskNumbersOf(descending));
        assertEquals(List.of("zebra", "apple", "Apple", "banana"), toList(tasks).stream()
                .map(Task::getDescription)
                .toList());
    }

    @Test
    public void getSortedView_dateSort_ordersValueShapesAndKeepsMissingValuesLast() {
        LocalDate earlierDate = LocalDate.of(2026, 8, 27);
        LocalDate laterDate = LocalDate.of(2026, 8, 28);
        TaskList tasks = new TaskList(
                new Todo("first todo"),
                new Deadline("later deadline", new DateTimeValue(laterDate, LocalTime.of(9, 0))),
                new Event("earlier event",
                        new DateTimeValue(earlierDate, LocalTime.of(9, 0)),
                        new DateTimeValue(earlierDate, LocalTime.of(10, 0))),
                new Deadline("date-only deadline", new DateTimeValue(earlierDate, null)),
                new Deadline("evening deadline", new DateTimeValue(null, LocalTime.of(18, 0))),
                new Event("morning event",
                        new DateTimeValue(null, LocalTime.of(9, 0)),
                        new DateTimeValue(null, LocalTime.of(10, 0))),
                new Todo("second todo"));

        List<TaskList.IndexedTask> ascending = tasks.getSortedView(
                SortCriterion.DATE, SortDirection.ASCENDING);
        List<TaskList.IndexedTask> descending = tasks.getSortedView(
                SortCriterion.DATE, SortDirection.DESCENDING);

        assertEquals(List.of(4, 3, 2, 6, 5, 1, 7), taskNumbersOf(ascending));
        assertEquals(List.of(2, 3, 4, 5, 6, 1, 7), taskNumbersOf(descending));
    }

    @Test
    public void getSortedView_invalidArguments_triggerAssertionErrors() {
        TaskList tasks = new TaskList(new Todo("task"));

        assertThrows(AssertionError.class, () ->
                tasks.getSortedView(null, SortDirection.ASCENDING));
        assertThrows(AssertionError.class, () ->
                tasks.getSortedView(SortCriterion.DESCRIPTION, null));
    }

    private List<Integer> taskNumbersOf(List<TaskList.IndexedTask> indexedTasks) {
        return indexedTasks.stream()
                .map(TaskList.IndexedTask::taskNumber)
                .toList();
    }

    private List<Task> toList(TaskList tasks) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            result.add(task);
        }
        return result;
    }
}
