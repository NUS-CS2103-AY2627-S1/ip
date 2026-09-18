package aster.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the figures a {@link TaskStatistics} derives from its counts.
 *
 * <p>The percentage cases include shares that do not divide evenly, so they pin down
 * that the percentage is rounded down and reaches 100 only when every task is done.
 */
class TaskStatisticsTest {

    @Test
    void completionPercent_noneCompleted_returnsZero() {
        assertEquals(0, statisticsOf(1, 0).completionPercent());
    }

    @Test
    void completionPercent_oneOfThree_roundsDownToThirtyThree() {
        assertEquals(33, statisticsOf(3, 1).completionPercent());
    }

    @Test
    void completionPercent_half_returnsFifty() {
        assertEquals(50, statisticsOf(2, 1).completionPercent());
    }

    @Test
    void completionPercent_twoOfThree_roundsDownToSixtySix() {
        assertEquals(66, statisticsOf(3, 2).completionPercent());
    }

    @Test
    void completionPercent_oneShortOfTwoHundred_roundsDownToNinetyNine() {
        assertEquals(99, statisticsOf(200, 199).completionPercent());
    }

    @Test
    void completionPercent_allCompleted_returnsHundred() {
        assertEquals(100, statisticsOf(1, 1).completionPercent());
        assertEquals(100, statisticsOf(200, 200).completionPercent());
    }

    @Test
    void completionPercent_noTasks_returnsZeroWithoutDividing() {
        assertEquals(0, statisticsOf(0, 0).completionPercent());
    }

    @Test
    void notCompleted_someCompleted_returnsTheRest() {
        assertEquals(3, new TaskStatistics(5, 2, 2, 2, 1).notCompleted());
    }

    // ---------- helpers ----------

    /**
     * Returns statistics holding only todos, with the given total and completed counts.
     *
     * @param total the number of tasks.
     * @param completed the number of tasks done.
     * @return the statistics.
     */
    private static TaskStatistics statisticsOf(int total, int completed) {
        return new TaskStatistics(total, completed, total, 0, 0);
    }
}
