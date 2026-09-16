package rambo.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import rambo.exception.RamboException;
import rambo.task.Task;

/**
 * Interprets and validates input entered by the user.
 */
public class Parser {
    /**
     * Parses a command entered at the main menu.
     *
     * @param input user input to parse
     * @return the character identifying the selected command
     * @throws RamboException if the input does not identify a command
     */
    public char parseCommand(String input) throws RamboException {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new RamboException("That option doesn't exist, my friend! Try again!");
        }

        if (trimmedInput.equalsIgnoreCase("bye") || trimmedInput.equalsIgnoreCase("q")) {
            return 'q';
        }

        if (trimmedInput.startsWith("3 ")) {
            return '3';
        }

        if (trimmedInput.length() != 1) {
            throw new RamboException("That option doesn't exist, my friend! Try again!");
        }

        char command = trimmedInput.charAt(0);
        if (command != '1' && command != '2' && command != '3'
                && command != '4' && command != '5' && command != '6' && command != 'q') {
            throw new RamboException("That option doesn't exist, my friend! Try again!");
        }
        return command;
    }

    /**
     * Parses the number identifying a type of task.
     *
     * @param input user input to parse
     * @return the task type number
     * @throws RamboException if the input is not a number
     */
    public int parseTaskType(String input) throws RamboException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new RamboException("Give me a valid task type number!", e);
        }
    }

    /**
     * Parses a one-based task number.
     *
     * @param input user input to parse
     * @return the task number
     * @throws RamboException if the input is not a number
     */
    public int parseTaskNumber(String input) throws RamboException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new RamboException("Give me a valid number!", e);
        }
    }

    /**
     * Parses and validates a task priority level.
     *
     * @param input user input to parse
     * @return a priority level from 1 (highest) to 3 (lowest)
     * @throws RamboException if the input is not a supported priority level
     */
    public int parsePriorityLevel(String input) throws RamboException {
        int priorityLevel;
        try {
            priorityLevel = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new RamboException("Give me a valid priority level from 1 to 3!", e);
        }

        Task.validatePriorityLevel(priorityLevel);
        return priorityLevel;
    }

    /**
     * Extracts an optional search term from a list command.
     *
     * @param input complete list command
     * @return the search term, or an empty string if none was supplied
     */
    public String parseSearchTerm(String input) {
        assert input != null : "A parsed list command should not be null";
        assert !input.isEmpty() && input.trim().charAt(0) == '3'
                : "A search term should only be extracted from a list command";
        return input.trim().substring(1).trim();
    }

    /**
     * Parses a date in YYYY-MM-DD format.
     *
     * @param date date text to parse
     * @return the parsed date
     * @throws RamboException if the date is not in the required format
     */
    public static LocalDate parseDate(String date) throws RamboException {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new RamboException("Please make sure your date is the following format (YYYY-MM-DD)");
        }
    }
}
