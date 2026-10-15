package trackie.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import trackie.exception.TrackieException;
import trackie.task.Deadline;
import trackie.task.Event;
import trackie.task.Task;
import trackie.task.Todo;
import trackie.task.WithinPeriod;

/** Converts user commands into validated task data. */
public class Parser {
    private static final char STORAGE_DELIMITER = '|';

    private Parser() {
    }

    /**
     * Extracts the first word that identifies a command.
     *
     * @param command full command entered by the user
     * @return command word, or an empty string for blank input
     */
    public static String getCommandWord(String command) {
        String trimmedCommand = command.trim();
        int firstSpace = trimmedCommand.indexOf(' ');
        return firstSpace < 0 ? trimmedCommand : trimmedCommand.substring(0, firstSpace);
    }

    /**
     * Parses and validates the task number in a mark, unmark, or delete command.
     *
     * @param command full command entered by the user
     * @param commandName name of the command being parsed
     * @param taskCount number of tasks currently stored
     * @return zero-based index of the selected task
     * @throws TrackieException if the number is missing, invalid, or out of range
     */
    public static int parseTaskIndex(String command, String commandName, int taskCount)
            throws TrackieException {
        if (taskCount == 0) {
            throw new TrackieException("Oops! There are no tasks to " + commandName + ".");
        }
        String numberText = command.substring(commandName.length()).trim();
        if (numberText.isEmpty()) {
            throw new TrackieException("Oops! Please specify a task number after " + commandName + ".");
        }
        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new TrackieException("Oops! Choose a task number between 1 and " + taskCount + ".");
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new TrackieException("Oops! The task number must be a whole number.");
        }
    }

    /**
     * Parses and validates a todo command.
     *
     * @param command full todo command
     * @return todo represented by the command
     * @throws TrackieException if the description is missing
     */
    public static Task parseTodo(String command) throws TrackieException {
        String description = command.substring("todo".length()).trim();
        validateDescription(description, "A todo needs a description.");
        return new Todo(description);
    }

    /**
     * Parses and validates a deadline command and its ISO date.
     *
     * @param command full deadline command
     * @return deadline represented by the command
     * @throws TrackieException if required data is missing or the date is invalid
     */
    public static Task parseDeadline(String command) throws TrackieException {
        int byIndex = command.indexOf(" /by");
        if (byIndex < 0 || byIndex != command.lastIndexOf(" /by")) {
            throw new TrackieException("Oops! Use: deadline DESCRIPTION /by DATE");
        }
        String description = command.substring("deadline".length(), byIndex).trim();
        String by = command.substring(byIndex + " /by".length()).trim();
        validateDescription(description, "A deadline needs a description.");
        if (by.isEmpty()) {
            throw new TrackieException("Oops! A deadline needs a due date after /by.");
        }
        LocalDate deadlineDate = parseIsoDate(by,
                "Oops! Use a deadline date in yyyy-MM-dd format.",
                "Oops! Enter a valid deadline date.");
        return new Deadline(description, deadlineDate);
    }

    /**
     * Parses and validates an event command and its two ISO dates.
     *
     * @param command full event command
     * @return event represented by the command
     * @throws TrackieException if required data is missing or either date is invalid
     */
    public static Task parseEvent(String command) throws TrackieException {
        int fromIndex = command.indexOf(" /from");
        int toIndex = command.indexOf(" /to");
        boolean hasDuplicateMarker = fromIndex != command.lastIndexOf(" /from")
                || toIndex != command.lastIndexOf(" /to");
        if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex || hasDuplicateMarker) {
            throw new TrackieException(
                    "Oops! Use: event DESCRIPTION /from START_DATE /to END_DATE");
        }
        String description = command.substring("event".length(), fromIndex).trim();
        String from = command.substring(fromIndex + " /from".length(), toIndex).trim();
        String to = command.substring(toIndex + " /to".length()).trim();
        validateDescription(description, "An event needs a description.");
        if (from.isEmpty()) {
            throw new TrackieException("Oops! An event needs a start date after /from.");
        }
        if (to.isEmpty()) {
            throw new TrackieException("Oops! An event needs an end date after /to.");
        }
        LocalDate startDate = parseIsoDate(from,
                "Oops! Use event dates in yyyy-MM-dd format.",
                "Oops! Enter valid event dates.");
        LocalDate endDate = parseIsoDate(to,
                "Oops! Use event dates in yyyy-MM-dd format.",
                "Oops! Enter valid event dates.");
        if (endDate.isBefore(startDate)) {
            throw new TrackieException("Oops! The end date cannot be before the start date.");
        }
        return new Event(description, startDate, endDate);
    }

    /**
     * Parses and validates a within-period command and its two inclusive ISO dates.
     *
     * @param command full within-period command
     * @return within-period task represented by the command
     * @throws TrackieException if required data is missing or either date is invalid
     */
    public static Task parseWithinPeriod(String command) throws TrackieException {
        int fromIndex = command.indexOf(" /from");
        int toIndex = command.indexOf(" /to");
        boolean hasDuplicateMarker = fromIndex != command.lastIndexOf(" /from")
                || toIndex != command.lastIndexOf(" /to");
        if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex || hasDuplicateMarker) {
            throw new TrackieException("Oops! Use: within DESCRIPTION /from START_DATE /to END_DATE");
        }
        String description = command.substring("within".length(), fromIndex).trim();
        String fromText = command.substring(fromIndex + " /from".length(), toIndex).trim();
        String toText = command.substring(toIndex + " /to".length()).trim();
        validateDescription(description, "A within-period task needs a description.");
        if (fromText.isEmpty()) {
            throw new TrackieException("Oops! A within-period task needs a start date after /from.");
        }
        if (toText.isEmpty()) {
            throw new TrackieException("Oops! A within-period task needs an end date after /to.");
        }
        LocalDate from = parseIsoDate(fromText,
                "Oops! Use within-period dates in yyyy-MM-dd format.",
                "Oops! Enter valid within-period dates.");
        LocalDate to = parseIsoDate(toText,
                "Oops! Use within-period dates in yyyy-MM-dd format.",
                "Oops! Enter valid within-period dates.");
        if (to.isBefore(from)) {
            throw new TrackieException("Oops! The end date cannot be before the start date.");
        }
        return new WithinPeriod(description, from, to);
    }

    /**
     * Parses and validates the keyword in a find command.
     *
     * @param command full find command
     * @return keyword to search for
     * @throws TrackieException if the keyword is missing
     */
    public static String parseFindKeyword(String command) throws TrackieException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new TrackieException("Oops! Please specify a keyword after find.");
        }
        return keyword;
    }

    /**
     * Ensures a task description exists and cannot corrupt the storage format.
     *
     * @param description task description to validate
     * @param missingDescriptionMessage detail used when the description is blank
     * @throws TrackieException if the description is blank or contains the storage delimiter
     */
    private static void validateDescription(String description, String missingDescriptionMessage)
            throws TrackieException {
        if (description.isEmpty()) {
            throw new TrackieException("Oops! " + missingDescriptionMessage);
        }
        if (description.indexOf(STORAGE_DELIMITER) >= 0) {
            throw new TrackieException("Oops! Task descriptions cannot contain the | character.");
        }
    }

    /**
     * Parses a date while distinguishing a malformed format from an impossible calendar date.
     *
     * @param dateText date supplied by the user
     * @param formatMessage message used when the text is not in ISO format
     * @param invalidDateMessage message used when an ISO-shaped date does not exist
     * @return parsed date
     * @throws TrackieException if the date cannot be parsed
     */
    private static LocalDate parseIsoDate(String dateText, String formatMessage,
            String invalidDateMessage) throws TrackieException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            boolean hasIsoDateShape = dateText.matches("\\d{4}-\\d{2}-\\d{2}");
            String message = hasIsoDateShape ? invalidDateMessage : formatMessage;
            throw new TrackieException(message);
        }
    }
}
