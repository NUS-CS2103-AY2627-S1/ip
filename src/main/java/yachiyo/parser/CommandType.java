package yachiyo.parser;

import static yachiyo.exception.ErrorCategory.WARNING;

import yachiyo.exception.YachiyoException;

/**
 * Represents a command word supported by Yachiyo.
 */
public enum CommandType {
    MARK,
    UNMARK,
    LIST,
    FIND,
    TODO,
    DEADLINE,
    EVENT,
    ON,
    EDIT,
    DELETE,
    BYE;

    /**
     * Returns the command type represented by the specified command word.
     *
     * @param command Command word to parse.
     * @return Matching command type.
     * @throws YachiyoException If the command word is not supported.
     */
    public static CommandType parse(String command) throws YachiyoException {
        try {
            return CommandType.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new YachiyoException(WARNING,
                    "Oh? I don’t recognize that command just yet. Could you try another one?"
            );
        }
    }
}
