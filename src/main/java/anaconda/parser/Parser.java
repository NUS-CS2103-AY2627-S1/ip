package anaconda.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import anaconda.exception.AnacondaException;
import anaconda.task.Deadline;
import anaconda.task.Event;
import anaconda.task.Task;
import anaconda.task.ToDo;

/**
 * Interprets commands and validates their arguments without reading input or modifying tasks.
 */
public class Parser {
    private static final DateTimeFormatter DAY_FIRST_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Holds a recognized command and its remaining argument text.
     *
     * @param command Recognized command type.
     * @param arguments Trimmed arguments following the command word.
     */
    public record ParsedCommand(Command command, String arguments) {
    }

    /**
     * Holds the date and exact-match option parsed from a date filter.
     *
     * @param date Date to compare against.
     * @param isSharp Whether to match only tasks ending on that exact date.
     */
    public record DateFilter(LocalDate date, boolean isSharp) {
    }

    /**
     * Splits a command and validates argument restrictions for list, help, clear, undo, and bye.
     *
     * @param input Complete user input.
     * @return Recognized command and its argument text.
     * @throws AnacondaException If the command is empty, unknown, or has unexpected arguments.
     */
    public ParsedCommand parse(String input) throws AnacondaException {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new AnacondaException("Give me a command to work with.", AnacondaException.Reason.UNKNOWN_COMMAND);
        }

        String[] inputParts = trimmedInput.split("\\s+", 2);
        Command command = parseCommand(inputParts[0]);
        String arguments = inputParts.length == 2 ? inputParts[1].trim() : "";
        switch (command) {
            case HELP:
                requireNoArguments(arguments, "help");
                break;
            case LIST:
                requireNoArguments(arguments, "list");
                break;
            case CLEAR:
                requireNoArguments(arguments, "clear");
                break;
            case UNDO:
                if (!arguments.isEmpty() && !arguments.equalsIgnoreCase("undo")) {
                    throw new AnacondaException("Use 'undo' or 'undo undo'.");
                }
                break;
            case BYE:
                if (!arguments.isEmpty()) {
                    throw new AnacondaException("The bye command cannot have extra text.");
                }
                break;
            default:
                break;
        }
        return new ParsedCommand(command, arguments);
    }

    /**
     * Recognizes a standalone exit command before displaying a response separator.
     *
     * @param input Complete user input.
     * @return Whether this is a valid exit command.
     */
    public boolean isExitCommand(String input) {
        return input.trim().equalsIgnoreCase("bye");
    }

    /**
     * Parses a task-creation command without adding the task to the list.
     *
     * @param command TODO, DEADLINE, or EVENT.
     * @param arguments Description and any required date fields.
     * @return New incomplete task.
     * @throws AnacondaException If required fields or dates are invalid.
     * @throws IllegalArgumentException If the command does not create a task.
     */
    public Task parseTask(Command command, String arguments) throws AnacondaException {
        switch (command) {
            case TODO:
                requireDescription(arguments, "todo");
                return new ToDo(arguments);
            case DEADLINE:
                return parseDeadline(arguments);
            case EVENT:
                return parseEvent(arguments);
            default:
                throw new IllegalArgumentException("Command does not create a task: " + command);
        }
    }

    /**
     * Parses a one-based task number without checking the current list size.
     *
     * @param arguments Task number entered by the user.
     * @return Parsed task number.
     * @throws AnacondaException If the argument is not one integer.
     */
    public int parseTaskNumber(String arguments) throws AnacondaException {
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException exception) {
            throw new AnacondaException("I need one task number here.");
        }
    }

    /**
     * Validates and returns a task-description search keyword.
     *
     * @param arguments Keyword entered after the find command.
     * @return Search keyword with its original casing and internal whitespace.
     * @throws AnacondaException If no keyword was provided.
     */
    public String parseKeyword(String arguments) throws AnacondaException {
        if (arguments.isEmpty()) {
            throw new AnacondaException("Give me a keyword to look for.");
        }
        return arguments;
    }

    /**
     * Parses a filter date and its optional sharp modifier.
     *
     * @param arguments Filter date followed by an optional sharp keyword.
     * @param command BY or FROM, used to explain the required syntax.
     * @return Parsed filter values.
     * @throws AnacondaException If the filter syntax or date is invalid.
     * @throws IllegalArgumentException If the command is not BY or FROM.
     */
    public DateFilter parseDateFilter(String arguments, Command command) throws AnacondaException {
        String commandWord = switch (command) {
            case BY -> "/by";
            case FROM -> "/from";
            case null, default -> throw new IllegalArgumentException("Command does not filter by date: " + command);
        };
        String[] filterParts = arguments.split("\\s+");
        boolean hasValidPartCount = !arguments.isEmpty() && filterParts.length <= 2;
        boolean isSharp = filterParts.length == 2 && filterParts[1].equalsIgnoreCase("sharp");
        if (!hasValidPartCount || (filterParts.length == 2 && !isSharp)) {
            throw new AnacondaException("Use '" + commandWord + " DATE' or '"
                    + commandWord + " DATE sharp'.");
        }
        return new DateFilter(parseDate(filterParts[0]), isSharp);
    }

    /**
     * Converts a command word, including slash aliases, to the existing command enum.
     */
    private Command parseCommand(String commandWord) throws AnacondaException {
        try {
            return switch (commandWord.toUpperCase(Locale.ROOT)) {
                case "/BY" -> Command.BY;
                case "/FROM" -> Command.FROM;
                default -> Command.valueOf(commandWord.toUpperCase(Locale.ROOT));
            };
        } catch (IllegalArgumentException exception) {
            throw new AnacondaException("Yeah... I don't recognize that command.",
                    AnacondaException.Reason.UNKNOWN_COMMAND);
        }
    }

    /**
     * Parses the description and date fields of a deadline.
     */
    private Deadline parseDeadline(String arguments) throws AnacondaException {
        int byPosition = arguments.indexOf("/by");
        if (byPosition < 0) {
            throw new AnacondaException("A deadline needs '/by' followed by a date or time.");
        }

        String description = arguments.substring(0, byPosition).trim();
        String byText = arguments.substring(byPosition + "/by".length()).trim();
        requireDescription(description, "deadline");
        if (byText.isEmpty()) {
            throw new AnacondaException("A deadline needs a date or time after '/by'.");
        }
        LocalDate by = parseDate(byText);
        return new Deadline(description, by);
    }

    /**
     * Parses an event's description and dates, allowing equal dates but rejecting a reversed range.
     */
    private Event parseEvent(String arguments) throws AnacondaException {
        int fromPosition = arguments.indexOf("/from");
        int toPosition = fromPosition < 0
                ? -1
                : arguments.indexOf("/to", fromPosition + "/from".length());
        if (fromPosition < 0 || toPosition < 0) {
            throw new AnacondaException("An event needs both '/from' and '/to' times.");
        }

        // The /to search starts after /from, so extracting the start date must use ordered bounds.
        assert toPosition >= fromPosition + "/from".length() : "Event markers must not overlap or be reversed";

        String description = arguments.substring(0, fromPosition).trim();
        String fromText = arguments.substring(fromPosition + "/from".length(), toPosition).trim();
        String toText = arguments.substring(toPosition + "/to".length()).trim();
        requireDescription(description, "event");
        if (fromText.isEmpty() || toText.isEmpty()) {
            throw new AnacondaException("An event needs times after both '/from' and '/to'.");
        }
        LocalDate from = parseDate(fromText);
        LocalDate to = parseDate(toText);
        if (from.isAfter(to)) {
            throw new AnacondaException("An event's start date cannot be later than its end date.");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses an ISO or day-first date, rejecting impossible calendar dates.
     */
    private LocalDate parseDate(String dateText) throws AnacondaException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            try {
                return LocalDate.parse(dateText, DAY_FIRST_DATE_FORMATTER);
            } catch (DateTimeParseException secondException) {
                throw new AnacondaException("Please enter a valid calendar date in yyyy-MM-dd or dd-MM-yyyy format.");
            }
        }
    }

    /**
     * Ensures that a description is present and cannot conflict with the saved-file separator.
     */
    private void requireDescription(String description, String taskType) throws AnacondaException {
        if (description.isEmpty()) {
            throw new AnacondaException("The description of a " + taskType + " cannot be empty.");
        }
        if (description.contains("|")) {
            throw new AnacondaException("Hisssssss. . . remove that pesky column");
        }
    }

    /**
     * Rejects arguments supplied to a command that takes none.
     */
    private void requireNoArguments(String arguments, String command) throws AnacondaException {
        if (!arguments.isEmpty()) {
            throw new AnacondaException("The " + command + " command does not take extra text.");
        }
    }
}
