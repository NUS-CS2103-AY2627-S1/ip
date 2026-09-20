package recordbase.types;

import java.util.Arrays;
import java.util.Locale;

/**
 * Represents a command supported by the Record application.
 */
public enum CommandType {
    /** Exits Record after saving tasks. */
    BYE("bye"),
    /** Displays all tasks. */
    LIST("list"),
    /** Marks a numbered task as completed. */
    MARK("mark"),
    /** Marks a numbered task as incomplete. */
    UNMARK("unmark"),
    /** Removes a numbered task. */
    DELETE("delete"),
    /** Creates a to-do task. */
    TODO("todo"),
    /** Creates a task with a deadline. */
    DEADLINE("deadline"),
    /** Creates an event with a start and end. */
    EVENT("event"),
    /** Displays guidance for all supported commands. */
    HELP("help"),
    /** Represents an unrecognised command word. */
    UNKNOWN("");

    private final String commandWord;

    /**
     * Creates a command type with its corresponding command word.
     *
     * @param commandWord the word used to invoke the command
     */
    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Returns the command type represented by the given keyword.
     *
     * @param commandWord the keyword to interpret
     * @return the matching command {@code CommandType}, or {@code UNKNOWN}
     */
    public static CommandType fromString(String commandWord) {
        String normalizedCommand = commandWord.toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(command -> command.commandWord.equals(normalizedCommand))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
