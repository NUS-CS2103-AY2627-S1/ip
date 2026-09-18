package sandrone.task;

/** Stores a summary of the current task list for display to the user. */
public class TaskStatistics {
    private final int totalTasks;
    private final int completedTasks;
    private final int todoCount;
    private final int deadlineCount;
    private final int eventCount;
    private final int scheduledTodayCount;
    private final int taskLimit;

    /**
     * Creates task statistics from counts calculated from a task list.
     */
    public TaskStatistics(int totalTasks, int completedTasks, int todoCount, int deadlineCount,
                          int eventCount, int scheduledTodayCount, int taskLimit) {
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.todoCount = todoCount;
        this.deadlineCount = deadlineCount;
        this.eventCount = eventCount;
        this.scheduledTodayCount = scheduledTodayCount;
        this.taskLimit = taskLimit;
    }

    /** Returns the number of tasks in the task list. */
    public int getTotalTasks() {
        return totalTasks;
    }

    /** Returns the number of completed tasks. */
    public int getCompletedTasks() {
        return completedTasks;
    }

    /** Returns the number of incomplete tasks. */
    public int getIncompleteTasks() {
        return totalTasks - completedTasks;
    }

    /** Returns the percentage of tasks that are completed. */
    public double getCompletionRate() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (double) completedTasks / totalTasks * 100;
    }

    /** Returns the number of todo tasks. */
    public int getTodoCount() {
        return todoCount;
    }

    /** Returns the number of deadline tasks. */
    public int getDeadlineCount() {
        return deadlineCount;
    }

    /** Returns the number of event tasks. */
    public int getEventCount() {
        return eventCount;
    }

    /** Returns the number of tasks scheduled for the current date. */
    public int getScheduledTodayCount() {
        return scheduledTodayCount;
    }

    /** Returns the maximum number of tasks the task list can hold. */
    public int getTaskLimit() {
        return taskLimit;
    }

    /** Returns the number of task slots still available. */
    public int getRemainingTaskSlots() {
        return taskLimit - totalTasks;
    }
}
