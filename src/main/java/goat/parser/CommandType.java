package goat.parser;

import java.util.Arrays;
import java.util.stream.Collectors;

import goat.GoatException;

/**
 * The set of instructions Goat understands.
 *
 * Each constant pairs the name used internally with the keyword the user
 * actually types. Keeping them in an enum means a command can only be
 * referred to by a name the compiler knows: a mistyped "case LSIT" fails
 * to build, whereas a mistyped string literal would compile and simply
 * never match at runtime.
 *
 * The name says "type" rather than just "Command" because a constant here
 * only identifies <em>which</em> command was typed; it holds none of the
 * work of carrying that command out.
 *
 * The enum is package-private. Every use of it is inside {@link Parser}, and
 * all of them are in private methods -- the keyword a user typed is a detail
 * of how a line gets read, and no other package has any business knowing that
 * this enum is how the reading is done.
 */
enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETE("delete"),
    FIND("find"),
    SORT("sort");

    /** The word the user types to invoke this command. */
    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds the command that a typed word refers to.
     * This is the single place where unrecognized input is rejected.
     *
     * @param keyword the first word of the line the user typed
     * @return the matching command
     * @throws GoatException if no command uses that keyword
     */
    static CommandType fromKeyword(String keyword) throws GoatException {
        return Arrays.stream(values())
                .filter(command -> command.keyword.equals(keyword))
                .findFirst()
                .orElseThrow(() -> new GoatException("I do not know that one. I know: "
                        + Arrays.stream(values())
                                .map(command -> command.keyword)
                                .collect(Collectors.joining(", "))));
    }
}
