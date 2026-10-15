package potato.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import potato.command.AddCommand;
import potato.command.Command;
import potato.command.DeleteCommand;
import potato.command.ExitCommand;
import potato.command.FindCommand;
import potato.command.ListCommand;
import potato.command.MarkCommand;
import potato.command.SortCommand;
import potato.exception.PotatoException;
import potato.task.Deadline;
import potato.task.Event;
import potato.task.Todo;

/**
 * Parses user input strings into executable {@code Command} objects.
 */
public class Parser {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT)
    };

    /**
     * Enumerates supported command types and provides parsing from input strings.
     */
    private enum CommandType {
        BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, SORT, UNKNOWN;

        /**
         * Parses a raw word into a corresponding {@code CommandType}.
         *
         * @param word Command word to parse.
         * @return Matched {@code CommandType}, or {@code UNKNOWN} if invalid.
         */
        public static CommandType parse(String word) {
            assert word != null : "Word to parse in CommandType should not be null";
            try {
                return CommandType.valueOf(word.toUpperCase());
            } catch (IllegalArgumentException e) {
                return UNKNOWN;
            }
        }
    }

    /**
     * Parses the full command string entered by the user into an executable {@code Command}.
     *
     * @param fullCommand Entire line of user input.
     * @return Corresponding {@code Command} object.
     * @throws PotatoException If input is unknown or required arguments are missing/malformed.
     */
    public static Command parse(String fullCommand) throws PotatoException {
        assert fullCommand != null : "Command string passed to Parser should not be null";

        String trimmedCommand = fullCommand.trim();
        String[] words = trimmedCommand.split(" ", 2);
        String commandWord = words[0];
        String arguments = words.length > 1 ? words[1].trim() : "";

        assert !commandWord.isEmpty() || trimmedCommand.isEmpty() : "Command word should not be empty unless input is empty";

        CommandType type = CommandType.parse(commandWord);

        switch (type) {
            case BYE:
                return new ExitCommand();
            case LIST:
                return new ListCommand();
            case MARK:
                return new MarkCommand(parseIndex(arguments, "mark"), true);
            case UNMARK:
                return new MarkCommand(parseIndex(arguments, "unmark"), false);
            case DELETE:
                return new DeleteCommand(parseIndex(arguments, "delete"));
            case TODO:
                if (arguments.isEmpty()) {
                    throw new PotatoException("MON DIEU! You cannot give me an empty todo! Specify the ingredients!");
                }
                return new AddCommand(new Todo(arguments));
            case DEADLINE:
                return parseDeadline(arguments);
            case EVENT:
                return parseEvent(arguments);
            case FIND:
                if (arguments.isEmpty()) {
                    throw new PotatoException("MISERABLE! What keyword am I supposed to search for on the board?!");
                }
                return new FindCommand(arguments);
            case SORT:
                return new SortCommand();
            default:
                throw new PotatoException("SACREBLEU! What does '" + commandWord + "' even mean?! Speak clearly or leave the kitchen!");
        }
    }

    /**
     * Parses the zero-based task index from string arguments for index-based commands.
     *
     * @param arg String containing the index argument.
     * @param command Name of the command requesting index parsing.
     * @return Zero-based integer index.
     * @throws PotatoException If index string is empty or invalid.
     */
    private static int parseIndex(String arg, String command) throws PotatoException {
        assert arg != null : "Argument string passed to parseIndex should not be null";
        assert command != null : "Command name passed to parseIndex should not be null";

        if (arg.isEmpty()) {
            throw new PotatoException("MISERABLE! Specify the order number to " + command + "!");
        }
        try {
            return Integer.parseInt(arg) - 1;
        } catch (NumberFormatException e) {
            throw new PotatoException("THAT IS NOT A NUMBER! Specify a valid integer order index!");
        }
    }

    /**
     * Parses arguments for creating a {@code Deadline} task command.
     *
     * @param arguments Description and /by arguments.
     * @return Constructed {@code AddCommand} containing a {@code Deadline}.
     * @throws PotatoException If description or deadline target date is missing.
     */
    private static Command parseDeadline(String arguments) throws PotatoException {
        assert arguments != null : "Arguments passed to parseDeadline should not be null";

        if (arguments.isEmpty()) {
            throw new PotatoException("MON DIEU! The description of a deadline cannot be empty!");
        }
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new PotatoException("INCOMPETENT! Specify the deadline using '/by <date/time>' or it will be SERVED COLD!");
        }
        String deadlineDateTime = parts[1].trim();
        validateDateOrDateTime(deadlineDateTime, "deadline");
        return new AddCommand(new Deadline(parts[0].trim(), deadlineDateTime));
    }

    /**
     * Parses arguments for creating an {@code Event} task command.
     *
     * @param arguments Description, /from, and /to arguments.
     * @return Constructed {@code AddCommand} containing an {@code Event}.
     * @throws PotatoException If description, start time, or end time is missing.
     */
    private static Command parseEvent(String arguments) throws PotatoException {
        assert arguments != null : "Arguments passed to parseEvent should not be null";

        if (arguments.isEmpty()) {
            throw new PotatoException("MON DIEU! The description of an event cannot be empty!");
        }
        String[] parts = arguments.split(" /from ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty()) {
            throw new PotatoException("SACREBLEU! Specify event timing using '/from <start> /to <end>'!");
        }
        String[] timeParts = parts[1].split(" /to ", 2);
        if (timeParts.length < 2 || timeParts[0].trim().isEmpty() || timeParts[1].trim().isEmpty()) {
            throw new PotatoException("SACREBLEU! Event is missing end time! Specify using '/to <end>'!");
        }
        String startDateTime = timeParts[0].trim();
        String endDateTime = timeParts[1].trim();
        validateDateOrDateTime(startDateTime, "event start");
        validateDateOrDateTime(endDateTime, "event end");
        return new AddCommand(new Event(parts[0].trim(), startDateTime, endDateTime));
    }

    /**
     * Validates that a date/time argument uses one of Potato's supported date formats.
     *
     * @param input Date or date-time string entered by the user.
     * @param fieldName Name of the command field being validated.
     * @throws PotatoException If the input is not a real date or date-time in the supported format.
     */
    private static void validateDateOrDateTime(String input, String fieldName) throws PotatoException {
        assert input != null : "Date/time input passed to validateDateOrDateTime should not be null";
        assert fieldName != null : "Field name passed to validateDateOrDateTime should not be null";

        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime.parse(input, formatter);
                return;
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }

        try {
            LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new PotatoException("SACREBLEU! Invalid " + fieldName
                    + " date/time. Use yyyy-MM-dd, yyyy-MM-dd HHmm, or yyyy-MM-dd HH:mm with a real calendar date!");
        }
    }
}
