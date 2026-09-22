package yappa.parser;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import yappa.command.AddCommand;
import yappa.command.ClearCommand;
import yappa.command.Command;
import yappa.command.DeleteCommand;
import yappa.command.ExitCommand;
import yappa.command.FindCommand;
import yappa.command.HelpCommand;
import yappa.command.ListCommand;
import yappa.command.MarkCommand;
import yappa.command.SortCommand;
import yappa.command.UnmarkCommand;
import yappa.exception.YappaException;
import yappa.task.Deadline;
import yappa.task.Event;
import yappa.task.SortField;
import yappa.task.SortOrder;
import yappa.task.Task;
import yappa.task.Todo;
import yappa.util.DateUtil;

/**
 * Extracts commands and task details from raw user input.
 */
public class Parser {
    private static final Map<String, CommandParser> COMMAND_PARSERS = new HashMap<>();

    private Parser() {
    }

    static {
        COMMAND_PARSERS.put("list", arguments -> {
            requireNoArguments(arguments);
            return new ListCommand();
        });
        COMMAND_PARSERS.put("clear", arguments -> {
            requireNoArguments(arguments);
            return new ClearCommand();
        });
        COMMAND_PARSERS.put("bye", arguments -> {
            requireNoArguments(arguments);
            return new ExitCommand();
        });
        COMMAND_PARSERS.put("help", arguments -> {
            requireNoArguments(arguments);
            return new HelpCommand();
        });
        COMMAND_PARSERS.put("sort", Parser::parseSort);
        COMMAND_PARSERS.put("mark", arguments -> new MarkCommand(parseIndex(arguments)));
        COMMAND_PARSERS.put("unmark", arguments -> new UnmarkCommand(parseIndex(arguments)));
        COMMAND_PARSERS.put("delete", arguments -> new DeleteCommand(parseIndex(arguments)));
        COMMAND_PARSERS.put("find",
                arguments -> new FindCommand(requireNonEmpty(arguments, "Search query cannot be empty!")));
        COMMAND_PARSERS.put("todo", Parser::parseTodo);
        COMMAND_PARSERS.put("deadline", Parser::parseDeadline);
        COMMAND_PARSERS.put("event", Parser::parseEvent);
    }

    /**
     * Parses a one-based task number into a zero-based index.
     *
     * @param argument Task number argument.
     * @return Zero-based task index.
     * @throws YappaException If a task number is missing or invalid.
     */
    private static int parseIndex(String argument) throws YappaException {

        if (argument.isEmpty()) {
            throw new YappaException("Please specify a task number!");
        }

        try {
            int taskNumber = Integer.parseInt(argument);
            if (taskNumber <= 0) {
                throw new YappaException("Task number must be greater than 0!");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new YappaException("Please give me a valid task number!", e);
        }
    }

    /**
     * Parses a deadline command.
     *
     * @param argument Deadline command arguments.
     * @return Command that adds the deadline task.
     * @throws YappaException If the command is invalid.
     */
    private static Command parseDeadline(String argument)
            throws YappaException {

        if (!argument.contains(" /by ")) {
            throw new YappaException(
                    "Oh no! Please re-enter in this format: deadline <description> /by <dd/MM/yyyy HHmm>");
        }

        String[] parts = argument.split(" /by ", 2);

        String description = parts[0];
        String deadlineText = parts[1];

        if (description.isEmpty() || deadlineText.isEmpty()) {
            throw new YappaException(
                    "Deadline description and date/time cannot be empty.");
        }

        LocalDateTime deadline = DateUtil.parseDateTime(deadlineText);
        Task task = new Deadline(description, deadline);

        return new AddCommand(task);
    }

    /**
     * Parses an event command.
     *
     * @param argument Event command arguments.
     * @return Command that adds the event task.
     * @throws YappaException If the command is invalid.
     */
    private static Command parseEvent(String argument)
            throws YappaException {

        if (!argument.contains(" /from ")
                || !argument.contains(" /to ")) {
            throw new YappaException(
                    "Oh no! Please re-enter in this format: event <description> "
                            + "/from <dd/MM/yyyy HHmm> /to <dd/MM/yyyy HHmm>");
        }

        String[] parts = argument.split(" /from | /to ", 3);

        if (parts.length < 3) {
            throw new YappaException(
                    "Please provide both a start and end time.");
        }

        String description = parts[0];
        String startTimeText = parts[1];
        String endTimeText = parts[2];

        if (description.isEmpty() || startTimeText.isEmpty() || endTimeText.isEmpty()) {
            throw new YappaException(
                    "Event description, start time, and end time "
                            + "cannot be empty.");
        }

        LocalDateTime startTime = DateUtil.parseDateTime(startTimeText);
        LocalDateTime endTime = DateUtil.parseDateTime(endTimeText);
        Task task = new Event(description, startTime, endTime);

        return new AddCommand(task);
    }

    /**
     * Parses a todo command.
     *
     * @param argument Todo command arguments.
     * @return Command that adds the todo task.
     * @throws YappaException If the command is invalid.
     */
    private static Command parseTodo(String argument)
            throws YappaException {

        String description = requireNonEmpty(argument, "Oh no! Todo description must not be empty!");

        Task task = new Todo(description);

        return new AddCommand(task);

    }

    private static SortCommand parseSort(String arguments)
            throws YappaException {
        String trimmedArguments = arguments.trim();

        if (trimmedArguments.isEmpty()) {
            return new SortCommand(
                    SortField.DESCRIPTION,
                    SortOrder.ASCENDING);
        }

        String[] parts = trimmedArguments.split("\\s+");

        if (parts.length > 2) {
            throw new YappaException(
                    "Sort format: sort [alpha|datetime] [asc|desc]");
        }

        SortField field = SortField.fromInput(parts[0]);

        SortOrder order = SortOrder.ASCENDING;

        if (parts.length == 2) {
            order = SortOrder.fromInput(parts[1]);
        }

        return new SortCommand(field, order);
    }

    /**
     * Parses the search query of a find command.
     *
     * @param arguments Search arguments.
     * @return Search query from find command.
     */
    private static String requireNonEmpty(
            String arguments, String errorMessage) throws YappaException {

        if (arguments.isBlank()) {
            throw new YappaException(errorMessage);
        }

        return arguments;
    }

    /**
     * Parses user input into an executable command.
     *
     * @param input User input to parse.
     * @return Command represented by the input.
     * @throws YappaException If the input is blank, unknown, or malformed.
     */
    public static Command parse(String input) throws YappaException {
        if (input == null || input.isBlank()) {
            throw new YappaException("Please enter a command!");
        }

        String normalizedInput = input.trim().replaceAll("\\s+", " ");
        String[] parts = normalizedInput.split(" ", 2);

        String commandWord = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1].trim() : "";

        CommandParser commandParser = COMMAND_PARSERS.get(commandWord);

        if (commandParser == null) {
            throw new YappaException("Unknown command.");
        }

        return commandParser.parse(arguments);
    }

    /**
     * Ensures that no additional arguments were provided for a command that expects
     * none.
     *
     * @param arguments Raw argument string passed to the command parser.
     * @throws YappaException If the provided argument string is not blank.
     */
    private static void requireNoArguments(String arguments) throws YappaException {
        if (!arguments.isBlank()) {
            throw new YappaException(
                    "This command does not accept arguments.");
        }
    }
}
