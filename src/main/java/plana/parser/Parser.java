package plana.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import plana.command.AddCommand;
import plana.command.Command;
import plana.command.CommandType;
import plana.command.DeleteCommand;
import plana.command.ExitCommand;
import plana.command.FindCommand;
import plana.command.HelpCommand;
import plana.command.InvalidCommand;
import plana.command.ListCommand;
import plana.command.MarkCommand;
import plana.command.OnCommand;
import plana.command.UnmarkCommand;
import plana.exception.PlanaException;
import plana.task.Deadline;
import plana.task.Event;
import plana.task.ToDo;

/**
 * Converts raw console input into the command type and arguments used by
 * Plana's command loop.
 */
public class Parser {
    private static final String EMPTY_COMMAND_ERROR = "Oops, I didn't catch a command."
            + " Type 'help' to see what I can do :>";
    private static final String DEADLINE_USAGE = "Try: deadline <description> /by <date>.";
    private static final String EVENT_USAGE = "Try: event <description> /from <start> /to <end>.";
    private static final String DATE_FORMAT_HINT = "Use the date format yyyy-MM-dd, like 2019-10-15.";
    private final TaskCommandParser taskCommandParser = new TaskCommandParser(this);
    private final ClientCommandParser clientCommandParser = new ClientCommandParser();

    /**
     * Parses one complete line entered by the user.
     *
     * @param input the complete command entered by the user.
     * @return the recognized command and its trimmed arguments
     */
    public ParsedCommand parse(String input) {
        String normalizedInput = input == null ? "" : input.trim();
        CommandType type = CommandType.parseInput(normalizedInput);
        String arguments = extractArguments(normalizedInput, type);
        return new ParsedCommand(type, arguments);
    }

    /**
     * Parses one input line into an executable command.
     *
     * <p>This is the boundary between command syntax and command behavior:
     * {@link Plana} only needs to execute the returned command.</p>
     *
     * @param input the complete command entered by the user.
     * @return the command represented by the input
     * @throws PlanaException if the command contains invalid structured data
     */
    public Command parseCommand(String input) throws PlanaException {
        ParsedCommand parsedCommand = parse(input);
        return switch (parsedCommand.type()) {
            case BYE -> {
                ensureNoArguments("bye", parsedCommand.arguments());
                yield new ExitCommand();
            }
            case HELP -> {
                ensureNoArguments("help", parsedCommand.arguments());
                yield new HelpCommand();
            }
            case LIST -> {
                ensureNoArguments("list", parsedCommand.arguments());
                yield new ListCommand();
            }
            case ON -> parseOnCommand(parsedCommand.arguments());
            case FIND -> parseFindCommand(parsedCommand.arguments());
            case DELETE -> new DeleteCommand(parsedCommand.arguments());
            case MARK -> new MarkCommand(parsedCommand.arguments());
            case UNMARK -> new UnmarkCommand(parsedCommand.arguments());
            case CLIENT -> clientCommandParser.parse(parsedCommand.arguments());
            case DEADLINE, EVENT, TODO -> parseAddCommand(parsedCommand);
            case UNKNOWN -> parseInvalidCommand(input);
        };
    }

    /**
     * Parses the syntax and dates for a task-creation command.
     *
     * @param command the command and arguments returned by {@link #parse(String)}.
     * @return the validated task description and parsed date values
     * @throws PlanaException if the command syntax or any date is invalid
     */
    public TaskArguments parseTaskArguments(ParsedCommand command) throws PlanaException {
        return taskCommandParser.parseTaskArguments(command);
    }

    /**
     * Parses a date used by a task command or the {@code on} query.
     *
     * @param dateText the date entered by the user.
     * @param commandText the command using the date.
     * @return the parsed date
     * @throws PlanaException if the date is not in {@code yyyy-MM-dd} format
     */
    public LocalDate parseDate(String dateText, String commandText) throws PlanaException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            if (commandText.equals("deadline")) {
                throw deadlineError("that deadline date isn't valid. " + DATE_FORMAT_HINT);
            }
            if (commandText.equals("event")) {
                throw eventError("that event date isn't valid. " + DATE_FORMAT_HINT);
            }
            throw new PlanaException("Oops, that query date isn't valid. " + DATE_FORMAT_HINT
                    + " Try: on <date>.");
        }
    }

    /**
     * Parses the date argument of an {@code on} command.
     *
     * @param dateText the date text supplied with the command.
     * @return the date-filtering command
     * @throws PlanaException if the date is missing or invalid
     */
    private Command parseOnCommand(String dateText) throws PlanaException {
        if (dateText.isBlank()) {
            throw new PlanaException("Oops, on needs a date. Try: on <date>.");
        }
        return new OnCommand(parseDate(dateText, "on"));
    }

    /**
     * Rejects unexpected arguments for a command that takes none.
     *
     * @param commandText the command's user-facing name.
     * @param arguments the text supplied after the command.
     * @throws PlanaException if arguments were supplied.
     */
    private void ensureNoArguments(String commandText, String arguments) throws PlanaException {
        if (!arguments.isBlank()) {
            throw new PlanaException("Oops, " + commandText
                    + " doesn't take any arguments. Try: " + commandText + ".");
        }
    }

    /**
     * Parses the keyword argument of a {@code find} command.
     *
     * @param keyword the keyword supplied with the command.
     * @return the command that searches for the keyword
     * @throws PlanaException if the keyword is missing
     */
    private Command parseFindCommand(String keyword) throws PlanaException {
        if (keyword.isBlank()) {
            throw new PlanaException("Oops, find needs a keyword. Try: find <keyword>.");
        }
        return new FindCommand(keyword);
    }

    /**
     * Converts a parsed task-creation command into a concrete add command.
     *
     * @param command the parsed task-creation command.
     * @return the command that creates the requested task
     * @throws PlanaException if the task arguments are invalid
     */
    private Command parseAddCommand(ParsedCommand command) throws PlanaException {
        TaskArguments taskArguments = parseTaskArguments(command);
        // Each task parser validates its description, and date-bearing task
        // parsers populate exactly the date fields required by that task type.
        assert taskArguments.description() != null && !taskArguments.description().isBlank()
                : "Validated task arguments must contain a description.";
        return switch (command.type()) {
            case TODO -> {
                assert taskArguments.firstDate() == null && taskArguments.secondDate() == null
                        : "ToDo arguments must not contain dates.";
                yield new AddCommand(new ToDo(taskArguments.description()));
            }
            case DEADLINE -> {
                assert taskArguments.firstDate() != null && taskArguments.secondDate() == null
                        : "Deadline arguments must contain only a due date.";
                yield new AddCommand(new Deadline(taskArguments.description(), taskArguments.firstDate()));
            }
            case EVENT -> {
                assert taskArguments.firstDate() != null && taskArguments.secondDate() != null
                        : "Event arguments must contain both boundary dates.";
                yield new AddCommand(new Event(taskArguments.description(), taskArguments.firstDate(),
                        taskArguments.secondDate()));
            }
            default -> throw new IllegalArgumentException("Task arguments requested for a non-task command");
        };
    }

    /**
     * Creates a command that reports why an unrecognized input cannot run.
     *
     * @param input the raw input entered by the user.
     * @return an invalid command containing the appropriate error message
     */
    private Command parseInvalidCommand(String input) {
        if (input.isBlank()) {
            return new InvalidCommand(EMPTY_COMMAND_ERROR);
        }
        return new InvalidCommand("Oops, I don't recognize '" + input + "'."
                + " Type help to see the commands I know.");
    }

    /**
     * Creates a consistently formatted deadline parsing error.
     *
     * @param problem the specific problem found in the command.
     * @return an exception containing the problem and deadline usage guidance
     */
    static PlanaException deadlineError(String problem) {
        return new PlanaException("Oops, " + problem + " " + DEADLINE_USAGE);
    }

    /**
     * Creates a consistently formatted event parsing error.
     *
     * @param problem the specific problem found in the command.
     * @return an exception containing the problem and event usage guidance
     */
    static PlanaException eventError(String problem) {
        return new PlanaException("Oops, " + problem + " " + EVENT_USAGE);
    }

    /**
     * Extracts the portion of input after a command keyword.
     *
     * @param input the complete command entered by the user.
     * @param type the command recognized from the input.
     * @return the command arguments, or an empty string for commands without arguments
     */
    private String extractArguments(String input, CommandType type) {
        if (type == CommandType.UNKNOWN) {
            return "";
        }
        if (type == CommandType.HELP && input.startsWith("?")) {
            return input.substring(1).trim();
        }
        if (type == CommandType.HELP && !input.regionMatches(true, 0, type.getCommandText(), 0,
                type.getCommandText().length())) {
            return "";
        }
        return input.substring(type.getCommandText().length()).trim();
    }

    /**
     * Holds the result of parsing one user command.
     *
     * @param type the command recognized from the input.
     * @param arguments the trimmed text after the command keyword.
     */
    public record ParsedCommand(CommandType type, String arguments) {
    }

    /**
     * Holds the validated values needed to construct a task.
     *
     * @param description the task description.
     * @param firstDate the due date, or event start date.
     * @param secondDate the event end date, or {@code null} for other task types.
     */
    public record TaskArguments(String description, LocalDate firstDate, LocalDate secondDate) {
    }

}
