package minty.command;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import minty.exception.MintyException;
import minty.task.Deadline;
import minty.task.Event;
import minty.task.Todo;

/**
 * Interprets and validates commands entered by the user.
 */
public final class Parser {

    /**
     * Prevents creation of a stateless parser instance.
     */
    private Parser() {
    }

    /**
     * Converts complete user input into an executable command object.
     *
     * @param fullCommand complete command entered by the user.
     * @return command ready to execute.
     */
    public static Command parse(String fullCommand) {
        return parse(fullCommand, Clock.systemDefaultZone());
    }

    /**
     * Parses a command with the clock used to evaluate reminders.
     *
     * @param fullCommand complete user input.
     * @param clock source of the current local date.
     * @return executable command.
     */
    public static Command parse(String fullCommand, Clock clock) {
        if (fullCommand != null && fullCommand.codePoints().anyMatch(character ->
                Character.isISOControl(character) && character != '\t')) {
            return new UnknownCommand("One command at a time! Remove line breaks or control characters and try again.");
        }
        fullCommand = normalizeCommand(fullCommand);
        if (fullCommand.isEmpty()) {
            return new UnknownCommand("What shall we do? Try list or todo read a book.");
        }
        CommandType commandType = parseCommandType(fullCommand);
        switch (commandType) {
            case BYE:
                return new ExitCommand();
            case LIST:
                return new ListCommand();
            case REMINDERS:
            case REMIND:
                return new RemindersCommand(LocalDate.now(clock));
            case ON:
                return new OnCommand(fullCommand);
            case FIND:
                return new FindCommand(fullCommand);
            case MARK:
                return new MarkCommand(fullCommand);
            case UNMARK:
                return new UnmarkCommand(fullCommand);
            case DELETE:
                return new DeleteCommand(fullCommand);
            case TODO:
                return new TodoCommand(fullCommand);
            case DEADLINE:
                return new DeadlineCommand(fullCommand);
            case EVENT:
                return new EventCommand(fullCommand);
            case UNKNOWN:
            default:
                return new UnknownCommand();
        }
    }

    /**
     * Identifies the type of a complete command.
     *
     * @param command complete command entered by the user.
     * @return recognized command type, or {@link CommandType#UNKNOWN}.
     */
    public static CommandType parseCommandType(String command) {
        return CommandType.from(normalizeCommand(command));
    }

    /**
     * Parses and validates the task number supplied to a task-selection command.
     *
     * @param command complete command entered by the user.
     * @param commandType task-selection command type.
     * @param taskCount current number of stored tasks.
     * @return zero-based index of the selected task.
     * @throws MintyException if the task number is missing, non-numeric, or out of range.
     */
    public static int parseTaskIndex(String command, CommandType commandType, int taskCount)
            throws MintyException {
        String commandWord = commandType.getCommandWord();
        String taskNumber = getCommandArguments(command, commandType);
        if (taskNumber.isEmpty()) {
            throw new MintyException("Which task? Add a number after " + commandWord
                    + ", like " + commandWord + " 1.");
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new MintyException("Whoops! Use a whole number for the task, like " + commandWord + " 1.");
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new MintyException("I can't spot that task number! Type list to check your task numbers.");
        }
        assert taskIndex >= 0 && taskIndex < taskCount
                : "Validated task index must be within the task list";
        return taskIndex;
    }

    /**
     * Creates a todo after validating its description.
     *
     * @param command complete todo command.
     * @return validated todo.
     * @throws MintyException if the description is missing.
     */
    public static Todo parseTodo(String command) throws MintyException {
        String description = getCommandArguments(command, CommandType.TODO);
        if (description.isEmpty()) {
            throw new MintyException("Let's give that task a name! Try todo read a book.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline after validating its description and {@code /by} value.
     *
     * @param command complete deadline command.
     * @return validated deadline.
     * @throws MintyException if any required deadline detail is missing.
     */
    public static Deadline parseDeadline(String command) throws MintyException {
        String details = getCommandArguments(command, CommandType.DEADLINE);
        int bySeparator = findParameter(details, "/by");
        if (bySeparator < 0) {
            throw new MintyException("Let's give that deadline a date! Try deadline submit report /by 2026-09-18.");
        }
        assert bySeparator >= 0
                : "A deadline separator must exist before extracting its fields";

        String description = details.substring(0, bySeparator).trim();
        String byText = details.substring(bySeparator + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new MintyException("What's due? Add a description before /by.");
        }
        if (byText.isEmpty()) {
            throw new MintyException("When's it due? Add a date after /by, like 2026-09-18.");
        }
        LocalDate by = parseDate(byText, "deadline");
        return new Deadline(description, by);
    }

    /**
     * Creates an event after validating its description, start, and end values.
     *
     * @param command complete event command.
     * @return validated event.
     * @throws MintyException if any required event detail is missing or out of order.
     */
    public static Event parseEvent(String command) throws MintyException {
        String details = getCommandArguments(command, CommandType.EVENT);
        int fromSeparator = findParameter(details, "/from");
        int toSeparator = findParameter(details, "/to");

        if (toSeparator >= 0 && (fromSeparator < 0 || toSeparator < fromSeparator)) {
            throw new MintyException("Start first, finish second! Put /from before /to.");
        }
        if (fromSeparator < 0) {
            throw new MintyException("When does it start? Add /from followed by a date, like /from 2026-09-18.");
        }
        if (toSeparator < 0) {
            throw new MintyException("When does it wrap up? Add /to followed by a date, like /to 2026-09-19.");
        }
        assert fromSeparator >= 0 && toSeparator > fromSeparator
                : "Event separators must be ordered before extracting their fields";

        String description = details.substring(0, fromSeparator).trim();
        String fromText = details.substring(fromSeparator + "/from".length(), toSeparator).trim();
        String toText = details.substring(toSeparator + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new MintyException("What's the occasion? Add a description before /from.");
        }
        if (fromText.isEmpty()) {
            throw new MintyException("Let's set the start! Add a date after /from, like 2026-09-18.");
        }
        if (toText.isEmpty()) {
            throw new MintyException("Let's set the finish! Add a date after /to, like 2026-09-19.");
        }
        LocalDate from = parseDate(fromText, "event start");
        LocalDate to = parseDate(toText, "event end");
        if (to.isBefore(from)) {
            throw new MintyException("Whoops! The event ends before it starts. Set the end date to"
                    + " the start date or later.");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses the date supplied to an {@code on} command.
     *
     * @param command complete on command.
     * @return requested date.
     * @throws MintyException if the date is missing or invalid.
     */
    public static LocalDate parseOnDate(String command) throws MintyException {
        String dateText = getCommandArguments(command, CommandType.ON);
        if (dateText.isEmpty()) {
            throw new MintyException("Which day are we checking? Try on 2026-09-18.");
        }
        return parseDate(dateText, "requested");
    }

    /**
     * Parses and validates the keyword supplied to a {@code find} command.
     *
     * @param command complete find command.
     * @return keyword to search for.
     * @throws MintyException if the keyword is missing.
     */
    public static String parseFindKeyword(String command) throws MintyException {
        String keyword = getCommandArguments(command, CommandType.FIND);
        if (keyword.isEmpty()) {
            throw new MintyException("What are we looking for? Add a keyword, like find book.");
        }
        return keyword;
    }

    /**
     * Returns the trimmed text following a command's identifying word.
     *
     * @param command complete command entered by the user.
     * @param commandType recognized type of the command.
     * @return trimmed command arguments, or an empty string if none were supplied.
     */
    private static String getCommandArguments(String command, CommandType commandType) {
        return normalizeCommand(command).substring(commandType.getCommandWord().length()).trim();
    }

    /**
     * Accepts accidental whitespace around and within a single-line command.
     *
     * @param command user input, possibly null or blank.
     * @return command with whitespace runs replaced by one space.
     */
    private static String normalizeCommand(String command) {
        return command == null ? "" : command.replaceAll("(?U)\\s+", " ").strip();
    }

    /**
     * Finds a standalone date parameter and rejects accidental repetitions.
     *
     * @param details task description and parameters.
     * @param parameter expected date marker.
     * @return marker offset, or -1 if absent.
     * @throws MintyException if the marker occurs more than once.
     */
    private static int findParameter(String details, String parameter) throws MintyException {
        Matcher matcher = Pattern.compile("(?<!\\S)" + Pattern.quote(parameter) + "(?!\\S)").matcher(details);
        if (!matcher.find()) {
            return -1;
        }
        int position = matcher.start();
        if (matcher.find()) {
            throw new MintyException("Whoops! Use " + parameter + " only once.");
        }
        return position;
    }

    /**
     * Parses a date in Minty's required ISO format.
     *
     * @param dateText date entered by the user.
     * @param dateName name used to identify the date in an error message.
     * @return parsed date.
     * @throws MintyException if the date is not a valid {@code yyyy-MM-dd} value.
     */
    private static LocalDate parseDate(String dateText, String dateName) throws MintyException {
        try {
            if (!dateText.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                throw new DateTimeParseException("Expected yyyy-MM-dd", dateText, 0);
            }
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new MintyException("That " + dateName + " date doesn't look right!"
                    + " Use a valid date in yyyy-MM-dd format, like 2026-09-18.");
        }
    }
}
