package olaf;

/**
 * Associates a search result with its number in the full task list at the time of the search.
 *
 * @param taskNumber one-based number accepted by task commands
 * @param task matching task
 */
record TaskMatch(int taskNumber, Task task) {
}
