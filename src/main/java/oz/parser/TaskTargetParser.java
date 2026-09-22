package oz.parser;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oz.exception.OzException;
import oz.task.TaskDateTime;

/**
 * Parses task indexes and optional recurring occurrence dates.
 */
public final class TaskTargetParser {
    /** Error template shown when an unexpected parameter flag is supplied. */
    private static final String UNEXPECTED_FLAG_MESSAGE_FORMAT =
            "Unexpected '%s' parameter in %s command. %s";

    /** Error shown when no task number is provided. */
    private static final String EMPTY_TASK_NUMBER_MESSAGE =
            "Please specify a task number.";

    /** Error shown when the task list is empty. */
    private static final String EMPTY_TASK_LIST_MESSAGE =
            "Your task list is empty. Add tasks before referencing them.";

    /** Error shown when a task number is not positive. */
    private static final String NON_POSITIVE_TASK_NUMBER_MESSAGE =
            "Task number must be a positive whole number starting from 1.";

    /** Error template shown when a task number is out of bounds. */
    private static final String TASK_INDEX_OUT_OF_BOUNDS_MESSAGE_FORMAT =
            "Task number %d does not exist. Please provide a number between 1 and %d.";

    /** Error shown when a task number is not numeric. */
    private static final String INVALID_TASK_NUMBER_MESSAGE =
            "Please provide a valid whole number for the task index.";

    /** Error shown when a numeric task number cannot fit in an integer. */
    private static final String TASK_NUMBER_TOO_LARGE_MESSAGE =
            "That task number is too large.";

    /** Regex pattern parsing a task number and occurrence date. */
    private static final Pattern OCCURRENCE_ARGUMENTS_PATTERN = Pattern
            .compile("^(?<taskNumber>\\d+)\\s+/on\\s+(?<occurrenceDate>.+)$");

    /** Prevents instantiation of this utility class. */
    private TaskTargetParser() {
    }

    /**
     * Parses the target of a mark command.
     *
     * @param arguments Arguments supplied after the command word.
     * @param taskCount Current number of tasks.
     * @return Parsed task target.
     * @throws OzException If the arguments are invalid.
     */
    public static TaskTarget parseMarkTarget(String arguments, int taskCount)
            throws OzException {
        return parseMutationTarget(arguments, taskCount, "mark");
    }

    /**
     * Parses the target of an unmark command.
     *
     * @param arguments Arguments supplied after the command word.
     * @param taskCount Current number of tasks.
     * @return Parsed task target.
     * @throws OzException If the arguments are invalid.
     */
    public static TaskTarget parseUnmarkTarget(String arguments, int taskCount)
            throws OzException {
        return parseMutationTarget(arguments, taskCount, "unmark");
    }

    /**
     * Parses a one-based task number as a zero-based index.
     *
     * @param argument Argument containing the one-based task number.
     * @param taskCount Current number of tasks.
     * @return Zero-based task index.
     * @throws OzException If the task number is missing, malformed, or out of range.
     */
    public static int parseIndex(String argument, int taskCount) throws OzException {
        assert taskCount >= 0 : "The task count must not be negative";
        String trimmedArgument = argument == null ? "" : argument.trim();
        if (trimmedArgument.isEmpty()) {
            throw new OzException(EMPTY_TASK_NUMBER_MESSAGE);
        }

        int taskNumber = parseTaskNumber(trimmedArgument);
        if (taskNumber <= 0) {
            throw new OzException(NON_POSITIVE_TASK_NUMBER_MESSAGE);
        }
        if (taskCount == 0) {
            throw new OzException(EMPTY_TASK_LIST_MESSAGE);
        }
        if (taskNumber > taskCount) {
            throw new OzException(String.format(
                    TASK_INDEX_OUT_OF_BOUNDS_MESSAGE_FORMAT, taskNumber, taskCount));
        }

        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < taskCount
                : "A validated task number must map to an existing index";
        return taskIndex;
    }

    /**
     * Parses an optional occurrence date for a mark or unmark command.
     *
     * @param arguments Arguments supplied after the command word.
     * @param taskCount Current number of tasks.
     * @param commandWord Mark or unmark command word.
     * @return Parsed task target.
     * @throws OzException If flags, the task number, or the date are invalid.
     */
    private static TaskTarget parseMutationTarget(String arguments, int taskCount,
            String commandWord) throws OzException {
        validateMutationFlags(arguments, commandWord);

        Matcher occurrenceMatcher = OCCURRENCE_ARGUMENTS_PATTERN.matcher(arguments);
        if (!occurrenceMatcher.matches()) {
            return new TaskTarget(parseIndex(arguments, taskCount), null);
        }

        int taskIndex = parseIndex(occurrenceMatcher.group("taskNumber"), taskCount);
        LocalDate occurrenceDate = TaskDateTime.parseDate(
                occurrenceMatcher.group("occurrenceDate").trim());
        return new TaskTarget(taskIndex, occurrenceDate);
    }

    /**
     * Rejects duplicate occurrence flags and flags belonging to other commands.
     *
     * @param arguments Arguments supplied after the command word.
     * @param commandWord Mark or unmark command word.
     * @throws OzException If the flags are invalid.
     */
    private static void validateMutationFlags(String arguments, String commandWord)
            throws OzException {
        CommandArgumentValidator.validateNoDuplicateFlags(arguments, "/on");
        if (CommandArgumentValidator.containsAnyFlag(
                arguments, "/by", "/from", "/to")) {
            String usage = "Use: " + commandWord + " <number> [/on <date>].";
            throw new OzException(String.format(UNEXPECTED_FLAG_MESSAGE_FORMAT,
                    "flag", commandWord, usage));
        }
    }

    /**
     * Converts a numeric argument to an integer with user-focused errors.
     *
     * @param argument Trimmed task-number argument.
     * @return Parsed task number.
     * @throws OzException If the argument is malformed or too large.
     */
    private static int parseTaskNumber(String argument) throws OzException {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            if (argument.startsWith("-") && argument.substring(1).matches("\\d+")) {
                throw new OzException(NON_POSITIVE_TASK_NUMBER_MESSAGE);
            }
            if (argument.matches("\\d+")) {
                throw new OzException(TASK_NUMBER_TOO_LARGE_MESSAGE);
            }
            throw new OzException(INVALID_TASK_NUMBER_MESSAGE);
        }
    }

}
