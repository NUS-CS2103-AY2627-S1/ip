package tangent.parser;

import tangent.exception.ErrorMessages;
import tangent.exception.TangentException;

/** Contains supported command keywords that the program can parse. */
public enum CommandTypes {
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    BYE("bye"),
    FIND("find");

    /** The term associated with the specific command type. */
    private final String keyword;

    /**
     * Initializes a new command type with an associated {@code keyword}.
     */
    CommandTypes(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the corresponding command type from the user's input.
     *
     * @throws TangentException if the input is not part of the list of keywords corresponding to a command type.
     */
    public static CommandTypes fromInput(String input) throws TangentException {
        for (CommandTypes commandTypes : CommandTypes.values()) {
            if (commandTypes.keyword.equals(input)) {
                return commandTypes;
            }
        }
        throw new TangentException(ErrorMessages.INVALID_COMMAND_MESSAGE);
    }
}
