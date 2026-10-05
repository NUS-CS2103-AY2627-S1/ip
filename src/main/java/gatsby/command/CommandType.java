package gatsby.command;

import java.util.Set;

/**
 * The command words that Gatsby understands from user input.
 *
 * This vocabulary is separate from executable {@link Command} objects so the
 * parser can identify a command before Gatsby chooses its implementation.
 */
public enum CommandType {
    /** The command that ends the current Gatsby session. */
    BYE("bye", "byebye", "bye bye"),
    /** The command that displays the current task list. */
    LIST("list"),
    /** The command that marks a task as done. */
    MARK("mark"),
    /** The command that marks a task as not done. */
    UNMARK("unmark"),
    /** The command that creates a plain todo. */
    TODO("todo"),
    /** The command that creates a deadline. */
    DEADLINE("deadline"),
    /** The command that creates an event. */
    EVENT("event"),
    /** The command that removes a task. */
    DELETE("delete"),
    /** The command that searches for tasks that contain a keyword. */
    FIND("find"),
    /** The command that displays Gatsby's available commands. */
    HELP("help", "?"),
    /** A marker used when the parser cannot identify the command. */
    UNKNOWN();

    /** The lowercase words that can invoke this command type. */
    private final Set<String> aliases;

    /**
     * Creates a command type with its accepted aliases.
     *
     * @param aliases the words recognized as this command
     */
    CommandType(String... aliases) {
        this.aliases = Set.of(aliases);
    }

    /**
     * Checks whether this command can be invoked by the supplied normalized word.
     *
     * @param input a lowercase command word or whole-line alias
     * @return true when this command has the supplied alias
     */
    public boolean matchesAlias(String input) {
        return aliases.contains(input);
    }
}
