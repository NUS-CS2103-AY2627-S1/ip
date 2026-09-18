package seedu.mattchatbot;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The commands the chatbot understands, each paired with the keyword the user
 * types to invoke it.
 * <p>
 * Introduced by the A-Enums extension. Previously the keywords existed twice:
 * as string literals in the dispatch switch, and again inside the "I don't
 * know what that means" message that lists them. The two could drift apart
 * without the compiler noticing. Here each keyword is written once, and the
 * help text is generated from the same values that drive the dispatch.
 */
public enum Command {
    /** Adds a task with no date attached. */
    TODO("todo"),

    /** Adds a task due by a given date. */
    DEADLINE("deadline"),

    /** Adds a task running between two given dates. */
    EVENT("event"),

    /** Shows every task, numbered. */
    LIST("list"),

    /** Shows the tasks falling on a given date. */
    ON("on"),

    /** Shows the tasks whose description contains a given keyword. */
    FIND("find"),

    /** Marks a task as done. */
    MARK("mark"),

    /** Marks a task as not done yet. */
    UNMARK("unmark"),

    /** Removes a task from the list. */
    DELETE("delete"),

    /** Changes parts of a task that is already in the list. */
    UPDATE("update"),

    /** Ends the session. */
    BYE("bye");

    /** The word the user types to invoke this command. */
    private final String keyword;

    /**
     * Creates a command bound to the word that invokes it.
     *
     * @param keyword the word the user types
     */
    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the keyword, e.g. {@code deadline}
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Finds the command matching a word the user typed, ignoring case.
     *
     * @param word the first word of the user's input
     * @return the matching command
     * @throws MattChatBotException if no command uses that keyword
     */
    public static Command fromKeyword(String word) throws MattChatBotException {
        return Arrays.stream(values())
                .filter(command -> command.keyword.equalsIgnoreCase(word))
                .findFirst()
                .orElseThrow(() -> new MattChatBotException("I regret I did not follow \""
                        + word.toLowerCase() + "\". I understand: " + listKeywords()));
    }

    /**
     * Returns every keyword, comma separated, for use in messages to the user.
     * <p>
     * Generated from the enum, so the list stays in step with the commands
     * that actually exist rather than being maintained by hand.
     *
     * @return the keywords in declaration order, separated by ", "
     */
    public static String listKeywords() {
        String keywords = Arrays.stream(values())
                .map(Command::getKeyword)
                .collect(Collectors.joining(", "));
        // Used inside "I understand: ..." messages, which would read as a
        // dangling sentence if the enum ever had no constants left.
        assert !keywords.isEmpty() : "the chatbot always understands at least one command";
        return keywords;
    }
}
