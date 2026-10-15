package alexa;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Interprets command text and creates the tasks or values requested by a user.
 */
public class Parser {
    /** Delimiters that separate task descriptions from their date details. */
    private static final String DEADLINE_DATE_MARKER = "\\s+/by\\s+";
    private static final String EVENT_START_DATE_MARKER = "\\s+/from\\s+";
    private static final String EVENT_END_DATE_MARKER = "\\s+/to\\s+";

    /**
     * Returns whether the input contains a command followed by whitespace or nothing.
     *
     * @param command The complete user command.
     * @param commandWord The command word to check.
     * @return Whether the command word matches.
     */
    public boolean isCommand(String command, String commandWord) {
        return command.equals(commandWord)
                || (command.startsWith(commandWord)
                && command.length() > commandWord.length()
                && Character.isWhitespace(command.charAt(commandWord.length())));
    }

    /**
     * Returns the text entered after a command word.
     *
     * @param command The complete user command.
     * @param commandWord The command word at the start of the command.
     * @return The trimmed command argument.
     */
    public String getArgument(String command, String commandWord) {
        return command.substring(commandWord.length()).trim();
    }

    /** Parses the arguments for a to-do command. */
    public Todo parseTodo(String details) throws AlexaException {
        return new Todo(requireDescription(details, "todo"));
    }

    /** Parses and validates the keyword for a find command. */
    public String parseFindKeyword(String keyword) throws AlexaException {
        if (keyword.trim().isEmpty()) {
            throw new AlexaException("The keyword for a find command cannot be empty.");
        }
        return keyword.trim();
    }

    /** Parses the arguments for a deadline command. */
    public Deadline parseDeadline(String details) throws AlexaException {
        String[] parts = details.split(DEADLINE_DATE_MARKER, -1);
        if (parts.length < 2) {
            throw new AlexaException("A deadline needs a description and date: deadline DESCRIPTION /by yyyy-MM-dd.");
        }
        if (parts.length > 2) {
            throw new AlexaException("A deadline can contain /by only once.");
        }
        return new Deadline(requireDescription(parts[0], "deadline"), parseDate(parts[1], "deadline date"));
    }

    /** Parses the arguments for an event command. */
    public Event parseEvent(String details) throws AlexaException {
        String[] fromParts = details.split(EVENT_START_DATE_MARKER, -1);
        if (fromParts.length < 2) {
            throw new AlexaException("An event needs a description, start date, and end date: "
                    + "event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd.");
        }
        if (fromParts.length > 2) {
            throw new AlexaException("An event can contain /from only once.");
        }
        String[] toParts = fromParts[1].split(EVENT_END_DATE_MARKER, -1);
        if (toParts.length < 2) {
            throw new AlexaException("An event needs an end date after /to.");
        }
        if (toParts.length > 2) {
            throw new AlexaException("An event can contain /to only once.");
        }
        LocalDate startDate = parseDate(toParts[0], "event start date");
        LocalDate endDate = parseDate(toParts[1], "event end date");
        if (!startDate.isBefore(endDate)) {
            throw new AlexaException("The event end date must be later than the start date.");
        }
        return new Event(requireDescription(fromParts[0], "event"), startDate, endDate);
    }

    /** Parses and validates a one-based task number for a command. */
    public int parseTaskNumber(String numberText, String command, int taskCount) throws AlexaException {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText.trim());
        } catch (NumberFormatException exception) {
            throw new AlexaException("Please provide a task number, for example: " + command + " 1.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new AlexaException("There is no task " + taskNumber + ". Use list to see the task numbers.");
        }
        return taskNumber;
    }

    /** Parses a date in the required {@code yyyy-MM-dd} command format. */
    private LocalDate parseDate(String dateText, String dateName) throws AlexaException {
        try {
            return LocalDate.parse(requireValue(dateText, dateName));
        } catch (DateTimeParseException exception) {
            throw new AlexaException("The " + dateName + " must use yyyy-MM-dd, for example 2019-10-15.");
        }
    }

    /** Requires a non-empty task description. */
    private String requireDescription(String description, String taskType) throws AlexaException {
        String trimmedDescription = description.trim();
        if (trimmedDescription.isEmpty()) {
            throw new AlexaException("The description of a " + taskType + " cannot be empty.");
        }
        if (trimmedDescription.contains("|")) {
            throw new AlexaException("A task description cannot contain the | character.");
        }
        return trimmedDescription;
    }

    /** Requires a non-empty value for a task detail such as a deadline date. */
    private String requireValue(String value, String valueName) throws AlexaException {
        if (value.trim().isEmpty()) {
            throw new AlexaException("The " + valueName + " cannot be empty.");
        }
        return value.trim();
    }
}
