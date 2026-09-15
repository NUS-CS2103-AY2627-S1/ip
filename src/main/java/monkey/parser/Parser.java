package monkey.parser;

import monkey.command.AddCommand;
import monkey.command.Command;
import monkey.command.CommandAction;
import monkey.command.DeleteCommand;
import monkey.command.ExitCommand;
import monkey.command.FindCommand;
import monkey.command.ListCommand;
import monkey.command.MarkCommand;
import monkey.command.SnoozeCommand;
import monkey.command.TodoCommand;
import monkey.command.UnknownCommand;

/** Interprets raw user input as a command and its argument text. */
public class Parser {
    /** Converts the first word of input into a known command. */
    public Command parseCommand(String input) {
        return Command.fromInput(input);
    }

    /** Parses both the command keyword and its argument text. */
    public ParsedCommand parse(String input) {
        Command command = parseCommand(input);
        return new ParsedCommand(command, parseArguments(input, command));
    }

    /** Creates an executable command object for commands already supported by the stretch design. */
    public CommandAction parseAction(String input) {
        ParsedCommand parsedCommand = parse(input);
        Command command = parsedCommand.command();
        String arguments = parsedCommand.arguments();
        return switch (command) {
        case BYE -> arguments.isEmpty() ? new ExitCommand() : unexpectedArguments(command);
        case DELETE -> new DeleteCommand(arguments);
        case TODO -> new TodoCommand(arguments);
        case LIST -> arguments.isEmpty() ? new ListCommand() : unexpectedArguments(command);
        case FIND -> new FindCommand(arguments);
        case MARK, UNMARK -> new MarkCommand(arguments, command == Command.MARK);
        case EVENT, DEADLINE -> new AddCommand(command, arguments);
        case SNOOZE -> new SnoozeCommand(arguments);
        default -> new UnknownCommand();
        };
    }

    /** Returns the normalized text following the command keyword. */
    public String parseArguments(String input, Command command) {
        if (input == null) {
            return "";
        }
        String trimmedInput = input.trim();
        int keywordLength = command.getKeyword().length();
        String arguments = trimmedInput.length() > keywordLength
                ? trimmedInput.substring(keywordLength).trim() : "";
        return arguments.replaceAll("\\s+", " ");
    }

    private CommandAction unexpectedArguments(Command command) {
        return new UnknownCommand("OOPS! Monkey says: The '" + command.getKeyword()
                + "' command does not accept arguments.");
    }
}
