package seedu.goat.command;

import java.util.Arrays;
import java.util.stream.Collectors;

import seedu.goat.GoatException;

/**
 * The instructions GOAT understands, one constant per keyword the user can type.
 * <p>
 * Commands are a fixed, known set, which is exactly what an enum is for: the compiler now
 * knows every possible command, so a typo like {@code MRAK} fails to compile rather than
 * silently never matching the way a misspelled string literal would.
 */
public enum Command {
    /** Adds a task carrying no date. */
    TODO("todo"),

    /** Adds a task due on a given date. */
    DEADLINE("deadline"),

    /** Adds a task spanning a period between two dates. */
    EVENT("event"),

    /** Shows every task, numbered, with its status. */
    LIST("list"),

    /** Marks a task as done. */
    MARK("mark"),

    /** Marks a task as not done. */
    UNMARK("unmark"),

    /** Removes a task from the list. */
    DELETE("delete"),

    /** Shows the dated tasks falling on a given date. */
    ON("on"),

    /** Shows the tasks whose description contains a keyword. */
    FIND("find"),

    /** Ends the conversation. */
    BYE("bye");

    /** The word the user types to invoke this command. */
    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word that invokes this command, for use in error messages.
     *
     * @return the keyword, such as {@code mark}
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Finds the command matching a word typed by the user.
     *
     * @param keyword the first word of the user's input.
     * @return the matching command.
     * @throws GoatException if the word is empty or matches no command.
     */
    public static Command fromKeyword(String keyword) throws GoatException {
        for (Command command : values()) {
            if (command.keyword.equals(keyword)) {
                return command;
            }
        }

        if (keyword.isEmpty()) {
            throw new GoatException("I did not catch a command there. I know: "
                    + keywordList() + ".");
        }
        throw new GoatException("I do not know the command \"" + keyword + "\". I know: "
                + keywordList() + ".");
    }

    /**
     * Lists every keyword in declaration order.
     * <p>
     * Building this from {@link #values()} means adding a constant above automatically
     * updates the help text, so the two can never fall out of step.
     *
     * @return the keywords separated by commas.
     */
    private static String keywordList() {
        return Arrays.stream(values())
                .map(Command::getKeyword)
                .collect(Collectors.joining(", "));
    }
}
