package ted.command;

import java.util.Arrays;
import java.util.stream.Collectors;

import ted.TedException;

/**
 * The kinds of command Ted understands, together with the keyword the user
 * types for each one.
 * <p>
 * An enum fits here because the set of commands is fixed and known when the
 * code is written. Compared with the loose {@code String} constants this
 * replaces, the compiler now checks every use, a switch over a command can be
 * seen to cover every case, and the list of keywords shown to a confused user
 * is derived from the commands themselves rather than kept in step by hand.
 */
public enum CommandType {
    /** Adds a task with no date attached. */
    TODO("todo"),

    /** Adds a task due by a given time. */
    DEADLINE("deadline"),

    /** Adds a task spanning two times. */
    EVENT("event"),

    /** Displays every stored task. */
    LIST("list"),

    /** Marks a task as done. */
    MARK("mark"),

    /** Reverses the done status of a task. */
    UNMARK("unmark"),

    /** Removes a task from the list. */
    DELETE("delete"),

    /** Shows only the tasks whose description contains a keyword, or that have a tag. */
    FIND("find"),

    /** Attaches tags to a task. */
    TAG("tag"),

    /** Detaches tags from a task. */
    UNTAG("untag"),

    /** Ends the conversation. */
    BYE("bye");

    /** The word the user types to invoke this command. */
    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the command's keyword, e.g. {@code "delete"}.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Finds the command a keyword refers to, ignoring case, so that a keyword
     * capitalized by a phone keyboard or a stray Caps Lock still works.
     *
     * @param keyword first word of the user's input.
     * @return the matching command.
     * @throws TedException if no command uses that keyword.
     */
    public static CommandType fromKeyword(String keyword) throws TedException {
        return Arrays.stream(values())
                .filter(command -> command.keyword.equalsIgnoreCase(keyword))
                .findFirst()
                .orElseThrow(() -> new TedException("\"" + keyword + "\"? Never heard of it. "
                        + "Here's what I can do: " + listKeywords() + "."));
    }

    /**
     * Lists every keyword, for telling the user what Ted understands.
     * Built from {@link #values()} so that adding a command here is enough to
     * make it appear in the message.
     *
     * @return the keywords, separated by commas.
     */
    private static String listKeywords() {
        return Arrays.stream(values())
                .map(CommandType::getKeyword)
                .collect(Collectors.joining(", "));
    }
}
