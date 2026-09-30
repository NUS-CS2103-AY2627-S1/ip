package bean.command;

/** Represents the commands understood by the application and their aliases. */
public enum Commands {
    EXIT("exit", "q"),
    LIST("list", "ls"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo", "td"),
    DEADLINE("deadline", "dln"),
    EVENT("event", "evt"),
    DELETE("delete", "del"),
    FIND("find", "findtask", "f"),
    NONE;

    private final String[] names;

    Commands(String... names) {
        this.names = names;
    }

    /** Returns the command matching the given input, or {@link #NONE} when there is no match. */
    public static Commands match(String input) {
        if (input == null) {
            return NONE;
        }

        for (Commands command : Commands.values()) {
            for (String name : command.names) {
                if (name.equalsIgnoreCase(input)) {
                    return command;
                }
            }
        }
        return NONE;
    }

}
