package littledaisy.command;

import java.util.regex.Pattern;

import littledaisy.exception.LittleDaisyException;
import littledaisy.task.Deadline;
import littledaisy.task.Event;
import littledaisy.task.Todo;

/** Converts raw command lines into commands and domain objects. */
public final class Parser {
    private static final Pattern OPTION_BY = Pattern.compile("\\s+/by\\s+");
    private static final Pattern OPTION_FROM = Pattern.compile("\\s+/from\\s+");
    private static final Pattern OPTION_TO = Pattern.compile("\\s+/to\\s+");

    private Parser() {
    }

    /**
     * Separates a command word from the rest of its input line.
     *
     * @param input raw command line
     * @return recognized command and its remaining arguments
     */
    public static ParsedCommand parse(String input) {
        String trimmed = input.trim();
        int firstSpace = trimmed.indexOf(' ');
        String word = firstSpace < 0 ? trimmed : trimmed.substring(0, firstSpace);
        String arguments = firstSpace < 0 ? "" : trimmed.substring(firstSpace + 1).trim();
        return new ParsedCommand(Command.of(word), arguments);
    }

    /**
     * Parses and validates a user-facing task number as a zero-based index.
     *
     * @param arguments text following a mark, unmark, or delete command
     * @param taskCount number of tasks currently stored
     * @return zero-based task-list index
     * @throws LittleDaisyException if the task number is missing or invalid
     */
    public static int parseTaskIndex(String arguments, int taskCount)
            throws LittleDaisyException {
        if (arguments.isBlank()) {
            throw new LittleDaisyException("Please tell me which task number.");
        }

        if (!arguments.matches("\\d+")) {
            throw new LittleDaisyException("Please provide exactly one task number.");
        }

        int number;
        try {
            number = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new LittleDaisyException("A task number should be a number.");
        }
        if (number < 1 || number > taskCount) {
            throw new LittleDaisyException("Task " + number + " does not exist.");
        }
        return number - 1;
    }

    /**
     * Creates a todo from its command arguments.
     *
     * @param arguments user-entered todo description
     * @return parsed todo
     * @throws LittleDaisyException if the description is empty
     */
    public static Todo parseTodo(String arguments) throws LittleDaisyException {
        return new Todo(requireDescription(arguments, "todo"));
    }

    /**
     * Returns a non-empty keyword for a find command.
     *
     * @param arguments keyword supplied after the command word
     * @return validated keyword
     * @throws LittleDaisyException if the keyword is empty
     */
    public static String parseFindKeyword(String arguments) throws LittleDaisyException {
        if (arguments.isBlank()) {
            throw new LittleDaisyException("Please tell me what to find.");
        }
        return arguments;
    }

    /**
     * Creates a deadline from its description and {@code /by} date.
     *
     * @param arguments description followed by a date option
     * @return parsed deadline
     * @throws LittleDaisyException if the description or date is invalid
     */
    public static Deadline parseDeadline(String arguments) throws LittleDaisyException {
        String[] pieces = splitRequired(arguments, OPTION_BY,
                "A deadline needs \"/by <date>\" after the description.");
        return Deadline.fromInput(pieces[0], pieces[1]);
    }

    /**
     * Creates an event from its description, start, and end.
     *
     * @param arguments description followed by {@code /from} and {@code /to}
     * @return parsed event
     * @throws LittleDaisyException if a required field is missing
     */
    public static Event parseEvent(String arguments) throws LittleDaisyException {
        String[] descriptionAndTimes = splitRequired(arguments, OPTION_FROM,
                "An event needs \"/from <start>\" after the description.");
        String[] times = splitRequired(descriptionAndTimes[1], OPTION_TO,
                "An event needs \"/to <end>\" after the start time.");
        return new Event(descriptionAndTimes[0], times[0], times[1]);
    }

    /** Rejects an add-task command with no description. */
    private static String requireDescription(String arguments, String command)
            throws LittleDaisyException {
        if (arguments.isBlank()) {
            throw new LittleDaisyException(
                    "The description of a " + command + " cannot be empty.");
        }
        return arguments;
    }

    /** Splits around one required option and rejects missing values. */
    private static String[] splitRequired(String input, Pattern separator, String errorMessage)
            throws LittleDaisyException {
        String[] pieces = separator.split(input.trim(), -1);
        if (pieces.length != 2 || pieces[0].isBlank() || pieces[1].isBlank()) {
            throw new LittleDaisyException(errorMessage);
        }
        return new String[]{pieces[0].trim(), pieces[1].trim()};
    }
}
