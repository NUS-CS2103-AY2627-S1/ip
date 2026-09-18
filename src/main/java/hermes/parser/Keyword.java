package hermes.parser;

import hermes.command.Command;

/**
 * The command words Hermes understands, each paired with an example of correct
 * usage.
 *
 * <p>This names which command was typed. Carrying it out is the job of a
 * {@link Command}, which is built from one of these together with whatever
 * arguments followed it.
 *
 * <p>Keeping the example next to the keyword means error messages can quote the
 * correct usage without each helper method having to repeat it.
 */
public enum Keyword {
    BYE("bye", "bye"),
    LIST("list", "list"),
    MARK("mark", "mark 1"),
    UNMARK("unmark", "unmark 1"),
    DELETE("delete", "delete 3"),
    TODO("todo", "todo borrow book"),
    DEADLINE("deadline", "deadline complete tutorial /by 17/08/2026 1500"),
    EVENT("event", "event project meeting /from 17/08/2026 1500 /to 17/08/2026 1630"),
    DUE("due", "due /by 18/08/2026 1600"),
    SORT("sort", "sort"),
    FIND("find", "find book"),
    UNDO("undo", "undo"),

    /** Anything the user typed that is not a recognized command. */
    UNKNOWN("", "");

    private final String keyword;
    private final String example;

    /**
     * @param keyword commands (user input) recognized by the system.
     * @param example recommended input for the keyword if the user messes up.
     */
    Keyword(String keyword, String example) {
        this.keyword = keyword;
        this.example = example;
    }

    /**
     * Returns an example of how this command is used, for error messages.
     *
     * @return the example usage.
     */
    public String getExample() {
        return this.example;
    }

    /**
     * Finds the keyword matching what the user typed.
     *
     * @param input the first word of what the user typed.
     * @return the matching keyword, or {@link #UNKNOWN} if there is none.
     */
    public static Keyword of(String input) {
        for (Keyword candidate : values()) {
            if (candidate.keyword.equals(input)) {
                return candidate;
            }
        }

        return UNKNOWN;
    }
}
