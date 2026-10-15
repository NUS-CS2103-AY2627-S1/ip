package kafka.task;

/**
 * Stores display snapshots from before and after a task rename.
 *
 * @param oldDisplay Task display before renaming.
 * @param newDisplay Task display after renaming.
 */
public record RenameResult(String oldDisplay, String newDisplay) {
}
