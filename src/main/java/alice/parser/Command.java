package alice.parser;

/**
 * Represents a command understood by Alice.
 */
public enum Command {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    REPEAT("repeat"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DATE("date"),
    FIND("find"),
    DELETE("delete"),
    UNKNOWN("");

    private final String commandWord;

    Command(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Returns the word that invokes this command.
     *
     * @return The command word.
     */
    public String getCommandWord() {
        return commandWord;
    }

    /**
     * Returns the command represented by a command word.
     *
     * @param commandWord The first word of a user's input.
     * @return The matching command, or {@code UNKNOWN} when it is unsupported.
     */
    public static Command fromCommandWord(String commandWord) {
        for (Command command : values()) {
            if (command.commandWord.equals(commandWord)) {
                return command;
            }
        }
        return UNKNOWN;
    }
}
