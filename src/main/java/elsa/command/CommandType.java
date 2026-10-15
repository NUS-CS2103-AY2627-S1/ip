package elsa.command;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The kinds of command the chatbot understands. Each constant pairs the keyword the
 * user types with the usage shown when that command is typed wrongly, so the two
 * cannot be mismatched.
 *
 * <p>This is the vocabulary of the language, not the behaviour: it says which words
 * are commands and how each is written, and nothing about what any of them does.
 *
 * <p>The order the constants are declared in is the order {@link #getUsages}
 * hands them back, which is the order the window lists them in when it opens. It
 * therefore runs from what a new user needs first to what they need last:
 * putting a task in, looking at what is there, changing it, asking what else
 * there is, and leaving. A new command goes where a user would want to meet it,
 * not on the end.
 *
 * <p>Each command also names the group it belongs to, which is what lets help
 * be read as four short lists rather than one long one. The groups follow the
 * declaration order rather than cutting across it, so a command placed where a
 * user would want to meet it lands in the right group by doing so.
 */
public enum CommandType {
    /** Adds a task with no date attached to it. */
    TODO("todo", "todo <description>", Groups.ADDING),

    /** Adds a task that has to be done before a stated date. */
    DEADLINE("deadline", "deadline <description> /by <date>", Groups.ADDING),

    /** Adds a task that runs between two stated dates. */
    EVENT("event", "event <description> /from <date> /to <date>", Groups.ADDING),

    /** Shows every task in the list. */
    LIST("list", "list", Groups.SEEING),

    /** Shows the tasks falling on one date. */
    ON("on", "on <date>", Groups.SEEING),

    /** Shows the unfinished deadlines that are overdue or due within a week. */
    REMIND("remind", "remind", Groups.SEEING),

    /**
     * Shows the tasks whose description contains a keyword. The trailing "..."
     * is the usual way of writing that the argument before it may be repeated.
     */
    FIND("find", "find <keyword>...", Groups.SEEING),

    /** Marks a task as done. */
    MARK("mark", "mark <task number>", Groups.CHANGING),

    /** Marks a task as not done after all. */
    UNMARK("unmark", "unmark <task number>", Groups.CHANGING),

    /** Removes a task from the list. */
    DELETE("delete", "delete <task number>", Groups.CHANGING),

    /** Lists how every command is written. */
    HELP("help", "help", Groups.OTHER),

    /** Ends the session. */
    BYE("bye", "bye", Groups.OTHER),

    /** The user pressed enter without typing anything, so the keyword is the empty string. */
    NOTHING("", "", null),

    /**
     * The keyword matched no command. Its keyword is null rather than a real word,
     * so that no input can ever match it by accident in fromKeyword.
     */
    UNKNOWN(null, null, null);

    /**
     * The headings the commands are listed under in help.
     *
     * <p>Held in a class of their own rather than as fields of the enum, because
     * an enum's constants are built before its static fields exist, so a field
     * of this enum could not be named in the list of constants above. Reaching
     * them through another class sidesteps that, and keeps each heading written
     * once where three commands share it.
     */
    private static final class Groups {
        private static final String ADDING = "Adding a task";
        private static final String SEEING = "Seeing what you have";
        private static final String CHANGING = "Changing a task";
        private static final String OTHER = "Anything else";

        /** Prevents this holder from being instantiated; it only names things. */
        private Groups() {
        }
    }

    private final String keyword;
    private final String usage;
    private final String group;

    /**
     * Enum constructors are always private: the constants listed above are the only
     * instances that will ever exist.
     *
     * @param keyword the word the user types to invoke this command
     * @param usage   the correct way to type this command, shown in error messages
     * @param group   what this command is listed under in help, or null for the
     *                two that are never listed
     */
    CommandType(String keyword, String usage, String group) {
        this.keyword = keyword;
        this.usage = usage;
        this.group = group;
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the keyword, for example "deadline"
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the correct way to type this command.
     *
     * @return the usage text, for example "deadline &lt;description&gt; /by &lt;when&gt;"
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Returns how every command a user can type is written, in the order a new
     * user should meet them.
     *
     * <p>NOTHING and UNKNOWN are left out. They are not commands anyone types:
     * they are what the chatbot calls a blank line and a word it does not know,
     * and neither has a way of being written down.
     *
     * <p>This is read by whatever introduces the chatbot to the user, so that
     * the introduction cannot fall out of step with what the chatbot actually
     * understands. A command added above appears in it without anything else
     * being edited.
     *
     * @return one usage line per command, for example "find &lt;keyword&gt;"
     */
    public static List<String> getUsages() {
        // Flattened from the grouped list rather than gathered again, so the two
        // cannot come to disagree about which commands a user may type.
        return getUsagesByGroup().values().stream()
                .flatMap(List::stream)
                .toList();
    }

    /**
     * Returns how every command a user can type is written, gathered under the
     * heading each is listed beneath.
     *
     * <p>The map keeps the order the groups are met in, because the order the
     * constants are declared in is the order a new user should meet them and the
     * groups follow it. A plain HashMap would hand the groups back in whatever
     * order suited it, which would put leaving before adding as readily as not.
     *
     * @return the usages under each heading, in the order they should be shown
     */
    public static Map<String, List<String>> getUsagesByGroup() {
        return Arrays.stream(values())
                .filter(command -> command.usage != null && !command.usage.isEmpty())
                .collect(Collectors.groupingBy(
                        command -> command.group,
                        LinkedHashMap::new,
                        Collectors.mapping(command -> command.usage, Collectors.toList())));
    }

    /**
     * Finds the command a keyword refers to.
     *
     * @param keyword the first word of the line the user typed
     * @return the matching command, or UNKNOWN if no command uses that keyword
     */
    public static CommandType fromKeyword(String keyword) {
        // findFirst stops at the match, and orElse supplies the answer for a
        // word no command claims, so the "not found" case is stated rather than
        // being whatever the search happened to fall through to.
        return Arrays.stream(values())
                .filter(command -> keyword.equals(command.keyword))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
