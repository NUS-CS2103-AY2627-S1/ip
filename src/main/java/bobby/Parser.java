package bobby;

import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses command names, task commands, arguments, and task numbers from user input. */
public class Parser {
    /** Supported command names. */
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_PRIORITY = "priority";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";

    /** Markers used to separate task details from their dates. */
    private static final Pattern DEADLINE_DATE_MARKER = createMarkerPattern("/by");
    private static final Pattern EVENT_FROM_MARKER = createMarkerPattern("/from");
    private static final Pattern EVENT_TO_MARKER = createMarkerPattern("/to");

    /** Creates a parser for Bobby commands. */
    public Parser() {
    }

    /**
     * Returns whether an input is a command or has whitespace-separated arguments.
     *
     * @param input complete user input to inspect
     * @param commandNames command names to match
     * @return {@code true} when the input is exactly one command or starts with it
     */
    public boolean isCommand(String input, String... commandNames) {
        if (input == null) {
            return false;
        }
        for (String commandName : commandNames) {
            if (input.equals(commandName)
                    || (input.length() > commandName.length()
                    && input.startsWith(commandName)
                    && Character.isWhitespace(input.charAt(commandName.length())))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the argument text after a command name.
     *
     * @param command complete user input
     * @param commandName command whose argument should be extracted
     * @return trimmed text following the command name, or an empty string
     */
    public String getCommandArgument(String command, String commandName) {
        if (command.length() <= commandName.length()) {
            return "";
        }
        return command.substring(commandName.length()).strip();
    }

    /**
     * Returns whether the input requests that Bobby exits.
     *
     * @param command complete user input
     * @return {@code true} when the input is {@code bye}, ignoring case
     */
    public boolean isExitCommand(String command) {
        return command != null && command.equalsIgnoreCase(COMMAND_BYE);
    }

    /**
     * Returns whether the command creates a to-do, deadline, or event task.
     *
     * @param command complete user input
     * @return {@code true} for a supported task-creation command
     */
    public boolean isTaskCreationCommand(String command) {
        return isCommand(command, COMMAND_TODO, COMMAND_DEADLINE, COMMAND_EVENT);
    }

    /**
     * Parses a supported task command into an executable command.
     *
     * @param command complete user input
     * @return command object that performs the requested action
     * @throws BobbyException if the input does not begin with a supported command
     */
    public Command parse(String command) throws BobbyException {
        if (command == null || command.strip().isEmpty()) {
            throw new BobbyException("Error! The command cannot be empty!");
        }
        command = command.strip();

        if (isExitCommand(command)) {
            return new ExitCommand();
        } else if (isCommand(command, COMMAND_BYE)) {
            throw new BobbyException("Error! The bye command does not accept arguments.");
        } else if (command.equals(COMMAND_LIST)) {
            return new ListCommand();
        } else if (isCommand(command, COMMAND_LIST)) {
            throw new BobbyException("Error! The list command does not accept arguments.");
        } else if (isCommand(command, COMMAND_FIND)) {
            String keyword = getCommandArgument(command, COMMAND_FIND);
            if (keyword.isEmpty()) {
                throw new BobbyException("Error! The search keyword cannot be empty!");
            }
            return new FindCommand(keyword);
        } else if (isCommand(command, COMMAND_MARK)) {
            return new MarkCommand(command);
        } else if (isCommand(command, COMMAND_UNMARK)) {
            return new UnmarkCommand(command);
        } else if (isCommand(command, COMMAND_DELETE)) {
            return new DeleteCommand(command);
        } else if (isCommand(command, COMMAND_PRIORITY)) {
            return new PriorityCommand(command);
        } else if (isCommand(command, COMMAND_TODO)) {
            return new AddCommand(parseTodo(command));
        } else if (isCommand(command, COMMAND_DEADLINE)) {
            return new AddCommand(parseDeadline(command));
        } else if (isCommand(command, COMMAND_EVENT)) {
            return new AddCommand(parseEvent(command));
        }
        throw new BobbyException("No such task type available. Try again!");
    }

    /**
     * Parses and validates a one-based task number from a task command.
     *
     * @param command complete user input containing the task number
     * @param commandName command whose task number is being parsed
     * @param taskCount number of tasks currently available
     * @return zero-based index corresponding to the requested task
     * @throws BobbyException if no tasks exist or the task number is invalid
     */
    public int parseTaskIndex(String command, String commandName, int taskCount)
            throws BobbyException {
        assert taskCount >= 0 : "Task count cannot be negative";
        if (taskCount == 0) {
            throw new BobbyException("No tasks available to " + commandName + ".");
        }

        String taskNumber = getCommandArgument(command, commandName);
        if (taskNumber.isEmpty()) {
            throw new BobbyException("Error! The task number cannot be empty!");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new BobbyException("Error! The task number must be a valid integer.");
        }
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new BobbyException("Error! The task number must be between 1 and "
                    + taskCount + ".");
        }
        assert taskIndex >= 0 && taskIndex < taskCount
                : "A parsed task index must refer to an existing task";
        return taskIndex;
    }

    /**
     * Parses a to-do command and validates its description.
     *
     * @param command complete to-do command
     * @return parsed to-do task
     * @throws BobbyException if the description is empty or unsafe for storage
     */
    private Task parseTodo(String command) throws BobbyException {
        String description = normalizeWhitespace(getCommandArgument(command, COMMAND_TODO));
        if (description.isEmpty()) {
            throw new BobbyException("Error! The description of a todo cannot be empty!");
        }
        validateStorageField(description);
        return new ToDo(description);
    }

    /**
     * Parses a deadline command and validates its description and date.
     *
     * @param command complete deadline command
     * @return parsed deadline task
     * @throws BobbyException if the description or date is invalid
     */
    private Task parseDeadline(String command) throws BobbyException {
        String deadlineDetails = getCommandArgument(command, COMMAND_DEADLINE);
        Matcher markerMatcher = DEADLINE_DATE_MARKER.matcher(deadlineDetails);
        if (!markerMatcher.find()) {
            if (deadlineDetails.isBlank()) {
                throw new BobbyException("Error! The description of a deadline cannot be empty!");
            }
            throw new BobbyException("Error! The date of a deadline cannot be empty!");
        }

        int markerStart = markerMatcher.start();
        int markerEnd = markerMatcher.end();
        if (markerMatcher.find()) {
            throw new BobbyException("Error! The /by parameter can only be specified once!");
        }

        String description = normalizeWhitespace(deadlineDetails.substring(0, markerStart));
        String dueDate = normalizeWhitespace(deadlineDetails.substring(markerEnd));
        if (description.isEmpty()) {
            throw new BobbyException("Error! The description of a deadline cannot be empty!");
        } else if (dueDate.isEmpty()) {
            throw new BobbyException("Error! The date of a deadline cannot be empty!");
        }

        validateStorageField(description);
        validateStorageField(dueDate);
        try {
            return Deadline.fromInput(description, dueDate);
        } catch (DateTimeParseException exception) {
            throw new BobbyException("Error! The deadline must be a valid date. "
                    + "Use yyyy-MM-dd or yyyy-MM-dd HHmm.");
        }
    }

    /**
     * Parses an event command and validates its description, start, and end dates.
     *
     * @param command complete event command
     * @return parsed event task
     * @throws BobbyException if any event field is empty, invalid, or unsafe for storage
     */
    private Task parseEvent(String command) throws BobbyException {
        String eventDetails = getCommandArgument(command, COMMAND_EVENT);
        Matcher fromMatcher = EVENT_FROM_MARKER.matcher(eventDetails);
        Matcher toMatcher = EVENT_TO_MARKER.matcher(eventDetails);
        boolean hasFromMarker = fromMatcher.find();
        boolean hasToMarker = toMatcher.find();
        int fromMarkerStart = hasFromMarker ? fromMatcher.start() : -1;
        int fromMarkerEnd = hasFromMarker ? fromMatcher.end() : -1;
        int toMarkerStart = hasToMarker ? toMatcher.start() : -1;
        int toMarkerEnd = hasToMarker ? toMatcher.end() : -1;

        if (hasFromMarker && fromMatcher.find()) {
            throw new BobbyException("Error! The /from parameter can only be specified once!");
        } else if (hasToMarker && toMatcher.find()) {
            throw new BobbyException("Error! The /to parameter can only be specified once!");
        }

        if (hasFromMarker && hasToMarker && toMarkerStart < fromMarkerStart) {
            throw new BobbyException("Error! Event parameters must place /from before /to.");
        }

        int descriptionEnd = hasFromMarker ? fromMarkerStart
                : hasToMarker ? toMarkerStart : eventDetails.length();
        String description = normalizeWhitespace(eventDetails.substring(0, descriptionEnd));
        String from = hasFromMarker
                ? normalizeWhitespace(eventDetails.substring(fromMarkerEnd,
                        hasToMarker ? toMarkerStart : eventDetails.length())) : "";
        String to = hasToMarker
                ? normalizeWhitespace(eventDetails.substring(toMarkerEnd)) : "";

        if (description.isEmpty()) {
            throw new BobbyException("Error! The description of an event cannot be empty!");
        } else if (from.isEmpty()) {
            throw new BobbyException("Error! Start date of an event cannot be empty. Try again!");
        } else if (to.isEmpty()) {
            throw new BobbyException("Error! End date of an event cannot be empty. Try again!");
        }

        validateStorageField(description);
        validateStorageField(from);
        validateStorageField(to);
        try {
            return Event.fromInput(description, from, to);
        } catch (DateTimeParseException exception) {
            throw new BobbyException("Error! Event dates must be valid dates. Use yyyy-MM-dd.");
        } catch (IllegalArgumentException exception) {
            throw new BobbyException("Error! An event cannot end before it starts.");
        }
    }

    /** Creates a pattern that recognises a parameter only as a whitespace-delimited token. */
    private static Pattern createMarkerPattern(String marker) {
        return Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?=\\s|$)");
    }

    /** Trims a field and collapses all runs of whitespace to a single space. */
    private static String normalizeWhitespace(String input) {
        return input.strip().replaceAll("\\s+", " ");
    }

    /**
     * Rejects storage separator characters that would make a saved line ambiguous.
     *
     * @param field task field to validate
     * @throws BobbyException if {@code field} contains the storage separator
     */
    private void validateStorageField(String field) throws BobbyException {
        if (field.contains("|")) {
            throw new BobbyException("Error! Task details cannot contain the '|' character!");
        }
        for (int i = 0; i < field.length(); i++) {
            if (Character.isISOControl(field.charAt(i))) {
                throw new BobbyException("Error! Task details cannot contain control characters!");
            }
        }
    }
}
