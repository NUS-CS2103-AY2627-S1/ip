package linus.parser;

import java.util.List;

import linus.invalidtaskexception.InvalidTaskException;
/**
 * Represents a parser that takes the plaintext input from
 * the command line and makes sense of the input.
 */
public class Parser {
    private static final String LIST_COMMAND = "list";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";

    private static final String SIMPLE_COMMAND_REGEX = "^(mark|unmark|delete|find|todo) .+$";
    private static final String DEADLINE_COMMAND_REGEX = "^deadline .+ /by .+$";
    private static final String EVENT_COMMAND_REGEX = "^event .+ /from .+ /to .+$";

    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String BY_SEPARATOR = " /by ";
    private static final String EVENT_PREFIX = "event ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";

    private static final String INVALID_COMMAND_ERROR = "Here's a tech tip! Enter the command with a valid format! :-(";

    /**
     * Returns a list of strings containing the parsed input.
     * Only parses the input, does not do validation of arguments.
     *
     * @param input Plaintext string input from user.
     * @return Parsed input as a list of strings.
     */
    public List<String> parse(String input) throws InvalidTaskException {
        assert input != null : "Input text to be parsed is null";
        if (input.equals(LIST_COMMAND)) {
            return List.of(LIST_COMMAND);
        } else if (input.matches(SIMPLE_COMMAND_REGEX)) {
            return this.parseSimpleCommand(input);
        } else if (input.matches(DEADLINE_COMMAND_REGEX)) {
            return this.parseDeadlineCommand(input);
        } else if (input.matches(EVENT_COMMAND_REGEX)) {
            return this.parseEventCommand(input);
        } else {
            throw new InvalidTaskException(INVALID_COMMAND_ERROR);
        }
    }

    /**
     * Parses and breaks a simple command down to its command and argument.
     * A simple command consists of a command keyword, followed by a space and
     * exactly 1 argument after. Mark, Unmark, Delete, Find and Todo are all
     * the simple commands allowed for the chatbot.
     *
     * @param input Plaintext string input from user.
     * @return Parsed input as a list of strings.
     */
    private List<String> parseSimpleCommand(String input) {
        String[] parts = input.split(" ", 2);
        String command = parts[0];
        String argument = parts[1];
        return List.of(command, argument);
    }

    /**
     * Parses and breaks a deadline command down to its command and arguments.
     * A deadline command consists of the 'deadline' keyword, followed by a space and
     * the description of the task, followed by a space and the /by delimiter,
     * followed by a space and the deadline, e.g. deadline description /by deadline.
     *
     * @param input Plaintext string input from user.
     * @return Parsed input as a list of strings.
     */
    private List<String> parseDeadlineCommand(String input) {
        int byIndex = input.lastIndexOf(BY_SEPARATOR);
        String description = input.substring(DEADLINE_PREFIX.length(), byIndex);
        String date = input.substring(byIndex + BY_SEPARATOR.length());
        return List.of(DEADLINE_COMMAND, description, date);
    }

    /**
     * Parses and breaks an event command down to its command and arguments.
     * An event command consists of the 'event' keyword, followed by a space and
     * the description of the task, followed by a space and the /from delimiter,
     * followed by a space and the start date, followed by a space and the /to
     * delimiter, followed by a space and the end date.
     * E.g. event description /from start /to end.
     *
     * @param input Plaintext string input from user.
     * @return Parsed input as a list of strings.
     */
    private List<String> parseEventCommand(String input) {
        int fromIndex = input.lastIndexOf(FROM_SEPARATOR);
        int toIndex = input.lastIndexOf(TO_SEPARATOR);
        String description = input.substring(EVENT_PREFIX.length(), fromIndex);
        String startDate = input.substring(fromIndex + FROM_SEPARATOR.length(), toIndex);
        String endDate = input.substring(toIndex + TO_SEPARATOR.length());
        return List.of(EVENT_COMMAND, description, startDate, endDate);
    }
}
