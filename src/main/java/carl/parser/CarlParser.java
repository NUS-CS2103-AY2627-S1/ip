package carl.parser;

import java.util.HashMap;
import java.util.Map;

import carl.commands.ByeCommand;
import carl.commands.Command;
import carl.commands.DeleteCommand;
import carl.commands.HelpCommand;
import carl.commands.ListCommand;
import carl.commands.MarkCommand;
import carl.commands.SortCommand;
import carl.commands.UnmarkCommand;
import carl.exceptions.CarlCommandException;
import carl.exceptions.CarlException;
import carl.exceptions.CarlUnknownCommandException;

/**
 * Parses user input and maps it to the corresponding command for execution.
 */
public class CarlParser {
    private final Map<String, Parser<? extends Command>> commands = new HashMap<>();

    /**
     * Initializes the parser with a mapping of string commands to their respective parser objects.
     */
    public CarlParser() {
        commands.put("bye", new NoArgumentCommandParser<>("bye", ByeCommand::new));
        commands.put("help", new NoArgumentCommandParser<>("help", HelpCommand::new));
        commands.put("list", new NoArgumentCommandParser<>("list", ListCommand::new));
        commands.put("mark", new TaskIndexParser<>(MarkCommand::new));
        commands.put("unmark", new TaskIndexParser<>(UnmarkCommand::new));
        commands.put("deadline", new DeadlineCommandParser());
        commands.put("event", new EventCommandParser());
        commands.put("todo", new TodoCommandParser());
        commands.put("delete", new TaskIndexParser<>(DeleteCommand::new));
        commands.put("due", new DueCommandParser());
        commands.put("find", new FindCommandParser());
        commands.put("sort", new NoArgumentCommandParser<>("sort", SortCommand::new));
    }

    /**
     * Parses the raw user input and returns the mapped command.
     *
     * @param input       The full string input typed by the user.
     * @return The parsed Command object.
     * @throws CarlException If the command is unknown or arguments are invalid.
     */
    public Command parseCommands(String input) throws CarlException {
        if (input == null || input.isBlank()) {
            throw new CarlCommandException("Enter a command. Try `help` to see the available controls.");
        }

        String[] args = input.strip().split("\\s+", 2);
        String commandWord = args[0].toLowerCase();
        String arguments = args.length > 1 ? args[1].trim() : "";

        Parser<?> parseCommand = commands.get(commandWord);

        if (parseCommand == null) {
            throw new CarlUnknownCommandException();
        }

        return parseCommand.parse(arguments);
    }
}
