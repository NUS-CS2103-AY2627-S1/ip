package verity.parser;

import java.util.Arrays;

/**
 * The set of command keywords Verity recognizes. Each constant knows its own
 * keyword and whether that keyword must match the whole input exactly (no
 * arguments expected, e.g. {@code list}) or only needs to prefix it
 * (arguments follow on the same line, e.g. {@code mark 2}).
 */
public enum CommandWord {
    /** Lists every task in the task list. */
    LIST("list", false),
    /** Lists tasks occurring on a given date. */
    ON("on", true),
    /** Finds tasks whose description contains a given keyword. */
    FIND("find", true),
    /** Adds a task with no date attached. */
    TODO("todo", true),
    /** Adds a task with a due date. */
    DEADLINE("deadline", true),
    /** Adds a task with a start and end date. */
    EVENT("event", true),
    /** Marks a task as done. */
    MARK("mark", true),
    /** Marks a task as not done. */
    UNMARK("unmark", true),
    /** Removes a task from the task list. */
    DELETE("delete", true),
    /** Reverses the most recently executed undoable command. */
    UNDO("undo", false),
    /** Ends the program. */
    BYE("bye", false);

    private final String keyword;
    private final boolean takesArguments;

    CommandWord(String keyword, boolean takesArguments) {
        this.keyword = keyword;
        this.takesArguments = takesArguments;
    }

    /**
     * Returns this command's keyword, e.g. "mark".
     *
     * @return Keyword typed by the user to invoke this command.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the command word whose keyword matches the given (already
     * lowercased) input, or {@code null} if none match.
     *
     * @param command Lowercased user input to match against.
     * @return Matching command word, or {@code null} if unrecognized.
     */
    public static CommandWord match(String command) {
        return Arrays.stream(values())
                .filter(candidate -> candidate.takesArguments
                        ? matchesArgumentCommand(command, candidate.keyword)
                        : command.equals(candidate.keyword))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns whether the input starts with an argument-taking command as a complete word.
     * This prevents inputs such as {@code "todays plan"} from being mistaken for a
     * {@code todo} command.
     *
     * @param command Lowercased user input.
     * @param keyword Command keyword to look for.
     * @return {@code true} if the keyword is the whole input or is followed by whitespace.
     */
    private static boolean matchesArgumentCommand(String command, String keyword) {
        return command.equals(keyword)
                || (command.startsWith(keyword) && command.length() > keyword.length()
                && Character.isWhitespace(command.charAt(keyword.length())));
    }

    /**
     * Returns a human-readable, comma-separated list of every command's
     * keyword, e.g. "list, todo, ..., or bye", for use in error messages.
     *
     * @return Description of all recognized commands.
     */
    public static String describeAll() {
        CommandWord[] values = values();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                result.append(i == values.length - 1 ? ", or " : ", ");
            }
            result.append(values[i].keyword);
        }
        return result.toString();
    }
}
