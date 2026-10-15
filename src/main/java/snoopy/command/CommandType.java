package snoopy.command;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents the commands understood by Snoopy.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    FIND("find"),
    UPDATE("update"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    UNKNOWN("");

    private final String keyword;

    /**
     * Creates a command type associated with its user-facing keyword.
     *
     * @param keyword First word used to invoke the command.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword used to invoke this command.
     *
     * @return Command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Lists all supported command keywords in their enum order.
     *
     * @return Comma-separated command keywords, excluding {@link #UNKNOWN}.
     */
    public static String getCommandSummary() {
        return Arrays.stream(values())
                .filter(commandType -> commandType != UNKNOWN)
                .map(CommandType::getKeyword)
                .collect(Collectors.joining(", "));
    }

}
