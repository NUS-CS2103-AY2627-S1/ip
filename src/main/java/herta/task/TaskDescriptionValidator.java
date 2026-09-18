package herta.task;

import java.util.Objects;

/**
 * Validates task descriptions shared by command parsing and storage loading.
 */
public final class TaskDescriptionValidator {
    /** Maximum number of UTF-16 code units allowed in a task description. */
    public static final int MAX_DESCRIPTION_LENGTH = 1_000;

    private TaskDescriptionValidator() {
        // Utility class; do not instantiate.
    }

    /**
     * Validates a task description before it enters the domain model.
     *
     * @param description the description to validate.
     * @throws NullPointerException if the description is null.
     * @throws IllegalArgumentException if the description is blank, too long,
     *         or contains a delimiter or unsupported control character.
     */
    public static void validate(String description) {
        Objects.requireNonNull(description, "Task description cannot be null.");
        if (description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be blank.");
        }
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException("Task descriptions cannot exceed "
                    + MAX_DESCRIPTION_LENGTH + " characters.");
        }
        for (int i = 0; i < description.length(); i++) {
            int codePoint = description.codePointAt(i);
            if (Character.isHighSurrogate(description.charAt(i))) {
                if (i + 1 >= description.length()
                        || !Character.isLowSurrogate(description.charAt(i + 1))) {
                    throw createInvalidDescriptionCharacterException();
                }
                i++;
            } else if (Character.isLowSurrogate(description.charAt(i))) {
                throw createInvalidDescriptionCharacterException();
            }
            if (isInvalidCharacter(codePoint)) {
                throw createInvalidDescriptionCharacterException();
            }
        }
    }

    /** Returns the stable error used for every unsupported description character. */
    private static IllegalArgumentException createInvalidDescriptionCharacterException() {
        return new IllegalArgumentException(
                "Task descriptions cannot contain `|`, line breaks, or control characters.");
    }

    /** Identifies storage delimiters, line breaks, controls, and unsupported invisible characters. */
    private static boolean isInvalidCharacter(int codePoint) {
        boolean isStorageDelimiter = codePoint == '|';
        boolean isLineBreak = codePoint == '\n' || codePoint == '\r';
        boolean isControlCharacter = Character.isISOControl(codePoint);
        boolean isFormatCharacter = Character.getType(codePoint) == Character.FORMAT;
        boolean isUnsupportedSpace = Character.isSpaceChar(codePoint) && codePoint != ' ';
        return isStorageDelimiter || isLineBreak || isControlCharacter
                || isFormatCharacter || isUnsupportedSpace;
    }
}
