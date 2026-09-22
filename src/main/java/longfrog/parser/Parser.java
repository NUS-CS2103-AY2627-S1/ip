package longfrog.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import longfrog.command.AddCommand;
import longfrog.command.Command;
import longfrog.command.DateCommand;
import longfrog.command.DeleteCommand;
import longfrog.command.ExitCommand;
import longfrog.command.FindCommand;
import longfrog.command.ListCommand;
import longfrog.command.MarkCommand;
import longfrog.command.UnmarkCommand;
import longfrog.exception.LongfrogException;
import longfrog.task.Deadline;
import longfrog.task.Event;
import longfrog.task.TaskList;
import longfrog.task.Todo;
import longfrog.util.FormatUtils;

/**
 * Handles the parsing of raw user text inputs into executable {@link Command} objects.
 */
public class Parser {
    private static final String TODO_USAGE = "todo TASK";
    private static final String DEADLINE_USAGE = "deadline TASK /by d/M/yyyy HHmm";
    private static final String EVENT_USAGE = "event TASK /from d/M/yyyy HHmm /to d/M/yyyy HHmm";
    private static final String DATE_USAGE = "date d/M/yyyy (e.g., date 2/12/2019)";
    private static final String FIND_USAGE = "find KEYWORD";
    private static final String LIST_USAGE = "list";
    private static final String BYE_USAGE = "bye";
    private static final String MARK_USAGE = "mark INDEX";
    private static final String UNMARK_USAGE = "unmark INDEX";
    private static final String DELETE_USAGE = "delete INDEX";

    private final TaskList taskList;

    public Parser(TaskList taskList) {
        this.taskList = taskList;
    }

    /**
     * Parses the user input string and returns the corresponding {@code Longfrog.Commands.Command}.
     * @param fullInput The complete raw text entered by the user.
     * @return A {@link Command} ready for execution.
     * @throws LongfrogException if the input is not a complete, supported command.
     */
    public Command parse(String fullInput) throws LongfrogException {
        assert fullInput != null : "Parser input must be supplied by the UI";

        String cleanInput = fullInput.trim();
        if (cleanInput.isEmpty()) {
            throw new LongfrogException("Input buffer is empty. Please enter a command, ribbit.");
        }

        String[] words = cleanInput.split(" ", 2);
        CommandType commandType = CommandType.fromKeyword(words[0])
                .orElseThrow(() -> new LongfrogException(
                        "Unknown command token. My parser cannot compute that, ribbit."));

        switch (commandType) {
            case BYE:
                validateNoArgument(words, BYE_USAGE);
                return new ExitCommand();
            case TODO:
                return parseTodoCommand(words);
            case DEADLINE:
                return parseDeadlineCommand(words);
            case EVENT:
                return parseEventCommand(words);
            case LIST:
                validateNoArgument(words, LIST_USAGE);
                return new ListCommand(this.taskList);
            case MARK:
                return new MarkCommand(this.taskList, parseIndex(words, MARK_USAGE));
            case UNMARK:
                return new UnmarkCommand(this.taskList, parseIndex(words, UNMARK_USAGE));
            case DELETE:
                return new DeleteCommand(this.taskList, parseIndex(words, DELETE_USAGE));
            case DATE:
                return parseDateCommand(words);
            case FIND:
                return parseFindCommand(words);
            default:
                throw new LongfrogException("Unknown command token. My parser cannot compute that, ribbit.");
        }
    }

    /**
     * Parses a todo command.
     *
     * @param words the command keyword and task description
     * @return a command that adds the todo
     * @throws LongfrogException if the task description is missing
     */
    private Command parseTodoCommand(String[] words) throws LongfrogException {
        String taskName = getArgument(words, TODO_USAGE);
        validateTaskName(taskName);
        return new AddCommand(taskList, new Todo(taskName));
    }

    /**
     * Parses a deadline command.
     *
     * @param words the command keyword and deadline details
     * @return a command that adds the deadline
     * @throws LongfrogException if the task description or deadline is missing or invalid
     */
    private Command parseDeadlineCommand(String[] words) throws LongfrogException {
        String argument = getArgument(words, DEADLINE_USAGE);
        String[] deadlineParts = splitArgument(argument, " /by ", DEADLINE_USAGE);
        String taskName = deadlineParts[0].trim();
        validateTaskName(taskName);
        LocalDateTime deadline = parseDateTime(deadlineParts[1].trim());

        return new AddCommand(taskList, new Deadline(taskName, deadline));
    }

    /**
     * Parses an event command.
     *
     * @param words the command keyword and event details
     * @return a command that adds the event
     * @throws LongfrogException if the task description or event times are missing or invalid
     */
    private Command parseEventCommand(String[] words) throws LongfrogException {
        String argument = getArgument(words, EVENT_USAGE);
        String[] eventParts = splitArgument(argument, " /from ", EVENT_USAGE);
        String[] timeParts = splitArgument(eventParts[1], " /to ", EVENT_USAGE);

        String taskName = eventParts[0].trim();
        validateTaskName(taskName);
        LocalDateTime start = parseDateTime(timeParts[0].trim());
        LocalDateTime end = parseDateTime(timeParts[1].trim());
        if (!start.isBefore(end)) {
            throw new LongfrogException(
                    "This event must end after it begins. Check the /from and /to times.");
        }

        return new AddCommand(taskList, new Event(taskName, start, end));
    }

    /**
     * Rejects task names containing the delimiter reserved by the save-file format.
     *
     * @param taskName the proposed task description
     * @throws LongfrogException if the description contains the reserved delimiter
     */
    private void validateTaskName(String taskName) throws LongfrogException {
        if (taskName.contains("|")) {
            throw new LongfrogException(
                    "Task descriptions cannot contain “|” because it is reserved for storage.");
        }
    }

    /**
     * Splits an argument around a required delimiter and validates both resulting parts.
     *
     * @param argument the argument to split
     * @param delimiter the syntax delimiter separating the two parts
     * @param usage the required command format
     * @return the two non-blank argument parts
     * @throws LongfrogException if the delimiter or either argument part is missing
     */
    private String[] splitArgument(String argument, String delimiter, String usage) throws LongfrogException {
        String[] parts = argument.split(delimiter, 2);
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new LongfrogException("Syntax error. Expected: " + usage);
        }
        return parts;
    }

    /**
     * Returns a non-empty command argument or reports the command's required format.
     *
     * @param words the input split into its keyword and optional argument
     * @param usage the required command format
     * @return the trimmed command argument
     * @throws LongfrogException if the argument is missing or blank
     */
    private String getArgument(String[] words, String usage) throws LongfrogException {
        if (words.length < 2 || words[1].isBlank()) {
            throw new LongfrogException("Syntax error. Expected: " + usage);
        }

        String argument = words[1].trim();
        assert !argument.isEmpty() : "Validated command argument must not be empty";
        return argument;
    }

    /**
     * Parses a date-time string into a {@link LocalDateTime} object using the global format.
     *
     * @param dateTimeString the date-time text to parse (e.g., "2/12/2019 1800")
     * @return the parsed LocalDateTime object
     * @throws LongfrogException if the date-time format is invalid
     */
    private LocalDateTime parseDateTime(String dateTimeString) throws LongfrogException {
        try {
            return LocalDateTime.parse(dateTimeString, FormatUtils.INPUT_SAVE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new LongfrogException(
                    "Temporal parsing failed. Expected: d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
        }
    }

    /**
     * Parses a date-only string into a {@link LocalDate} object.
     *
     * @param dateString the date string (e.g., "2/12/2019")
     * @return the parsed LocalDate
     * @throws LongfrogException if the date format is invalid
     */
    private LocalDate parseDate(String dateString) throws LongfrogException {
        try {
            return LocalDate.parse(dateString, FormatUtils.DATE_ONLY_FORMAT);
        } catch (DateTimeParseException e) {
            throw new LongfrogException("Temporal parsing failed. Expected: d/M/yyyy (e.g., 2/12/2019)");
        }
    }

    /**
     * Parses a date command.
     *
     * @param words the command keyword and date argument
     * @return a command that lists tasks occurring on the requested date
     * @throws LongfrogException if the date argument is missing or invalid
     */
    private Command parseDateCommand(String[] words) throws LongfrogException {
        String dateString = getArgument(words, DATE_USAGE);
        LocalDate targetDate = parseDate(dateString);
        return new DateCommand(taskList, targetDate);
    }

    /**
     * Parses a find command.
     *
     * @param words the command keyword and search term
     * @return a command that searches task descriptions
     * @throws LongfrogException if the search term is missing
     */
    private Command parseFindCommand(String[] words) throws LongfrogException {
        String keyword = getArgument(words, FIND_USAGE);
        return new FindCommand(taskList, keyword);
    }

    /**
     * Rejects trailing text supplied to a command that takes no arguments.
     *
     * @param words the command keyword and any unexpected argument
     * @param usage the required command format
     * @throws LongfrogException if an argument is present
     */
    private void validateNoArgument(String[] words, String usage) throws LongfrogException {
        if (words.length > 1) {
            throw new LongfrogException("Syntax error. Expected: " + usage);
        }
    }

    /**
     * Extracts and validates the integer index from the command arguments.
     * @param words The split input array containing the keyword and argument.
     * @param usage the required command format
     * @return The 0-based task index.
     * @throws LongfrogException if the index is missing, malformed, or not positive
     */
    private int parseIndex(String[] words, String usage) throws LongfrogException {
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new LongfrogException("Index argument missing. Try: " + words[0] + " 1");
        }

        String indexArgument = words[1].trim();
        if (indexArgument.split("\\s+").length > 1) {
            throw new LongfrogException("Syntax error. Expected: " + usage);
        }

        try {
            int userIndex = Integer.parseInt(indexArgument);
            if (userIndex <= 0) {
                throw new LongfrogException("Index underflow: task numbers start at 1.");
            }

            int zeroBasedIndex = userIndex - 1;
            assert zeroBasedIndex >= 0 : "Validated task index must be non-negative";
            return zeroBasedIndex;
        } catch (NumberFormatException e) {
            throw new LongfrogException("Type mismatch: task number must be an integer.");
        }
    }
}
