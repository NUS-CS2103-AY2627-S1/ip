package atom.parser;

import atom.command.ByeCommand;
import atom.command.Command;
import atom.command.DeadlineCommand;
import atom.command.DeleteCommand;
import atom.command.EventCommand;
import atom.command.FindCommand;
import atom.command.ListCommand;
import atom.command.MarkCommand;
import atom.command.ScheduleCommand;
import atom.command.ToDoCommand;
import atom.command.UnmarkCommand;
import atom.exception.AtomException;
import atom.exception.AtomInvalidCommandException;
import javafx.util.Pair;

/**
 * Handles making sense of user input.
 */
public class Parser {
    /**
     * An enum for the valid commands.
     */
    public enum CommandWord {
        BYE,
        LIST,
        MARK,
        UNMARK,
        TODO,
        DEADLINE,
        EVENT,
        SCHEDULE,
        DELETE,
        FIND
    }

    /**
     * Reads the user input and breaks it down into its command and arguments
     *
     * @param input The input string.
     * @return A <code>Command</code> representing the broken down command and its arguments.
     * @throws AtomException If the command or its arguments are invalid.
     */
    public Command readLine(String input) throws AtomException {
        Pair<CommandWord, String[]> separatedLine = splitLine(input);
        CommandWord command = separatedLine.getKey();
        String[] args = separatedLine.getValue();

        return switch (command) {
            case CommandWord.BYE -> new ByeCommand(args);
            case CommandWord.LIST -> new ListCommand(args);
            case CommandWord.MARK -> new MarkCommand(args);
            case CommandWord.UNMARK -> new UnmarkCommand(args);
            case CommandWord.DELETE -> new DeleteCommand(args);
            case CommandWord.TODO -> new ToDoCommand(args);
            case CommandWord.FIND -> new FindCommand(args);
            case CommandWord.DEADLINE -> new DeadlineCommand(args);
            case CommandWord.EVENT -> new EventCommand(args);
            case CommandWord.SCHEDULE -> new ScheduleCommand(args);
        };
    }

    /**
     * Splits an input line into its command and arguments.
     *
     * @param input The input string.
     * @return A <code>Pair</code> with the first value being the command and the
     *      second value being the arguments.
     * @throws AtomInvalidCommandException If the command is invalid.
     */
    private Pair<CommandWord, String[]> splitLine(String input)
            throws AtomInvalidCommandException {
        String[] inputParts = input.strip().split("\\s+", 2);
        CommandWord command;

        try {
            command = CommandWord.valueOf(inputParts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AtomInvalidCommandException();
        }

        String[] args = new String[0];
        if (inputParts.length > 1) {
            args = inputParts[1].trim().split(" /");
        }

        return new Pair<>(command, args);
    }
}
