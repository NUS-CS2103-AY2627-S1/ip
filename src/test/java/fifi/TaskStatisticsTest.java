package fifi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import fifi.task.Deadline;
import fifi.task.Event;
import fifi.task.Task;
import fifi.task.ToDo;

/**
 * Tests task statistics categories and time-period filtering.
 */
public class TaskStatisticsTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 9, 15, 0);

    @Test
    public void getStatistics_allTaskStates_mutuallyExclusiveCountsReturned() {
        TaskStatistics statistics = new TaskList(getTasksCoveringAllStatuses()).getStatistics(NOW);

        assertEquals(10, statistics.getTotalTasks());
        assertEquals(3, statistics.getCompletedTasks());
        assertEquals(7, statistics.getIncompleteTasks());
        assertEquals(30, statistics.getCompletionRate());
        assertEquals(1, statistics.getTodoCompleted());
        assertEquals(1, statistics.getTodoIncomplete());
        assertEquals(1, statistics.getDeadlineCompleted());
        assertEquals(2, statistics.getDeadlinePending());
        assertEquals(1, statistics.getDeadlineOverdue());
        assertEquals(1, statistics.getEventCompleted());
        assertEquals(1, statistics.getEventUpcoming());
        assertEquals(1, statistics.getEventOngoing());
        assertEquals(1, statistics.getEventPast());
    }

    @Test
    public void getStatisticsSince_periodBoundariesAndLegacyTask_onlyEligibleTasksIncluded() {
        LocalDateTime periodStart = LocalDateTime.of(2026, 9, 2, 0, 0);
        List<Task> tasks = List.of(
                new ToDo(false, "at start", periodStart, null),
                new ToDo(true, "at end", NOW, NOW),
                new ToDo(false, "too early", periodStart.minusNanos(1), null),
                new ToDo(false, "future", NOW.plusNanos(1), null),
                new ToDo(false, "legacy", null, null));

        TaskStatistics statistics = new TaskList(tasks).getStatisticsSince(periodStart.toLocalDate(), NOW);

        assertEquals(2, statistics.getTotalTasks());
        assertEquals(1, statistics.getCompletedTasks());
        assertEquals(1, statistics.getIncompleteTasks());
        assertEquals(50, statistics.getCompletionRate());
        assertEquals(1, statistics.getExcludedUnknownCreationTimes());
    }

    @Test
    public void getStatistics_emptyList_zeroCompletionRateReturned() {
        TaskStatistics statistics = new TaskList().getStatistics(NOW);

        assertEquals(0, statistics.getTotalTasks());
        assertEquals(0, statistics.getCompletionRate());
    }

    private List<Task> getTasksCoveringAllStatuses() {
        LocalDate today = NOW.toLocalDate();
        LocalDateTime createdAt = NOW.minusDays(7);
        return List.of(
                new ToDo(true, "completed todo", createdAt, NOW.minusDays(1)),
                new ToDo(false, "incomplete todo", createdAt, null),
                new Deadline(true, "completed deadline", today.minusDays(1), createdAt, NOW.minusDays(1)),
                new Deadline(false, "due today", today, createdAt, null),
                new Deadline(false, "pending deadline", today.plusDays(1), createdAt, null),
                new Deadline(false, "overdue deadline", today.minusDays(1), createdAt, null),
                new Event(true, "completed event", today.minusDays(2), today.minusDays(1),
                        createdAt, NOW.minusDays(1)),
                new Event(false, "upcoming event", today.plusDays(1), today.plusDays(2), createdAt, null),
                new Event(false, "ongoing event", today.minusDays(1), today, createdAt, null),
                new Event(false, "past event", today.minusDays(2), today.minusDays(1), createdAt, null));
    }
}
