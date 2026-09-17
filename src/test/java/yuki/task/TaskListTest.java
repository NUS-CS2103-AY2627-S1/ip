package yuki.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import yuki.exception.YukiException;
import yuki.time.TaskDateTime;

/** Tests the ordered task collection and its user-facing numbering rules. */
class TaskListTest {
    @Test
    void constructor_nullListOrTask_exceptionThrown() {
        assertAll(() -> assertThrows(NullPointerException.class, () ->
                new TaskList(null)), () ->
                assertThrows(NullPointerException.class, () ->
                        new TaskList(Arrays.asList(new ToDo("valid"), null))));
    }

    @Test
    void addAndGetTask_validTaskNumber_taskStoredAtOneBasedNumber() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("read book");

        tasks.addTask(task);

        assertAll(() -> assertEquals(1, tasks.size()), () ->
                assertSame(task, tasks.getTask(1)));
    }

    @Test
    void addTask_sameDetailsIgnoringCase_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));

        YukiException exception = assertThrows(YukiException.class, () ->
                tasks.addTask(new ToDo("READ BOOK")));

        assertTrue(exception.getMessage().contains("already"));
    }

    @Test
    void markAndUnmarkTask_existingTask_statusUpdated() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));

        Task markedTask = tasks.markTask(1);
        assertAll(() -> assertSame(task, markedTask), () ->
                assertTrue(task.isDone()));

        Task unmarkedTask = tasks.unmarkTask(1);
        assertAll(() -> assertSame(task, unmarkedTask), () ->
                assertFalse(task.isDone()));
    }

    @Test
    void markAndUnmarkTask_redundantStatusChange_exceptionThrown() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(YukiException.class, () -> tasks.unmarkTask(1));
        tasks.markTask(1);

        assertThrows(YukiException.class, () -> tasks.markTask(1));
    }

    @Test
    void deleteTask_existingTask_taskRemovedAndReturned() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        Task deleted = tasks.deleteTask(1);

        assertAll(() -> assertSame(first, deleted), () ->
                assertEquals(1, tasks.size()), () ->
                assertSame(second, tasks.getTask(1)));
    }

    @Test
    void getTask_taskNumberOutsideList_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new ToDo("only task")));

        assertAll(() -> assertThrows(YukiException.class, () -> tasks.getTask(0)), () ->
                assertThrows(YukiException.class, () -> tasks.getTask(2)));
    }

    @Test
    void findTasks_keywordWithDifferentCase_matchingDescriptionsReturnedInOrder() {
        Task first = new ToDo("read book");
        Task second = new ToDo("buy groceries");
        Task third = new ToDo("return Book");
        TaskList tasks = new TaskList(List.of(first, second, third));

        List<Task> matches = tasks.findTasks("BOOK");

        assertAll(() -> assertEquals(2, matches.size()), () ->
                assertSame(first, matches.get(0)), () ->
                assertSame(third, matches.get(1)));
    }

    @Test
    void findTasks_noMatchingDescription_emptyListReturned() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));

        List<Task> matches = tasks.findTasks("movie");

        assertTrue(matches.isEmpty());
    }

    @Test
    void findTasks_blankKeyword_allTasksReturnedInOrder() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        assertEquals(List.of(first, second), tasks.findTasks(""));
    }

    @Test
    void getTasks_listLaterChanged_snapshotRemainsUnmodifiableAndUnchanged() {
        TaskList tasks = new TaskList(List.of(new ToDo("first")));
        List<Task> snapshot = tasks.getTasks();

        tasks.addTask(new ToDo("second"));

        assertAll(() -> assertEquals(1, snapshot.size()), () ->
                assertThrows(UnsupportedOperationException.class, () ->
                        snapshot.add(new ToDo("third"))));
    }

    @Test
    void findUpcomingTaskNumbers_mixedTasks_matchingNumbersReturnedInTimeOrder() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 9, 9, 0);
        Task completedDeadline = new Deadline("completed",
                TaskDateTime.of(now.plusHours(1)));
        completedDeadline.markAsDone();
        TaskList tasks = new TaskList(List.of(
                new ToDo("todo"),
                new Deadline("later deadline", TaskDateTime.of(now.plusHours(20))),
                new Event("earlier event", TaskDateTime.of(now.plusHours(2)),
                        TaskDateTime.of(now.plusHours(3))),
                new Deadline("outside window", TaskDateTime.of(now.plusHours(25))),
                new Deadline("free text", TaskDateTime.of("tomorrow")),
                completedDeadline));

        List<Integer> taskNumbers = tasks.findUpcomingTaskNumbers(now, now.plusHours(24));

        assertEquals(List.of(3, 2), taskNumbers);
    }

    @Test
    void findUpcomingTaskNumbers_boundaryTimes_boundariesIncluded() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 9, 9, 0);
        TaskList tasks = new TaskList(List.of(
                new Deadline("due now", TaskDateTime.of(now)),
                new Event("upper boundary", TaskDateTime.of(now.plusHours(24)),
                        TaskDateTime.of(now.plusHours(25)))));

        assertEquals(List.of(1, 2),
                tasks.findUpcomingTaskNumbers(now, now.plusHours(24)));
    }

    @Test
    void findUpcomingTaskNumbers_sameTime_taskListOrderUsedAsTieBreaker() {
        LocalDateTime dueTime = LocalDateTime.of(2026, 9, 9, 10, 0);
        TaskList tasks = new TaskList(List.of(
                new Deadline("first", TaskDateTime.of(dueTime)),
                new Deadline("second", TaskDateTime.of(dueTime))));

        assertEquals(List.of(1, 2),
                tasks.findUpcomingTaskNumbers(dueTime.minusHours(1), dueTime.plusHours(1)));
    }

    @Test
    void findUpcomingTaskNumbers_dateOnlyTask_dateTreatedAs2359() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 9, 23, 0);
        TaskList tasks = new TaskList(List.of(new Deadline("date only",
                TaskDateTime.of(LocalDate.of(2026, 9, 9)))));

        assertEquals(List.of(1),
                tasks.findUpcomingTaskNumbers(now, now.plusHours(1)));
    }

    @Test
    void findUpcomingTaskNumbers_endBeforeStart_exceptionThrown() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 9, 9, 0);
        TaskList tasks = new TaskList();

        assertThrows(IllegalArgumentException.class, () ->
                tasks.findUpcomingTaskNumbers(now, now.minusMinutes(1)));
    }
}
