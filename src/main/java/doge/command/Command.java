package doge.command;

import doge.exception.DogeException;

/** Represents a command that the user can enter in the Doge application. */
public enum Command {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    MARK("mark"),
    UNMARK("unmark"),
    PRIORITY("priority"),
    UNPRIORITY("unpriority"),
    LIST("list"),
    FIND("find"),
    BYE("bye"),
    DELETE("delete");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /** Returns the command represented by a keyword, or throws an error for an unknown keyword. */
    public static Command fromText(String text) throws DogeException {
        if (text == null || text.isBlank()) {
            throw new DogeException("Please enter a command.");
        }
        for (Command command : values()) {
            if (command.keyword.equals(text)) {
                return command;
            }
        }
        throw new DogeException("I don't understand that command");
    }
}
