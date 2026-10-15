package benbot;

import java.util.Arrays;

/** A task without an attached date or time. */
public class ToDo extends Task {

    /**
     * Creates a to-do task from a parsed {@code todo} command.
     *
     * @param words the variable-length sequence of words in the parsed command.
     */
    public ToDo(String... words) {
        super(String.join(" ", Arrays.copyOfRange(words, 1, words.length)), TaskType.TODO);
    }
}
