package tasktracker.command;

import tasktracker.exception.TaskTrackerException;
import tasktracker.ui.Message;

/**
 * Represents the set of valid commands supported by the TaskTracker application.
 * Provides a lookup method to parse raw input strings into enum constants.
 */
public enum CommandType {
    BYE,
    LIST,
    MARK,
    UNMARK,
    TODO,
    DEADLINE,
    EVENT,
    FIXED,
    DELETE,
    UNDO,
    FIND,
    HELP;

    /**
     * Converts a raw string command word to its corresponding CommandType.
     *
     * @param commandWord Raw string command word.
     * @return The matching CommandType enum constant.
     * @throws TaskTrackerException If the command word is unknown.
     */
    public static CommandType from(String commandWord) throws TaskTrackerException {
        try {
            return CommandType.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TaskTrackerException(Message.ERR_UNKNOWN_COMMAND);
        }
    }
}
