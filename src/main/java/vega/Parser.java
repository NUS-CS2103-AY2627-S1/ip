package vega;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Converts user input into command names, arguments, and task data.
 */
public class Parser {
    /**
     * Creates a parser for Vega commands.
     */
    public Parser() {
    }

    /**
     * Returns the first word of a command.
     *
     * @param input Full command entered by the user.
     * @return Command word without its arguments.
     */
    public String getCommandWord(String input) {
        String trimmedInput = input.trim();
        int firstSpace = trimmedInput.indexOf(' ');
        return firstSpace < 0 ? trimmedInput : trimmedInput.substring(0, firstSpace);
    }

    /**
     * Returns the text after the command word.
     *
     * @param input Full command entered by the user.
     * @return Command arguments, or an empty string if none were supplied.
     */
    public String getArguments(String input) {
        String trimmedInput = input.trim();
        int firstSpace = trimmedInput.indexOf(' ');
        return firstSpace < 0 ? "" : trimmedInput.substring(firstSpace + 1).trim();
    }

    /**
     * Parses a one-based task number from a command argument.
     *
     * @param argument Text expected to contain a task number.
     * @param commandWord Command name used in the error hint.
     * @return Parsed one-based task number.
     * @throws VegaException If the argument is not an integer.
     */
    public int parseTaskNumber(String argument, String commandWord) throws VegaException {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new VegaException("Please give a valid task number. Try: " + commandWord + " 1");
        }
    }

    /**
     * Parses a deadline argument in the form {@code DESCRIPTION /by yyyy-MM-dd}.
     *
     * @param argument Deadline description and date.
     * @return Parsed deadline task.
     * @throws VegaException If the description, separator, or date is invalid.
     */
    public Deadline parseDeadline(String argument) throws VegaException {
        String[] parts = argument.split(" /by ", 2);
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new VegaException("A deadline needs a description and a /by date. "
                    + "Try: deadline submit report /by 2026-08-31");
        }
        try {
            return new Deadline(parts[0].trim(), LocalDate.parse(parts[1].trim()));
        } catch (DateTimeParseException e) {
            throw new VegaException("Please enter the deadline date as yyyy-MM-dd, for example 2026-08-31.");
        }
    }

    /**
     * Parses an event argument in the form {@code DESCRIPTION /from START /to END}.
     *
     * @param argument Event description, start time, and end time.
     * @return Parsed event task.
     * @throws VegaException If a required event component is missing.
     */
    public Event parseEvent(String argument) throws VegaException {
        String[] fromParts = argument.split(" /from ", 2);
        if (fromParts.length < 2) {
            throw new VegaException("An event needs /from and /to times. "
                    + "Try: event meeting /from 2pm /to 4pm");
        }
        String[] toParts = fromParts[1].split(" /to ", 2);
        if (fromParts[0].isBlank() || toParts.length < 2 || toParts[0].isBlank() || toParts[1].isBlank()) {
            throw new VegaException("An event needs a description, /from time, and /to time.");
        }
        return new Event(fromParts[0].trim(), toParts[0].trim(), toParts[1].trim());
    }
}
