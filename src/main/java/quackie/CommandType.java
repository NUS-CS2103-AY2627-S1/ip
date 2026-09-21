package quackie;

/**
 * The command keywords understood by Quackie.
 */
public enum CommandType {
    /** Terminates the current session. */
    BYE,
    /** Lists all stored tasks. */
    LIST,
    /** Finds tasks whose descriptions contain a keyword. */
    FIND,
    /** Deletes one stored task. */
    DELETE,
    /** Marks one stored task as done. */
    MARK,
    /** Marks one stored task as not done. */
    UNMARK,
    /** Replaces the details of one stored task. */
    UPDATE,
    /** Adds an event task. */
    EVENT,
    /** Adds a deadline task. */
    DEADLINE,
    /** Adds a ToDo task. */
    TODO,
    /** Represents an input that is not a recognised command. */
    UNKNOWN;

    /**
     * Identifies the command keyword at the start of a raw user input.
     *
     * @param input the complete command entered by the user
     * @return the matching command type, or {@link #UNKNOWN} if none matches
     */
    public static CommandType fromInput(String input) {
        if (input == null) {
            return UNKNOWN;
        }

        String normalisedInput = input.strip().replaceAll("\\s+", " ");
        if (normalisedInput.equals("bye")) {
            return BYE;
        } else if (normalisedInput.equals("list")) {
            return LIST;
        } else if (normalisedInput.equals("find") || normalisedInput.startsWith("find ")) {
            return FIND;
        } else if (normalisedInput.equals("delete") || normalisedInput.startsWith("delete ")) {
            return DELETE;
        } else if (normalisedInput.equals("mark") || normalisedInput.startsWith("mark ")) {
            return MARK;
        } else if (normalisedInput.equals("unmark") || normalisedInput.startsWith("unmark ")) {
            return UNMARK;
        } else if (normalisedInput.equals("update") || normalisedInput.startsWith("update ")) {
            return UPDATE;
        } else if (normalisedInput.equals("event") || normalisedInput.startsWith("event ")) {
            return EVENT;
        } else if (normalisedInput.equals("deadline") || normalisedInput.startsWith("deadline ")) {
            return DEADLINE;
        } else if (normalisedInput.equals("todo") || normalisedInput.startsWith("todo ")) {
            return TODO;
        }
        return UNKNOWN;
    }
}
