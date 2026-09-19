package nudge.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import nudge.exception.NudgeException;
import nudge.task.Deadline;
import nudge.task.Event;
import nudge.task.Todo;

/**
 * Parses and validates commands entered by the user.
 */
public final class Parser {
    private static final String INPUT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String INPUT_DEADLINE_FORMAT =
            "deadline DESCRIPTION /by " + INPUT_DATE_FORMAT;
    private static final String INPUT_EVENT_FORMAT = "event DESCRIPTION /from "
            + INPUT_DATE_FORMAT + " /to " + INPUT_DATE_FORMAT;

    private Parser() {
    }

    /**
     * Identifies the type of the specified command.
     *
     * @param command full user command.
     * @return matching command type, or {@link CommandType#UNKNOWN} if unrecognized.
     */
    public static CommandType parseCommandType(String command) {
        if ("bye".equalsIgnoreCase(command)) {
            return CommandType.BYE;
        }
        if ("list".equalsIgnoreCase(command)) {
            return CommandType.LIST;
        }
        if (matchesCommand(command, "find")) {
            return CommandType.FIND;
        }
        if (matchesSortCommand(command)) {
            return CommandType.SORT;
        }
        if (matchesCommand(command, "mark")) {
            return CommandType.MARK;
        }
        if (matchesCommand(command, "unmark")) {
            return CommandType.UNMARK;
        }
        if (matchesCommand(command, "delete")) {
            return CommandType.DELETE;
        }
        if (matchesCommand(command, "todo")) {
            return CommandType.TODO;
        }
        if (matchesCommand(command, "deadline")) {
            return CommandType.DEADLINE;
        }
        if (matchesCommand(command, "event")) {
            return CommandType.EVENT;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Parses and validates the task number supplied to a task command.
     *
     * @param command full user command.
     * @param commandWord command word that precedes the task number.
     * @return zero-based index of the requested task.
     * @throws NudgeException if the task number is missing or invalid.
     */
    public static int parseTaskIndex(String command, String commandWord) throws NudgeException {
        String taskNumber = command.substring(commandWord.length()).trim();
        if (taskNumber.isEmpty()) {
            throw new NudgeException("`" + commandWord + "` needs a task number. "
                    + "Try: " + commandWord + " NUMBER");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new NudgeException("The task number must be a whole number. "
                    + "Try: " + commandWord + " NUMBER");
        }

        return taskIndex;
    }

    /**
     * Parses and validates the keyword supplied to a find command.
     *
     * @param command full user command.
     * @return keyword to find in task descriptions.
     * @throws NudgeException if the keyword is missing.
     */
    public static String parseFindKeyword(String command) throws NudgeException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new NudgeException("`find` needs a keyword. Try: find KEYWORD");
        }
        return keyword;
    }

    /**
     * Validates that a sort command does not contain arguments.
     *
     * @param command full user command.
     * @throws NudgeException if the command contains arguments.
     */
    public static void validateSortCommand(String command) throws NudgeException {
        String arguments = command.trim().substring("sort".length()).trim();
        if (!arguments.isEmpty()) {
            throw new NudgeException("`sort` does not take any arguments. Try: sort");
        }
    }

    /**
     * Parses and validates a todo command.
     *
     * @param command full user command.
     * @return todo described by the command.
     * @throws NudgeException if the description is missing.
     */
    public static Todo parseTodo(String command) throws NudgeException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NudgeException("A todo needs a description. Try: todo DESCRIPTION");
        }
        return new Todo(description);
    }

    /**
     * Parses and validates a deadline command.
     *
     * @param command full user command.
     * @return deadline described by the command.
     * @throws NudgeException if the description, delimiter, or due date is invalid.
     */
    public static Deadline parseDeadline(String command) throws NudgeException {
        String deadlineDetails = command.substring("deadline".length()).trim();
        if (deadlineDetails.isEmpty()) {
            throw new NudgeException("A deadline needs a description. Try: "
                    + INPUT_DEADLINE_FORMAT);
        }

        String[] deadlineParts = deadlineDetails.split("/by", -1);
        if (deadlineParts.length < 2) {
            throw new NudgeException("A deadline needs `/by` before its due date. Try: "
                    + INPUT_DEADLINE_FORMAT);
        }
        if (deadlineParts.length > 2) {
            throw new NudgeException("A deadline should contain `/by` once. Try: "
                    + INPUT_DEADLINE_FORMAT);
        }

        String description = deadlineParts[0].trim();
        String dueDateText = deadlineParts[1].trim();
        if (description.isEmpty()) {
            throw new NudgeException("A deadline needs a description. Try: "
                    + INPUT_DEADLINE_FORMAT);
        }
        if (dueDateText.isEmpty()) {
            throw new NudgeException("A deadline needs a date after `/by`. Try: "
                    + INPUT_DEADLINE_FORMAT);
        }
        LocalDate dueDate = parseDate(dueDateText, "deadline date");
        return new Deadline(description, dueDate);
    }

    /**
     * Parses and validates an event command.
     *
     * @param command full user command.
     * @return event described by the command.
     * @throws NudgeException if the description, delimiters, or dates are invalid.
     */
    public static Event parseEvent(String command) throws NudgeException {
        String eventDetails = command.substring("event".length()).trim();
        if (eventDetails.isEmpty()) {
            throw new NudgeException("An event needs a description. Try: " + INPUT_EVENT_FORMAT);
        }

        String[] eventParts = eventDetails.split("/from", -1);
        if (eventParts.length < 2) {
            throw new NudgeException("An event needs `/from` before its start time. Try: "
                    + INPUT_EVENT_FORMAT);
        }
        if (eventParts.length > 2) {
            throw new NudgeException("An event should contain `/from` once. Try: "
                    + INPUT_EVENT_FORMAT);
        }

        String description = eventParts[0].trim();
        if (description.isEmpty()) {
            throw new NudgeException("An event needs a description. Try: " + INPUT_EVENT_FORMAT);
        }

        String[] timeParts = eventParts[1].split("/to", -1);
        if (timeParts.length < 2) {
            throw new NudgeException("An event needs `/to` before its end time. Try: "
                    + INPUT_EVENT_FORMAT);
        }
        if (timeParts.length > 2) {
            throw new NudgeException("An event should contain `/to` once. Try: "
                    + INPUT_EVENT_FORMAT);
        }

        String startDateText = timeParts[0].trim();
        String endDateText = timeParts[1].trim();
        if (startDateText.isEmpty()) {
            throw new NudgeException("An event needs a start date after `/from`. Try: "
                    + INPUT_EVENT_FORMAT);
        }
        if (endDateText.isEmpty()) {
            throw new NudgeException("An event needs an end date after `/to`. Try: "
                    + INPUT_EVENT_FORMAT);
        }
        LocalDate startDate = parseDate(startDateText, "event start date");
        LocalDate endDate = parseDate(endDateText, "event end date");
        if (!startDate.isBefore(endDate)) {
            throw new NudgeException("The event start date must be before the end date.");
        }
        return new Event(description, startDate, endDate);
    }

    /**
     * Returns whether a command consists of the specified command word, optionally followed by
     * arguments.
     *
     * @param command full user command.
     * @param commandWord command word to match.
     * @return true if the command starts with the complete command word.
     */
    private static boolean matchesCommand(String command, String commandWord) {
        return commandWord.equals(command)
                || (command.startsWith(commandWord)
                && command.length() > commandWord.length()
                && Character.isWhitespace(command.charAt(commandWord.length())));
    }

    /**
     * Returns whether the trimmed command is {@code sort}, optionally followed by arguments.
     *
     * @param command full user command.
     * @return true if the command starts with the complete {@code sort} command word.
     */
    private static boolean matchesSortCommand(String command) {
        String trimmedCommand = command.trim();
        return "sort".equals(trimmedCommand)
                || (trimmedCommand.startsWith("sort")
                && trimmedCommand.length() > "sort".length()
                && Character.isWhitespace(trimmedCommand.charAt("sort".length())));
    }

    /**
     * Parses a date in the supported input format and converts parsing failures into a
     * user-facing error.
     *
     * @param dateText date text to parse.
     * @param dateName name used to identify the date in an error message.
     * @return parsed date.
     * @throws NudgeException if the date text is not a valid date in the supported format.
     */
    private static LocalDate parseDate(String dateText, String dateName) throws NudgeException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new NudgeException("The " + dateName + " must be a valid date in "
                    + INPUT_DATE_FORMAT + " format.");
        }
    }
}
