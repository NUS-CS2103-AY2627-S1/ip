package tasktracker.parser;

import tasktracker.exception.TaskTrackerException;
import tasktracker.task.Deadline;
import tasktracker.task.Event;
import tasktracker.task.Fixed;
import tasktracker.task.TaskDateTime;
import tasktracker.task.ToDo;
import tasktracker.ui.Message;

/**
 * Handles parsing and validation of raw user command strings.
 * <p>
 * Note: Gemini AI was used substantially at this section to handle input edge cases.
 * Initial idea to create a dedicated parser class was by me, further refinement by AI.
 * Prompt "I currently have these methods in TaskTracker.java, but I want to move them."
 * Prompt "How do I create a new Parser class that checks and validates input?"
 * Prompt "I basically only want new Task (or its subclasses) created post validation."
 * Prompt "Guide me on the process and explain to me the inner working mechanism."
 * Add code only after an intermediate level of understanding achieved.
 */
public class Parser {
    private static final String DELIMITER_BY = " /by ";
    private static final String DELIMITER_FROM = " /from ";
    private static final String DELIMITER_TO = " /to ";
    private static final String DELIMITER_NEEDS = " /needs ";
    private static final String ILLEGAL_STORAGE_DELIMITER = "|";

    /**
     * Parses and validates that the argument is a valid integer task index.
     *
     * @param argument The raw input string containing the task index.
     * @return The parsed 1-based task index as an integer.
     * @throws TaskTrackerException If the argument is empty or cannot be parsed into an integer.
     */
    public static int parseIndex(String argument) throws TaskTrackerException {
        String trimmed = validateArgument(argument, Message.ERR_MISSING_INDEX);
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            throw new TaskTrackerException(Message.ERR_INVALID_INDEX);
        }
    }

    /**
     * Parses and validates the search keyword argument for the find command.
     *
     * @param argument The raw input string containing the search keyword.
     * @return The trimmed search keyword.
     * @throws TaskTrackerException If the keyword is missing or empty.
     */
    public static String parseFind(String argument) throws TaskTrackerException {
        return validateArgument(argument, Message.ERR_EMPTY_FIND);
    }

    /**
     * Parses argument into a {@code ToDo} object.
     *
     * @param argument The raw input string containing the todo description.
     * @return A new {@code ToDo} instance created from the parsed description.
     * @throws TaskTrackerException If the description is empty or missing.
     */
    public static ToDo parseToDo(String argument) throws TaskTrackerException {
        String description = validateArgument(argument, Message.ERR_EMPTY_TODO);
        return new ToDo(description);
    }

    /**
     * Parses argument into a {@code Deadline} object.
     *
     * @param argument The raw input string containing the deadline description and date.
     * @return A new {@code Deadline} instance created from the parsed description and by-date.
     * @throws TaskTrackerException If the description or date is empty, or if {@code "/by"} is missing.
     */
    public static Deadline parseDeadline(String argument) throws TaskTrackerException {
        String trimmed = validateArgument(argument, Message.ERR_EMPTY_DEADLINE);

        if (!trimmed.contains(DELIMITER_BY)) {
            throw new TaskTrackerException(Message.ERR_MISSING_BY);
        }

        String[] parts = trimmed.split(DELIMITER_BY, 2);
        String description = validateArgument(parts[0], Message.ERR_EMPTY_DEADLINE);
        String by = (parts.length < 2)
                ? ""
                : validateArgument(parts[1], Message.ERR_MISSING_BY);

        return new Deadline(description, new TaskDateTime(by));
    }

    /**
     * Parses argument into an {@code Event} object.
     * <p>
     * Note: Gemini AI used to make the parseEvent validation better to handle improper user input.
     *
     * @param argument The raw input string containing the event description, start time, and end time.
     * @return A new {@code Event} instance created from the parsed description, start time, and end time.
     * @throws TaskTrackerException If any field is empty, or if {@code "/from"} or {@code "/to"} flags are missing.
     */
    public static Event parseEvent(String argument) throws TaskTrackerException {
        String trimmed = validateArgument(argument, Message.ERR_EMPTY_EVENT);
        validateEventOrder(trimmed);

        if (!trimmed.contains(DELIMITER_FROM)) {
            throw new TaskTrackerException(Message.ERR_MISSING_FROM);
        }

        String[] fromParts = trimmed.split(DELIMITER_FROM, 2);
        String description = validateArgument(fromParts[0], Message.ERR_EMPTY_EVENT);

        String[] times = extractEventTimes(fromParts[1]);
        TaskDateTime startDateTime = new TaskDateTime(times[0]);
        TaskDateTime endDateTime = new TaskDateTime(times[1]);
        validateChronology(startDateTime, endDateTime);

        return new Event(description, startDateTime, endDateTime);
    }

    /**
     * Parses argument into a {@code Fixed} object.
     *
     * @param argument The raw input string containing the task description and duration.
     * @return A new {@code Fixed} instance created from the parsed description and duration.
     * @throws TaskTrackerException If any field is empty, or if {@code "/needs"} is missing.
     */
    public static Fixed parseFixedDurationTask(String argument)
            throws TaskTrackerException {
        if (argument == null || argument.trim().isEmpty()) {
            throw new TaskTrackerException(Message.ERR_EMPTY_FIXED);
        }

        if (!argument.contains(DELIMITER_NEEDS)) {
            throw new TaskTrackerException(Message.ERR_MISSING_NEEDS);
        }

        String[] parts = argument.split(DELIMITER_NEEDS, 2);
        String description = validateArgument(parts[0], Message.ERR_EMPTY_FIXED);
        String duration = (parts.length < 2)
                ? ""
                : validateArgument(parts[1], Message.ERR_EMPTY_DURATION);

        return new Fixed(description, duration);
    }

    /**
     * Ensures an argument string is non-empty after trimming and contains no illegal delimiters.
     *
     * @param argument The raw argument string to check.
     * @param errorMessage The exception message to throw if validation fails.
     * @return The trimmed, non-empty argument string.
     * @throws TaskTrackerException If the argument is null, empty after trimming, or contains illegal characters.
     */
    private static String validateArgument(String argument, String errorMessage)
            throws TaskTrackerException {
        if (argument == null || argument.trim().isEmpty()) {
            throw new TaskTrackerException(errorMessage);
        }
        if (argument.contains(ILLEGAL_STORAGE_DELIMITER)) {
            throw new TaskTrackerException(Message.ERR_NO_DELIMITER);
        }
        return argument.trim();
    }

    /**
     * Checks that the {@code "/from"} delimiter appears before the {@code "/to"} delimiter if both exist.
     *
     * @param argument Raw argument string to inspect.
     * @throws TaskTrackerException If delimiters appear out of chronological order.
     */
    private static void validateEventOrder(String argument) throws TaskTrackerException {
        boolean hasFrom = argument.contains(DELIMITER_FROM);
        boolean hasTo = argument.contains(DELIMITER_TO);

        if (hasFrom && hasTo && argument.indexOf(DELIMITER_TO) < argument.indexOf(DELIMITER_FROM)) {
            throw new TaskTrackerException(Message.ERR_OUT_OF_ORDER);
        }
    }

    /**
     * Ensures that the event start time does not occur after the end time.
     *
     * @param start Parsed starting date-time.
     * @param end Parsed ending date-time.
     * @throws TaskTrackerException If start time occurs strictly after end time.
     */
    private static void validateChronology(TaskDateTime start, TaskDateTime end)
            throws TaskTrackerException {
        if (start.isAfter(end)) {
            throw new TaskTrackerException(Message.ERR_EVENT_CHRONOLOGY);
        }
    }

    /**
     * Extracts and validates start and end time strings from the portion after {@code "/from"}.
     *
     * @param afterFrom Substring of event arguments following the start-time flag.
     * @return Two-element array containing trimmed start and end time strings.
     * @throws TaskTrackerException If boundary tokens are missing or invalid.
     */
    private static String[] extractEventTimes(String afterFrom) throws TaskTrackerException {
        if (!afterFrom.contains(DELIMITER_TO)) {
            if (afterFrom.trim().isEmpty() || afterFrom.trim().startsWith("/to")) {
                throw new TaskTrackerException(Message.ERR_MISSING_FROM);
            }
            throw new TaskTrackerException(Message.ERR_MISSING_TO);
        }

        String[] toParts = afterFrom.split(DELIMITER_TO, 2);
        String fromStr = validateArgument(toParts[0], Message.ERR_MISSING_FROM);
        String toStr = (toParts.length < 2)
                ? ""
                : validateArgument(toParts[1], Message.ERR_MISSING_TO);

        return new String[]{fromStr, toStr};
    }
}
