package aster.task;

/**
 * A summary of the tasks in a list at one moment: how many there are, how many are done,
 * and how many there are of each type.
 *
 * <p>The figures are counted once and then fixed, so everything derived from them, such
 * as the share of tasks completed, describes the same moment.
 *
 * @param total the number of tasks.
 * @param completed the number of tasks marked as done.
 * @param todos the number of todos.
 * @param deadlines the number of deadlines.
 * @param events the number of events.
 */
public record TaskStatistics(int total, int completed, int todos, int deadlines, int events) {
    /**
     * Returns the number of tasks not yet marked as done.
     *
     * @return the total minus the completed tasks.
     */
    public int notCompleted() {
        return total - completed;
    }

    /**
     * Returns the share of tasks completed, as a whole percentage rounded down.
     *
     * <p>Rounding down means 100% is shown only when every task is done, so 199 of 200
     * is 99%. With no tasks there is no share to take, so this returns 0 rather than
     * dividing by zero.
     *
     * @return the completed tasks as a percentage of all tasks, from 0 to 100.
     */
    public int completionPercent() {
        if (total == 0) {
            return 0;
        }
        return (int) ((long) completed * 100 / total);
    }
}
