package wodan.parser;

import java.util.Arrays;
import java.util.stream.Collectors;

import wodan.WodanException;

/**
 * Words the chatbot recognizes as the first token of a command line.
 */
public enum CommandWord {
    /** Add a todo task. */
    TODO,
    /** Add a deadline task. */
    DEADLINE,
    /** Add an event task. */
    EVENT,
    /** Show all tasks. */
    LIST,
    /** Mark a task as done. */
    MARK,
    /** Mark a task as not done. */
    UNMARK,
    /** Remove a task from the list. */
    DELETE,
    /** Show tasks that occur on a date. */
    ON,
    /** Find tasks by a keyword in the description. */
    FIND,
    /** Set a task's category. */
    TAG,
    /** Stop the chatbot. */
    BYE;

    /**
     * Returns the command word matching {@code word}, ignoring case.
     *
     * @param word First token of the user input.
     * @return The matching command word.
     * @throws WodanException If {@code word} is not a known command.
     */
    public static CommandWord parse(String word) throws WodanException {
        try {
            return CommandWord.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WodanException(unknownCommandMessage());
        }
    }

    /**
     * Returns the message used when the user types an unknown command.
     *
     * @return The unknown-command error text.
     */
    public static String unknownCommandMessage() {
        return "That rune is unknown. Speak "
                + joinWithOr("todo", "deadline", "event", "list", "mark",
                        "unmark", "delete", "on", "find", "tag", "bye")
                + ".";
    }

    /**
     * Returns {@code words} joined with commas, with {@code or} before the last word.
     *
     * @param words Words to join. Must contain at least one word.
     * @return A spoken list such as {@code todo, deadline, or bye}.
     */
    private static String joinWithOr(String... words) {
        // joinWithOr will only be called by code. An empty array would not be caused by the user.
        assert words != null && words.length > 0 : "joinWithOr needs at least one word";
        if (words.length == 1) {
            return words[0];
        }
        String listed = Arrays.stream(words, 0, words.length - 1)
                .collect(Collectors.joining(", "));
        return listed + ", or " + words[words.length - 1];
    }
}
