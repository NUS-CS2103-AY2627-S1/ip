package slotbot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import slotbot.task.Deadline;
import slotbot.task.Event;
import slotbot.task.Task;
import slotbot.task.Todo;

/**
 * Interprets user input as SlotBot commands and task details.
 */
public final class Parser {
    private static final String DEADLINE_SEPARATOR_REGEX = "\\s+/by\\s+";
    private static final String EVENT_START_SEPARATOR_REGEX = "\\s+/from\\s+";
    private static final String EVENT_END_SEPARATOR_REGEX = "\\s+/to\\s+";
    private static final DateTimeFormatter EVENT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    private Parser() {
    }

    /**
     * Returns the command type represented by the user input.
     *
     * @param userInput Command entered by the user.
     * @return Matching command type, or UNKNOWN if there is no match.
     */
    public static CommandType parseCommandType(String userInput) {
        String[] commandParts = userInput.trim().split("\\s+", 2);
        return CommandType.getCommandType(commandParts[0]);
    }

    /**
     * Parses a task number and returns its zero-based index.
     *
     * @param userInput Command containing the task number.
     * @param taskCount Number of tasks in the list.
     * @return Zero-based index of the selected task.
     * @throws SlotBotException If the task number is missing, invalid, or out of range.
     */
    public static int parseTaskNumber(String userInput, int taskCount) throws SlotBotException {
        String[] commandParts = userInput.trim().split("\\s+", 2);
        String command = commandParts[0];

        if (commandParts.length < 2) {
            throw new SlotBotException("Please provide a task number.\n"
                    + "Use: " + command + " [NUMBER]");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(commandParts[1]);
        } catch (NumberFormatException e) {
            throw new SlotBotException("Please enter a whole number for the task number.\n"
                    + "Use: " + command + " [NUMBER]");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new SlotBotException("That task number does not exist.\n"
                    + "Use: " + command + " [NUMBER]");
        }

        return taskNumber - 1;
    }

    /**
     * Returns the search text from a find command.
     *
     * @param userInput Find command entered by the user.
     * @return Non-blank search text.
     * @throws SlotBotException If the search text is missing or blank.
     */
    public static String parseFindKeyword(String userInput) throws SlotBotException {
        String[] commandParts = userInput.trim().split("\\s+", 2);

        if (commandParts.length < 2 || commandParts[1].isBlank()) {
            throw new SlotBotException("Please provide a keyword.\n"
                    + "Use: find KEYWORD");
        }

        return commandParts[1];
    }

    /**
     * Creates a task from a user command.
     *
     * @param userInput Command entered by the user.
     * @param commandType Type of the entered command.
     * @return Task created from the valid command.
     * @throws SlotBotException If the command or its arguments are invalid.
     */
    public static Task parseTask(String userInput, CommandType commandType) throws SlotBotException {
        String taskDetails = extractTaskDetails(userInput);

        return switch (commandType) {
            case TODO -> parseTodo(taskDetails);
            case DEADLINE -> parseDeadline(taskDetails);
            case EVENT -> parseEvent(taskDetails);
            default -> throw new SlotBotException("I don't recognise that command.\n"
                    + "Try: todo DESCRIPTION, deadline DESCRIPTION /by DATE,\n"
                    + "event DESCRIPTION /from START /to END, list, find KEYWORD,\n"
                    + "reminders, mark [NUMBER], unmark [NUMBER], delete [NUMBER],\n"
                    + "or bye.");
        };
    }

    /**
     * Returns the part of a task command after its command word.
     */
    private static String extractTaskDetails(String userInput) {
        String[] commandParts = userInput.trim().split("\\s+", 2);
        return commandParts.length < 2 ? "" : commandParts[1];
    }

    /**
     * Creates a todo from its validated description.
     */
    private static Todo parseTodo(String taskDetails) throws SlotBotException {
        if (taskDetails.isBlank()) {
            throw new SlotBotException("The description of a todo cannot be empty.\n"
                    + "Use: todo DESCRIPTION");
        }

        validateDescription(taskDetails);
        return new Todo(taskDetails);
    }

    /**
     * Creates a deadline from its description and date fields.
     */
    private static Deadline parseDeadline(String taskDetails) throws SlotBotException {
        if (taskDetails.isBlank()) {
            throw new SlotBotException("The description of a deadline cannot be empty.\n"
                    + "Use: deadline DESCRIPTION /by DATE");
        }

        String usage = "Use: deadline DESCRIPTION /by DATE";
        String[] fields = splitRequiredFields(taskDetails, DEADLINE_SEPARATOR_REGEX, usage);
        String description = fields[0];
        validateDescription(description);

        try {
            LocalDate by = LocalDate.parse(fields[1]);
            return new Deadline(description, by);
        } catch (DateTimeParseException e) {
            throw new SlotBotException("The deadline date is invalid.\n"
                    + "Use: deadline DESCRIPTION /by yyyy-MM-dd");
        }
    }

    /**
     * Creates an event from its description and time-range fields.
     */
    private static Event parseEvent(String taskDetails) throws SlotBotException {
        if (taskDetails.isBlank()) {
            throw new SlotBotException("The description of an event cannot be empty.\n"
                    + "Use: event DESCRIPTION /from START /to END");
        }

        String usage = "Use: event DESCRIPTION /from START /to END";
        String[] eventFields = splitRequiredFields(taskDetails, EVENT_START_SEPARATOR_REGEX, usage);
        String description = eventFields[0];
        validateDescription(description);
        String[] timeFields = splitRequiredFields(eventFields[1], EVENT_END_SEPARATOR_REGEX, usage);
        return createEvent(description, timeFields);
    }

    /**
     * Splits two required fields and rejects missing or repeated separators.
     */
    private static String[] splitRequiredFields(String text, String separatorRegex, String usage)
            throws SlotBotException {
        String[] fields = text.split(separatorRegex, -1);
        if (fields.length != 2 || fields[0].isBlank() || fields[1].isBlank()) {
            throw new SlotBotException(usage);
        }
        return fields;
    }

    /**
     * Creates an event after parsing and validating its start and end times.
     */
    private static Event createEvent(String description, String[] timeFields) throws SlotBotException {
        try {
            LocalDateTime from = LocalDateTime.parse(timeFields[0], EVENT_DATE_TIME_FORMATTER);
            LocalDateTime to = LocalDateTime.parse(timeFields[1], EVENT_DATE_TIME_FORMATTER);
            if (!from.isBefore(to)) {
                throw new SlotBotException("The event start time must be before its end time.\n"
                        + "Use: event DESCRIPTION /from START /to END");
            }
            return new Event(description, from, to);
        } catch (DateTimeParseException e) {
            throw new SlotBotException("The event date or time is invalid.\n"
                    + "Use: event DESCRIPTION /from yyyy-MM-dd HH:mm /to yyyy-MM-dd HH:mm");
        }
    }

    /**
     * Checks that a task description can be saved without corrupting its record.
     *
     * @param description Task description to validate.
     * @throws SlotBotException If the description contains the storage separator.
     */
    private static void validateDescription(String description) throws SlotBotException {
        if (description.contains("|")) {
            throw new SlotBotException("Task descriptions cannot contain the | character.");
        }
    }
}
