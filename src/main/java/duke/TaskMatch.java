package duke;

/**
 * Associates a search result with its task number in the full list.
 *
 * @param taskNumber One-based position used by mark, unmark, and delete.
 * @param task Matching task.
 */
public record TaskMatch(int taskNumber, Task task) {
}
