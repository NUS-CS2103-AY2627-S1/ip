package tally.parser;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import tally.TallyException;

/**
 * The commands Tally understands, each paired with the word the user types for it.
 *
 * <p>Holding the words here rather than spelling them out at each branch of the
 * dispatch means the list Tally offers when it does not recognize a command is
 * built from the same source, so the two cannot fall out of step.
 */
public enum Command {
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    WINDOW("window", true),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    FIND("find", false),
    FREE("free", false),
    BYE("bye", false);

    /**
     * The commands that take nothing after the command word.
     *
     * <p>Kept as a set rather than a second flag on each constant, because a constant
     * declared as TODO("todo", true, true) says which word it is and nothing about
     * which answer belongs to which question.
     */
    private static final Set<Command> TAKE_NO_ARGUMENTS = EnumSet.of(LIST, BYE);

    private final String keyword;

    /** Whether carrying this command out can leave the tally different from before. */
    private final boolean canChangeTally;

    Command(String keyword, boolean canChangeTally) {
        this.keyword = keyword;
        this.canChangeTally = canChangeTally;
    }

    /**
     * Returns whether this command can change what is on the tally.
     *
     * <p>A command that only reads the tally needs no save afterwards, and saving
     * anyway would rewrite the file for a command that changed nothing.
     *
     * @return true for the commands that add, remove or alter a task.
     */
    public boolean canChangeTally() {
        return canChangeTally;
    }

    /**
     * Returns whether anything may follow this command word.
     *
     * <p>A command that takes nothing is given nothing on purpose: text after it means
     * the user meant something Tally is not about to guess at.
     *
     * @return false for the commands that must be typed on their own.
     */
    public boolean takesArguments() {
        return !TAKE_NO_ARGUMENTS.contains(this);
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the command word, such as "mark".
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the command named by the given word.
     *
     * @param word the first word of the line the user typed.
     * @return the command that word names.
     * @throws TallyException if no command uses that word.
     */
    public static Command parse(String word) throws TallyException {
        // Matched without regard to case, since "List" and "TODO" are the shift key
        // rather than a different intention. Locale.ROOT so the machine's own language
        // cannot decide what the command words fold to.
        String folded = word.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(command -> command.keyword.equals(folded))
                .findFirst()
                .orElseThrow(() -> new TallyException(
                        "Unknown command. The ones I answer to: " + listKeywords() + "."));
    }

    /**
     * Returns every command word, in the order the commands are declared here.
     *
     * @return the words separated by commas, such as "todo, deadline, event".
     */
    private static String listKeywords() {
        return Arrays.stream(values())
                .map(command -> command.keyword)
                .collect(Collectors.joining(", "));
    }
}
