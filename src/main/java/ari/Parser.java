package ari;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ari.exception.EmptyArgumentException;
import ari.task.DeadlineTask;
import ari.task.EventTask;
import ari.task.Task;
import ari.task.TodoTask;

/**
 * Converts raw user input and saved records into domain values.
 */
public class Parser {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /**
     * Identifies the command represented by the user's input.
     *
     * @param input Raw user input.
     * @return Matching command type, or UNKNOWN when none matches.
     */
    public static CommandType parseCommandType(String input) {
        if (input == null || input.isBlank()) {
            return CommandType.UNKNOWN;
        }

        String keyword = input.strip().split("\\s+", 2)[0];
        try {
            return CommandType.valueOf(keyword.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Enforces fixed command arity and prevents multiline records.
     *
     * @param input Raw user input.
     * @param command Parsed command type.
     * @throws IllegalArgumentException If command arguments are invalid.
     */
    public static void validateCommand(String input, CommandType command) {
        if (input == null) {
            return;
        }
        if (input.indexOf('\n') >= 0 || input.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("Enter one command on a single line.");
        }
        if (command == CommandType.LIST || command == CommandType.SORT
                || command == CommandType.BYE || command == CommandType.EXIT) {
            if (input.strip().split("\\s+").length != 1) {
                throw new IllegalArgumentException("Use '" + command.name().toLowerCase(Locale.ROOT)
                        + "' without any arguments.");
            }
        }
    }

    /**
     * Creates the task described by a task-creation command.
     *
     * @param input Raw user input.
     * @param commandType Type of task-creation command.
     * @return Task represented by the command.
     * @throws EmptyArgumentException If a required task field is empty.
     */
    public static Task parseTask(String input, CommandType commandType)
            throws EmptyArgumentException {
        if (input.contains("|") || input.contains("\n") || input.contains("\r")) {
            throw new IllegalArgumentException("Task fields cannot contain '|' or line breaks.");
        }
        switch (commandType) {
            case TODO:
                return new TodoTask(parseTodoDescription(input));
            case DEADLINE:
                return parseDeadlineTask(input);
            case EVENT:
                return parseEventTask(input);
            default:
                throw new IllegalArgumentException("Command does not create a task");
        }
    }

    /**
     * Extracts the search keyword from a find command.
     *
     * @param input Raw find command.
     * @return Search keyword, with surrounding whitespace removed.
     * @throws EmptyArgumentException If the command has no search keyword.
     */
    public static String parseFindKeyword(String input) throws EmptyArgumentException {
        String[] commandParts = input.strip().split("\\s+", 2);
        if (commandParts.length < 2 || commandParts[1].isBlank()) {
            throw new EmptyArgumentException("finding task");
        }
        return commandParts[1].strip();
    }

    /**
     * Extracts a task ID from a command.
     *
     * @param input Raw user input.
     * @return Parsed task ID.
     * @throws NumberFormatException If the command does not contain an integer ID.
     */
    public static int parseTaskId(String input) {
        String[] commandParts = input.strip().split("\\s+");
        if (commandParts.length < 2) {
            throw new NumberFormatException("Missing task ID");
        }
        if (commandParts.length != 2) {
            throw new IllegalArgumentException("Enter exactly one task ID, with no extra arguments.");
        }
        return Integer.parseInt(commandParts[1]);
    }

    /**
     * Reconstructs a task from one saved data record.
     *
     * @param line Saved data record.
     * @return Reconstructed task.
     * @throws IllegalArgumentException If the record is invalid.
     */
    public static Task parseStoredTask(String line) {
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3) {
            throw new IllegalArgumentException("Invalid saved task: " + line);
        }
        for (String field : fields) {
            if (field.isBlank()) {
                throw new IllegalArgumentException("Empty field in saved task: " + line);
            }
        }

        String status = fields[1];
        if (!status.equals("0") && !status.equals("1")) {
            throw new IllegalArgumentException("Invalid saved task status: " + status);
        }

        Task task;
        switch (fields[0]) {
            case "T":
                if (fields.length != 3) {
                    throw new IllegalArgumentException("Invalid saved task: " + line);
                }
                task = new TodoTask(fields[2]);
                break;
            case "D":
                if (fields.length != 4) {
                    throw new IllegalArgumentException("Invalid saved task: " + line);
                }
                task = createDeadlineTask(fields[2], fields[3]);
                break;
            case "E":
                if (fields.length != 5) {
                    throw new IllegalArgumentException("Invalid saved task: " + line);
                }
                task = new EventTask(fields[2], fields[3], fields[4]);
                break;
            default:
                throw new IllegalArgumentException("Unknown saved task type: " + fields[0]);
        }

        if (status.equals("1")) {
            task.markTask();
        }
        return task;
    }

    /**
     * Returns the description from a todo command.
     *
     * @param input Raw todo command.
     * @return Todo description.
     * @throws EmptyArgumentException If the todo description is empty.
     */
    private static String parseTodoDescription(String input) throws EmptyArgumentException {
        String[] commandParts = input.strip().split("\\s+", 2);
        if (commandParts.length < 2 || commandParts[1].isBlank()) {
            throw new EmptyArgumentException("todo");
        }
        return commandParts[1].strip();
    }

    /**
     * Returns the deadline task represented by a command.
     *
     * @param input Raw deadline command.
     * @return Deadline task represented by the command.
     * @throws EmptyArgumentException If the description or deadline is empty.
     */
    private static DeadlineTask parseDeadlineTask(String input)
            throws EmptyArgumentException {
        validateSeparators(input, "deadline", "/by");
        String[] commandParts = input.strip().split("\\s+/by\\s+", 2);
        if (commandParts.length < 2 || commandParts[1].isBlank()) {
            throw new EmptyArgumentException("deadline");
        }

        String[] descriptionParts = commandParts[0].split("\\s+", 2);
        if (descriptionParts.length < 2 || descriptionParts[1].isBlank()) {
            throw new EmptyArgumentException("deadline");
        }
        validateIsoDate(commandParts[1].strip());
        return createDeadlineTask(
                descriptionParts[1].strip(),
                commandParts[1].strip()
        );
    }

    /**
     * Creates a deadline task containing a date when the deadline uses ISO format.
     *
     * @param description Deadline task description.
     * @param deadlineText Date or free-form deadline text.
     * @return Deadline task containing the appropriate deadline representation.
     */
    private static DeadlineTask createDeadlineTask(String description, String deadlineText) {
        assert description != null : "Parsed deadline description must not be null";
        assert deadlineText != null : "Parsed deadline text must not be null";
        try {
            LocalDate deadlineDate = LocalDate.parse(deadlineText);
            return new DeadlineTask(description, deadlineDate);
        } catch (DateTimeParseException e) {
            return new DeadlineTask(description, deadlineText);
        }
    }

    /**
     * Returns the event task represented by a command.
     *
     * @param input Raw event command.
     * @return Event task represented by the command.
     * @throws EmptyArgumentException If the description, start time, or end time is empty.
     */
    private static EventTask parseEventTask(String input) throws EmptyArgumentException {
        validateSeparators(input, "event", "/from", "/to");
        String[] commandParts = input.strip().split("\\s+/from\\s+", 2);
        if (commandParts.length < 2) {
            throw new EmptyArgumentException("event");
        }

        String[] descriptionParts = commandParts[0].split("\\s+", 2);
        String[] timeParts = commandParts[1].split("\\s+/to\\s+", 2);
        boolean hasDescription = descriptionParts.length == 2 && !descriptionParts[1].isBlank();
        boolean hasTimes = timeParts.length == 2
                && !timeParts[0].isBlank()
                && !timeParts[1].isBlank();
        if (!hasDescription || !hasTimes) {
            throw new EmptyArgumentException("event");
        }

        LocalDate fromDate = validateIsoDate(timeParts[0].strip());
        LocalDate toDate = validateIsoDate(timeParts[1].strip());
        if (fromDate != null && toDate != null && !toDate.isAfter(fromDate)) {
            throw new IllegalArgumentException("The event end date must be after its start date.");
        }

        return new EventTask(
                descriptionParts[1].strip(),
                timeParts[0].strip(),
                timeParts[1].strip()
        );
    }

    /** Requires reserved separator tokens exactly once and in their documented order. */
    private static void validateSeparators(String input, String taskType, String... expected)
            throws EmptyArgumentException {
        Matcher matcher = Pattern.compile("(?<!\\S)/(?:by|from|to)(?!\\S)").matcher(input);
        int index = 0;
        while (matcher.find()) {
            if (index >= expected.length || !matcher.group().equals(expected[index])) {
                throw new IllegalArgumentException("Use each " + taskType + " separator once, in the correct order.");
            }
            index++;
        }
        if (index != expected.length) {
            throw new EmptyArgumentException(taskType);
        }
    }

    /** Validates ISO-shaped new input without changing legacy stored free-form values. */
    private static LocalDate validateIsoDate(String text) {
        if (!text.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            return null;
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid calendar date: " + text + ". Use a real YYYY-MM-DD date.");
        }
    }

    /**
     * Returns a formatted date when the input uses ISO format, or the original input otherwise.
     *
     * @param input Date or free-form date and time text.
     * @return Formatted date or the original input.
     */
    public static String parseDateTime(String input) {
        try {
            LocalDate date = LocalDate.parse(input);
            return date.format(DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return input;
        }
    }
}
