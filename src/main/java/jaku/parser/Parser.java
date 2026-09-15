package jaku.parser;

import java.util.Locale;

import jaku.JakuException;

/**
 * Interprets a user's input line as a Jaku command and its arguments.
 */
public class Parser {
    /** Prevents instantiation of this utility class. */
    private Parser() {
    }

    /**
     * Finds the command represented by the first word of an input line.
     *
     * @param input the trimmed, non-empty user input
     * @return the matching command, or {@link Command#UNKNOWN} when no command matches
     */
    public static Command parseCommand(String input) {
        String commandWord = input.split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
        for (Command command : Command.values()) {
            if (!command.getKeyword().isEmpty() && command.getKeyword().equals(commandWord)) {
                return command;
            }
        }
        return Command.UNKNOWN;
    }

    /**
     * Returns the portion of an input line after a command's keyword.
     *
     * @param input a user input line beginning with {@code command}
     * @param command the command at the beginning of the input
     * @return the trimmed command arguments, or an empty string when absent
     */
    public static String getArguments(String input, Command command) {
        return input.substring(command.getKeyword().length()).trim();
    }

    /**
     * Parses the one-based task number supplied to a task command.
     *
     * @param input the complete user input
     * @param command the command whose task number should be parsed
     * @return the parsed task number
     * @throws JakuException if the number is missing or nonnumeric
     */
    public static int parseTaskNumber(String input, Command command) throws JakuException {
        String arguments = getArguments(input, command);
        if (!arguments.matches("[+-]?[0-9]+")) {
            throw new JakuException("Use: " + command.getKeyword() + " <task number>.");
        }
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException exception) {
            throw new JakuException("Use: " + command.getKeyword() + " <task number>.");
        }
    }
}
