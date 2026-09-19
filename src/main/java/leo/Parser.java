package leo;

/**
 * Parses raw user input into a command word and its remaining
 * arguments, so that Leo does not need to inspect the raw input string
 * (e.g. via startsWith/substring) directly.
 */
public class Parser {
    /**
     * Extracts the command word: the first whitespace-separated token
     * of the input.
     *
     * @param input raw user input
     * @return the command word, or an empty string if input is blank
     */
    public static String getCommandWord(String input) {
        String trimmed = input.trim();
        int spaceIndex = trimmed.indexOf(' ');
        return spaceIndex == -1 ? trimmed : trimmed.substring(0, spaceIndex);
    }

    /**
     * Extracts the arguments that follow the command word.
     *
     * @param input raw user input
     * @return the text after the first whitespace-separated token,
     *     trimmed; an empty string if there are no arguments
     */
    public static String getArguments(String input) {
        String trimmed = input.trim();
        int spaceIndex = trimmed.indexOf(' ');
        return spaceIndex == -1 ? "" : trimmed.substring(spaceIndex + 1).trim();
    }

    /**
     * Checks whether a delimiter (e.g. " /by ") appears more than once
     * in the input. A repeated delimiter usually means the user
     * mistyped a command (e.g. "deadline return book /by mon /by
     * tue") - since commands split on only the first occurrence (see
     * {@link leo.Leo}'s handleDeadline/handleEvent), a second one would
     * otherwise silently end up folded into the previous field instead
     * of being rejected with a clear error.
     *
     * @param input text to search
     * @param delimiter delimiter text to check for repeats of
     * @return true if delimiter occurs 2 or more times in input
     */
    public static boolean hasDuplicateDelimiter(String input, String delimiter) {
        int firstIndex = input.indexOf(delimiter);
        return firstIndex != -1 && input.indexOf(delimiter, firstIndex + 1) != -1;
    }
}