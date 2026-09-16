package nova.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import nova.exception.NovaException;

/**
 * Parses user input into commands and task-related information.
 * Provides helper methods for extracting descriptions, task numbers,
 * and date-time values from user commands.
 */
public class Parser {

    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String FIND_COMMAND = "find";

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm");

    /**
     * Extracts the command word from the user's input.
     *
     * @param input full command entered by the user
     * @return the first word of the command, or an empty string if the input is blank
     */
    public static String getCommandWord(String input) {
        String trimmedInput = input.trim();

        if (trimmedInput.isEmpty()) {
            return "";
        }

        return trimmedInput.split(" ", 2)[0];
    }

    /**
     * Extracts the description from a todo command.
     *
     * @param input full todo command entered by the user
     * @return the todo description
     * @throws NovaException if the todo description is missing
     */
    public static String parseTodoDescription(String input)
            throws NovaException {

        String description =
                input.substring(TODO_COMMAND.length()).trim();

        if (description.isEmpty()) {
            throw new NovaException(
                    "Your todo description is missing!\n"
                            + "Example: todo read book"
            );
        }

        return description;
    }

    /**
     * Extracts the description and deadline date-time from a deadline command.
     *
     * @param input full deadline command entered by the user
     * @return an array containing the task description at index 0
     *         and the deadline date-time string at index 1
     * @throws NovaException if the description or /by information is missing
     */
    public static String[] parseDeadlineDetails(String input)
            throws NovaException {

        String details =
                input.substring(DEADLINE_COMMAND.length()).trim();

        if (details.isEmpty()) {
            throw new NovaException(
                    "Your deadline description is missing!\n"
                            + "Example: deadline return book /by 2/12/2019 1800"
            );
        }

        if (!details.contains(" /by ")) {
            throw new NovaException(
                    "Your deadline is missing '/by'.\n"
                            + "Example: deadline return book /by 2/12/2019 1800"
            );
        }

        String[] parts = details.split(" /by ", 2);

        String description = parts[0].trim();
        String byString = parts[1].trim();

        if (description.isEmpty()) {
            throw new NovaException(
                    "Your deadline description is missing!"
            );
        }

        if (byString.isEmpty()) {
            throw new NovaException(
                    "Your deadline date and time are missing!"
            );
        }

        return new String[] {description, byString};
    }

    /**
     * Extracts the description, start time, and end time from an event command.
     *
     * @param input full event command entered by the user
     * @return an array containing the description at index 0,
     *         start date-time at index 1, and end date-time at index 2
     * @throws NovaException if any required event information is missing
     */
    public static String[] parseEventDetails(String input)
            throws NovaException {

        String details =
                input.substring(EVENT_COMMAND.length()).trim();

        if (details.isEmpty()) {
            throw new NovaException(
                    "Your event description is missing!\n"
                            + "Example: event project meeting "
                            + "/from 19/8/2026 1400 /to 19/8/2026 1600"
            );
        }

        if (!details.contains(" /from ")) {
            throw new NovaException(
                    "Your event is missing '/from'."
            );
        }

        if (!details.contains(" /to ")) {
            throw new NovaException(
                    "Your event is missing '/to'."
            );
        }

        String[] fromParts = details.split(" /from ", 2);

        String description = fromParts[0].trim();

        String[] toParts = fromParts[1].split(" /to ", 2);

        String fromString = toParts[0].trim();
        String toString = toParts[1].trim();

        if (description.isEmpty()) {
            throw new NovaException(
                    "Your event description is missing!"
            );
        }

        if (fromString.isEmpty()) {
            throw new NovaException(
                    "Your event starting date and time are missing!"
            );
        }

        if (toString.isEmpty()) {
            throw new NovaException(
                    "Your event ending date and time are missing!"
            );
        }

        return new String[] {description, fromString, toString};

    }

    /**
     * Extracts a task number from commands such as mark, unmark, or delete.
     *
     * @param input full command entered by the user
     * @param commandLength number of characters in the command word
     * @return the task number entered by the user
     * @throws NovaException if the task number is missing or is not a valid integer
     */
    public static int parseTaskNumber(String input, int commandLength)
            throws NovaException {

        String numberString =
                input.substring(commandLength).trim();

        if (numberString.isEmpty()) {
            throw new NovaException(
                    "You forgot to provide a task number."
            );
        }

        try {
            return Integer.parseInt(numberString);

        } catch (NumberFormatException e) {
            throw new NovaException(
                    "The task number must be a valid number."
            );
        }
    }

    /**
     * Converts a date-time string into a LocalDateTime object.
     *
     * @param dateTimeString date and time in the format d/M/yyyy HHmm
     * @return parsed LocalDateTime value
     * @throws NovaException if the date-time string is not in the expected format
     */
    public static LocalDateTime parseDateTime(String dateTimeString)
            throws NovaException {

        try {
            return LocalDateTime.parse(
                    dateTimeString,
                    DATE_TIME_FORMAT
            );

        } catch (DateTimeParseException e) {
            throw new NovaException(
                    "NOVA couldn't understand that date and time.\n"
                            + "Use this format: 2/12/2019 1800"
            );
        }
    }

    /**
     * Extracts the search keyword from a find command.
     *
     * @param input full find command entered by the user
     * @return keyword to search for
     * @throws NovaException if no keyword is provided
     */
    public static String parseFindKeyword(String input)
            throws NovaException {

        String keyword =
                input.substring(FIND_COMMAND.length()).trim();

        if (keyword.isEmpty()) {
            throw new NovaException(
                    "You forgot to tell NOVA what to find.\n"
                            + "Example: find book"
            );
        }

        return keyword;
    }
}

