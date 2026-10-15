package thomas.command;

import java.util.Arrays;

import thomas.ThomasException;

/**
 * The word a user types to name a command.
 * <p>
 * The set of commands is closed, so a word the user typed either maps to one of
 * these constants or is not a command at all. Naming them here means the
 * compiler checks every use: a misspelled {@code case DEADLINE} does not compile,
 * where a misspelled {@code keyword.equals("dedline")} used to compile into a
 * branch that could never run.
 * <p>
 * These do not stand one to one against the {@link Command} classes, and are
 * deliberately not named as though they did: {@code todo}, {@code deadline} and
 * {@code event} are three keywords all carried out by one {@link AddCommand}.
 * This is the vocabulary of the language the user types.
 */
public enum Keyword {
    BYE("bye"),
    LIST("list"),
    ON("on"),
    FIND("find"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    UNDO("undo"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event");

    /**
     * The word the user types for this command.
     * <p>
     * Held as its own field rather than derived from {@link #name()}, so that
     * what the user types stays independent of what the constant is called.
     */
    private final String keyword;

    Keyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword a typed word names.
     * <p>
     * This is the one place that decides whether a word is a command, so by the
     * time a caller has a {@code Keyword} in hand it is known to be valid
     * and only real commands need handling. Matching is case-sensitive, as it
     * was when each keyword was compared with {@code equals}.
     *
     * @param keyword The first word of the line the user typed.
     * @return The matching keyword.
     * @throws ThomasException If no command has that keyword.
     */
    public static Keyword of(String keyword) throws ThomasException {
        return Arrays.stream(values())
                .filter(type -> type.keyword.equals(keyword))
                .findFirst()
                .orElseThrow(() -> new ThomasException(
                        "Cinders and ashes! I don't know that signal. What does it mean?"));
    }
}
