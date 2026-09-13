package yappy.command;

import java.util.Locale;

/**
 * Represents the fixed set of commands that Yappy understands.
 */
public enum Command {
    /** Ends the conversation. */
    BYE("bye"),

    /** Lists the current tasks. */
    LIST("list"),

    /** Marks a task as done. */
    MARK("mark"),

    /** Marks a task as not done yet. */
    UNMARK("unmark"),

    /** Deletes a task from the list. */
    DELETE("delete"),

    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find"),

    /** Lists incomplete deadlines that need attention soon. */
    REMIND("remind"),

    /** Adds a todo task. */
    TODO("todo"),

    /** Adds a deadline task. */
    DEADLINE("deadline"),

    /** Adds an event task. */
    EVENT("event"),

    /** Represents an input whose command word is not known. */
    UNKNOWN("");

    /** Text the user types to invoke this command. */
    private final String word;

    /**
     * Creates a command with its user-facing word.
     */
    Command(String word) {
        this.word = word;
    }

    /** Returns the text the user types to invoke this command. */
    public String getWord() {
        return word;
    }

    /**
     * Finds the command matching the first word of the user's input.
     */
    public static Command fromInput(String input) {
        String commandWord = input.trim().split("\\s+", 2)[0].toLowerCase(Locale.ROOT);
        for (Command command : values()) {
            if (command.word.equals(commandWord)) {
                return command;
            }
        }
        return UNKNOWN;
    }
}
