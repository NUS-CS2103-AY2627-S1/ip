package bob.command;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents the words the chatbot understands at the start of a line, and the
 * rules for recognizing each one.
 *
 * <p>The name says <em>word</em> because a word is all this is. It holds the
 * chatbot's vocabulary and the rules for recognizing one of those words at the
 * start of a line — nothing about what is to be <em>done</em> once a word has
 * been recognized. Keeping the two apart leaves the plainer name {@code Command}
 * free for the classes that carry the doing.
 *
 * <p>These were eight separate {@code String} constants in {@link bob.Bob Bob}. An enum
 * suits them better because they are a fixed, known-in-advance set of values
 * that belong together: the compiler now knows the whole set, so a command can
 * be passed around as a {@code CommandWord} rather than as a {@code String} that
 * might hold any text at all, and a typo like {@code "delet"} becomes a
 * compile error instead of a command that silently never matches.
 *
 * <p>Two things follow from the set being known in one place. {@link #parse}
 * finds the command a line begins with by walking {@link #values()}, so
 * recognizing a command is no longer a chain of {@code else if} branches that
 * has to be extended by hand. And {@link #getAllKeywords} builds the list of
 * commands shown to a user who typed something unrecognized, so that message can
 * no longer fall out of step with the commands that actually exist — which is
 * exactly the kind of mistake that is easy to make when adding a command.
 */
public enum CommandWord {

    /** Adds a task with no date attached; used as {@code todo <description>}. */
    TODO("todo", true),

    /** Adds a task with a due date; used as {@code deadline <description> /by <when>}. */
    DEADLINE("deadline", true),

    /** Adds a task with a start and an end; used as {@code event <description> /from <when> /to <when>}. */
    EVENT("event", true),

    /** Prints everything stored so far; used on its own. */
    LIST("list", false),

    /** Prints the tasks falling on one day; used as {@code on <date>}. */
    ON("on", true),

    /** Prints the tasks falling before one day; used as {@code before <date>}. */
    BEFORE("before", true),

    /** Prints the tasks falling after one day; used as {@code after <date>}. */
    AFTER("after", true),

    /** Prints the most urgent tasks, soonest first; used as {@code next <how many>}. */
    NEXT("next", true),

    /** Prints the tasks whose description contains a word; used as {@code find <keyword>}. */
    FIND("find", true),

    /** Marks a task as done; used as {@code mark <task number>}. */
    MARK("mark", true),

    /** Marks a task as not done again; used as {@code unmark <task number>}. */
    UNMARK("unmark", true),

    /** Removes a task from the list; used as {@code delete <task number>}. */
    DELETE("delete", true),

    /**
     * Changes some details of a task, keeping its number and done status; used as
     * {@code edit <task number>} followed by any of {@code /desc <description>},
     * {@code /by <when>}, {@code /from <when>} and {@code /to <when>}.
     */
    EDIT("edit", true),

    /** Ends the conversation; used on its own. */
    BYE("bye", false);

    /** The word the user types to invoke this command. */
    private final String keyword;

    /**
     * Whether anything may follow the keyword on the same line.
     *
     * <p>This is what tells {@code list} and {@code bye}, which stand alone,
     * apart from the others, which are followed by what they act on.
     * The rule was previously implicit — some commands were compared with
     * {@code equals} and the rest with a separate helper — which made it
     * something a reader had to notice rather than something the code states.
     */
    private final boolean canTakeArguments;

    /**
     * Creates a command. The constructor is private, as every enum constructor
     * is: the constants declared above are the only instances there will ever be.
     *
     * @param keyword          the word the user types.
     * @param canTakeArguments whether text may follow that word.
     */
    CommandWord(String keyword, boolean canTakeArguments) {
        this.keyword = keyword;
        this.canTakeArguments = canTakeArguments;
    }

    /** Returns the word the user types to invoke this command, for example {@code delete}. */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns whether {@code line} is this command.
     *
     * <p>A command that takes arguments matches its keyword either alone or
     * followed by whitespace and the arguments. Requiring that whitespace is what
     * keeps {@code todolist} from being read as {@code todo} with the description
     * {@code list}. Any whitespace will do, not only a space, so a tab typed or
     * pasted after the keyword is read the same way as a space would be.
     * Matching the bare keyword as well is what lets {@code mark}
     * on its own be answered with "which task?" rather than "I don't know what
     * that means": the command is recognized first, and only then is it found to
     * be incomplete.
     *
     * <p>A command that takes no arguments matches only its exact keyword, so
     * {@code bye now} is not treated as {@code bye}.
     *
     * @param line one whole line as the user typed it, with surrounding spaces removed.
     */
    public boolean matches(String line) {
        if (line.equals(keyword)) {
            return true;
        }
        return canTakeArguments
                && line.startsWith(keyword)
                && line.length() > keyword.length()
                && Character.isWhitespace(line.charAt(keyword.length()));
    }

    /**
     * Returns everything the user typed after the keyword, with surrounding
     * spaces removed, or an empty string if they typed nothing after it.
     *
     * @param line a line that {@link #matches} has already accepted.
     */
    public String getArgumentsIn(String line) {
        // The arithmetic below counts past the keyword, so it is only meaningful
        // on a line that begins with it — which is what matches() has checked.
        assert matches(line) : "Arguments read from a line that is not " + keyword + ": " + line;
        // Whatever whitespace separates the keyword from the arguments is trimmed
        // off with the rest; on a line holding the keyword alone, nothing is left.
        return line.substring(keyword.length()).trim();
    }

    /**
     * Returns the command that {@code line} begins with, if it begins with one.
     *
     * <p>An {@link Optional} is returned rather than {@code null} so that the
     * caller cannot forget the "no such command" case: the result has to be
     * unwrapped before the command inside can be used, and the compiler enforces
     * that. Returning {@code null} would let an unrecognized line travel on
     * unnoticed and fail later as a {@code NullPointerException}, well away from
     * the line that caused it.
     *
     * <p>Written as a stream because the question is exactly "the first command
     * that matches, if any", and {@code findFirst} answers it with that
     * {@code Optional} already built. Like the loop it replaces, the stream stops
     * at the first match rather than testing every command.
     *
     * @param line one whole line as the user typed it, with surrounding spaces removed.
     * @return the matching command, or an empty {@code Optional} if there is none.
     */
    public static Optional<CommandWord> parse(String line) {
        return Arrays.stream(values())
                .filter(command -> command.matches(line))
                .findFirst();
    }

    /**
     * Returns the command whose keyword is exactly {@code word}, if there is one.
     *
     * <p>Unlike {@link #parse}, this looks at a single word rather than a whole
     * line, so whether the command may take arguments plays no part. That is what
     * explaining an unrecognized line needs: whether it at least began with a
     * command word, or would have if its letters were lowercase.
     *
     * @param word a single word, with no whitespace in it.
     * @return the command with that keyword, or an empty {@code Optional} if there is none.
     */
    public static Optional<CommandWord> findByKeyword(String word) {
        return Arrays.stream(values())
                .filter(command -> command.keyword.equals(word))
                .findFirst();
    }

    /**
     * Returns every command word, separated by commas, in the order the constants
     * are declared above — for example {@code todo, deadline, event, ...}.
     *
     * <p>Built from {@link #values()} rather than written out by hand, so adding
     * a command to this enum is all it takes for the chatbot to start offering it.
     *
     * <p>Written as a stream because turning each command into its keyword and
     * joining the results is exactly what {@code map} and
     * {@code Collectors.joining} do, with no joiner to set up and read back.
     */
    public static String getAllKeywords() {
        return Arrays.stream(values())
                .map(CommandWord::getKeyword)
                .collect(Collectors.joining(", "));
    }
}
