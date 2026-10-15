package nutrisoy.parser;

import java.util.Locale;

import nutrisoy.command.Command;
import nutrisoy.command.DeadlineCommand;
import nutrisoy.command.DeleteCommand;
import nutrisoy.command.EventCommand;
import nutrisoy.command.ExitCommand;
import nutrisoy.command.FindCommand;
import nutrisoy.command.ListCommand;
import nutrisoy.command.MarkCommand;
import nutrisoy.command.TagCommand;
import nutrisoy.command.TodoCommand;
import nutrisoy.command.UnmarkCommand;
import nutrisoy.command.UntagCommand;
import nutrisoy.exception.DukeException;

/**
 * Converts user input into executable commands.
 */
public class Parser {
    /**
     * Parses a complete user command into its corresponding command object.
     *
     * @param fullCommand command text entered by the user
     * @return command represented by the supplied text
     * @throws DukeException if the command is empty or unrecognised
     */
    public static Command parse(String fullCommand) throws DukeException {
        if (fullCommand == null) {
            throw new DukeException("Command cannot be null.");
        }
        String trimmed = fullCommand.trim();
        if (trimmed.isEmpty()) {
            throw new DukeException("Command cannot be empty.");
        }

        String[] parts = trimmed.split("\\s+", 2);
        assert parts.length >= 1 : "A trimmed command must have a command word";
        String commandWord = parts[0].toLowerCase(Locale.ROOT);
        String arguments = parts.length > 1 ? parts[1].trim() : "";

        switch (commandWord) {
            case "bye":
                rejectUnexpectedArguments(commandWord, arguments);
                return new ExitCommand();
            case "list":
                rejectUnexpectedArguments(commandWord, arguments);
                return new ListCommand();
            case "todo":
                return new TodoCommand(arguments);
            case "deadline":
                return new DeadlineCommand(arguments);
            case "event":
                return new EventCommand(arguments);
            case "mark":
                return new MarkCommand(arguments);
            case "unmark":
                return new UnmarkCommand(arguments);
            case "delete":
                return new DeleteCommand(arguments);
            case "find":
                return new FindCommand(arguments);
            case "tag":
                return new TagCommand(arguments);
            case "untag":
                return new UntagCommand(arguments);
            default:
                throw new DukeException("That command isn't on my guest list. Check the spelling and try again.");
        }
    }

    private static void rejectUnexpectedArguments(String commandWord, String arguments) throws DukeException {
        if (!arguments.isEmpty()) {
            throw new DukeException("The '" + commandWord + "' command does not accept extra arguments.");
        }
    }
}
