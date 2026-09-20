package axiom.command;

import java.util.Arrays;

/**
 * Represents the commands supported by the AXIOM chatbot.
 */
public enum Command {
    BYE("bye"),
    LIST("list"),
    SORT("sort"),
    FIND("find"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    UNKNOWN("");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword that identifies this command in user input.
     *
     * @return The command keyword (e.g. {@code "todo"}).
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Splits a command line into whitespace-separated tokens.
     * Leading, trailing, and repeated spaces are ignored.
     *
     * @param input Full command line from the user.
     * @return The tokens in order, or an empty array if {@code input} is blank.
     */
    public static String[] tokens(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return new String[0];
        }
        return trimmed.split("\\s+");
    }

    /**
     * Returns whether {@code input} is this command, with or without arguments.
     *
     * @param input Full command line from the user.
     * @return {@code true} if the first token is this command's keyword.
     */
    public boolean matches(String input) {
        String[] tokens = tokens(input);
        return tokens.length > 0 && keyword.equals(tokens[0]);
    }

    /**
     * Returns the argument portion of the user input after the command keyword.
     * Surrounding and repeated whitespace is collapsed to single spaces.
     *
     * @param input Full command line from the user.
     * @return The argument text, or an empty string if none was provided.
     */
    public String getArgument(String input) {
        assert this != UNKNOWN : "UNKNOWN has no keyword from which to extract an argument";
        assert matches(input) : "getArgument should only be used after this command was matched";
        String[] tokens = tokens(input);
        if (tokens.length <= 1) {
            return "";
        }
        return String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
    }

    /**
     * Returns the command keyword from a line of user input.
     *
     * @param input Full command line from the user.
     * @return The matching command, or {@link #UNKNOWN} if no command matches.
     */
    public static Command fromInput(String input) {
        String[] tokens = tokens(input);
        if (tokens.length == 0) {
            return UNKNOWN;
        }
        return Arrays.stream(values())
                .filter(command -> command != UNKNOWN)
                .filter(command -> command.keyword.equals(tokens[0]))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
