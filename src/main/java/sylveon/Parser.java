package sylveon;

/** Parses user commands into command words and arguments. */
public class Parser {
    /** Extracts the first word from a command. */
    public String getCommandWord(String command) {
        String trimmedCommand = command == null ? "" : command.trim();
        return trimmedCommand.isEmpty() ? "" : trimmedCommand.split("\\s+", 2)[0];
    }

    /** Extracts the arguments following the command word. */
    public String getArguments(String command) {
        String trimmedCommand = command == null ? "" : command.trim();
        if (trimmedCommand.isEmpty()) {
            return "";
        }
        String[] words = trimmedCommand.split("\\s+", 2);
        return words.length > 1 ? words[1].trim() : "";
    }
}
