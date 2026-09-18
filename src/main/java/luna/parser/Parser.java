package luna.parser;

import java.util.Locale;

import luna.LunaException;
import luna.command.ArchiveCommand;
import luna.command.Command;
import luna.command.DeadlineCommand;
import luna.command.DeleteCommand;
import luna.command.EventCommand;
import luna.command.ExitCommand;
import luna.command.FindCommand;
import luna.command.HelpCommand;
import luna.command.ListArchivedCommand;
import luna.command.ListCommand;
import luna.command.MarkCommand;
import luna.command.TodoCommand;
import luna.command.UnmarkCommand;

/**
 * Converts raw user input into executable commands.
 */
public class Parser {
    /**
     * Parses raw user input into a command object.
     *
     * @param input Full command entered by the user.
     * @return Parsed command object.
     * @throws LunaException If the input is blank or the command format is invalid.
     */
    public static Command parse(String input) throws LunaException {
        if (input == null || input.isBlank()) {
            throw new LunaException("Please enter a command.");
        }

        String trimmedInput = input.trim();
        String commandWord = trimmedInput.split("\\s+", 2)[0].toLowerCase(Locale.ENGLISH);

        switch (commandWord) {
            case "list":
                return parseListCommand(trimmedInput);
            case "bye":
                requireNoArguments(trimmedInput, "bye");
                return new ExitCommand();
            case "mark":
                return new MarkCommand(trimmedInput);
            case "unmark":
                return new UnmarkCommand(trimmedInput);
            case "todo":
                return new TodoCommand(trimmedInput);
            case "deadline":
                return new DeadlineCommand(trimmedInput);
            case "event":
                return new EventCommand(trimmedInput);
            case "delete":
                return new DeleteCommand(trimmedInput);
            case "find":
                return new FindCommand(trimmedInput);
            case "help":
                requireNoArguments(trimmedInput, "help");
                return new HelpCommand();
            case "archive":
                return new ArchiveCommand(trimmedInput);
            default:
                throw new LunaException("I don't know this command :(");
        }
    }

    /**
     * Parses a command that lists either active or archived tasks.
     *
     * @param input Full command entered by the user.
     * @return Command for the requested task collection.
     * @throws LunaException If the list command has unsupported arguments.
     */
    private static Command parseListCommand(String input) throws LunaException {
        String arguments = input.substring(4).trim();
        if (arguments.isEmpty()) {
            return new ListCommand();
        }
        if (arguments.equalsIgnoreCase("archived")) {
            return new ListArchivedCommand();
        }

        throw new LunaException("Please use list or list archived.");
    }

    /**
     * Rejects unexpected text after a command that takes no arguments.
     */
    private static void requireNoArguments(String input, String commandWord) throws LunaException {
        if (!input.equalsIgnoreCase(commandWord)) {
            throw new LunaException("Please use " + commandWord + " without additional parameters.");
        }
    }
}
