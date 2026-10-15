package charliek.parser;

import java.time.DateTimeException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import charliek.command.AddCommand;
import charliek.command.Command;
import charliek.command.CommandType;
import charliek.command.DeleteCommand;
import charliek.command.ExitCommand;
import charliek.command.FindCommand;
import charliek.command.HelpCommand;
import charliek.command.ListCommand;
import charliek.command.MarkCommand;
import charliek.command.UnmarkCommand;
import charliek.exception.CharlieKException;
import charliek.exception.DuplicateParameterException;
import charliek.exception.DuplicateTaskException;
import charliek.exception.EmptyParameterException;
import charliek.exception.EmptyTaskDescriptionException;
import charliek.exception.InvalidCommandFormatException;
import charliek.exception.InvalidDateTimeException;
import charliek.exception.InvalidEventRangeException;
import charliek.exception.UnknownCommandException;
import charliek.model.Deadline;
import charliek.model.Event;
import charliek.model.Task;
import charliek.model.TaskList;
import charliek.model.ToDo;
import charliek.storage.Storage;
import charliek.ui.Ui;

/**
 * Interprets complete lines entered by the user.
 *
 * <p>The parser identifies the command and separates its argument from the
 * command keyword. It also validates task arguments and creates task objects;
 * it does not execute commands or persist tasks.</p>
 */
public class Parser {
    /**
     * Marker separating a deadline description from its date/time.
     */
    private static final String DEADLINE_MARKER = "/by";

    /**
     * Marker separating an event description from its start date/time.
     */
    private static final String EVENT_START_MARKER = "/from";

    /**
     * Marker separating an event start date/time from its end date/time.
     */
    private static final String EVENT_END_MARKER = "/to";

    /**
     * Matches a parameter marker that is a complete whitespace-delimited token.
     */
    private static final Pattern PARAMETER_TOKEN = Pattern.compile("(?<!\\S)%s(?!\\S)");

    /**
     * Matches task-number arguments without signs, decimals, or other symbols.
     */
    private static final Pattern TASK_NUMBER = Pattern.compile("[0-9]+");

    /**
     * Matches single-word arguments accepted by search and help commands.
     */
    private static final Pattern SINGLE_WORD = Pattern.compile("[A-Za-z0-9_-]+");

    /**
     * The task list used by commands created by this parser.
     */
    private final TaskList tasks;

    /**
     * The UI used by commands created by this parser.
     */
    private final Ui ui;

    /**
     * The storage used by commands created by this parser.
     */
    private final Storage storage;

    /**
     * Creates a parser that can construct executable commands.
     *
     * @param tasks the task list commands will operate on.
     * @param ui the UI commands will use for output.
     * @param storage the storage commands will use for persistence.
     */
    public Parser(TaskList tasks, Ui ui, Storage storage) {
        this.tasks = Objects.requireNonNull(tasks);
        this.ui = Objects.requireNonNull(ui);
        this.storage = Objects.requireNonNull(storage);
    }

    /**
     * Parses one complete user input line.
     *
     * @param input the line entered by the user.
     * @return an executable command for the input.
     * @throws CharlieKException when the command or its arguments are invalid.
     */
    public Command parse(String input) throws CharlieKException {
        validateInputFormat(input);
        CommandType command = CommandType.getCommandFromInput(input)
                .orElseThrow(UnknownCommandException::new);
        String argument = command.getArgumentFromInput(input);
        return switch (command) {
            case BYE -> new ExitCommand(ui);
            case LIST -> new ListCommand(tasks, ui, validateListArgument(argument));
            case MARK -> new MarkCommand(tasks, ui, storage, validateTaskNumberArgument(argument));
            case UNMARK -> new UnmarkCommand(tasks, ui, storage, validateTaskNumberArgument(argument));
            case FIND -> new FindCommand(tasks, ui, validateFindArgument(argument));
            case HELP -> new HelpCommand(ui, validateHelpArgument(argument));
            case DELETE -> new DeleteCommand(tasks, ui, storage, validateTaskNumberArgument(argument));
            case TODO -> new AddCommand(tasks, ui, storage, ensureUnique(parseToDo(argument)));
            case DEADLINE -> new AddCommand(tasks, ui, storage, ensureUnique(parseDeadline(argument)));
            case EVENT -> new AddCommand(tasks, ui, storage, ensureUnique(parseEvent(argument)));
        };
    }

    /**
     * Parses a to-do description into a task.
     *
     * @param command the argument following the {@code todo} keyword.
     * @return the parsed to-do task.
     * @throws EmptyTaskDescriptionException when the description is blank.
     */
    public ToDo parseToDo(String command) throws EmptyTaskDescriptionException, InvalidCommandFormatException {
        String description = command == null ? "" : command.trim();
        if (description.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }
        if (containsParameterMarker(description, DEADLINE_MARKER)
                || containsParameterMarker(description, EVENT_START_MARKER)
                || containsParameterMarker(description, EVENT_END_MARKER)) {
            throw new InvalidCommandFormatException(CommandType.TODO.getUsage());
        }
        return new ToDo(description);
    }

    /**
     * Parses a deadline description and date/time into a task.
     *
     * @param command the argument following the {@code deadline} keyword.
     * @return the parsed deadline task.
     * @throws EmptyTaskDescriptionException when the description is blank.
     * @throws EmptyParameterException when the {@code /by} parameter is missing or blank.
     * @throws InvalidDateTimeException when the deadline has an unsupported format.
     */
    public Deadline parseDeadline(String command)
            throws EmptyTaskDescriptionException, EmptyParameterException,
            InvalidDateTimeException, InvalidCommandFormatException, DuplicateParameterException {
        String commandText = command == null ? "" : command.trim();
        if (commandText.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        Matcher markerMatcher = matcherFor(commandText, DEADLINE_MARKER);
        int markerIndex = markerMatcher.find() ? markerMatcher.start() : -1;
        if (countParameterMarkers(commandText, DEADLINE_MARKER) > 1) {
            throw new DuplicateParameterException(DEADLINE_MARKER, CommandType.DEADLINE.getUsage());
        }
        if (containsParameterMarker(commandText, EVENT_START_MARKER)
                || containsParameterMarker(commandText, EVENT_END_MARKER)) {
            throw new InvalidCommandFormatException(CommandType.DEADLINE.getUsage());
        }

        String description = markerIndex < 0
                ? commandText
                : commandText.substring(0, markerIndex).trim();
        if (description.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }
        if (markerIndex < 0) {
            throw new EmptyParameterException();
        }

        String deadline = commandText.substring(markerMatcher.end()).trim();
        if (deadline.isEmpty()) {
            throw new EmptyParameterException();
        }

        try {
            return new Deadline(description, DateTimeParser.parseUserInput(deadline));
        } catch (DateTimeException exception) {
            throw new InvalidDateTimeException();
        }
    }

    /**
     * Parses an event description and its start/end date-times into a task.
     *
     * @param command the argument following the {@code event} keyword.
     * @return the parsed event task.
     * @throws EmptyTaskDescriptionException when the description is blank.
     * @throws EmptyParameterException when an event parameter is missing or blank.
     * @throws InvalidDateTimeException when either date/time has an unsupported format.
     */
    public Event parseEvent(String command)
            throws EmptyTaskDescriptionException, EmptyParameterException,
            InvalidDateTimeException, InvalidCommandFormatException,
            DuplicateParameterException, InvalidEventRangeException {
        String commandText = command == null ? "" : command.trim();
        if (commandText.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        int fromMarkerCount = countParameterMarkers(commandText, EVENT_START_MARKER);
        int toMarkerCount = countParameterMarkers(commandText, EVENT_END_MARKER);
        if (fromMarkerCount > 1) {
            throw new DuplicateParameterException(EVENT_START_MARKER, CommandType.EVENT.getUsage());
        }
        if (toMarkerCount > 1) {
            throw new DuplicateParameterException(EVENT_END_MARKER, CommandType.EVENT.getUsage());
        }
        if (containsParameterMarker(commandText, DEADLINE_MARKER)) {
            throw new InvalidCommandFormatException(CommandType.EVENT.getUsage());
        }

        Matcher fromMatcher = matcherFor(commandText, EVENT_START_MARKER);
        Matcher toMatcher = matcherFor(commandText, EVENT_END_MARKER);
        int fromMarkerIndex = fromMatcher.find() ? fromMatcher.start() : -1;
        String description = fromMarkerIndex < 0
                ? commandText
                : commandText.substring(0, fromMarkerIndex).trim();
        if (description.isEmpty()) {
            throw new EmptyTaskDescriptionException();
        }

        int toMarkerIndex = toMatcher.find() ? toMatcher.start() : -1;
        if (fromMarkerIndex < 0 || toMarkerIndex < 0) {
            throw new EmptyParameterException();
        }
        if (toMarkerIndex < fromMarkerIndex) {
            throw new InvalidCommandFormatException(CommandType.EVENT.getUsage());
        }

        int fromValueStart = fromMatcher.end();
        if (toMarkerIndex <= fromValueStart) {
            throw new EmptyParameterException();
        }

        String from = commandText.substring(fromValueStart, toMarkerIndex).trim();
        String to = commandText.substring(toMatcher.end()).trim();
        if (from.isEmpty() || to.isEmpty()) {
            throw new EmptyParameterException();
        }

        try {
            return new Event(description,
                    DateTimeParser.parseUserInput(from), DateTimeParser.parseUserInput(to));
        } catch (DateTimeException exception) {
            throw new InvalidDateTimeException();
        } catch (IllegalArgumentException exception) {
            throw new InvalidEventRangeException();
        }
    }

    /**
     * Rejects whitespace and control characters that make a command ambiguous.
     */
    private void validateInputFormat(String input) throws InvalidCommandFormatException {
        if (input == null || input.isBlank()
                || !input.equals(input.trim())
                || input.chars().anyMatch(character -> isUnexpectedWhitespace((char) character))
                || input.contains("  ")
                || input.chars().anyMatch(character -> character < 32 || character == 127)) {
            throw new InvalidCommandFormatException();
        }
    }

    /**
     * Validates the optional argument accepted by {@code list}.
     */
    private String validateListArgument(String argument) throws InvalidCommandFormatException {
        if (argument.isEmpty() || "time".equals(argument)) {
            return argument;
        }
        throw new InvalidCommandFormatException(CommandType.LIST.getUsage());
    }

    /**
     * Validates a one-based task number before a command is constructed.
     */
    private String validateTaskNumberArgument(String argument)
            throws EmptyParameterException, InvalidCommandFormatException {
        if (argument.isEmpty()) {
            throw new EmptyParameterException();
        }
        if (!TASK_NUMBER.matcher(argument).matches()) {
            throw new InvalidCommandFormatException("<number>, for example: mark 1");
        }
        return argument;
    }

    /**
     * Validates the single keyword accepted by {@code find}.
     */
    private String validateFindArgument(String argument)
            throws EmptyParameterException, InvalidCommandFormatException {
        if (argument.isEmpty()) {
            throw new EmptyParameterException();
        }
        if (!SINGLE_WORD.matcher(argument).matches()) {
            throw new InvalidCommandFormatException(CommandType.FIND.getUsage());
        }
        return argument;
    }

    /**
     * Validates the optional command keyword accepted by {@code help}.
     */
    private String validateHelpArgument(String argument) throws InvalidCommandFormatException {
        if (argument.isEmpty() || SINGLE_WORD.matcher(argument).matches()) {
            return argument;
        }
        throw new InvalidCommandFormatException(CommandType.HELP.getUsage());
    }

    /**
     * Rejects a duplicate task before an add command can mutate the list.
     */
    private Task ensureUnique(Task task) throws DuplicateTaskException {
        if (tasks.containsEquivalent(task)) {
            throw new DuplicateTaskException();
        }
        return task;
    }

    /**
     * Creates a matcher for a complete parameter token.
     */
    private static Matcher matcherFor(String command, String parameter) {
        String parameterPattern = String.format(PARAMETER_TOKEN.pattern(), Pattern.quote(parameter));
        return Pattern.compile(parameterPattern).matcher(command);
    }

    /**
     * Counts complete occurrences of a parameter marker.
     */
    private static int countParameterMarkers(String command, String parameter) {
        Matcher matcher = matcherFor(command, parameter);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Checks whether a complete parameter marker occurs in a command.
     */
    private static boolean containsParameterMarker(String command, String parameter) {
        return matcherFor(command, parameter).find();
    }

    /**
     * Recognizes whitespace that is not the single ordinary space allowed in commands.
     */
    private static boolean isUnexpectedWhitespace(char character) {
        return (Character.isWhitespace(character) || Character.isSpaceChar(character)) && character != ' ';
    }

}
