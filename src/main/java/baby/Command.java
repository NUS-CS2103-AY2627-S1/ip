package baby;
/**
 * Represents the command words that Baby understands.
 */
public enum Command {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    FIND("find");

    private final String commandWord;

    /**
     * Creates a command with the word typed by the user.
     *
     * @param commandWord The text used to run this command.
     */
    Command(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Returns the word typed by the user for this command.
     *
     * @return The command word.
     */
    public String getCommandWord() {
        return commandWord;
    }
}
