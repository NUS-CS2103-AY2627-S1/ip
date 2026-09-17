package harvey.command;

import java.util.Arrays;
import java.util.stream.Collectors;

import harvey.HarveyException;
/**
 * The set of instructions Harvey understands, and how each one is written.
 * <p>
 * Each constant carries the keyword the user types and one correct example of the
 * command in use, so the keyword and its help text cannot drift apart. Using an enum
 * instead of separate {@code String} constants means the compiler knows the full list:
 * a misspelt {@code CommandType.DEADLIEN} will not compile, whereas a misspelt string
 * would silently never match.
 */
public enum CommandType {
    BYE("bye", "bye"),
    LIST("list", "list"),
    FIND("find", "find book"),
    MARK("mark", "mark 2"),
    UNMARK("unmark", "unmark 2"),
    DELETE("delete", "delete 3"),
    TODO("todo", "todo borrow book"),
    DEADLINE("deadline", "deadline return book /by 2019-10-15"),
    EVENT("event", "event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");

    /** The word the user types to invoke this command. */
    private final String keyword;

    /** A correctly formed use of this command, shown to the user after a mistake. */
    private final String example;

    /**
     * Creates a command. Enum constructors are always private: the constants listed
     * above are the only instances that will ever exist.
     *
     * @param keyword the word the user types.
     * @param example a correct use of the command.
     */
    CommandType(String keyword, String example) {
        this.keyword = keyword;
        this.example = example;
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the keyword, e.g. {@code delete}.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns a correct use of this command, for showing the user how to fix a mistake.
     *
     * @return one line the user could type, e.g. {@code delete 3}.
     */
    public String getExample() {
        return example;
    }

    /**
     * Finds the command a keyword refers to, ignoring case, so {@code LIST} and
     * {@code List} work as well as {@code list}.
     *
     * @param keyword the first word the user typed.
     * @return the matching command.
     * @throws HarveyException if no command uses that keyword.
     */
    public static CommandType fromKeyword(String keyword) throws HarveyException {
        // values() returns every constant declared above, so this automatically covers
        // any command added later. findFirst stops at the match rather than examining
        // the rest, the same as returning from inside a loop.
        return Arrays.stream(values())
                .filter(command -> command.keyword.equalsIgnoreCase(keyword))
                .findFirst()
                .orElseThrow(() -> unknownKeyword(keyword));
    }

    /**
     * Returns the complaint to raise when no command uses a keyword.
     * <p>
     * Written as a method rather than inline so that {@link #fromKeyword(String)} stays a
     * single expression. It is passed to {@code orElseThrow} as a supplier, so the message
     * is only built when the keyword really was unknown.
     *
     * @param keyword the first word the user typed.
     * @return the exception explaining what Harvey does understand.
     */
    private static HarveyException unknownKeyword(String keyword) {
        if (keyword.isEmpty()) {
            return new HarveyException("You didn't say anything, and I don't read minds. " + listKeywords());
        }
        return new HarveyException("I don't negotiate with gibberish like \"" + keyword + "\". "
                + listKeywords());
    }

    /**
     * Lists every keyword Harvey understands, for use in error messages.
     *
     * @return a sentence naming all the commands.
     */
    public static String listKeywords() {
        // joining takes the separator, the opening text and the closing text, so the
        // "comma before every keyword except the first" rule is stated once as an
        // argument instead of being spelled out with an index check.
        return Arrays.stream(values())
                .map(CommandType::getKeyword)
                .collect(Collectors.joining(", ", "I understand: ", "."));
    }
}
