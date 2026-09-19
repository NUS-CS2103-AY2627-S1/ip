package carl.parser;

import carl.exceptions.CarlCommandException;

/**
 * Provides shared validation and normalization for text entered in commands.
 */
public final class InputValidator {
    private static final int MAX_TASK_NAME_LENGTH = 500;

    private InputValidator() {
    }

    /**
     * Validates and normalizes a task description.
     *
     * @param input raw description entered by the user.
     * @param usage command format to display when the description is empty.
     * @return a trimmed description with repeated whitespace collapsed.
     * @throws CarlCommandException If the description is empty, too long, or unsafe for storage.
     */
    public static String normalizeTaskName(String input, String usage) throws CarlCommandException {
        if (input == null || input.isBlank()) {
            throw new CarlCommandException(usage);
        }
        if (input.indexOf('|') >= 0) {
            throw new CarlCommandException("Task descriptions cannot contain the `|` character.");
        }

        String normalized = input.strip().replaceAll("\\s+", " ");
        if (normalized.length() > MAX_TASK_NAME_LENGTH) {
            throw new CarlCommandException("Task descriptions cannot exceed "
                    + MAX_TASK_NAME_LENGTH + " characters.");
        }
        return normalized;
    }
}
