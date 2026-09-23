package sallman.command;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import sallman.SallmanException;

/**
 * The command words the chatbot understands, each paired with the keyword the
 * user types for it.
 * <p>
 * Having a fixed set of named constants lets the compiler check that a command
 * is one of the known ones, which a bare {@code String} could not do. It also
 * keeps the list of keywords in one place: {@link #keywords()} builds the help
 * text from these constants, so a new command cannot be added without the help
 * text following it.
 */
public enum CommandType {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    FIND("find"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TAG("tag"),
    UNTAG("untag"),
    UNDO("undo"),
    SORT("sort"),
    ON("on"),
    BYE("bye");

    /** What the user types to invoke this command. */
    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword the user types for this command, for use in messages
     * that should name the command the way the user wrote it.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Finds the command matching a keyword typed by the user.
     * <p>
     * Case is ignored, so a stray Caps Lock does not turn {@code LIST} into an
     * unknown command. When nothing matches but the keyword is one small typo
     * away from a command, that command is suggested instead of the full list.
     *
     * @param keyword first word of the user's input
     * @return the matching command type
     * @throws SallmanException if no command uses that keyword
     */
    public static CommandType fromKeyword(String keyword) throws SallmanException {
        String typed = keyword.toLowerCase(Locale.ROOT);
        Optional<CommandType> match = Arrays.stream(values())
                .filter(command -> command.keyword.equals(typed))
                .findFirst();
        if (match.isPresent()) {
            return match.get();
        }
        String message = "I'm sorry, but I don't know what \"" + keyword + "\" means.";
        Optional<CommandType> guess = closestTo(typed);
        if (guess.isPresent()) {
            throw new SallmanException(message, "Did you mean " + guess.get().keyword + "?");
        }
        throw new SallmanException(message, "I understand: " + keywords() + ".");
    }

    /**
     * Returns the command whose keyword is nearest to what was typed, if it is
     * close enough to be a likely typo.
     * <p>
     * Short keywords allow only one slip, since at two slips almost any short
     * word would look like {@code on} or {@code bye}.
     *
     * @param typed the unrecognized keyword, in lower case
     * @return the closest command, or empty when none is close enough
     */
    private static Optional<CommandType> closestTo(String typed) {
        int allowedSlips = typed.length() <= 4 ? 1 : 2;
        return Arrays.stream(values())
                .filter(command -> editDistance(typed, command.keyword) <= allowedSlips)
                .min(Comparator.comparingInt(command -> editDistance(typed, command.keyword)));
    }

    /**
     * Returns how many single-character insertions, deletions or substitutions
     * turn one word into another.
     *
     * @param first  one word
     * @param second the other word
     * @return the Levenshtein distance between them
     */
    private static int editDistance(String first, String second) {
        int[] previous = new int[second.length() + 1];
        for (int j = 0; j <= second.length(); j++) {
            previous[j] = j;
        }
        for (int i = 1; i <= first.length(); i++) {
            int[] current = new int[second.length() + 1];
            current[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                int substitution = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1),
                        previous[j - 1] + substitution);
            }
            previous = current;
        }
        return previous[second.length()];
    }

    /**
     * Lists every keyword in declaration order, for use in help and error text.
     *
     * @return the keywords separated by commas, e.g. {@code todo, deadline, ...}
     */
    public static String keywords() {
        return Arrays.stream(values())
                .map(CommandType::getKeyword)
                .collect(Collectors.joining(", "));
    }
}
