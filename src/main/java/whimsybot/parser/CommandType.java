package whimsybot.parser;

import whimsybot.Personality;
import whimsybot.WhimsyBotException;

/**
 * Represents the different commands Whimsy Bot understands.
 */
public enum CommandType {
    LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, TAG, UNTAG, FIND, HELP, BYE;

    /**
     * Parses the first word of a line of user input into a {@code CommandType}.
     *
     * @param commandWord the first word of the user's input, e.g. {@code "todo"}
     * @return the matching command type
     * @throws WhimsyBotException if the word does not match any known command
     */
    public static CommandType fromString(String commandWord) throws WhimsyBotException {
        switch (commandWord) {
        case "list":
            return LIST;
        case "mark":
            return MARK;
        case "unmark":
            return UNMARK;
        case "delete":
            return DELETE;
        case "todo":
            return TODO;
        case "deadline":
            return DEADLINE;
        case "event":
            return EVENT;
        case "tag":
            return TAG;
        case "untag":
            return UNTAG;
        case "find":
            return FIND;
        case "help":
            return HELP;
        case "bye":
            return BYE;
        default:
            throw new WhimsyBotException(Personality.unknownCommand());
        }
    }
}
