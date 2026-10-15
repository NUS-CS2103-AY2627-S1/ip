package slotbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void constructor_nullTaskList_assertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    public void constructor_taskListContainingNull_assertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(Arrays.asList((Task) null)));
    }

    @Test
    public void add_nullTask_assertionError() {
        TaskList tasks = new TaskList(List.of());

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void get_indexEqualToSize_assertionError() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.get(1));
    }

    @Test
    public void delete_negativeIndex_assertionError() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.delete(-1));
    }

    @Test
    public void addGetDeleteAndSize_validTasks_updatesList() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first));

        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
        assertEquals(first, tasks.delete(0));
        assertEquals(List.of(second), tasks.getTasks());
    }

    @Test
    public void constructorAndGetTasks_externalChanges_doNotChangeTaskList() {
        List<Task> initialTasks = new ArrayList<>(List.of(new Todo("first")));
        TaskList tasks = new TaskList(initialTasks);
        List<Task> snapshot = tasks.getTasks();

        initialTasks.add(new Todo("external"));
        tasks.add(new Todo("later"));

        assertEquals(2, tasks.size());
        assertEquals(1, snapshot.size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("blocked")));
    }

    @Test
    public void invalidIndexes_assertionError() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.get(-1));
        assertThrows(AssertionError.class, () -> tasks.delete(1));
    }

    @Test
    public void findMatchingTaskIndexes_matchingDescriptions_returnsOriginalIndexes() {
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Todo("join club");
        Task secondMatch = new Deadline("return book", LocalDate.of(2026, 9, 1));
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Integer> matchingIndexes = tasks.findMatchingTaskIndexes("book");

        assertEquals(List.of(0, 2), matchingIndexes);
    }

    @Test
    public void findMatchingTaskIndexes_zeroMatches_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read notes")));

        assertEquals(List.of(), tasks.findMatchingTaskIndexes("book"));
    }

    @Test
    public void findMatchingTaskIndexes_caseMismatch_doesNotMatch() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.findMatchingTaskIndexes("Book"));
    }

    @Test
    public void findMatchingTaskIndexes_keywordOnlyInRenderedTask_doesNotMatch() {
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", LocalDate.of(2026, 9, 1))));

        assertEquals(List.of(), tasks.findMatchingTaskIndexes("2026"));
    }

    @Test
    public void findMatchingTaskIndexes_nullKeyword_assertionError() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.findMatchingTaskIndexes(null));
    }

    @Test
    public void findMatchingTaskIndexes_resultIsUnmodifiable() {
        List<Integer> matchingIndexes = new TaskList(List.of(new Todo("read book")))
                .findMatchingTaskIndexes("book");

        assertThrows(UnsupportedOperationException.class, () -> matchingIndexes.add(1));
    }

    @Test
    public void findUpcomingDeadlines_withinWindow_returnsSortedUnfinishedDeadlines() {
        Deadline laterDeadline = new Deadline("later", LocalDate.of(2026, 9, 15));
        Deadline earlierDeadline = new Deadline("earlier", LocalDate.of(2026, 9, 12));
        Deadline completedDeadline = new Deadline("completed", LocalDate.of(2026, 9, 13));
        completedDeadline.markDone();
        TaskList tasks = new TaskList(List.of(
                laterDeadline,
                new Todo("not a deadline"),
                earlierDeadline,
                completedDeadline));

        List<Deadline> reminders = tasks.findUpcomingDeadlines(LocalDate.of(2026, 9, 11), 4);

        assertEquals(List.of(earlierDeadline, laterDeadline), reminders);
    }

    @Test
    public void findUpcomingDeadlines_excludesOutsideWindow() {
        Deadline overdue = new Deadline("overdue", LocalDate.of(2026, 9, 10));
        Deadline atBoundary = new Deadline("boundary", LocalDate.of(2026, 9, 18));
        Deadline beyondBoundary = new Deadline("later", LocalDate.of(2026, 9, 19));
        TaskList tasks = new TaskList(List.of(overdue, atBoundary, beyondBoundary));

        List<Deadline> reminders = tasks.findUpcomingDeadlines(LocalDate.of(2026, 9, 11), 7);

        assertEquals(List.of(atBoundary), reminders);
    }

    @Test
    public void findUpcomingDeadlines_nullStartDate_assertionError() {
        TaskList tasks = new TaskList(List.of());

        assertThrows(AssertionError.class, () -> tasks.findUpcomingDeadlines(null, 7));
    }

    @Test
    public void findUpcomingDeadlines_negativeWindow_assertionError() {
        TaskList tasks = new TaskList(List.of());

        assertThrows(AssertionError.class, () -> tasks.findUpcomingDeadlines(
                LocalDate.of(2026, 9, 18), -1));
    }

    @Test
    public void findUpcomingDeadlines_resultIsUnmodifiable() {
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 9, 18));
        TaskList tasks = new TaskList(List.of(deadline));

        List<Deadline> reminders = tasks.findUpcomingDeadlines(LocalDate.of(2026, 9, 18), 0);

        assertTrue(reminders.contains(deadline));
        assertThrows(UnsupportedOperationException.class, () -> reminders.add(
                new Deadline("blocked", LocalDate.of(2026, 9, 18))));
    }
}
