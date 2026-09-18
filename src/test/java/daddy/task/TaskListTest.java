package daddy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection operations that change or query Daddy's task list.
 */
class TaskListTest {
    /**
     * Verifies that tasks are retained in their insertion order.
     */
    @Test
    void addTasks_multipleTasks_preservesOrderAndSize() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");

        tasks.add(firstTask, secondTask);

        assertEquals(2, tasks.size());
        assertEquals(List.of(firstTask, secondTask), asList(tasks));
    }

    /** Verifies that duplicate detection uses task type, description, and type-specific date details. */
    @Test
    void containsEquivalentTask_sameAndDifferentDetails_returnsExpectedResult() {
        TaskList tasks = new TaskList();
        LocalDateTime deadline = LocalDateTime.of(2026, 12, 2, 18, 0);
        LocalDateTime eventStart = LocalDateTime.of(2026, 12, 3, 14, 0);
        Task todo = new Todo("Read Book");
        Task deadlineTask = new Deadline("return book", deadline);
        Task event = new Event("meeting", eventStart, eventStart.plusHours(2));
        todo.markAsDone();
        tasks.add(todo, deadlineTask, event);

        assertTrue(tasks.containsEquivalentTask(new Todo("read book")));
        assertTrue(tasks.containsEquivalentTask(new Deadline("RETURN BOOK", deadline)));
        assertTrue(tasks.containsEquivalentTask(new Event("Meeting", eventStart, eventStart.plusHours(2))));
        assertFalse(tasks.containsEquivalentTask(new Deadline("return book", deadline.plusDays(1))));
        assertFalse(tasks.containsEquivalentTask(new Event("meeting", eventStart, eventStart.plusHours(3))));
        assertFalse(tasks.containsEquivalentTask(new Todo("write book")));
    }

    /**
     * Verifies that null values cannot break the task-list invariant.
     */
    @Test
    void addTasks_nullArrayOrTask_assertionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add((Task[]) null));
        assertThrows(AssertionError.class, () -> tasks.add(new Todo("read book"), null));
        assertEquals(0, tasks.size());
    }

    /**
     * Verifies that only valid zero-based task indexes are accepted.
     */
    @Test
    void hasTaskAt_negativeValidAndPastEndIndexes_returnsExpectedResult() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertFalse(tasks.hasTaskAt(-1));
        assertTrue(tasks.hasTaskAt(0));
        assertFalse(tasks.hasTaskAt(1));
    }

    /** Verifies that insertion and direct access preserve the requested list position. */
    @Test
    void insertAndGetTaskAt_validPosition_insertsAndReturnsTask() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task insertedTask = new Todo("buy bread");
        tasks.add(firstTask);

        tasks.insert(0, insertedTask);

        assertSame(insertedTask, tasks.getTaskAt(0));
        assertSame(firstTask, tasks.getTaskAt(1));
    }

    /** Verifies that insertion and direct access enforce their index and value contracts. */
    @Test
    void insertAndGetTaskAt_invalidArguments_assertionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(AssertionError.class, () -> tasks.insert(-1, new Todo("buy bread")));
        assertThrows(AssertionError.class, () -> tasks.insert(2, new Todo("buy bread")));
        assertThrows(AssertionError.class, () -> tasks.insert(0, null));
        assertThrows(AssertionError.class, () -> tasks.getTaskAt(-1));
        assertThrows(AssertionError.class, () -> tasks.getTaskAt(1));
        assertThrows(AssertionError.class, () -> tasks.containsEquivalentTask(null));
    }

    /**
     * Verifies that marking and unmarking change the selected task's status.
     */
    @Test
    void markAndUnmarkTask_existingTask_updatesAndReturnsSameTask() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.add(task);

        assertSame(task, tasks.markTaskAsDone(0));
        assertEquals("X", task.getStatusIcon());
        assertSame(task, tasks.markTaskAsNotDone(0));
        assertEquals(" ", task.getStatusIcon());
    }

    /**
     * Verifies that task-changing operations reject indexes outside the list.
     */
    @Test
    void taskChangingOperations_invalidIndexes_assertionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(AssertionError.class, () -> tasks.markTaskAsDone(-1));
        assertThrows(AssertionError.class, () -> tasks.markTaskAsNotDone(1));
        assertThrows(AssertionError.class, () -> tasks.removeTaskAt(1));
    }

    /**
     * Verifies that deletion returns the removed task and closes the remaining index gap.
     */
    @Test
    void removeTaskAt_middleTask_returnsTaskAndRenumbersRemainingTasks() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task removedTask = new Todo("return book");
        Task lastTask = new Todo("buy bread");
        tasks.add(firstTask);
        tasks.add(removedTask);
        tasks.add(lastTask);

        assertSame(removedTask, tasks.removeTaskAt(1));
        assertEquals(2, tasks.size());
        assertEquals(List.of(firstTask, lastTask), asList(tasks));
    }

    /**
     * Verifies that date filtering includes matching deadlines and either boundary of matching events.
     */
    @Test
    void getTasksOccurringOn_matchingDate_returnsDatedTasksAndImmutableResult() {
        TaskList tasks = new TaskList();
        LocalDate selectedDate = LocalDate.of(2026, 12, 2);
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", selectedDate.atTime(18, 0));
        Task eventEndingOnDate = new Event("trip",
                LocalDateTime.of(2026, 12, 1, 9, 0), selectedDate.atTime(17, 0));
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(eventEndingOnDate);

        List<Task> matchingTasks = tasks.getTasksOccurringOn(selectedDate);

        assertEquals(List.of(deadline, eventEndingOnDate), matchingTasks);
        assertThrows(UnsupportedOperationException.class, () -> matchingTasks.add(todo));
    }

    /**
     * Verifies that description matching ignores case, preserves task order, and returns an immutable result.
     */
    @Test
    void getTasksMatching_matchingDescriptions_returnsTasksInOrderAndImmutableResult() {
        TaskList tasks = new TaskList();
        Task matchingTodo = new Todo("read Book");
        Task nonMatchingTask = new Todo("buy bread");
        Task matchingDeadline = new Deadline("return book", LocalDateTime.of(2026, 12, 2, 18, 0));
        tasks.add(matchingTodo);
        tasks.add(nonMatchingTask);
        tasks.add(matchingDeadline);

        List<Task> matchingTasks = tasks.getTasksMatching("BOOK");

        assertEquals(List.of(matchingTodo, matchingDeadline), matchingTasks);
        assertThrows(UnsupportedOperationException.class, () -> matchingTasks.add(nonMatchingTask));
    }

    /**
     * Verifies that query operations reject missing search criteria.
     */
    @Test
    void queryTasks_nullCriteria_assertionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.getTasksOccurringOn(null));
        assertThrows(AssertionError.class, () -> tasks.getTasksMatching(null));
    }

    /** Verifies that an empty weekday schedule begins at the later of now and the working-day start. */
    @Test
    void findEarliestFreeTime_emptyWeekdaySchedule_returnsEarliestWorkingTime() {
        TaskList tasks = new TaskList();
        LocalDate monday = LocalDate.of(2026, 9, 14);

        assertEquals(monday.atTime(9, 0),
                tasks.findEarliestFreeTime(Duration.ofHours(4), monday.atTime(8, 30)));
        assertEquals(monday.atTime(10, 15),
                tasks.findEarliestFreeTime(Duration.ofMinutes(90), monday.atTime(10, 15)));
    }

    /** Verifies that todos and deadlines do not occupy free-time intervals. */
    @Test
    void findEarliestFreeTime_onlyTodosAndDeadlines_returnsUnblockedTime() {
        TaskList tasks = new TaskList();
        LocalDateTime searchStart = LocalDateTime.of(2026, 9, 14, 10, 0);
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", searchStart.plusHours(1)));

        assertEquals(searchStart, tasks.findEarliestFreeTime(Duration.ofHours(4), searchStart));
    }

    /** Verifies that overlapping, adjacent, and completed events form one continuous busy period. */
    @Test
    void findEarliestFreeTime_overlappingAdjacentAndDoneEvents_returnsTimeAfterBusyPeriod() {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2026, 9, 14);
        Event firstEvent = new Event("briefing", date.atTime(9, 0), date.atTime(10, 30));
        Event overlappingEvent = new Event("workshop", date.atTime(10, 0), date.atTime(12, 0));
        Event adjacentEvent = new Event("lunch", date.atTime(12, 0), date.atTime(13, 0));
        overlappingEvent.markAsDone();
        tasks.add(firstEvent, overlappingEvent, adjacentEvent);

        LocalDateTime slotStart = tasks.findEarliestFreeTime(Duration.ofHours(2), date.atTime(9, 0));

        assertEquals(date.atTime(13, 0), slotStart);
    }

    /** Verifies that a slot may end when an event begins and begin when an event ends. */
    @Test
    void findEarliestFreeTime_exactEventBoundaries_returnsBoundarySlot() {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2026, 9, 14);
        tasks.add(new Event("meeting", date.atTime(13, 0), date.atTime(14, 0)));

        assertEquals(date.atTime(9, 0),
                tasks.findEarliestFreeTime(Duration.ofHours(4), date.atTime(9, 0)));
        assertEquals(date.atTime(14, 0),
                tasks.findEarliestFreeTime(Duration.ofHours(4), date.atTime(13, 0)));
    }

    /** Verifies that weekend searches and multi-day events advance to the first usable weekday time. */
    @Test
    void findEarliestFreeTime_weekendAndMultiDayEvent_returnsMondayAfterEvent() {
        TaskList tasks = new TaskList();
        LocalDate friday = LocalDate.of(2026, 9, 11);
        LocalDate monday = LocalDate.of(2026, 9, 14);
        tasks.add(new Event("conference", friday.atTime(17, 0), monday.atTime(12, 0)));
        tasks.add(new Event("after hours", monday.atTime(18, 0), monday.atTime(20, 0)));

        LocalDateTime slotStart = tasks.findEarliestFreeTime(
                Duration.ofHours(4), LocalDateTime.of(2026, 9, 12, 10, 0));

        assertEquals(monday.atTime(12, 0), slotStart);
    }

    /** Verifies that a request that no longer fits today continues at the next weekday's opening time. */
    @Test
    void findEarliestFreeTime_tooLateToday_returnsNextWeekdayOpening() {
        TaskList tasks = new TaskList();

        LocalDateTime slotStart = tasks.findEarliestFreeTime(
                Duration.ofHours(3), LocalDateTime.of(2026, 9, 14, 16, 0));

        assertEquals(LocalDateTime.of(2026, 9, 15, 9, 0), slotStart);
    }

    /** Verifies that events ending before the search point are ignored. */
    @Test
    void findEarliestFreeTime_eventBeforeSearchStart_ignoresPastEvent() {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2026, 9, 14);
        tasks.add(new Event("breakfast", date.atTime(9, 0), date.atTime(10, 0)));

        LocalDateTime slotStart = tasks.findEarliestFreeTime(Duration.ofHours(2), date.atTime(11, 0));

        assertEquals(date.atTime(11, 0), slotStart);
    }

    /** Verifies that a late blocking event advances a search to the next working day. */
    @Test
    void findEarliestFreeTime_eventLeavesInsufficientTime_returnsNextWeekday() {
        TaskList tasks = new TaskList();
        LocalDate monday = LocalDate.of(2026, 9, 14);
        tasks.add(new Event("afternoon workshop", monday.atTime(12, 0), monday.atTime(17, 0)));

        LocalDateTime slotStart = tasks.findEarliestFreeTime(Duration.ofHours(4), monday.atTime(10, 0));

        assertEquals(monday.plusDays(1).atTime(9, 0), slotStart);
    }

    /** Verifies that durations must be positive and fit within one working day. */
    @Test
    void findEarliestFreeTime_invalidDuration_assertionThrown() {
        TaskList tasks = new TaskList();
        LocalDateTime searchStart = LocalDateTime.of(2026, 9, 14, 9, 0);
        Duration excessiveDuration = Duration.ofHours(10);

        assertThrows(AssertionError.class, () -> tasks.findEarliestFreeTime(Duration.ZERO, searchStart));
        assertThrows(AssertionError.class, () -> tasks.findEarliestFreeTime(Duration.ofMinutes(-1), searchStart));
        assertThrows(AssertionError.class, () -> tasks.findEarliestFreeTime(excessiveDuration, searchStart));
        assertThrows(AssertionError.class, () -> tasks.findEarliestFreeTime(null, searchStart));
        assertThrows(AssertionError.class, () -> tasks.findEarliestFreeTime(Duration.ofHours(1), null));
    }

    /**
     * Converts a task list into a list to allow order assertions.
     *
     * @param tasks the task list to read
     * @return tasks in their iteration order
     */
    private List<Task> asList(TaskList tasks) {
        List<Task> taskValues = new ArrayList<>();
        for (Task task : tasks) {
            taskValues.add(task);
        }
        return taskValues;
    }
}
