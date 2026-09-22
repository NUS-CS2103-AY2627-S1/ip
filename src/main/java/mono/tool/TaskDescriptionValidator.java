package mono.tool;

import mono.exception.WrongFormatException;

/** Validates task descriptions before they are written to the line-based storage format. */
final class TaskDescriptionValidator {
    private TaskDescriptionValidator() {
    }

    /**
     * Rejects characters that would make a persisted task ambiguous or multi-line.
     *
     * @param description task description to validate
     * @param formatMessage user-facing format message for the calling command
     * @throws WrongFormatException if the description cannot be stored safely
     */
    static void validate(String description, String formatMessage) throws WrongFormatException {
        if (description.indexOf('|') >= 0 || description.chars().anyMatch(Character::isISOControl)) {
            throw new WrongFormatException(formatMessage);
        }
    }
}
