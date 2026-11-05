package larp;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Interprets user input and converts it into task data.
 */
public class Parser {
    private static final String FIND_COMMAND = "find";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String DEADLINE_SEPARATOR = "(?i)\\s*/by\\s*";
    private static final String EVENT_START_SEPARATOR = "(?i)\\s*/from\\s*";
    private static final String EVENT_END_SEPARATOR = "(?i)\\s*/to\\s*";

    private Parser() {
    }

    /**
     * Returns the keyword specified by a find command.
     *
     * @param input Full user input.
     * @return Keyword to search for.
     * @throws LarpException If the keyword is missing.
     */
    public static String parseFindKeyword(String input) throws LarpException {
        String keyword = getArguments(input, FIND_COMMAND);
        if (keyword.isEmpty()) {
            throw new LarpException("Please provide a keyword after 'find'.");
        }
        return validateText(keyword, "Search keywords");
    }

    /**
     * Returns the zero-based task index specified by a command.
     *
     * @param input Full user input.
     * @param command Command whose task number is being parsed.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based index of the selected task.
     * @throws LarpException If the task number is missing or invalid.
     */
    public static int parseTaskIndex(String input, String command, int taskCount)
            throws LarpException {
        String indexText = input.substring(command.length()).trim();
        if (indexText.isEmpty()) {
            throw new LarpException("Please provide a task number after '" + command + "'.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            throw new LarpException("The task number must be a whole number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new LarpException("That task number is outside your task list.");
        }
        return taskNumber - 1;
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     *
     * @param input Full user input.
     * @return Task described by the command.
     * @throws LarpException If the command or its arguments are invalid.
     */
    public static Task parseTask(String input) throws LarpException {
        if (isCommand(input, TODO_COMMAND)) {
            return parseTodo(input);
        }
        if (isCommand(input, DEADLINE_COMMAND)) {
            return parseDeadline(input);
        }
        if (isCommand(input, EVENT_COMMAND)) {
            return parseEvent(input);
        }

        throw new LarpException("I don't recognize that command.");
    }

    /**
     * Creates a todo from a validated todo command prefix.
     *
     * @param input Full user input.
     * @return Todo described by the command.
     * @throws LarpException If the description is missing.
     */
    private static Todo parseTodo(String input) throws LarpException {
        String description = getArguments(input, TODO_COMMAND);
        if (description.isEmpty()) {
            throw new LarpException("The description of a todo cannot be empty.");
        }
        return new Todo(validateText(description, "Task descriptions"));
    }

    /**
     * Creates a deadline from a validated deadline command prefix.
     *
     * @param input Full user input.
     * @return Deadline described by the command.
     * @throws LarpException If the description or date is invalid.
     */
    private static Deadline parseDeadline(String input) throws LarpException {
        String[] parts = getArguments(input, DEADLINE_COMMAND).split(DEADLINE_SEPARATOR, -1);
        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new LarpException("Use: deadline DESCRIPTION /by yyyy-MM-dd.");
        }
        try {
            LocalDate by = LocalDate.parse(parts[1].trim());
            return new Deadline(validateText(parts[0].trim(), "Task descriptions"), by);
        } catch (DateTimeParseException e) {
            throw new LarpException("Use a valid deadline date in yyyy-MM-dd format.");
        }
    }

    /**
     * Creates an event from a validated event command prefix.
     *
     * @param input Full user input.
     * @return Event described by the command.
     * @throws LarpException If its description or time range is invalid.
     */
    private static Event parseEvent(String input) throws LarpException {
        String[] eventParts = getArguments(input, EVENT_COMMAND).split(EVENT_START_SEPARATOR, -1);
        if (eventParts.length != 2 || eventParts[0].isBlank()) {
            throw new LarpException("Use: event DESCRIPTION /from START /to END.");
        }

        String[] timeParts = eventParts[1].split(EVENT_END_SEPARATOR, -1);
        if (timeParts.length != 2 || timeParts[0].isBlank() || timeParts[1].isBlank()) {
            throw new LarpException("Use: event DESCRIPTION /from START /to END.");
        }
        String description = validateText(eventParts[0].trim(), "Task descriptions");
        String start = validateText(timeParts[0].trim(), "Event times");
        String end = validateText(timeParts[1].trim(), "Event times");
        return new Event(description, start, end);
    }

    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    private static String getArguments(String input, String command) {
        return input.substring(command.length()).trim();
    }

    /**
     * Rejects text that cannot be stored safely in Larp's line-based data format.
     *
     * @param text Text supplied by the user.
     * @param fieldName User-facing name of the field being validated.
     * @return The validated text.
     * @throws LarpException If the text contains a reserved character or line break.
     */
    private static String validateText(String text, String fieldName) throws LarpException {
        if (text.contains("|") || text.contains("\n") || text.contains("\r")) {
            throw new LarpException(fieldName + " cannot contain '|' or line breaks.");
        }
        return text;
    }
}
