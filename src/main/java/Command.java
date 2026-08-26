import java.util.Optional;

/**
 * Represents a command that the Mona REPL can process.
 */
public enum Command {
    BYE("bye", false),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true);

    private final String commandWord;
    private final boolean hasArguments;

    Command(String commandWord, boolean hasArguments) {
        this.commandWord = commandWord;
        this.hasArguments = hasArguments;
    }

    /**
     * Returns the command represented by the given user input.
     *
     * @param userInput the trimmed line entered by the user.
     * @return the matching command, or an empty value if the input does not invoke a command.
     */
    public static Optional<Command> from(String userInput) {
        for (Command command : values()) {
            if (command.matches(userInput)) {
                return Optional.of(command);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns the text following this command's word.
     *
     * @param userInput the trimmed line that invokes this command.
     * @return the command's arguments, or an empty string if none were entered.
     */
    public String extractArguments(String userInput) {
        return userInput.length() == commandWord.length()
                ? ""
                : userInput.substring(commandWord.length() + 1);
    }

    /**
     * Returns the word a user enters to invoke this command.
     *
     * @return this command's user-facing word.
     */
    @Override
    public String toString() {
        return commandWord;
    }

    private boolean matches(String userInput) {
        return userInput.equals(commandWord)
                || hasArguments && userInput.startsWith(commandWord + " ");
    }
}
