package ev;

/**
 * Represents E.V.'s understood commands, each paired with the keyword the user types.
 */
public enum Command {
    BYE("bye", false),
    LIST("list", false),
    MARK("mark", true),
    UNMARK("unmark", true),
    DELETE("delete", true),
    TODO("todo", true),
    DEADLINE("deadline", true),
    EVENT("event", true),
    FIND("find", false),
    SORT("sort", false),
    HELP("help", false);

    private final String keyword;
    private final boolean isMutating;

    Command(String keyword, boolean isMutating) {
        this.keyword = keyword;
        this.isMutating = isMutating;
    }

    /**
     * Returns whether carrying out this command changes the task registry.
     *
     * @return true if the registry must be saved after this command runs.
     */
    public boolean isMutating() {
        return isMutating;
    }

    /**
     * Returns the command whose keyword matches the given word.
     *
     * @param keyword the first word of the user's input.
     * @return the matching command.
     * @throws EvException if no command uses that keyword.
     */
    public static Command parseKeyword(String keyword) throws EvException {
        for (Command command : values()) {
            if (command.keyword.equals(keyword)) {
                return command;
            }
        }

        throw new EvException("I'm sorry Spidey, but this command seems to be outside "
                + "my current scope. Try again.");
    }
}
