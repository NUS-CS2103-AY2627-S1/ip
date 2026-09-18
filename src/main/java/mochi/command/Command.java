package mochi.command;

/**
 * Identifies the commands understood by Mochi.
 */
public enum Command {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    SORT("sort"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    FIND("find"),
    BYE("bye"),
    UNKNOWN("");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the text keyword that invokes this command.
     *
     * @return command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Identifies a command using the first word of the user's input.
     *
     * @param input complete input entered by the user
     * @return matching command, or {@link #UNKNOWN} when no command matches
     */
    public static Command fromInput(String input) {
        if (input == null || input.isBlank()) {
            return UNKNOWN;
        }
        String commandWord = input.strip().split("\\s+", 2)[0];
        for (Command command : values()) {
            if (command.keyword.equals(commandWord)) {
                return command;
            }
        }
        return UNKNOWN;
    }
}
