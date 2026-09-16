package brucli.parser;

import brucli.command.Command;
import brucli.command.DeadlineCommand;
import brucli.command.DeleteCommand;
import brucli.command.EventCommand;
import brucli.command.ExitCommand;
import brucli.command.FindCommand;
import brucli.command.GameCommand;
import brucli.command.HelpCommand;
import brucli.command.ListCommand;
import brucli.command.MarkCommand;
import brucli.command.SudoCommand;
import brucli.command.TodoCommand;
import brucli.command.UnknownCommand;
import brucli.command.UnmarkCommand;
import brucli.task.DateTimes;
import brucli.task.ListFilter;
import brucli.ui.Messages;

/**
 * Converts raw user input into executable commands.
 */
public class Parser {

    /**
     * Parses one line of user input and creates the corresponding command.
     */
    public Command parse(String input) {
        String trimmedInput = input.trim();

        if (trimmedInput.isEmpty()) {
            return new UnknownCommand();
        }

        String commandWord = trimmedInput.split("\\s+", 2)[0].toUpperCase();
        return switch (commandWord) {
            case "TODO" -> parseTodo(trimmedInput);
            case "DEADLINE" -> parseDeadline(trimmedInput);
            case "EVENT" -> parseEvent(trimmedInput);
            case "LIST" -> parseList(trimmedInput);
            case "FIND" -> parseFind(trimmedInput);
            case "MARK" -> new MarkCommand(parseTaskId(trimmedInput, "mark"));
            case "UNMARK" -> new UnmarkCommand(parseTaskId(trimmedInput, "unmark"));
            case "DELETE" -> new DeleteCommand(parseTaskId(trimmedInput, "delete"));
            case "BYE" -> new ExitCommand();
            case "SUDO" -> new SudoCommand();
            case "GAME" -> new GameCommand();
            case "HELP" -> new HelpCommand();
            default -> new UnknownCommand();
        };
    }

    /**
     * Parses a todo command and its required description.
     */
    private Command parseTodo(String input) {
        String description = getArguments(input);
        if (description.isEmpty()) {
            throw new IllegalArgumentException(Messages.todoDescriptionRequired());
        }
        return new TodoCommand(description);
    }

    /**
     * Parses a deadline command and converts its due time.
     */
    private Command parseDeadline(String input) {
        String arguments = getArguments(input);
        int byIndex = arguments.indexOf("/by");

        if (byIndex == -1) {
            throw new IllegalArgumentException(Messages.deadlineByRequired());
        }

        String description = arguments.substring(0, byIndex).trim();
        String dueText = arguments.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty() || dueText.isEmpty()) {
            throw new IllegalArgumentException(Messages.deadlineUsage());
        }

        return new DeadlineCommand(
                description,
                DateTimes.parse(dueText)
        );
    }

    /**
     * Parses an event command and converts its start and end times.
     */
    private Command parseEvent(String input) {
        String arguments = getArguments(input);
        int fromIndex = arguments.indexOf("/from");
        int toIndex = arguments.indexOf("/to");

        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new IllegalArgumentException(Messages.eventUsage());
        }

        String description = arguments.substring(0, fromIndex).trim();
        String startText = arguments.substring(
                fromIndex + "/from".length(),
                toIndex
        ).trim();
        String endText = arguments.substring(toIndex + "/to".length()).trim();

        if (description.isEmpty() || startText.isEmpty() || endText.isEmpty()) {
            throw new IllegalArgumentException(Messages.eventUsage());
        }

        return new EventCommand(
                description,
                DateTimes.parse(startText),
                DateTimes.parse(endText)
        );
    }

    /**
     * Parses either a plain list command or a date-filtered list command.
     */
    private Command parseList(String input) {
        String arguments = getArguments(input);
        if (arguments.isEmpty()) {
            return new ListCommand(null);
        }

        String[] parts = arguments.split("\\s+", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException(Messages.listUsage());
        }

        ListFilter.Type type;
        try {
            type = ListFilter.Type.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(Messages.invalidListFilter());
        }

        ListFilter filter = new ListFilter(
                type,
                DateTimes.parse(parts[1])
        );
        return new ListCommand(filter);
    }

    /**
     * Parses a find command and its required keyword.
     */
    private Command parseFind(String input) {
        String keyword = getArguments(input);
        if (keyword.isEmpty()) {
            throw new IllegalArgumentException("A find command needs a keyword!");
        }
        return new FindCommand(keyword);
    }

    /**
     * Parses a one-based task number and converts it to a zero-based ID.
     */
    private int parseTaskId(String input, String commandName) {
        String arguments = getArguments(input);
        if (arguments.isEmpty()) {
            throw new IllegalArgumentException(Messages.taskNumberRequired(commandName));
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(Messages.taskNumberMustBeNumeric());
        }

        if (taskNumber <= 0) {
            throw new IllegalArgumentException(Messages.taskNumberMustBePositive());
        }

        return taskNumber - 1;
    }

    /**
     * Returns everything after the command word.
     */
    private String getArguments(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        return parts.length < 2 ? "" : parts[1].trim();
    }
}
