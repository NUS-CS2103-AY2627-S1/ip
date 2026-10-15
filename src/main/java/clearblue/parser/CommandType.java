package clearblue.parser;

import java.util.Arrays;

/**
 * Identifies the fixed set of commands understood by Clearblue.
 */
public enum CommandType {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    ON("on"),
    FIND("find"),
    UNDO("undo"),
    BYE("bye"),
    UNKNOWN("");

    private final String commandWord;

    /**
     * Creates a command type associated with its user-facing command word.
     *
     * @param commandWord word entered at the start of a command
     */
    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Finds the command type represented by the first word of an input line.
     *
     * @param command complete command entered by the user
     * @return matching command type, or {@link #UNKNOWN} when no command matches
     */
    public static CommandType fromCommand(String command) {
        String trimmedCommand = command.trim();
        if (trimmedCommand.isEmpty()) {
            return UNKNOWN;
        }

        int firstSpace = trimmedCommand.indexOf(' ');
        String firstWord = firstSpace < 0
                ? trimmedCommand
                : trimmedCommand.substring(0, firstSpace);

        // UNKNOWN's command word is "" (see the enum constants above); if
        // firstWord could be blank here, it would spuriously match UNKNOWN
        // inside the loop below instead of via the isEmpty() check above.
        // The isEmpty() check guarantees trimmedCommand starts with a
        // non-space character, so firstWord can never be blank at this point.
        assert !firstWord.isEmpty() : "firstWord must be non-blank once trimmedCommand is non-empty";

        return Arrays.stream(values())
                .filter(type -> type.commandWord.equals(firstWord))
                .findFirst()
                .orElse(UNKNOWN);
    }

    /**
     * Returns the word users enter to select this command type.
     *
     * @return command word, or an empty string for {@link #UNKNOWN}
     */
    public String getCommandWord() {
        return commandWord;
    }
}
