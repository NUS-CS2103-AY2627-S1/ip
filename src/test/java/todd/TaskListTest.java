package todd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the state-changing and date-search operations in {@link TaskList}. */
public class TaskListTest {
    @Test
    public void addAndDelete_tasks_updatesListAndReturnsDeletedTask() throws TodException {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write report");

        taskList.add(firstTask);
        taskList.add(secondTask);
        Task deletedTask = taskList.delete(0);

        assertSame(firstTask, deletedTask);
        assertEquals(List.of(secondTask), taskList.asList());
    }

    @Test
    public void delete_invalidIndex_throwsAssertionError() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        AssertionError error = assertThrows(AssertionError.class, () -> taskList.delete(1));

        assertEquals("task index must refer to an existing task", error.getMessage());
    }

    @Test
    public void mark_unmarkedTask_marksAndReturnsTask() throws TodException {
        Todo task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task));

        Task markedTask = taskList.mark(0);

        assertSame(task, markedTask);
        assertTrue(task.isDone());
    }

    @Test
    public void mark_alreadyMarkedTask_throwsTodException() throws TodException {
        Todo task = new Todo("read book");
        TaskList taskList = new TaskList(List.of(task));
        taskList.mark(0);

        assertThrows(TodException.class, () -> taskList.mark(0));
    }

    @Test
    public void unmark_markedTask_unmarksAndReturnsTask() throws TodException {
        Todo task = new Todo("read book");
        task.markAsDone();
        TaskList taskList = new TaskList(List.of(task));

        Task unmarkedTask = taskList.unmark(0);

        assertSame(task, unmarkedTask);
        assertFalse(task.isDone());
    }

    @Test
    public void unmark_alreadyUnmarkedTask_throwsTodException() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertThrows(TodException.class, () -> taskList.unmark(0));
    }

    @Test
    public void findTaskNumbersOn_mixedTasks_returnsOriginalMatchingNumbers() {
        TaskList taskList = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("submit report", LocalDateTime.of(2026, 9, 4, 23, 59)),
                new Deadline("pay bill", LocalDateTime.of(2026, 9, 5, 12, 0)),
                new Event("conference", LocalDateTime.of(2026, 9, 3, 9, 0),
                        LocalDateTime.of(2026, 9, 5, 17, 0))));

        List<Integer> result = taskList.findTaskNumbersOn(LocalDate.of(2026, 9, 4));

        assertEquals(List.of(2, 4), result);
    }

    @Test
    public void findTaskNumbersOn_eventBoundaryDates_includesBothBoundaries() {
        TaskList taskList = new TaskList(List.of(
                new Event("camp", LocalDateTime.of(2026, 9, 4, 9, 0),
                        LocalDateTime.of(2026, 9, 6, 17, 0))));

        assertEquals(List.of(1), taskList.findTaskNumbersOn(LocalDate.of(2026, 9, 4)));
        assertEquals(List.of(1), taskList.findTaskNumbersOn(LocalDate.of(2026, 9, 6)));
        assertEquals(List.of(), taskList.findTaskNumbersOn(LocalDate.of(2026, 9, 7)));
    }

    @Test
    public void findTaskNumbers_mixedDescriptions_returnsOriginalNumbers() {
        Todo firstMatch = new Todo("read book");
        Deadline nonMatch = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 4, 23, 59));
        Event secondMatch = new Event("book club meeting",
                LocalDateTime.of(2026, 9, 5, 9, 0),
                LocalDateTime.of(2026, 9, 5, 10, 0));
        TaskList taskList = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Integer> result = taskList.findTaskNumbers("book");

        assertEquals(List.of(1, 3), result);
    }

    @Test
    public void findTaskNumbers_keywordAbsent_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(
                new Todo("read book"),
                new Todo("write report")));

        assertEquals(List.of(), taskList.findTaskNumbers("exercise"));
    }

    @Test
    public void findUpcomingTaskNumbers_mixedTasks_returnsIncompleteTasksInWindow() {
        LocalDate startDate = LocalDate.of(2026, 9, 11);
        Deadline completedDeadline = new Deadline(
                "already submitted", LocalDateTime.of(2026, 9, 13, 12, 0));
        completedDeadline.markAsDone();
        TaskList taskList = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("due today", LocalDateTime.of(2026, 9, 11, 23, 59)),
                new Deadline("due on last day", LocalDateTime.of(2026, 9, 17, 12, 0)),
                new Deadline("due later", LocalDateTime.of(2026, 9, 18, 12, 0)),
                new Event("ongoing camp", LocalDateTime.of(2026, 9, 10, 9, 0),
                        LocalDateTime.of(2026, 9, 12, 17, 0)),
                completedDeadline));

        List<Integer> result = taskList.findUpcomingTaskNumbers(startDate, 7);

        assertEquals(List.of(2, 3, 5), result);
    }
    @Test
    public void add_duplicateDetails_rejectsEachTypeRegardlessOfCompletion() throws TodException {
        LocalDateTime start = LocalDateTime.of(2026, 9, 18, 14, 0);
        Task[] originals = {new Todo("study"), new Deadline("study", start),
            new Event("study", start, start.plusHours(1))};
        Task[] duplicates = {new Todo("study"), new Deadline("study", start),
            new Event("study", start, start.plusHours(1))};
        String[] types = {"task", "deadline", "event"};
        for (int i = 0; i < originals.length; i++) {
            TaskList list = new TaskList();
            list.add(originals[i]);
            Task duplicate = duplicates[i];
            String expected = "Wait, that " + types[i] + " is already in the list.";
            assertEquals(expected, assertThrows(TodException.class, () -> list.add(duplicate)).getMessage());
            list.mark(0);
            assertEquals(expected, assertThrows(TodException.class, () -> list.add(duplicate)).getMessage());
            assertEquals(1, list.size());
            assertSame(originals[i], list.asList().get(0));
            assertTrue(originals[i].isDone());
        }
    }

    @Test
    public void add_distinctTypesDescriptionsOrTimes_allowsSeparateTasks() throws TodException {
        LocalDateTime start = LocalDateTime.of(2026, 9, 18, 14, 0);
        TaskList list = new TaskList();
        list.add(new Todo("study"));
        list.add(new Todo("Study"));
        list.add(new Todo("study notes"));
        list.add(new Deadline("study", start));
        list.add(new Deadline("study", start.plusHours(1)));
        list.add(new Event("study", start, start.plusHours(2)));
        list.add(new Event("study", start.plusHours(1), start.plusHours(2)));
        list.add(new Event("study", start, start.plusHours(3)));
        assertEquals(8, list.size());
    }

    @Test
    public void add_deletedTask_allowsAddingItAgain() throws TodException {
        TaskList list = new TaskList();
        list.add(new Todo("study"));
        list.delete(0);
        list.add(new Todo("study"));
        assertEquals(1, list.size());
    }
    @Test
    public void findUpcomingTaskNumbers_sevenDateWindow_excludesExactlySevenDaysLater() {
        LocalDate today = LocalDate.of(2026, 9, 18);
        TaskList list = new TaskList(List.of(
                new Deadline("today", today.atStartOfDay()),
                new Deadline("last included minute", today.plusDays(6).atTime(23, 59)),
                new Deadline("seven days later", today.plusDays(7).atStartOfDay()),
                new Event("later event", today.plusDays(7).atTime(10, 0), today.plusDays(7).atTime(11, 0))));
        assertEquals(List.of(1, 2), list.findUpcomingTaskNumbers(today, 7));
        assertEquals(List.of(2, 3, 4), list.findUpcomingTaskNumbers(today.plusDays(1), 7));
    }
}
