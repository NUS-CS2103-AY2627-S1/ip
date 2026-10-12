package duke;

import java.util.Map;

/**
 * Interprets raw user input into commands and arguments.
 */
public class Parser {

    /** Short-form aliases mapped to their full commands, for friendlier syntax. */
    private static final Map<String, Command> ALIASES = Map.ofEntries(
            Map.entry("t", Command.TODO),
            Map.entry("d", Command.DEADLINE),
            Map.entry("e", Command.EVENT),
            Map.entry("m", Command.MARK),
            Map.entry("u", Command.UNMARK),
            Map.entry("del", Command.DELETE),
            Map.entry("ls", Command.LIST),
            Map.entry("f", Command.FIND)
    );

    /**
     * Parses the command keyword from a line of user input. Accepts both the full
     * command name (e.g. "todo") and its short alias (e.g. "t").
     *
     * @param input the full line typed by the user
     * @return the Command matching the first word
     * @throws DuckeException if the keyword is not a recognized command or alias
     */
    public static Command parseCommand(String input) throws DuckeException {
        String keyword = input.split(" ", 2)[0].toLowerCase();
        if (ALIASES.containsKey(keyword)) {
            return ALIASES.get(keyword);
        }
        try {
            return Command.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DuckeException("I don't understand your quack command.");
        }
    }

    /**
     * Extracts the argument portion of the input (everything after the keyword).
     *
     * @param input the full line typed by the user
     * @return the argument, or an empty string if there is none
     */
    public static String parseArgument(String input) {
        String[] parts = input.split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }
}
