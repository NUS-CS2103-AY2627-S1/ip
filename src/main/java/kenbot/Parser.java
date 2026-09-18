package kenbot;

/**
 * Makes sense of what the user typed.
 *
 * <p>Turns a raw line of input into a command and the text that followed it,
 * and refuses anything it cannot make sense of. This class never prints
 * anything and never changes a task; working out <em>what was asked for</em> is
 * kept separate from <em>carrying it out</em>.</p>
 */
public class Parser {

    /**
     * One command, ready to be carried out.
     *
     * @param command which command the user asked for
     * @param argument the text typed after the command word, or an empty string
     */
    public record ParsedCommand(CommandType command, String argument) { }

    /** No instances: this class holds no state, so there is nothing to create. */
    private Parser() {
    }

    /**
     * Checks that a command marker such as {@code /by} was given at most once.
     *
     * <p>Without this a second marker is not rejected but quietly absorbed:
     * {@code deadline x /by 2019-10-15 /by 2019-10-20} used to split at the
     * first one and keep " /by 2019-10-20" as the time of day, producing a task
     * that reads back as nonsense. Refusing is better than storing something the
     * user did not mean.</p>
     *
     * <p>Lives here rather than in each task type so all three agree on what
     * counts as a repeat.</p>
     *
     * @param argument the text typed after the command word
     * @param marker the marker to count, spaced as it appears, such as {@code " /by "}
     * @throws KenbotException if the marker appears more than once
     */
    public static void requireAtMostOne(String argument, String marker)
            throws KenbotException {
        int first = argument.indexOf(marker);
        if (first >= 0 && argument.indexOf(marker, first + marker.length()) >= 0) {
            throw new KenbotException("You've given" + marker.stripTrailing()
                    + " more than once. I only know what to do with one.");
        }
    }

    /**
     * Works out which command a line of input is asking for.
     *
     * @param input the whole line the user typed, already trimmed and not empty
     * @return the command and its argument
     * @throws KenbotException if the line holds a bar, or the first word is not
     *         a command Kenbot knows
     */
    public static ParsedCommand parse(String input) throws KenbotException {
        // The Javadoc above promises this; an assertion is the same sentence in
        // a form that can fail. Ui.readCommand() trims, run() skips empty
        // lines, and getResponse() trims after its own blank check, so a
        // failure here means a caller stopped keeping that promise.
        assert input != null && !input.isBlank() && input.equals(input.trim())
                : "parse expects a trimmed, non-empty line, got: '" + input + "'";

        // Rejected here rather than when saving: once a description holding a
        // bar reaches the file, its line can no longer be split back into the
        // right fields, and the task would silently come back incomplete.
        if (input.contains("|")) {
            throw new KenbotException("Can't use '|' in a task, that's what"
                    + " splits the fields in my save file.");
        }

        // A limit of 2 keeps the rest of the line in one piece, so a
        // description may contain spaces.
        String[] parts = input.split("\\s+", 2);
        String argument = parts.length > 1 ? parts[1] : "";
        return new ParsedCommand(CommandType.from(parts[0]), argument);
    }
}
