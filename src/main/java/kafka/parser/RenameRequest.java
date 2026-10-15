package kafka.parser;

/**
 * Stores the arguments supplied to a rename command.
 *
 * @param taskNumber One-based number of the task to rename.
 * @param newName Replacement task name.
 */
public record RenameRequest(int taskNumber, String newName) {
}
