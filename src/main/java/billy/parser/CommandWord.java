package billy.parser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import billy.BillyException;

/**
 * The words Billy accepts as commands, one for each thing it can be asked to do.
 *
 * <p>Using an enum rather than loose {@code String} constants means the set of
 * commands is fixed and known to the compiler: a command can only be one of these
 * values, and a misspelled name is a compile error instead of a command that quietly
 * never matches. The list of keywords shown to the user is built from these values,
 * so it cannot fall out of step with what Billy actually accepts.
 *
 * <p>Each command may also answer to other words. Most are abbreviations —
 * {@code t read book} is quicker to type than {@code todo read book}, and a
 * chatbot that is talked to all day should not insist on the long form every
 * time. A few are simply what people reach for out of habit, such as
 * {@code exit} and {@code quit} for {@code bye}, which are no shorter but are
 * what someone leaving a program tends to type first.
 *
 * <p>They live beside the keyword they stand for, so a command and everything
 * that invokes it can be read in one line.
 *
 * <p>The order below is the order the keywords are listed to the user.
 */
public enum CommandWord {
    /** Adds a task with no date attached, e.g. {@code todo borrow book}. */
    TODO(Group.ADDING, "todo", "t"),

    /** Adds a task with a due date, e.g. {@code deadline return book /by Sunday}. */
    DEADLINE(Group.ADDING, "deadline", "d", "dl"),

    /** Adds a task spanning a period, e.g. {@code event meeting /from 2pm /to 4pm}. */
    EVENT(Group.ADDING, "event", "e", "ev"),

    /** Prints every stored task. */
    LIST(Group.SEEING, "list", "l", "ls"),

    /** Prints the tasks falling on one day, e.g. {@code on 2019-12-02}. */
    ON(Group.SEEING, "on"),

    /** Prints the tasks whose description mentions a word, e.g. {@code find book}. */
    FIND(Group.SEEING, "find", "f"),

    /** Marks a task as done, e.g. {@code mark 2}. */
    MARK(Group.CHANGING, "mark", "m"),

    /** Marks a task as not done again, e.g. {@code unmark 2}. */
    UNMARK(Group.CHANGING, "unmark", "um"),

    /** Removes a task from the list, e.g. {@code delete 3}. */
    DELETE(Group.CHANGING, "delete", "del", "rm"),

    /** Lists every command, with the shorter words that also invoke it. */
    HELP(Group.OTHER, "help", "h", "?"),

    /** Ends the conversation. */
    BYE(Group.OTHER, "bye", "exit", "quit", "q");

    /**
     * The kind of work a command does, used to group the commands when they are
     * listed to the user.
     *
     * <p>This is the one thing about a command that cannot be worked out from
     * the command itself: nothing in the word {@code delete} says it belongs
     * with {@code mark}. It lives here beside the commands rather than as a list
     * inside the command that prints them, so a new command is grouped where it
     * is declared and cannot be left out of the listing by accident.
     */
    public enum Group {
        /** Commands that put a new task in the list. */
        ADDING("Adding"),

        /** Commands that show tasks without changing any. */
        SEEING("Seeing"),

        /** Commands that change or remove a task that is already there. */
        CHANGING("Changing"),

        /** Everything that is not about a particular task. */
        OTHER("Other");

        /** How this group is named when the commands are listed. */
        private final String heading;

        Group(String heading) {
            this.heading = heading;
        }

        /**
         * Returns how this group is named when the commands are listed.
         *
         * @return the heading, capitalized as it is shown
         */
        public String getHeading() {
            return heading;
        }
    }

    /** The kind of work this command does, which is what it is listed under. */
    private final Group group;

    /** The word the user types to invoke this command. */
    private final String keyword;

    /**
     * Other words that invoke this command.
     *
     * <p>Never null, and never changes once built. A command with nothing worth
     * adding, such as {@code on}, simply has none.
     */
    private final List<String> aliases;

    /**
     * Creates a command with the word that invokes it, and any other words that
     * invoke it as well.
     *
     * <p>The aliases are taken as a varargs list so that a command with none is
     * written {@code ON(Group.SEEING, "on")}, with nothing standing in for the
     * ones it does not have.
     *
     * @param group what kind of work the command does
     * @param keyword the word the user types, in lower case
     * @param aliases other words meaning the same thing, in lower case
     */
    CommandWord(Group group, String keyword, String... aliases) {
        this.group = group;
        this.keyword = keyword;
        // List.of copies what it is given, so nothing outside can change the
        // aliases afterwards.
        this.aliases = List.of(aliases);
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the keyword, in lower case
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the other words that invoke this command.
     *
     * @return the aliases, in lower case, empty if this command has none
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Returns the kind of work this command does.
     *
     * @return the group it is listed under
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Returns whether a typed word invokes this command, ignoring capitalization.
     *
     * @param word the first word of what the user typed
     * @return whether it is this command's keyword or one of its aliases
     */
    private boolean matches(String word) {
        return keyword.equalsIgnoreCase(word)
                || aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(word));
    }

    /**
     * Finds the command a typed word refers to, ignoring capitalization.
     *
     * <p>The aliases are looked at alongside the full keyword, so {@code rm 2} and
     * {@code delete 2} reach the same command.
     *
     * @param word the first word of what the user typed
     * @return the matching command
     * @throws BillyException if no command uses that word
     */
    public static CommandWord fromKeyword(String word) throws BillyException {
        return Arrays.stream(values())
                .filter(command -> command.matches(word))
                .findFirst()
                .orElseThrow(() -> new BillyException(
                        "I don't know what '" + word + "' means. " + describeAll()));
    }

    /**
     * Lists every keyword Billy accepts, for use when a command is not recognized.
     *
     * <p>Only the full keywords are named. The aliases are a convenience for
     * someone who already knows the command, whereas this sentence is read by
     * someone who has just got one wrong, and is the wrong moment to teach two
     * ways of saying the same thing.
     *
     * @return a sentence such as {@code I understand: todo, deadline, ..., bye.}
     */
    public static String describeAll() {
        String keywords = Arrays.stream(values())
                .map(CommandWord::getKeyword)
                .collect(Collectors.joining(", "));
        return "I understand: " + keywords + ". Type 'help' for the short forms.";
    }

    /**
     * Lists every command with the shorter words that also invoke it, grouped by
     * the kind of work each does.
     *
     * <p>This is the long answer that {@link #describeAll()} deliberately is
     * not. Someone reading this asked for it, so there is room to name the
     * abbreviations; someone reading the other has just got a command wrong, and
     * wants the right word rather than two ways of spelling it.
     *
     * <p>Built from the values and their aliases, so a command added later
     * appears here without this method being touched. Only the group headings
     * are written down, and an empty group is skipped rather than printed with
     * nothing under it.
     *
     * @return one line per group, e.g. {@code Adding: todo (t), deadline (d, dl)}
     */
    public static String describeCommands() {
        return Arrays.stream(Group.values())
                .map(CommandWord::describeGroup)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.joining("\n"));
    }

    /**
     * Returns one line naming every command in a group, or an empty string when
     * the group holds none.
     *
     * @param group the group to describe
     * @return the line for that group, without a trailing newline
     */
    private static String describeGroup(Group group) {
        String commands = Arrays.stream(values())
                .filter(command -> command.group == group)
                .map(CommandWord::describeSelf)
                .collect(Collectors.joining(", "));
        return commands.isEmpty() ? "" : group.getHeading() + ": " + commands;
    }

    /**
     * Returns this command's keyword, with its shorter words in brackets after
     * it, e.g. {@code deadline (d, dl)}.
     *
     * <p>A command with no shorter word is named on its own rather than with an
     * empty pair of brackets after it.
     *
     * @return the keyword, and the aliases if there are any
     */
    private String describeSelf() {
        if (aliases.isEmpty()) {
            return keyword;
        }
        return keyword + " (" + String.join(", ", aliases) + ")";
    }
}
