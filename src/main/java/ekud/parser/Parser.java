package ekud.parser;

import ekud.EkudException;
import ekud.command.Command;
import ekud.command.CommandType;

/**
 * Turns a line of user input into a {@link Command}.
 * Command words are the first token and are case-sensitive.
 * {@link CommandType} holds the vocabulary; each command class owns its argument grammar.
 */
public class Parser {
    /**
     * Parses one line of input.
     *
     * @param input raw line from the user
     * @return the command to execute
     * @throws EkudException if the line is blank or a known command has invalid arguments
     */
    public static Command parse(String input) throws EkudException {
        if (input == null || input.isBlank()) {
            throw new EkudException("Please enter a command (" + CommandType.getHelpList() + ").");
        }

        String trimmed = input.trim();
        String[] parts = trimmed.split("\\s+", 2);
        String commandWord = parts[0];
        String arguments = parts.length > 1 ? parts[1] : "";
        // Blank input was already rejected, so splitting must yield a non-empty first token.
        assert !commandWord.isEmpty() : "Command word should be non-empty after trimming";

        CommandType commandType = CommandType.fromKeyword(commandWord);
        if (commandType == null) {
            throw new EkudException("I don't recognize that command. Try " + CommandType.getHelpList() + ".");
        }
        Command command = commandType.parse(arguments);
        // Each CommandType maps to a parse method that returns a command or throws.
        assert command != null : "A recognized command should produce a Command object";
        return command;
    }

    /**
     * Rejects leftover text after a command that takes no arguments.
     *
     * @param commandWord the command being checked
     * @param arguments   text after the command word
     * @throws EkudException if {@code arguments} is not blank
     */
    public static void requireNoArguments(String commandWord, String arguments) throws EkudException {
        if (!arguments.isBlank()) {
            throw new EkudException("The \"" + commandWord + "\" command does not take any arguments.");
        }
    }

    /**
     * Parses the single 1-based task number used by {@code mark}, {@code unmark}, and {@code delete}.
     *
     * @param commandWord the command being parsed, used in error messages
     * @param arguments   text after the command word
     * @return the task number, starting from 1
     * @throws EkudException if the number is missing, not an integer, or extra text is present
     */
    public static int parseOneBasedIndex(String commandWord, String arguments) throws EkudException {
        if (arguments.isBlank()) {
            throw new EkudException("Please provide a task number, e.g. " + commandWord + " 1.");
        }
        String[] tokens = arguments.split("\\s+");
        if (tokens.length != 1) {
            throw new EkudException("The \"" + commandWord + "\" command takes exactly one task number.");
        }
        try {
            return Integer.parseInt(tokens[0]);
        } catch (NumberFormatException e) {
            throw new EkudException("Task number must be an integer, e.g. " + commandWord + " 1.");
        }
    }

    /**
     * Returns the index of {@code flag} as its own token, or {@code -1} if absent.
     * The flag must be bounded by whitespace or the ends of the string, so
     * {@code /by} inside {@code /bye} is not treated as a match.
     *
     * @param input text to search
     * @param flag  token to find, for example {@code /by}
     * @return starting index of the flag, or {@code -1}
     */
    public static int indexOfFlag(String input, String flag) {
        int searchFrom = 0;
        while (searchFrom <= input.length() - flag.length()) {
            int found = input.indexOf(flag, searchFrom);
            if (found < 0) {
                return -1;
            }
            boolean validStart = found == 0 || Character.isWhitespace(input.charAt(found - 1));
            int after = found + flag.length();
            boolean validEnd = after == input.length() || Character.isWhitespace(input.charAt(after));
            if (validStart && validEnd) {
                return found;
            }
            searchFrom = found + 1;
        }
        return -1;
    }
}
