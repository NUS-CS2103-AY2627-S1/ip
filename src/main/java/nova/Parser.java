package nova;

/**
 * Parses one line of raw user input into a command word and its arguments.
 * Leading and trailing spaces are ignored, runs of spaces count as one, and the
 * command word is matched without regard to case, so "  LIST  " and "list" mean
 * the same thing.
 */
public class Parser {
    private static final String SPACE_RUN = "\\s+";
    private static final String SPACE = " ";

    private final String commandWord;
    private final String arguments;

    /**
     * Creates a parser for one line of user input.
     *
     * @param rawInput the line typed by the user
     */
    public Parser(String rawInput) {
        assert rawInput != null : "Raw input must not be null";
        String[] parts = normalize(rawInput).split(SPACE, 2);
        this.commandWord = parts[0].toLowerCase();
        this.arguments = parts.length == 2 ? parts[1] : "";
    }

    /** Removes leading and trailing spaces and squeezes runs of spaces into one. */
    private static String normalize(String text) {
        return text.strip().replaceAll(SPACE_RUN, SPACE);
    }

    /**
     * Returns the command word, in lower case.
     *
     * @return the command word, or an empty string when the input was blank
     */
    public String getCommandWord() {
        return this.commandWord;
    }

    /**
     * Returns the arguments that follow the command word.
     *
     * @return the arguments, or an empty string when there were none
     */
    public String getArguments() {
        return this.arguments;
    }

    /**
     * Returns whether the input carried any arguments.
     *
     * @return true if there is at least one argument
     */
    public boolean hasArgument() {
        return !this.arguments.isEmpty();
    }

    /**
     * Returns whether the arguments are a single word, such as a task number.
     *
     * @return true if there is exactly one word of arguments
     */
    public boolean hasSingleWordArgument() {
        return hasArgument() && !this.arguments.contains(SPACE);
    }

    /**
     * Splits text around the first occurrence of a parameter label.
     *
     * @param text the text to split, e.g. "return book /by 2026-08-28"
     * @param parameter the parameter label, e.g. "/by"
     * @return the text before and after the label with surrounding spaces
     *         removed, or an empty array when the label is absent
     */
    public static String[] splitAround(String text, String parameter) {
        assert text != null && parameter != null && !parameter.isEmpty()
                : "Text and parameter label must not be empty";
        int at = text.indexOf(parameter);
        if (at < 0) {
            return new String[0];
        }
        String before = text.substring(0, at).trim();
        String after = text.substring(at + parameter.length()).trim();
        return new String[] {before, after};
    }

    /**
     * Counts how many times a parameter label occurs in text. Users repeat a
     * parameter by accident, and the command handlers reject those inputs.
     *
     * @param text the text to search
     * @param parameter the parameter label, e.g. "/by"
     * @return the number of occurrences
     */
    public static int countOf(String text, String parameter) {
        assert text != null && parameter != null && !parameter.isEmpty()
                : "Text and parameter label must not be empty";
        int count = 0;
        int at = text.indexOf(parameter);
        while (at >= 0) {
            count++;
            at = text.indexOf(parameter, at + parameter.length());
        }
        return count;
    }
}
