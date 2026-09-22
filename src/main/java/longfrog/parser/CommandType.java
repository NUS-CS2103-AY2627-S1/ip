package longfrog.parser;

import java.util.Arrays;
import java.util.Optional;

/**
 * Defines the command keywords recognised by Longfrog's parser.
 */
public enum CommandType {
    BYE("bye"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    DATE("date"),
    FIND("find");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds the command type that matches a user-entered keyword.
     *
     * @param keyword the command keyword to look up
     * @return the matching command type, or an empty result for an unknown keyword
     */
    public static Optional<CommandType> fromKeyword(String keyword) {
        return Arrays.stream(values())
                .filter(commandType -> commandType.keyword.equalsIgnoreCase(keyword))
                .findFirst();
    }
}
