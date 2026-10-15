package suki;

/**
 * Turns raw lines typed by the user into something Suki can act on.
 *
 * <p>All knowledge of the command syntax — where the spaces and slashes go —
 * is confined to this class, so changing the syntax does not ripple into the
 * main loop or the task classes.
 */
public class Parser {
    /** Separates a deadline's description from its due date. */
    private static final String BY_DELIMITER = "\\s+/by\\s+";

    /** Separates an event's description from its start. */
    private static final String FROM_DELIMITER = "\\s+/from\\s+";

    /** Separates an event's start from its end. */
    private static final String TO_DELIMITER = "\\s+/to\\s+";

    /** Splits a line into the command word and everything after it. */
    private static final String COMMAND_DELIMITER = "\\s+";

    /** Not meant to be instantiated: this class only holds static helpers. */
    private Parser() {
    }

    /**
     * The result of reading one line of input: which command it is, and
     * whatever text followed the command word.
     *
     * <p>A record is used because this is a plain carrier of two values with no
     * behaviour of its own.
     */
    public record ParsedInput(CommandType commandType, String commandWord, String arguments) {
    }

    /**
     * Splits a line into its command word and the arguments that follow.
     *
     * @param input a full line typed by the user
     * @return the command type and its arguments
     */
    public static ParsedInput parse(String input) {
        assert input != null : "the caller reads a line before parsing it, so it is never null";
        String[] split = input.trim().split(COMMAND_DELIMITER, 2);
        assert split.length >= 1 : "String#split always yields at least one element";
        String commandWord = split[0];
        String arguments = split.length > 1 ? split[1].trim() : "";
        return new ParsedInput(CommandType.fromWord(commandWord), commandWord, arguments);
    }

    /**
     * Reads a one-based task number as typed by the user and converts it to a
     * zero-based list index.
     *
     * @param arguments the text following the command word
     * @param commandWord the command being run, used only to build the error message
     * @return the zero-based index
     * @throws SukiException if the text is not a number
     */
    public static int parseIndex(String arguments, String commandWord) throws SukiException {
        try {
            int taskNumber = Integer.parseInt(arguments.trim());
            if (taskNumber < 1) {
                throw new NumberFormatException();
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new SukiException("Please provide a valid task number, e.g. " + commandWord + " 1");
        }
    }

    /**
     * Builds a {@link Todo} from the arguments of a "todo" command.
     *
     * @param arguments the text following the command word
     * @return the todo described by the arguments
     * @throws SukiException if no description was given
     */
    public static Todo parseTodo(String arguments) throws SukiException {
        if (arguments.isEmpty()) {
            throw new SukiException("The description of a todo cannot be empty.");
        }
        return new Todo(arguments);
    }

    /**
     * Builds a {@link Deadline} from the arguments of a "deadline" command,
     * which must be of the form {@code <description> /by <date>}.
     *
     * @param arguments the text following the command word
     * @return the deadline described by the arguments
     * @throws SukiException if the description or the date is missing or unreadable
     */
    public static Deadline parseDeadline(String arguments) throws SukiException {
        String[] parts = arguments.split(BY_DELIMITER, 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new SukiException("A deadline needs a description and a '/by' date/time, "
                    + "e.g. deadline return book /by 2019-10-15");
        }
        assert parts.length == 2 : "the guard above rejects anything that did not split in two";
        return new Deadline(parts[0].trim(), DateTime.parse(parts[1]));
    }

    /**
     * Builds an {@link Event} from the arguments of an "event" command, which
     * must be of the form {@code <description> /from <date> /to <date>}.
     *
     * @param arguments the text following the command word
     * @return the event described by the arguments
     * @throws SukiException if any part is missing or unreadable
     */
    public static Event parseEvent(String arguments) throws SukiException {
        String[] fromSplit = arguments.split(FROM_DELIMITER, 2);
        if (fromSplit.length < 2 || fromSplit[0].trim().isEmpty()) {
            throw new SukiException(eventFormatMessage());
        }
        String[] toSplit = fromSplit[1].split(TO_DELIMITER, 2);
        if (toSplit.length < 2 || toSplit[0].trim().isEmpty() || toSplit[1].trim().isEmpty()) {
            throw new SukiException(eventFormatMessage());
        }
        DateTime from = DateTime.parse(toSplit[0]);
        DateTime to = DateTime.parse(toSplit[1]);
        if (from.compareTo(to) >= 0) {
            throw new SukiException("An event must end after it starts.");
        }
        return new Event(fromSplit[0].trim(), from, to);
    }

    /**
     * Reads the keyword of a "find" command.
     *
     * @param arguments the text following the command word
     * @return the keyword to search for
     * @throws SukiException if no keyword was given
     */
    public static String parseFindKeyword(String arguments) throws SukiException {
        if (arguments.isEmpty()) {
            throw new SukiException("Tell me what to look for, e.g. find book");
        }
        return arguments;
    }

    /**
     * Rejects arguments supplied to a command that does not accept any.
     *
     * @param arguments text following the command word
     * @param commandWord command being checked
     * @throws SukiException if unexpected arguments were supplied
     */
    public static void requireNoArguments(String arguments, String commandWord) throws SukiException {
        if (!arguments.isEmpty()) {
            throw new SukiException("The '" + commandWord + "' command does not take any arguments.");
        }
    }

    /**
     * Returns the message describing the expected "event" syntax, shared by the
     * two places that reject malformed input.
     *
     * @return the error message to show the user
     */
    private static String eventFormatMessage() {
        return "An event needs a description, a '/from' and a '/to' date/time, "
                + "e.g. event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600";
    }
}
