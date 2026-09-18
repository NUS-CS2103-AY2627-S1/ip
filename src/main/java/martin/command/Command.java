package martin.command;

import martin.exception.IllegalCommandException;

/**
 * Represents the available commands supported by the application.
 */
public enum Command {
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    FIND("find"),
    BYE("bye");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the {@code Command} enum constant corresponding to the keyword in the
     * input.
     *
     * @param input The raw input string entered by the user.
     * @return The matching {@code Command}.
     * @throws IllegalCommandException If the command word is unrecognized.
     */
    public static Command from(String input) {
        String commandWord = input.trim().split("\\s+", 2)[0];
        for (Command type : values()) {
            if (type.keyword.equals(commandWord)) {
                return type;
            }
        }
        throw new IllegalCommandException("I am afraid that command is entirely unknown to me.");
    }

}
