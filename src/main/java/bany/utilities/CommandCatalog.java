package bany.utilities;

import java.util.List;
import java.util.Set;

/**
 * Stores the commands recognized by Bany and provides command validation.
 */
public final class CommandCatalog {
    /** Commands that create a new task. */
    private static final Set<String> VALID_TASK_COMMANDS = Set.of(
            "TODO", "DEADLINE", "EVENT");

    /** Commands that act on existing tasks or the application session. */
    private static final Set<String> VALID_ACTION_COMMANDS = Set.of(
            "BYE", "LIST", "MARK", "UNMARK", "DELETE", "FIND", "RESCHEDULE");

    /** All recognized commands in the order used for suggestions. */
    private static final List<String> ALL_COMMANDS = List.of(
            "TODO", "DEADLINE", "EVENT",
            "BYE", "LIST", "MARK", "UNMARK", "DELETE", "FIND", "RESCHEDULE");

    private CommandCatalog() {
        // Prevent this utility class from being instantiated.
    }

    /**
     * Checks whether a word is a task command.
     *
     * @param word command word to check.
     * @return true if the word creates a task.
     */
    public static boolean isValidTaskWord(String word) {
        return VALID_TASK_COMMANDS.contains(word);
    }

    /**
     * Checks whether a word is an action command.
     *
     * @param word command word to check.
     * @return true if the word performs an action.
     */
    public static boolean isValidActionWord(String word) {
        return VALID_ACTION_COMMANDS.contains(word);
    }

    /**
     * Checks whether a word is any recognized Bany command.
     *
     * @param word command word to check.
     * @return true if the word is recognized.
     */
    public static boolean isValidAllCommandWord(String word) {
        return isValidTaskWord(word) || isValidActionWord(word);
    }

    /**
     * Returns all commands in a stable order for command suggestions.
     *
     * @return all commands accepted by Bany.
     */
    public static List<String> getAllCommands() {
        return ALL_COMMANDS;
    }
}
