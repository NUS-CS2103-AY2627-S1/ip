package cheecken;

/**
 * Converts raw user input into a recognized command.
 */
public class Parser {
    /**
     * Normalizes null-safe raw console input.
     * @param rawInput raw input before normalization
     * @return stripped input, or an empty string for null input
     */
    public String normalize(String rawInput) {
        return rawInput == null ? "" : rawInput.strip();
    }

    /**
     * Identifies the command represented by normalized input.
     * @param input command input to process
     * @return recognized command, or null if no command matches
     */
    public CommandType parseCommand(String input) {
        String keyword = input.split("\\s+", 2)[0];
        for (CommandType command : CommandType.values()) {
            if (command.matches(input) || command.name().equalsIgnoreCase(keyword)) {
                return command;
            }
        }
        return null;
    }
}
