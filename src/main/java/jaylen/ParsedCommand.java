package jaylen;

/** The structured meaning of one line entered by a Jaylen user. */
public final class ParsedCommand {
    /** The command forms understood by Jaylen. */
    public enum Type {
        /** Ends the current Jaylen session. */
        BYE,
        /** Displays all stored tasks. */
        LIST,
        /** Finds tasks whose descriptions contain the search terms. */
        FIND,
        /** Marks a task as complete. */
        MARK,
        /** Marks a task as incomplete. */
        UNMARK,
        /** Removes a task from the list. */
        DELETE,
        /** Creates a task without a date or time. */
        TODO,
        /** Creates a task with a deadline. */
        DEADLINE,
        /** Creates a task with a start and end time. */
        EVENT
    }

    private final Type type;
    private final String description;
    private final String firstDetail;
    private final String secondDetail;
    private final int taskNumber;

    private ParsedCommand(Type type, String description, String firstDetail,
                          String secondDetail, int taskNumber) {
        this.type = type;
        this.description = description;
        this.firstDetail = firstDetail;
        this.secondDetail = secondDetail;
        this.taskNumber = taskNumber;
    }

    /**
     * Creates a command with no arguments.
     *
     * @param type the command type
     * @return the structured command
     */
    public static ParsedCommand simple(Type type) {
        return new ParsedCommand(type, "", "", "", 0);
    }

    /**
     * Creates a command that refers to a task number.
     *
     * @param type the command type
     * @param taskNumber the one-based task number
     * @return the structured command
     */
    public static ParsedCommand forTask(Type type, int taskNumber) {
        return new ParsedCommand(type, "", "", "", taskNumber);
    }

    /**
     * Creates a todo command.
     *
     * @param description the task description
     * @return the structured todo command
     */
    public static ParsedCommand todo(String description) {
        return new ParsedCommand(Type.TODO, description, "", "", 0);
    }

    /**
     * Creates a command that searches task descriptions for one or more terms.
     *
     * @param query the space-separated search terms
     * @return the structured find command
     */
    public static ParsedCommand find(String query) {
        return new ParsedCommand(Type.FIND, query, "", "", 0);
    }

    /**
     * Creates a deadline command.
     *
     * @param description the task description
     * @param by the deadline text
     * @return the structured deadline command
     */
    public static ParsedCommand deadline(String description, String by) {
        return new ParsedCommand(Type.DEADLINE, description, by, "", 0);
    }

    /**
     * Creates an event command.
     *
     * @param description the task description
     * @param from the event start text
     * @param to the event end text
     * @return the structured event command
     */
    public static ParsedCommand event(String description, String from, String to) {
        return new ParsedCommand(Type.EVENT, description, from, to, 0);
    }

    /**
     * Returns the command type.
     *
     * @return the command type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the task description, when present.
     *
     * @return the task description, or an empty string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the first detail, such as a deadline or event start.
     *
     * @return the first command detail, or an empty string
     */
    public String getFirstDetail() {
        return firstDetail;
    }

    /**
     * Returns the second detail, such as an event end.
     *
     * @return the second command detail, or an empty string
     */
    public String getSecondDetail() {
        return secondDetail;
    }

    /**
     * Returns the one-based task number, when present.
     *
     * @return the one-based task number, or zero when absent
     */
    public int getTaskNumber() {
        return taskNumber;
    }
}
