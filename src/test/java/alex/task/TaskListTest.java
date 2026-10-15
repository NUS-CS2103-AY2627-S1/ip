package alex.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import alex.util.TaskDateTime;

/**
 * Tests task-list search behavior.
 */
public class TaskListTest {
    @Test
    public void find_partialKeywordInDescriptions_returnsMatchingTasks() {
        Task readBook = new Todo("read book");
        Task returnBook = new Deadline("return book", LocalDate.of(2026, 6, 6));
        Task unrelatedTask = new Todo("buy groceries");
        TaskList tasks = new TaskList(readBook, returnBook, unrelatedTask);

        assertEquals(List.of(readBook, returnBook), tasks.find("boo"));
    }

    @Test
    public void find_keywordHasDifferentCase_returnsMatchingTasks() {
        Task readBook = new Todo("read book");
        TaskList tasks = new TaskList(readBook, new Todo("buy groceries"));

        assertEquals(List.of(readBook), tasks.find("BOOK"));
    }

    @Test
    public void find_keywordAbsent_returnsEmptyList() {
        TaskList tasks = new TaskList(new Todo("read book"));

        assertEquals(List.of(), tasks.find("movie"));
    }

    @Test
    public void findUpcomingDeadlines_deadlinesWithinWindow_returnsMatchingDeadlines() {
        LocalDateTime currentDateTime = LocalDateTime.of(2026, 9, 16, 12, 0);
        Task dueNow = createTimedDeadline("due now", currentDateTime);
        Task dueLater = createTimedDeadline("due later", currentDateTime.plusHours(12));
        Task dateOnlyToday = new Deadline("date only today", currentDateTime.toLocalDate());
        Task dueAtWindowEnd = createTimedDeadline(
                "due at window end", currentDateTime.plusHours(24));
        TaskList tasks = new TaskList(dueNow, dueLater, dateOnlyToday, dueAtWindowEnd);

        assertEquals(
                List.of(dueNow, dueLater, dateOnlyToday, dueAtWindowEnd),
                tasks.findUpcomingDeadlines(currentDateTime));
    }

    @Test
    public void findUpcomingDeadlines_irrelevantTasks_returnsEmptyList() {
        LocalDateTime currentDateTime = LocalDateTime.of(2026, 9, 16, 12, 0);
        Task completedDeadline = createTimedDeadline(
                "completed", currentDateTime.plusHours(1));
        completedDeadline.markAsDone();
        TaskList tasks = new TaskList(
                createTimedDeadline("overdue", currentDateTime.minusMinutes(1)),
                createTimedDeadline("too late", currentDateTime.plusHours(24).plusMinutes(1)),
                new Deadline("date only tomorrow", currentDateTime.toLocalDate().plusDays(1)),
                completedDeadline,
                new Todo("buy groceries"),
                new Event("meeting", currentDateTime.toLocalDate(),
                        currentDateTime.toLocalDate().plusDays(1)));

        assertEquals(List.of(), tasks.findUpcomingDeadlines(currentDateTime));
    }

    @Test
    public void getTasks_returnedList_isUnmodifiableSnapshot() {
        Task firstTask = new Todo("first task");
        Task secondTask = new Todo("second task");
        TaskList tasks = new TaskList(firstTask);

        List<Task> snapshot = tasks.getTasks();
        tasks.add(secondTask);

        assertEquals(List.of(firstTask), snapshot);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(secondTask));
    }

    private static Deadline createTimedDeadline(String description, LocalDateTime dueDateTime) {
        return new Deadline(description, new TaskDateTime(
                dueDateTime.toLocalDate(), dueDateTime.toLocalTime()));
    }
}
