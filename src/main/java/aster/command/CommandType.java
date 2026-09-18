package aster.command;

/**
 * The commands Aster understands, one constant per keyword the user can type.
 *
 * <p>The keywords are a closed set, so keeping them here means the switch that carries
 * out a command, the messages that name a command, and the list of commands shown when
 * one is not recognized all read from this single declaration and cannot drift apart.
 * The declaration order is the order {@link #formatKeywordList()} presents them in.
 */
public enum CommandType {
    /**
     * Adds a task with no date attached.
     */
    TODO("todo"),
    /**
     * Adds a task that must be done by a stated date or time.
     */
    DEADLINE("deadline"),
    /**
     * Adds a task that spans a stated start and end point.
     */
    EVENT("event"),
    /**
     * Shows the stored tasks.
     */
    LIST("list"),
    /**
     * Shows the tasks whose description contains a keyword.
     */
    FIND("find"),
    /**
     * Shows how many tasks there are, how many are done, and how many of each type.
     */
    STATS("stats"),
    /**
     * Marks one task as done.
     */
    MARK("mark"),
    /**
     * Marks one task as not done.
     */
    UNMARK("unmark"),
    /**
     * Removes one task.
     */
    DELETE("delete"),
    /**
     * Ends the conversation.
     */
    BYE("bye");

    private final String keyword;

    /**
     * Creates a command identified by the keyword the user types.
     *
     * @param keyword the word the user types to give this command.
     */
    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the keyword the user types to give this command.
     *
     * @return the keyword, for example {@code "mark"}.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the command a keyword names.
     *
     * <p>This is the one place where a word the user typed becomes a command, so the
     * match is made here and nowhere else. The match is exact: no trimming, and no
     * difference in letter case is allowed.
     *
     * @param keyword the first word of the command line.
     * @return the command using that keyword, or {@code null} if no command uses it.
     */
    public static CommandType findByKeyword(String keyword) {
        for (CommandType command : values()) {
            if (command.keyword.equals(keyword)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Returns every keyword in declaration order as a phrase to show the user, for
     * example {@code todo, deadline, event, list, find, stats, mark, unmark, delete and bye}.
     *
     * <p>Building the phrase here rather than writing it out means it stays correct if
     * a command is ever added, removed or renamed.
     *
     * @return the keywords separated by commas, with {@code and} before the last.
     */
    public static String formatKeywordList() {
        CommandType[] commands = values();
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < commands.length; i++) {
            if (i > 0) {
                list.append(i == commands.length - 1 ? " and " : ", ");
            }
            list.append(commands[i].keyword);
        }
        return list.toString();
    }
}
