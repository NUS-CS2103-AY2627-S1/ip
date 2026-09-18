package herta.parser;

import java.time.LocalDate;

import herta.command.ArchiveCommand;
import herta.command.ArchivedCommand;
import herta.command.Command;
import herta.command.DeadlineCommand;
import herta.command.DeleteCommand;
import herta.command.EventCommand;
import herta.command.ExitCommand;
import herta.command.FilterCommand;
import herta.command.FindCommand;
import herta.command.ListCommand;
import herta.command.MarkCommand;
import herta.command.RestoreCommand;
import herta.command.SortCommand;
import herta.command.TodoCommand;
import herta.command.UnknownCommand;
import herta.command.UnmarkCommand;
import herta.command.UpcomingCommand;
import herta.exception.HertaException;
import herta.task.Deadline;
import herta.task.Event;
import herta.task.Todo;

/**
 * Interprets user commands and delegates specialized argument parsing.
 */
public class Parser {
    private static final String LOWERCASE_COMMAND_ERROR = "Lowercase only. Try: %s.";
    private static final String NO_ARGUMENT_COMMAND_ERROR =
            "Just use: %s. Nothing else is required.";
    private final TaskCreationParser taskCreationParser = new TaskCreationParser();
    private final ArchiveCommandParser archiveCommandParser = new ArchiveCommandParser();
    private final QueryCommandParser queryCommandParser = new QueryCommandParser();
    private final TaskIndexParser taskIndexParser = new TaskIndexParser();

    /**
     * Identifies the command represented by the user's input.
     *
     * @param input the complete user input.
     * @return the corresponding command type, or {@link CommandType#UNKNOWN}.
     */
    public CommandType parseCommandType(String input) {
        return CommandType.fromInput(input);
    }

    /**
     * Parses user input into an executable command.
     *
     * @param input the complete user input.
     * @return a command representing the input, including an
     *         {@link UnknownCommand} for unsupported input.
     * @throws HertaException if command-specific parsing fails.
     */
    public Command parse(String input) throws HertaException {
        return parse(input, parseCommandType(input));
    }

    /**
     * Parses user input using a command type that has already been identified.
     *
     * @param input the complete user input.
     * @param commandType the command type identified for the input.
     * @return a command representing the input, including an
     *         {@link UnknownCommand} for unsupported input.
     * @throws HertaException if command-specific parsing fails.
     */
    public Command parse(String input, CommandType commandType) throws HertaException {
        if (input == null || commandType == null) {
            throw new HertaException("Command parsing requires input and a command type.");
        }
        validateUnknownNoArgumentCommand(input, commandType);
        return createCommand(input, commandType);
    }

    /** Creates a command after its type and command-specific arguments are validated. */
    private Command createCommand(String input, CommandType commandType) throws HertaException {
        return switch (commandType) {
            case BYE -> createExitCommand(input, commandType);
            case LIST -> createListCommand(input, commandType);
            case FIND -> new FindCommand(parseFindKeyword(input));
            case TODO -> new TodoCommand(parseTodo(input));
            case DEADLINE -> new DeadlineCommand(parseDeadline(input));
            case EVENT -> new EventCommand(parseEvent(input));
            case DELETE -> new DeleteCommand(parseTaskIndex(input, commandType.getKeyword()));
            case MARK -> new MarkCommand(parseTaskIndex(input, commandType.getKeyword()));
            case UNMARK -> new UnmarkCommand(parseTaskIndex(input, commandType.getKeyword()));
            case ARCHIVE -> new ArchiveCommand(parseArchiveSelection(input));
            case ARCHIVED -> createArchivedCommand(input, commandType);
            case RESTORE -> new RestoreCommand(parseRestoreTaskNumber(input));
            case FILTER -> new FilterCommand(parseFilterDate(input));
            case UPCOMING -> new UpcomingCommand(parseUpcomingDays(input));
            case SORT -> createSortCommand(input);
            default -> new UnknownCommand(input);
        };
    }

    /** Creates the exit command after rejecting unexpected arguments. */
    private Command createExitCommand(String input, CommandType commandType) throws HertaException {
        validateNoArguments(input, commandType);
        return new ExitCommand();
    }

    /** Creates the list command after rejecting unexpected arguments. */
    private Command createListCommand(String input, CommandType commandType) throws HertaException {
        validateNoArguments(input, commandType);
        return new ListCommand();
    }

    /** Creates the archive-view command after rejecting unexpected arguments. */
    private Command createArchivedCommand(String input, CommandType commandType)
            throws HertaException {
        validateNoArguments(input, commandType);
        return new ArchivedCommand();
    }

    /** Creates the sort command after validating its sorting option. */
    private Command createSortCommand(String input) throws HertaException {
        validateSortCommand(input);
        return new SortCommand();
    }

    /**
     * Parses a todo command into a todo task.
     *
     * @param input the complete todo command.
     * @return the parsed todo task.
     * @throws HertaException if the todo description is empty.
     */
    public Todo parseTodo(String input) throws HertaException {
        return taskCreationParser.parseTodo(input);
    }

    /**
     * Parses the keyword from a find command.
     *
     * @param input the complete find command.
     * @return the keyword to search for.
     * @throws HertaException if the keyword is empty.
     */
    public String parseFindKeyword(String input) throws HertaException {
        return queryCommandParser.parseFindKeyword(input);
    }

    /**
     * Parses a deadline command into a deadline task.
     *
     * @param input the complete deadline command.
     * @return the parsed deadline task.
     * @throws HertaException if the command format or date/time is invalid.
     */
    public Deadline parseDeadline(String input) throws HertaException {
        return taskCreationParser.parseDeadline(input);
    }

    /**
     * Parses an event command into an event task.
     *
     * @param input the complete event command.
     * @return the parsed event task.
     * @throws HertaException if the command format, date/time, or event range is invalid.
     */
    public Event parseEvent(String input) throws HertaException {
        return taskCreationParser.parseEvent(input);
    }

    /**
     * Parses the date from a filter command.
     *
     * @param input the complete filter command.
     * @return the requested filter date.
     * @throws HertaException if the command format or date is invalid.
     */
    public LocalDate parseFilterDate(String input) throws HertaException {
        return queryCommandParser.parseFilterDate(input);
    }

    /**
     * Parses the number of days from an upcoming command.
     *
     * @param input the complete upcoming command.
     * @return a positive number of days.
     * @throws HertaException if the command does not contain a positive number.
     */
    public int parseUpcomingDays(String input) throws HertaException {
        return queryCommandParser.parseUpcomingDays(input);
    }

    /**
     * Validates a sort command.
     *
     * @param input the complete sort command.
     * @throws HertaException if the command does not request date sorting.
     */
    public void validateSortCommand(String input) throws HertaException {
        queryCommandParser.validateSortCommand(input);
    }

    /**
     * Parses a one-based task number from a task-selection command.
     *
     * @param input the complete task-selection command.
     * @param commandKeyword the command keyword used in the input.
     * @return the corresponding zero-based task index.
     * @throws HertaException if the task number is not numeric.
     */
    public int parseTaskIndex(String input, String commandKeyword) throws HertaException {
        return taskIndexParser.parseTaskIndex(input, commandKeyword);
    }

    /** Rejects trailing input for commands whose grammar has no arguments. */
    private void validateNoArguments(String input, CommandType commandType) throws HertaException {
        if (!commandType.extractArguments(input).isEmpty()) {
            throw new HertaException(getNoArgumentError(commandType.getKeyword()));
        }
    }

    /** Gives no-argument commands a usage error even when their extra text prevents recognition. */
    private void validateUnknownNoArgumentCommand(String input, CommandType commandType)
            throws HertaException {
        if (commandType != CommandType.UNKNOWN) {
            return;
        }
        String normalizedInput = input.trim();
        String commandToken = normalizedInput.split("[ \\t]+", 2)[0];
        for (CommandType supportedCommand : CommandType.values()) {
            String supportedKeyword = supportedCommand.getKeyword();
            if (isIncorrectlyCasedCommand(commandToken, supportedKeyword)) {
                throw new HertaException(LOWERCASE_COMMAND_ERROR.formatted(supportedKeyword));
            }
        }
        for (CommandType noArgumentCommand : CommandType.values()) {
            if (noArgumentCommand.canAcceptArguments()) {
                continue;
            }
            String noArgumentKeyword = noArgumentCommand.getKeyword();
            if (noArgumentKeyword.isEmpty()) {
                continue;
            }
            if (hasTrailingArguments(normalizedInput, noArgumentKeyword)) {
                throw new HertaException(getNoArgumentError(noArgumentKeyword));
            }
        }
    }

    /** Returns whether a supported command uses an incorrect letter case. */
    private boolean isIncorrectlyCasedCommand(String commandToken, String supportedKeyword) {
        return !supportedKeyword.isEmpty()
                && commandToken.equalsIgnoreCase(supportedKeyword)
                && !commandToken.equals(supportedKeyword);
    }

    /** Identifies trailing text after a no-argument command using supported separators. */
    private boolean hasTrailingArguments(String input, String keyword) {
        return input.startsWith(keyword)
                && input.length() > keyword.length()
                && CommandTokenizer.isHorizontalWhitespace(input.charAt(keyword.length()));
    }

    /** Returns usage guidance for a command that does not accept arguments. */
    private String getNoArgumentError(String keyword) {
        return NO_ARGUMENT_COMMAND_ERROR.formatted(keyword);
    }

    /**
     * Parses the selectors from an archive command.
     *
     * @param input the complete archive command.
     * @return the validated archive selection.
     * @throws HertaException if the selectors are malformed or out of integer range.
     */
    public ArchiveSelection parseArchiveSelection(String input) throws HertaException {
        return archiveCommandParser.parseArchiveSelection(input);
    }

    /**
     * Parses the single archive task number from a restore command.
     *
     * @param input the complete restore command.
     * @return the selected archive task number as a zero-based index.
     * @throws HertaException if the argument is invalid.
     */
    public int parseRestoreTaskNumber(String input) throws HertaException {
        return archiveCommandParser.parseRestoreTaskNumber(input);
    }
}
