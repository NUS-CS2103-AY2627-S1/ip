package sandrone.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaskStatisticsTest {
    @Test
    void taskStatistics_withTasks_returnsCountsAndDerivedValues() {
        TaskStatistics statistics = new TaskStatistics(8, 3, 3, 2, 3, 2, 10);

        assertEquals(8, statistics.getTotalTasks());
        assertEquals(3, statistics.getCompletedTasks());
        assertEquals(5, statistics.getIncompleteTasks());
        assertEquals(37.5, statistics.getCompletionRate());
        assertEquals(3, statistics.getTodoCount());
        assertEquals(2, statistics.getDeadlineCount());
        assertEquals(3, statistics.getEventCount());
        assertEquals(2, statistics.getScheduledTodayCount());
        assertEquals(10, statistics.getTaskLimit());
        assertEquals(2, statistics.getRemainingTaskSlots());
    }

    @Test
    void taskStatistics_emptyTaskList_returnsZeroCompletionRate() {
        TaskStatistics statistics = new TaskStatistics(0, 0, 0, 0, 0, 0, 10);

        assertEquals(0.0, statistics.getCompletionRate());
    }
}
