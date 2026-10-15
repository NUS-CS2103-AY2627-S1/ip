package erina;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The set of instructions Erina understands.
 *
 * <p>Using an enum rather than loose String constants means the valid
 * commands are stated in exactly one place, the compiler checks every use
 * against that list instead of trusting a quoted string, and a switch over
 * this type can be checked for completeness.
 *
 * <p>Each command also carries its own usage line and a short description,
 * so the {@code help} text is generated from this list rather than written
 * separately. A command added here appears in {@code help} automatically,
 * and the two can never disagree.
 */
public enum Command {
    /** Shows every task added so far. */
    LIST("list", "list", "show every task"),

    /** Marks a task as completed. */
    MARK("mark", "mark <task number>", "mark a task as done"),

    /** Marks a task as not yet completed. */
    UNMARK("unmark", "unmark <task number>", "mark a task as not done yet"),

    /** Adds a task with only a description. */
    TODO("todo", "todo <description>", "add a task with no date"),

    /** Adds a task due by a stated time. */
    DEADLINE("deadline", "deadline <description> /by <yyyy-mm-dd>", "add a task due by a date"),

    /** Adds a task spanning a period of time. */
    EVENT("event", "event <description> /from <start> /to <end>", "add a task with a start and end"),

    /** Removes a task from the list. */
    DELETE("delete", "delete <task number>", "remove a task"),

    /** Shows the tasks whose descriptions contain a keyword. */
    FIND("find", "find <keyword>", "show tasks whose description contains the keyword"),

    /** Lists every command with what it does. */
    HELP("help", "help", "show this list of commands"),

    /** Ends the conversation. */
    BYE("bye", "bye", "say goodbye and exit");

    /** Shown when the user types something that is not a command. */
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Pardon me, but I don't know what that means.\n"
            + "Type help to see what I can do.";

    /** The word the user types to invoke this command. */
    private final String keyword;

    /** How the command is typed, including any arguments, for example {@code mark <task number>}. */
    private final String usage;

    /** What the command does, in a few words, for the help text. */
    private final String description;

    Command(String keyword, String usage, String description) {
        this.keyword = keyword;
        this.usage = usage;
        this.description = description;
    }

    /**
     * Returns the command the user asked for.
     *
     * <p>This is the single point at which typed text becomes a known
     * command, so it is also the single place that can decide the text is
     * not one.
     *
     * @param keyword the first word the user typed
     * @return the matching command
     * @throws ErinaException if no command uses that keyword
     */
    public static Command fromKeyword(String keyword) throws ErinaException {
        // Optional makes the "not found" case explicit instead of falling out
        // of a loop; the checked exception is thrown outside the stream, since
        // lambdas cannot throw checked exceptions.
        Optional<Command> match = Arrays.stream(values())
                .filter(command -> command.keyword.equals(keyword))
                .findFirst();
        if (match.isEmpty()) {
            throw new ErinaException(UNKNOWN_COMMAND_MESSAGE);
        }
        return match.get();
    }

    /**
     * Returns the help text: one line per command, in the order declared
     * above, each showing how it is typed and what it does.
     *
     * @return the help text, with lines separated by newlines
     */
    public static String describeAll() {
        return Arrays.stream(values())
                .map(command -> "  " + command.usage + "\n      " + command.description)
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the keyword, for example {@code "mark"}
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns how the command is typed, including its arguments.
     *
     * @return the usage line, for example {@code "mark <task number>"}
     */
    public String getUsage() {
        return usage;
    }
}
