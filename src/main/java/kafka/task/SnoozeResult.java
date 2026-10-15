package kafka.task;

/**
 * Stores display snapshots from before and after a task is snoozed.
 *
 * @param oldDisplay Task display before rescheduling.
 * @param newDisplay Task display after rescheduling.
 */
public record SnoozeResult(String oldDisplay, String newDisplay) {
}
