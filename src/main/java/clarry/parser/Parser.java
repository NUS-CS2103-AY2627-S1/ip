package clarry.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import clarry.ClarryException;
import clarry.task.Deadline;
import clarry.task.Event;
import clarry.task.Todo;

/**
 * Parses and validates commands entered by the user.
 */
public class Parser {
    /**
     * Normalizes spaces and tabs while rejecting blank or multiline commands.
     *
     * @param input raw command text
     * @return command with single spaces and no surrounding whitespace
     * @throws ClarryException if the input is blank or contains control characters
     */
    public String normalizeInput(String input) throws ClarryException {
        if (input == null || input.isBlank()) {
            throw new ClarryException("Please enter a command. Type 'help' to see what I understand.");
        }
        if (input.chars().anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw new ClarryException("Please enter one command on a single line without control characters.");
        }
        return input.strip().replaceAll("\\h+", " ");
    }

    /**
     * Returns the type of the command entered by the user.
     *
     * @param input complete command input
     * @return recognized command type, or {@code UNKNOWN}
     */
    public CommandType parseCommandType(String input) {
        return CommandType.fromWord(getCommandWord(input));
    }

    /**
     * Ensures that a command has no arguments or extra text.
     *
     * @param input complete command input
     * @param command expected command word
     * @throws ClarryException if the input is not exactly the command word
     */
    public void requireExactCommand(String input, String command) throws ClarryException {
        if (!input.strip().equals(command)) {
            throwUnknownCommand();
        }
    }

    /**
     * Parses a todo task from a command input.
     *
     * @param input complete todo command
     * @return parsed todo task
     * @throws ClarryException if the description is empty
     */
    public Todo parseTodo(String input) throws ClarryException {
        assert CommandType.fromWord(getCommandWord(input)) == CommandType.TODO
                : "Todo parsing requires a todo command";
        String description = parseArguments(input, "todo");
        if (description.isEmpty()) {
            throw new ClarryException("The description of a todo cannot be empty.");
        }
        validateDescription(description);
        return new Todo(description);
    }

    /**
     * Parses a deadline task from a command input.
     *
     * @param input complete deadline command
     * @return parsed deadline task
     * @throws ClarryException if the command is incomplete or the date is invalid
     */
    public Deadline parseDeadline(String input) throws ClarryException {
        assert CommandType.fromWord(getCommandWord(input)) == CommandType.DEADLINE
                : "Deadline parsing requires a deadline command";
        String details = parseArguments(input, "deadline");
        if (details.isEmpty()) {
            throw new ClarryException("The description of a deadline cannot be empty.");
        }
        requireSingleMarker(details, "/by");
        if (!details.contains(" /by ")) {
            throw new ClarryException(
                    "A deadline needs a '/by' date, e.g. deadline return book /by 2019-10-15");
        }
        String[] parts = details.split(" /by(?: |$)", -1);
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new ClarryException("A deadline needs both a description and a '/by' date.");
        }
        validateDescription(parts[0].trim());
        return new Deadline(parts[0].trim(), parts[1].trim());
    }

    /**
     * Parses an event task from a command input.
     *
     * @param input complete event command
     * @return parsed event task
     * @throws ClarryException if the command is incomplete or either date and time is invalid
     */
    public Event parseEvent(String input) throws ClarryException {
        assert CommandType.fromWord(getCommandWord(input)) == CommandType.EVENT
                : "Event parsing requires an event command";
        String details = parseArguments(input, "event");
        requireSingleMarker(details, "/from");
        requireSingleMarker(details, "/to");
        String[] fromSplit = details.split(" /from(?: |$)", -1);
        String[] toSplit = fromSplit.length == 2 ? fromSplit[1].split(" /to(?: |$)", -1) : new String[0];
        if (fromSplit.length != 2 || toSplit.length != 2 || fromSplit[0].trim().isEmpty()
                || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
            throw new ClarryException("An event needs a description, '/from', and '/to' date and time.");
        }
        validateDescription(fromSplit[0].trim());
        return new Event(fromSplit[0].trim(), toSplit[0].trim(), toSplit[1].trim());
    }

    /**
     * Parses a task number as a zero-based index.
     *
     * @param input complete command input
     * @param command command word
     * @param taskCount number of available tasks
     * @return zero-based task index
     * @throws ClarryException if no valid existing task number is supplied
     */
    public int parseIndex(String input, String command, int taskCount) throws ClarryException {
        assert getCommandWord(input).equals(command) : "Index parsing requires the dispatched command";
        assert taskCount >= 0 : "Task count cannot be negative";
        String numberPart = parseArguments(input, command);
        if (numberPart.isEmpty()) {
            throw new ClarryException("Please specify which task number to " + command + ".");
        }
        if (!numberPart.matches("[0-9]+")) {
            throw new ClarryException("Please provide a valid task number.");
        }
        int number;
        try {
            number = Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            throw new ClarryException("Please provide a valid task number.");
        }
        if (number < 1 || number > taskCount) {
            throw new ClarryException("That task number doesn't exist. Type 'list' to check your tasks.");
        }
        return number - 1;
    }

    /**
     * Parses the ISO date supplied to the {@code on} command.
     *
     * @param input complete on command
     * @return parsed date
     * @throws ClarryException if the command does not contain one valid ISO date
     */
    public LocalDate parseDate(String input) throws ClarryException {
        assert CommandType.fromWord(getCommandWord(input)) == CommandType.ON
                : "Date parsing requires an on command";
        String dateText = parseArguments(input, "on");
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new ClarryException("Please use yyyy-mm-dd for the date, e.g. on 2019-10-15.");
        }
    }

    /**
     * Parses the keyword supplied to a {@code find} command.
     *
     * @param input complete find command
     * @return non-empty search keyword
     * @throws ClarryException if the command does not contain a keyword
     */
    public String parseFindKeyword(String input) throws ClarryException {
        assert CommandType.fromWord(getCommandWord(input)) == CommandType.FIND
                : "Keyword parsing requires a find command";
        String keyword = parseArguments(input, "find");
        if (keyword.isEmpty()) {
            throw new ClarryException("Please specify a keyword to find.");
        }
        return keyword;
    }

    /** Throws Clarry's standard error for an unsupported command. */
    public void throwUnknownCommand() throws ClarryException {
        throw new ClarryException("I'm a little lost! Type 'help' to see what I understand.");
    }

    /** Returns the trimmed text following a command word. */
    private String parseArguments(String input, String command) {
        input = input.strip();
        return input.length() > command.length() ? input.substring(command.length()).trim() : "";
    }

    /** Returns the first whitespace-delimited word in a command. */
    private String getCommandWord(String input) {
        return input.strip().split("\\s+", 2)[0];
    }

    /** Rejects repeated reserved markers before attempting to parse their values. */
    private void requireSingleMarker(String details, String marker) throws ClarryException {
        long count = java.util.Arrays.stream(details.split("\\s+")).filter(marker::equals).count();
        if (count > 1) {
            throw new ClarryException("Please specify '" + marker + "' only once.");
        }
    }

    /** Prevents a task description from corrupting the pipe-separated save format. */
    private void validateDescription(String description) throws ClarryException {
        if (description.contains("|")) {
            throw new ClarryException("Task descriptions cannot contain '|'; please use another character.");
        }
    }
}
