package bambolino.parser;

import java.util.Locale;
import java.util.Map;

/**
 * Separates a full user input into a command and its arguments.
 */
public class Parser {
    private static final Map<String, String> COMMAND_ALIASES = Map.of(
            "t", "todo", "d", "deadline", "e", "event", "l", "list", "f", "find",
            "del", "delete", "m", "mark", "um", "unmark", "q", "bye");

    /**
     * Parses user input into a normalized command and trimmed arguments.
     *
     * @param userInput The full command entered by the user.
     * @return The parsed command.
     */
    public Command parse(String userInput) {
        String[] commandParts = userInput.trim().split("\\s+", 2);
        String command = commandParts.length == 0 ? "" : commandParts[0].toLowerCase(Locale.ROOT);
        command = COMMAND_ALIASES.getOrDefault(command, command);
        String arguments = commandParts.length == 2 ? commandParts[1].trim() : "";
        return new Command(command, arguments);
    }

    /** Stores the command name and its arguments. */
    public record Command(String name, String arguments) {
    }
}
