package herta.parser;

import java.math.BigInteger;

import herta.exception.HertaException;

/**
 * Parses one-based task numbers used by task-selection commands.
 */
final class TaskIndexParser {
    private static final String INVALID_TASK_NUMBER_ERROR =
            "That's not a task number. Try: %s 1.";

    /** Converts a one-based task number in a command to a zero-based index. */
    int parseTaskIndex(String input, String commandKeyword) throws HertaException {
        if (input == null || commandKeyword == null) {
            throw createInvalidTaskNumberException(commandKeyword);
        }
        String normalizedInput = input.trim();
        if (!hasTaskNumberPrefix(normalizedInput, commandKeyword)) {
            throw createInvalidTaskNumberException(commandKeyword);
        }
        String taskNumber = normalizedInput.substring(commandKeyword.length()).trim();
        if (!isPositiveDecimal(taskNumber)) {
            throw createInvalidTaskNumberException(commandKeyword);
        }
        try {
            return new BigInteger(taskNumber).intValueExact() - 1;
        } catch (ArithmeticException e) {
            throw createInvalidTaskNumberException(commandKeyword);
        }
    }

    /** Checks that a task number follows its command keyword with a supported separator. */
    private boolean hasTaskNumberPrefix(String input, String commandKeyword) {
        return input.startsWith(commandKeyword)
                && input.length() > commandKeyword.length()
                && CommandTokenizer.isHorizontalWhitespace(input.charAt(commandKeyword.length()));
    }

    /** Checks a bounded, positive decimal number before constructing a BigInteger. */
    private boolean isPositiveDecimal(String input) {
        if (!ParserLimits.isBoundedDecimal(input)) {
            return false;
        }
        try {
            return new BigInteger(input).signum() > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Creates the stable guidance used for an invalid task number. */
    private HertaException createInvalidTaskNumberException(String commandKeyword) {
        return new HertaException(INVALID_TASK_NUMBER_ERROR.formatted(commandKeyword));
    }
}
