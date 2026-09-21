package echo.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import echo.command.AddCommand;
import echo.command.Command;
import echo.command.DeleteCommand;
import echo.command.ExitCommand;
import echo.command.FindCommand;
import echo.command.ListCommand;
import echo.command.MarkCommand;
import echo.command.StatsCommand;
import echo.exception.DeadlineFormatException;
import echo.exception.DeleteFormatException;
import echo.exception.EchoException;
import echo.exception.EventFormatException;
import echo.exception.FindFormatException;
import echo.exception.InvalidEventPeriodException;
import echo.exception.InvalidTaskNumberException;
import echo.exception.StatsFormatException;
import echo.exception.TaskNumberFormatException;
import echo.exception.TodoFormatException;
import echo.exception.UnexpectedArgumentsException;
import echo.exception.UnknownCommandException;
import echo.task.Deadline;
import echo.task.Event;
import echo.task.Todo;
import echo.utils.DateTimeUtility;


/**
 * Converts a line entered by the user into an executable {@link Command}.
 * It validates the input and builds the matching command object; running
 * the command against the task list happens later in {@link Command#execute}.
 */
public class Parser {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String FIND_COMMAND = "find";
    private static final String STATS_COMMAND = "stats";
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";

    /**
     * Parses one user-input line into a ready-to-run command.
     *
     * @param input command line entered by the user
     * @return a command ready for Echo to execute
     * @throws EchoException if the command or its arguments are invalid
     */
    public Command parse(String input) throws EchoException {
        if (input == null || input.isBlank()) {
            throw new UnknownCommandException();
        }
        String normalizedInput = normalizeWhitespace(input);
        String keyword = normalizedInput.split(" ", 2)[0];
        String arguments = normalizedInput.substring(keyword.length()).trim();

        return switch (keyword) {
            case BYE_COMMAND -> parseBye(arguments);
            case LIST_COMMAND -> parseList(arguments);
            case TODO_COMMAND -> parseTodo(arguments);
            case DEADLINE_COMMAND -> parseDeadline(arguments);
            case EVENT_COMMAND -> parseEvent(arguments);
            case MARK_COMMAND -> new MarkCommand(parseIndex(arguments,
                    new TaskNumberFormatException(MARK_COMMAND), new InvalidTaskNumberException()), true);
            case UNMARK_COMMAND -> new MarkCommand(parseIndex(arguments,
                    new TaskNumberFormatException(UNMARK_COMMAND), new InvalidTaskNumberException()), false);
            case DELETE_COMMAND -> new DeleteCommand(parseIndex(arguments,
                    new DeleteFormatException(), new DeleteFormatException()));
            case FIND_COMMAND -> parseFind(arguments);
            case STATS_COMMAND -> parseStats(arguments);
            default -> throw new UnknownCommandException();
        };
    }

    private Command parseBye(String arguments) throws EchoException {
        requireNoArguments(BYE_COMMAND, arguments);
        return new ExitCommand();
    }

    private Command parseList(String arguments) throws EchoException {
        requireNoArguments(LIST_COMMAND, arguments);
        return new ListCommand();
    }

    /**
     * Parses the string arguments according to what is expected
     * of the {@code todo} command and returns a Command.
     * Throws EchoException otherwise.
     * @param arguments string arguments of the command
     * @return a {@link Command} that can be executed
     * @throws EchoException if the arguments do not adhere to the {@code todo} format
     */
    private Command parseTodo(String arguments) throws EchoException {
        if (arguments.isEmpty()) {
            throw new TodoFormatException();
        }
        requireSavable(arguments);
        return new AddCommand(new Todo(arguments));
    }

    /**
     * Parses the string arguments according to what is expected
     * of the deadline command and returns a {@link Command}.
     * Throws EchoException otherwise.
     * @param arguments string arguments of the command
     * @return a {@link Command} that can be executed
     * @throws EchoException if the arguments do not adhere to the deadline format
     */
    private Command parseFind(String arguments) throws EchoException {
        if (arguments.isEmpty()) {
            throw new FindFormatException();
        }
        return new FindCommand(arguments);
    }

    private Command parseStats(String arguments) throws EchoException {
        if (!arguments.isEmpty()) {
            throw new StatsFormatException();
        }
        return new StatsCommand();
    }

    private Command parseDeadline(String arguments) throws EchoException {
        if (countParameter(arguments, "/by") != 1) {
            throw new DeadlineFormatException();
        }

        int separatorIndex = arguments.indexOf(BY_SEPARATOR);
        if (separatorIndex < 0) {
            throw new DeadlineFormatException();
        }
        String description = arguments.substring(0, separatorIndex).trim();
        String dueDateTime = arguments.substring(separatorIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty() || dueDateTime.isEmpty()) {
            throw new DeadlineFormatException();
        }
        requireSavable(description);
        requireSavable(dueDateTime);

        LocalDateTime by = parseDateTime(dueDateTime, new DeadlineFormatException());
        return new AddCommand(new Deadline(description, by));
    }

    /**
     * Parses the string arguments according to what is expected
     * of the event command and returns a {@link Command}.
     * Throws EchoException otherwise.
     * @param arguments string arguments of the command
     * @return a {@link Command} that can be executed
     * @throws EchoException if the arguments do not adhere to the event format
     */
    private Command parseEvent(String arguments) throws EchoException {
        if (countParameter(arguments, "/from") != 1 || countParameter(arguments, "/to") != 1) {
            throw new EventFormatException();
        }

        int fromIndex = arguments.indexOf(FROM_SEPARATOR);
        int toIndex = arguments.indexOf(TO_SEPARATOR);
        if (fromIndex < 0 || toIndex < 0 || fromIndex >= toIndex) {
            throw new EventFormatException();
        }

        String description = arguments.substring(0, fromIndex).trim();
        String startDateTime = arguments.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String endDateTime = arguments.substring(toIndex + TO_SEPARATOR.length()).trim();
        if (description.isEmpty() || startDateTime.isEmpty() || endDateTime.isEmpty()) {
            throw new EventFormatException();
        }
        requireSavable(description);
        requireSavable(startDateTime);
        requireSavable(endDateTime);

        LocalDateTime from = parseDateTime(startDateTime, new EventFormatException());
        LocalDateTime to = parseDateTime(endDateTime, new EventFormatException());
        if (!from.isBefore(to)) {
            throw new InvalidEventPeriodException();
        }
        return new AddCommand(new Event(description, from, to));
    }

    private LocalDateTime parseDateTime(String dateTimeText, EchoException invalidFormatError)
            throws EchoException {
        try {
            return LocalDateTime.parse(dateTimeText, DateTimeUtility.INPUT_FORMAT);
        } catch (DateTimeParseException exception) {
            throw invalidFormatError;
        }
    }

    /**
     * Converts a one-based task number into a zero-based index. Mark,
     * unmark, and delete share this method because they differ only in
     * which errors their format messages describe.
     *
     * @param taskNumberText raw text following the command keyword
     * @param emptyError error for a missing number, e.g. {@code mark} alone
     * @param invalidError error for text that is not a whole number
     * @return 0-based index into the task list
     * @throws EchoException using the supplied errors when the text is unusable
     */
    private int parseIndex(String taskNumberText, EchoException emptyError, EchoException invalidError)
            throws EchoException {
        if (taskNumberText.isEmpty()) {
            throw emptyError;
        }
        if (!taskNumberText.matches("[0-9]+")) {
            throw invalidError;
        }
        try {
            return Integer.parseInt(taskNumberText) - 1;
        } catch (NumberFormatException exception) {
            throw invalidError;
        }
    }

    /**
     * Rejects task details containing the pipe character, which is reserved
     * as the field separator in the save file.
     *
     * @param detail one user-supplied task field
     * @throws EchoException when the detail contains a pipe character
     */
    private void requireSavable(String detail) throws EchoException {
        if (detail.contains("|")) {
            throw new EchoException("The '|' character interferes with mission records, so it cannot be used.");
        }
    }

    private void requireNoArguments(String keyword, String arguments) throws EchoException {
        if (!arguments.isEmpty()) {
            throw new UnexpectedArgumentsException(keyword);
        }
    }

    private String normalizeWhitespace(String input) {
        return input.trim().replaceAll("\\s+", " ");
    }

    private int countParameter(String arguments, String parameter) {
        int count = 0;
        for (String token : arguments.split(" ")) {
            if (token.equals(parameter)) {
                count++;
            }
        }
        return count;
    }
}
