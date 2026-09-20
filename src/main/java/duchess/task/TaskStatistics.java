package duchess.task;

/** Stores summary statistics for a Duchess task list. */
public final class TaskStatistics {
    /** The total number of tasks. */
    private final int totalTasks;

    /** The number of currently completed tasks. */
    private final int completedTasks;

    /** The number of currently completed tasks finished in the last seven days. */
    private final int recentlyCompletedTasks;

    /**
     * Creates a task statistics value.
     *
     * @param totalTasks the total number of tasks
     * @param completedTasks the number of completed tasks
     * @param recentlyCompletedTasks the number completed in the last seven days
     */
    public TaskStatistics(int totalTasks, int completedTasks, int recentlyCompletedTasks) {
        assert totalTasks >= 0 : "Total task count cannot be negative";
        assert completedTasks >= 0 && completedTasks <= totalTasks
                : "Completed task count must be within the total task count";
        assert recentlyCompletedTasks >= 0 && recentlyCompletedTasks <= completedTasks
                : "Recent completion count must be within the completed task count";
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.recentlyCompletedTasks = recentlyCompletedTasks;
    }

    /** Returns the total number of tasks. */
    public int getTotalTasks() {
        return totalTasks;
    }

    /** Returns the number of currently completed tasks. */
    public int getCompletedTasks() {
        return completedTasks;
    }

    /** Returns the number of incomplete tasks. */
    public int getIncompleteTasks() {
        return totalTasks - completedTasks;
    }

    /** Returns the number of currently completed tasks finished in the last seven days. */
    public int getRecentlyCompletedTasks() {
        return recentlyCompletedTasks;
    }

    /** Returns the rounded completion percentage, or zero for an empty list. */
    public int getCompletionRatePercentage() {
        if (totalTasks == 0) {
            return 0;
        }
        return (int) Math.round((double) completedTasks * 100 / totalTasks);
    }
}
