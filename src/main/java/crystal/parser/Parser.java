package crystal.parser;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import crystal.command.AddCommand;
import crystal.command.Command;
import crystal.command.DeleteCommand;
import crystal.command.ExitCommand;
import crystal.command.FindCommand;
import crystal.command.HelpCommand;
import crystal.command.ListCommand;
import crystal.command.MarkCommand;
import crystal.command.UnmarkCommand;
import crystal.exception.CrystalException;
import crystal.task.Deadline;
import crystal.task.Event;
import crystal.task.TaskDateTime;
import crystal.task.Todo;
import crystal.task.WithinPeriod;

/**
 * Interprets user commands and converts their arguments into application data.
 */
public class Parser {
    /** Prefix before a date in a list-on-date command. */
    private static final String LIST_ON_PREFIX = "list /on ";

    /** Separator between a deadline description and its date or time. */
    private static final String BY_SEPARATOR = " /by ";

    /** Separator between an event description and its start. */
    private static final String FROM_SEPARATOR = " /from ";

    /** Separator before an event's end. */
    private static final String TO_SEPARATOR = " /to ";

    /** Delimiter reserved for separating fields in the saved task file. */
    private static final String STORAGE_SEPARATOR = " | ";

    /** Message shown when the user submits no visible text. */
    private static final String EMPTY_COMMAND_MESSAGE = "I cant reply to nothing...";

    /** Message shown when outer spaces make a command ambiguous. */
    private static final String OUTER_WHITESPACE_MESSAGE =
            "Please remove spaces from the start or end of your command!";

    /**
     * Prevents construction because command parsing has no instance state.
     */
    private Parser() {
    }

    /**
     * Parses a complete user instruction into an executable command.
     *
     * @param command command entered by the user.
     * @return concrete command containing all parsed arguments.
     * @throws CrystalException if the command or its arguments are invalid.
     */
    public static Command parse(String command) throws CrystalException {
        if (command == null || command.isBlank()) {
            throw new CrystalException(EMPTY_COMMAND_MESSAGE);
        }
        if (!command.equals(command.strip())) {
            throw new CrystalException(OUTER_WHITESPACE_MESSAGE);
        }

        CommandType commandType = parseCommandType(command);
        return switch (commandType) {
            case TODO, DEADLINE, EVENT, WITHIN -> parseAddCommand(command, commandType);
            case MARK, UNMARK, DELETE -> parseMutationCommand(command, commandType);
            case LIST -> new ListCommand(parseListCommand(command));
            case FIND -> parseFindCommand(command);
            case HELP -> new HelpCommand();
            case EXIT -> new ExitCommand();
            case UNKNOWN -> throw new CrystalException("I don't know what that means :-(");
        };
    }

    /**
     * Creates a find command containing a nonblank search keyword.
     *
     * @param command full find command.
     * @return command containing the trimmed keyword.
     * @throws CrystalException if no keyword is supplied.
     */
    private static Command parseFindCommand(String command) throws CrystalException {
        String prefix = CommandType.FIND.getKeyword() + " ";
        if (!command.startsWith(prefix)) {
            throw new CrystalException("To find tasks, enter 'find [keyword]'!");
        }

        String keyword = command.substring(prefix.length()).trim();
        if (keyword.isBlank()) {
            throw new CrystalException("To find tasks, enter 'find [keyword]'!");
        }
        return new FindCommand(keyword);
    }

    /**
     * Determines the type of a command entered by the user.
     * The exit command must match exactly, while other known commands are
     * recognized by prefix so their malformed forms receive specific errors.
     *
     * @param command command entered by the user.
     * @return matching command type, or {@link CommandType#UNKNOWN} if none matches.
     */
    private static CommandType parseCommandType(String command) {
        if (command.equals(CommandType.EXIT.getKeyword())) {
            return CommandType.EXIT;
        }
        if (command.equals(CommandType.HELP.getKeyword())) {
            return CommandType.HELP;
        }

        return Arrays.stream(CommandType.values())
                .filter(commandType -> commandType != CommandType.EXIT)
                .filter(commandType -> commandType != CommandType.HELP)
                .filter(commandType -> commandType != CommandType.UNKNOWN)
                .filter(commandType -> command.startsWith(commandType.getKeyword()))
                .findFirst()
                .orElse(CommandType.UNKNOWN);
    }

    /**
     * Parses a list command and returns its optional date filter.
     *
     * @param command full list command.
     * @return empty for {@code list}, or the date supplied to {@code list /on}.
     * @throws CrystalException if the list command or its date is invalid.
     */
    private static Optional<LocalDate> parseListCommand(String command)
            throws CrystalException {
        if (command.equals(CommandType.LIST.getKeyword())) {
            return Optional.empty();
        }

        if (command.equals("list /on") || command.startsWith(LIST_ON_PREFIX)) {
            if (!command.startsWith(LIST_ON_PREFIX)
                    || command.substring(LIST_ON_PREFIX.length()).isBlank()) {
                throw new CrystalException(
                        "To list tasks on a date, enter 'list /on [date]'!");
            }

            String dateInput = command.substring(LIST_ON_PREFIX.length());
            LocalDate date = TaskDateTime.parseDate(dateInput);
            if (date == null) {
                throw new CrystalException("I couldn't understand that date!");
            }
            return Optional.of(date);
        }

        throw new CrystalException("To view your task list, simply enter 'list'!");
    }

    /**
     * Creates the mutation command described by a mark, unmark, or delete command.
     *
     * @param command full mark, unmark, or delete command.
     * @param commandType type of task mutation.
     * @return command targeting the parsed task number.
     * @throws CrystalException if the command does not contain a task number.
     */
    private static Command parseMutationCommand(String command, CommandType commandType)
            throws CrystalException {
        assert commandType == CommandType.MARK
                || commandType == CommandType.UNMARK
                || commandType == CommandType.DELETE
                : "Mutation parsing requires a mutation command type";

        int taskIndex = parseTaskIndex(command, commandType);
        return switch (commandType) {
            case MARK -> new MarkCommand(taskIndex);
            case UNMARK -> new UnmarkCommand(taskIndex);
            case DELETE -> new DeleteCommand(taskIndex);
            default -> throw new CrystalException("I don't know what that means :-(");
        };
    }

    /**
     * Converts the task number in a command into a zero-based list index.
     * Existence is checked when the resulting command executes against the task list.
     *
     * @param command full mark, unmark, or delete command.
     * @param commandType type of task mutation.
     * @return zero-based index supplied by the user.
     * @throws CrystalException if the command does not contain a task number.
     */
    private static int parseTaskIndex(String command, CommandType commandType)
            throws CrystalException {
        String action = commandType.getKeyword();
        String prefix = action + " ";
        if (!command.startsWith(prefix)) {
            throw new CrystalException("You have to " + action + " a task number!");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(command.substring(prefix.length()));
        } catch (NumberFormatException exception) {
            throw new CrystalException("You have to " + action + " a task number!");
        }
        return taskNumber - 1;
    }

    /**
     * Creates an add command containing the described todo, deadline, or event.
     *
     * @param command task creation command.
     * @param commandType type of task to create.
     * @return add command containing the parsed task.
     * @throws CrystalException if required task details are missing.
     */
    private static Command parseAddCommand(String command, CommandType commandType)
            throws CrystalException {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                || commandType == CommandType.WITHIN
                : "Add parsing requires an add command type";

        return switch (commandType) {
            case TODO -> parseTodoCommand(command);
            case DEADLINE -> parseDeadlineCommand(command);
            case EVENT -> parseEventCommand(command);
            case WITHIN -> parseWithinPeriodCommand(command);
            default -> throw new CrystalException("I don't know what that means :-(");
        };
    }

    /**
     * Creates an add command containing a todo description.
     *
     * @param command complete todo command.
     * @return add command containing the parsed todo.
     * @throws CrystalException if the description is missing.
     */
    private static Command parseTodoCommand(String command) throws CrystalException {
        String usageMessage = "A todo must have a description!";
        String prefix = CommandType.TODO.getKeyword() + " ";
        if (!command.startsWith(prefix)) {
            throw new CrystalException(usageMessage);
        }

        String description = command.substring(prefix.length());
        validateTaskFields(usageMessage, description);
        return new AddCommand(new Todo(description));
    }

    /**
     * Creates an add command containing a deadline description and time.
     *
     * @param command complete deadline command.
     * @return add command containing the parsed deadline.
     * @throws CrystalException if the description or deadline is missing.
     */
    private static Command parseDeadlineCommand(String command) throws CrystalException {
        String usageMessage = "A deadline must have a description and a /by time!";
        String prefix = CommandType.DEADLINE.getKeyword() + " ";
        if (!command.startsWith(prefix)) {
            throw new CrystalException(usageMessage);
        }

        String details = command.substring(prefix.length());
        int byIndex = details.indexOf(BY_SEPARATOR);
        if (byIndex <= 0 || byIndex + BY_SEPARATOR.length() >= details.length()
                || hasRepeatedSeparator(details, BY_SEPARATOR, byIndex)) {
            throw new CrystalException(usageMessage);
        }

        String description = details.substring(0, byIndex);
        String by = details.substring(byIndex + BY_SEPARATOR.length());
        validateTaskFields(usageMessage, description, by);
        return new AddCommand(new Deadline(description, by));
    }

    /**
     * Creates an add command containing an event description and time period.
     *
     * @param command complete event command.
     * @return add command containing the parsed event.
     * @throws CrystalException if the description, start, or end is missing.
     */
    private static Command parseEventCommand(String command) throws CrystalException {
        String usageMessage =
                "An event must have a description, a /from time and a /to time!";
        String prefix = CommandType.EVENT.getKeyword() + " ";
        if (!command.startsWith(prefix)) {
            throw new CrystalException(usageMessage);
        }

        String details = command.substring(prefix.length());
        int fromIndex = details.indexOf(FROM_SEPARATOR);
        int toIndex = details.indexOf(TO_SEPARATOR, fromIndex + FROM_SEPARATOR.length());
        if (fromIndex <= 0 || toIndex <= fromIndex + FROM_SEPARATOR.length()
                || toIndex + TO_SEPARATOR.length() >= details.length()
                || hasRepeatedSeparator(details, FROM_SEPARATOR, fromIndex)
                || hasRepeatedSeparator(details, TO_SEPARATOR, toIndex)) {
            throw new CrystalException(usageMessage);
        }

        String description = details.substring(0, fromIndex);
        String from = details.substring(fromIndex + FROM_SEPARATOR.length(), toIndex);
        String to = details.substring(toIndex + TO_SEPARATOR.length());
        validateTaskFields(usageMessage, description, from, to);
        return new AddCommand(new Event(description, from, to));
    }

    /**
     * Creates an add command containing a task and its completion period.
     *
     * @param command complete within-period task command.
     * @return add command containing the parsed within-period task.
     * @throws CrystalException if the description or period is missing.
     */
    private static Command parseWithinPeriodCommand(String command) throws CrystalException {
        String prefix = CommandType.WITHIN.getKeyword() + " ";
        String usageMessage = "A within-period task must have a description, "
                + "a /from value and a /to value!";
        if (!command.startsWith(prefix)) {
            throw new CrystalException(usageMessage);
        }

        String details = command.substring(prefix.length());
        int fromIndex = details.indexOf(FROM_SEPARATOR);
        int toIndex = details.indexOf(TO_SEPARATOR, fromIndex + FROM_SEPARATOR.length());
        if (fromIndex <= 0 || toIndex <= fromIndex + FROM_SEPARATOR.length()
                || toIndex + TO_SEPARATOR.length() >= details.length()
                || hasRepeatedSeparator(details, FROM_SEPARATOR, fromIndex)
                || hasRepeatedSeparator(details, TO_SEPARATOR, toIndex)) {
            throw new CrystalException(usageMessage);
        }

        String description = details.substring(0, fromIndex);
        String from = details.substring(fromIndex + FROM_SEPARATOR.length(), toIndex);
        String to = details.substring(toIndex + TO_SEPARATOR.length());
        validateTaskFields(usageMessage, description, from, to);
        return new AddCommand(new WithinPeriod(description, from, to));
    }

    /**
     * Returns whether a command field separator occurs more than once.
     *
     * @param details command details containing the separator.
     * @param separator separator to count.
     * @param firstIndex position of the first separator, or {@code -1}.
     * @return {@code true} if another separator follows the first.
     */
    private static boolean hasRepeatedSeparator(
            String details, String separator, int firstIndex) {
        return firstIndex >= 0
                && details.indexOf(separator, firstIndex + separator.length()) >= 0;
    }

    /**
     * Validates nonblank task fields without restricting free-form date text.
     *
     * @param usageMessage error shown for a missing or padded field.
     * @param fields task fields to validate.
     * @throws CrystalException if a field is blank, padded, or unsafe to save.
     */
    private static void validateTaskFields(String usageMessage, String... fields)
            throws CrystalException {
        for (String field : fields) {
            if (field.isBlank() || !field.equals(field.strip())) {
                throw new CrystalException(usageMessage);
            }
            if (field.contains(STORAGE_SEPARATOR)) {
                throw new CrystalException("Task details cannot contain ' | '!");
            }
        }
    }
}
