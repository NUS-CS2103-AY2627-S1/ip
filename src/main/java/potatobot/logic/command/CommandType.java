package potatobot.logic.command;

import java.util.Arrays;

import potatobot.exception.PotatoBotException;

/**
 * Represents a command supported by PotatoBot.
 */
public enum CommandType {
    /** Adds a task without a specialized type. */
    ADD("add"),
    /** Displays the current task list. */
    LIST("list"),
    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find"),
    /** Marks a task as completed. */
    MARK("mark"),
    /** Marks a task as incomplete. */
    UNMARK("unmark"),
    /** Deletes a task. */
    DELETE("delete"),
    /** Reverses the most recent state-changing command. */
    UNDO("undo"),
    /** Adds an undated todo task. */
    TODO("todo"),
    /** Adds a task with a deadline. */
    DEADLINE("deadline"),
    /** Adds a task with start and end dates. */
    EVENT("event"),
    /** Saves the tasks and exits PotatoBot. */
    BYE("bye");

    private final String commandWord;

    /**
     * Creates a command type associated with its user-facing command word.
     *
     * @param commandWord Word that identifies the command in user input.
     */
    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /**
     * Converts a command word into its matching command type.
     *
     * @param command Command word to convert.
     * @return Command type matching the command word.
     * @throws PotatoBotException If the command word is not supported.
     */
    public static CommandType parse(String command) throws PotatoBotException {
        return Arrays.stream(CommandType.values())
                .filter(commandType -> commandType.commandWord.equals(command))
                .findFirst()
                .orElseThrow(() -> new PotatoBotException("Me no gets?"));
    }
}
