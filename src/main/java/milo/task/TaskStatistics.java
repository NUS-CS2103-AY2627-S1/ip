package milo.task;

/** Provides an immutable summary of the tasks currently in a list. */
public class TaskStatistics {
    private final int totalCount;
    private final int completedCount;
    private final int todoCount;
    private final int deadlineCount;
    private final int eventCount;

    /**
     * Counts current tasks and their completion states without changing them.
     *
     * @param tasks task list to summarize.
     */
    public TaskStatistics(TaskList tasks) {
        int completed = 0;
        int todos = 0;
        int deadlines = 0;
        int events = 0;
        for (Task task : tasks.getTasks()) {
            if (task.isDone()) {
                completed++;
            }
            if (task instanceof Todo) {
                todos++;
            } else if (task instanceof Deadline) {
                deadlines++;
            } else if (task instanceof Event) {
                events++;
            }
        }
        totalCount = tasks.size();
        completedCount = completed;
        todoCount = todos;
        deadlineCount = deadlines;
        eventCount = events;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public int getPendingCount() {
        return totalCount - completedCount;
    }

    /**
     * Returns the percentage of current tasks completed, or zero for an empty list.
     *
     * @return completion percentage between zero and one hundred.
     */
    public double getCompletionPercentage() {
        return totalCount == 0 ? 0.0 : 100.0 * completedCount / totalCount;
    }

    public int getTodoCount() {
        return todoCount;
    }

    public int getDeadlineCount() {
        return deadlineCount;
    }

    public int getEventCount() {
        return eventCount;
    }
}
