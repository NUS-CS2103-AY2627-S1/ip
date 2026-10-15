package littler.task;

/**
 * Represents a snapshot of counts across the current task list, used for the stats command.
 */
public record Statistics(
    int total,
    int completed,
    int pending,
    int todoCount,
    int deadlineCount,
    int eventCount,
    int highPriorityCount,
    int mediumPriorityCount,
    int lowPriorityCount,
    int noPriorityCount,
    int taggedCount,
    int untaggedCount
) {}
