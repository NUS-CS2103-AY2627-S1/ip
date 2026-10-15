package happybot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskStatisticsTest {
    @Test
    public void getters_valuesProvidedAtConstruction_sameValuesReturned() {
        TaskStatistics statistics = new TaskStatistics(10, 5, 2, 1, 2, 3);

        assertEquals(10, statistics.getTotalTaskCount());
        assertEquals(5, statistics.getCompletedTaskCount());
        assertEquals(2, statistics.getCompletedToDoCount());
        assertEquals(1, statistics.getCompletedDeadlineCount());
        assertEquals(2, statistics.getCompletedEventCount());
        assertEquals(3, statistics.getUncompletedDeadlineCount());
    }
}
