package happybot;

/**
 * Stores the counts displayed by HappyBot's statistics command.
 */
class TaskStatistics {
    private final int totalTaskCount;
    private final int completedTaskCount;
    private final int completedToDoCount;
    private final int completedDeadlineCount;
    private final int completedEventCount;
    private final int uncompletedDeadlineCount;

    /**
     * Creates statistics containing the supplied task counts.
     */
    TaskStatistics(int totalTaskCount, int completedTaskCount, int completedToDoCount,
                   int completedDeadlineCount, int completedEventCount, int uncompletedDeadlineCount) {
        this.totalTaskCount = totalTaskCount;
        this.completedTaskCount = completedTaskCount;
        this.completedToDoCount = completedToDoCount;
        this.completedDeadlineCount = completedDeadlineCount;
        this.completedEventCount = completedEventCount;
        this.uncompletedDeadlineCount = uncompletedDeadlineCount;
    }

    int getTotalTaskCount() {
        return totalTaskCount;
    }

    int getCompletedTaskCount() {
        return completedTaskCount;
    }

    int getCompletedToDoCount() {
        return completedToDoCount;
    }

    int getCompletedDeadlineCount() {
        return completedDeadlineCount;
    }

    int getCompletedEventCount() {
        return completedEventCount;
    }

    int getUncompletedDeadlineCount() {
        return uncompletedDeadlineCount;
    }
}
