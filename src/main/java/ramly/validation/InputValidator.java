package ramly.validation;

import ramly.exception.CommandFormatException;
import ramly.exception.TaskValidationException;

/** Normalizes user text and enforces limits needed by commands and storage. */
public final class InputValidator {
    /** Maximum accepted length of a normalized command. */
    public static final int MAX_COMMAND_LENGTH = 500;
    /** Maximum accepted length of a task description. */
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    /** Maximum accepted length of a search keyword. */
    public static final int MAX_KEYWORD_LENGTH = 100;

    private InputValidator() {
    }

    /** Trims and collapses whitespace in a raw command. */
    public static String normalizeCommand(String input) {
        assert input != null : "Command input must not be null";
        boolean containsUnsupportedControl = input.codePoints()
                .anyMatch(character -> Character.isISOControl(character) && character != '\t');
        if (containsUnsupportedControl) {
            throw new CommandFormatException("Commands cannot contain line breaks or control characters.");
        }
        String normalized = input.strip().replaceAll("\\s+", " ");
        if (normalized.length() > MAX_COMMAND_LENGTH) {
            throw new CommandFormatException("That command is too long. Keep it within 500 characters.");
        }
        return normalized;
    }

    /** Validates and returns a normalized task description. */
    public static String normalizeDescription(String description) {
        return normalizeStoredText(description, "task description", MAX_DESCRIPTION_LENGTH);
    }

    /** Validates and returns a normalized find keyword. */
    public static String normalizeKeyword(String keyword) {
        return normalizeStoredText(keyword, "find keyword", MAX_KEYWORD_LENGTH);
    }

    /** Validates text that will be stored as one field in the task file. */
    public static String normalizeStoredText(String value, String fieldName, int maximumLength) {
        if (value == null || value.isBlank()) {
            throw new TaskValidationException("The " + fieldName + " cannot be empty.");
        }
        if (value.indexOf('|') >= 0 || value.codePoints().anyMatch(Character::isISOControl)) {
            throw new TaskValidationException("The " + fieldName + " cannot contain '|' or control characters.");
        }
        String normalized = value.strip().replaceAll("\\s+", " ");
        if (normalized.length() > maximumLength) {
            throw new TaskValidationException("The " + fieldName + " must not exceed " + maximumLength
                    + " characters.");
        }
        return normalized;
    }
}
