package crack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The commands Crack knows, each tied to the words the user can type for it,
 * the arguments it takes and a one-line summary of what it does.
 * Listed in the order the help command shows them: adding first, then looking
 * things up, then changing what is already there.
 */
public enum Command {
    /** Adds a task with no date. */
    TODO("<what>", "ts with no date", "todo", "t"),
    /** Adds a task due by a date. */
    DEADLINE("<what> /by <when>", "ts with a due date", "deadline", "d"),
    /** Adds a task spanning two dates. */
    EVENT("<what> /from <start> /to <end>", "ts spanning two dates", "event", "e"),
    /** Prints every task. */
    LIST("", "everything you got on deck", "list", "ls"),
    /** Prints whatever lands on one day. */
    ON("<when>", "what lands on one day", "on"),
    /** Prints every task whose description contains a keyword. */
    FIND("<word>", "hunt ts down by description", "find", "f"),
    /** Marks a task done. */
    MARK("<num>", "call ts done", "mark", "m"),
    /** Marks a task not done. */
    UNMARK("<num>", "put ts back on the pile", "unmark", "um"),
    /** Removes a task. */
    DELETE("<num>", "drop ts off the list", "delete", "rm", "del"),
    /** Prints this list. */
    HELP("", "this right here", "help", "h", "?"),
    /** Ends the session. */
    BYE("", "I fade", "bye");

    private final String arguments;
    private final String summary;
    private final List<String> keywords;

    Command(String arguments, String summary, String... keywords) {
        this.arguments = arguments;
        this.summary = summary;
        this.keywords = List.of(keywords);
    }

    /**
     * Returns the command matching the word the user typed.
     * A command answers to its full name and to any alias listed against it,
     * in whatever capitalisation the user happened to use.
     *
     * @param keyword The first word of the user's input.
     * @return The matching command.
     * @throws CrackException If no command uses that word.
     */
    public static Command fromKeyword(String keyword) throws CrackException {
        String normalised = keyword.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(command -> command.keywords.contains(normalised))
                .findFirst()
                .orElseThrow(() -> new CrackException("Icl I got no clue what ts means. Type help."));
    }

    /**
     * Returns every word that calls up this command, full name first.
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Returns the short forms of this command, leaving out its full name.
     */
    public List<String> getAliases() {
        return keywords.subList(1, keywords.size());
    }

    /**
     * Returns how the command is typed out, full name followed by any arguments.
     */
    public String getUsage() {
        return arguments.isEmpty() ? keywords.get(0) : keywords.get(0) + " " + arguments;
    }

    /**
     * Returns what the command does, in Crack's words.
     */
    public String getSummary() {
        return summary;
    }
}
