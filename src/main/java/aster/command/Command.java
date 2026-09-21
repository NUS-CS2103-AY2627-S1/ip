package aster.command;

import java.util.Arrays;

/**
 * The commands the Aster task-list application understands.
 */
public enum Command {
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    LIST("list", false),
    ONDATE("ondate", true),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    FIND("find", true),
    TAG("tag", true),
    BYE("bye", false);

    private final String keyword;
    private final boolean acceptsArguments;

    Command(String keyword, boolean acceptsArguments) {
        this.keyword = keyword;
        this.acceptsArguments = acceptsArguments;
    }

    /**
     * Returns the command keyword used at the start of user input.
     *
     * @return command keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Identifies the command at the beginning of an input line.
     *
     * @param input a complete, unnormalised input line
     * @return the matching command, or {@code null} if the input is not a valid command form
     */
    public static Command fromInput(String input) {
        assert input != null : "Parser should reject null input before command lookup.";
        return Arrays.stream(values())
                .filter(command -> input.equals(command.keyword)
                        || command.acceptsArguments && input.startsWith(command.keyword + " "))
                .findFirst()
                .orElse(null);
    }
}
