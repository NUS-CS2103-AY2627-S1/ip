package bob.parser;

/**
 * Contains the target and new value supplied in an edit command.
 *
 * @param taskNumber One-based number of the task to edit.
 * @param field Field of the task to edit.
 * @param newValue Replacement value for the field.
 */
public record EditDetails(int taskNumber, EditField field, String newValue) {
}
