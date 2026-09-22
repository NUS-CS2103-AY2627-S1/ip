package wiz;

/**
 * Parses user input strings into command words and argument strings.
 */
public class Parser {

    /**
     * Prevents instantiation of this utility class.
     */
    private Parser() {
    }

    /**
     * Extracts the command word from raw user input.
     *
     * @param input The raw input string from the user.
     * @return The command word in lowercase, or empty string if input is blank.
     */
    public static String getCommandWord(String input) {
        assert input != null : "Input string cannot be null";
        String trimmed = input.trim();

        if (trimmed.isEmpty()) {
            return "";
        }

        String[] parts = trimmed.split("\\s+", 2);
        return parts[0].toLowerCase();
    }

    /**
     * Extracts the arguments portion from raw user input.
     *
     * @param input The raw input string from the user.
     * @return The trimmed argument string, or empty string if no arguments are provided.
     */
    public static String getArguments(String input) {
        assert input != null : "Input string cannot be null";
        String trimmed = input.trim();

        if (trimmed.isEmpty()) {
            return "";
        }

        String[] parts = trimmed.split("\\s+", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }
}