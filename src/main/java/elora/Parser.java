package elora;

import java.util.HashMap;
import java.util.Map;

/**
 * Deals with making sense of the user's raw input: splitting it into a
 * command word and its arguments, and mapping the command word to a
 * CommandType.
 */
public class Parser {
    private static final Map<String, CommandType> COMMAND_WORDS = buildCommandWordMap();

    /**
     * Returns the first word of the given input, treated as the command
     * word.
     *
     * @param input Full line of raw user input.
     * @return The command word, or the entire input if it contains no space.
     */
    public static String getCommandWord(String input) {
        String trimmed = input.strip();
        int spaceIndex = trimmed.indexOf(' ');
        return (spaceIndex == -1) ? trimmed : trimmed.substring(0, spaceIndex);
    }

    /**
     * Returns everything after the command word, with leading and
     * trailing whitespace removed. Leading/trailing whitespace on the
     * whole input (e.g. a user pasting "  todo book  ") is stripped
     * first, so it never leaks into the command word or the arguments.
     *
     * @param input Full line of raw user input.
     * @return The arguments following the command word, or an empty
     *     string if the input contains no space.
     */
    public static String getArguments(String input) {
        String trimmed = input.strip();
        int spaceIndex = trimmed.indexOf(' ');
        return (spaceIndex == -1) ? "" : trimmed.substring(spaceIndex + 1).strip();
    }

    /**
     * Returns the CommandType matching the given command word.
     *
     * @param commandWord The first word of the user's input.
     * @return The matching CommandType, or CommandType.UNKNOWN if the
     *     word isn't recognized.
     */
    public static CommandType parseCommandType(String commandWord) {
        return COMMAND_WORDS.getOrDefault(commandWord, CommandType.UNKNOWN);
    }

    /**
     * Builds the lookup table from command word to CommandType, used by
     * {@link #parseCommandType(String)}.
     *
     * @return The command word to CommandType mapping.
     */
    private static Map<String, CommandType> buildCommandWordMap() {
        Map<String, CommandType> commandWords = new HashMap<>();
        commandWords.put("bye", CommandType.BYE);
        commandWords.put("list", CommandType.LIST);
        commandWords.put("mark", CommandType.MARK);
        commandWords.put("unmark", CommandType.UNMARK);
        commandWords.put("delete", CommandType.DELETE);
        commandWords.put("todo", CommandType.TODO);
        commandWords.put("deadline", CommandType.DEADLINE);
        commandWords.put("event", CommandType.EVENT);
        commandWords.put("on", CommandType.ON);
        commandWords.put("find", CommandType.FIND);
        commandWords.put("sort", CommandType.SORT);
        return commandWords;
    }
}
