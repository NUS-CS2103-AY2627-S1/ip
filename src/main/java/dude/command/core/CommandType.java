package dude.command.core;

/**
 * Identifies the command words understood by DUDE.
 */
public enum CommandType {
    /**
     * Terminates the application.
     */
    BYE("bye", "Usage: bye"),
    /**
     * Displays all tasks.
     */
    LIST("list", "Usage: list"),
    /**
     * Finds tasks whose descriptions contain a keyword.
     */
    FIND("find", "Usage: find <keyword>"),
    /**
     * Displays tasks occurring on a date.
     */
    ON("on", "Usage: on <yyyy-MM-dd>"),
    /**
     * Marks a task as completed.
     */
    MARK("mark", "Usage: mark <task number>"),
    /**
     * Marks a task as not completed.
     */
    UNMARK("unmark", "Usage: unmark <task number>"),
    /**
     * Removes a task.
     */
    DELETE("delete", "Usage: delete <task number>"),
    /**
     * Creates a todo task.
     */
    TODO("todo", "Usage: todo <task details>"),
    /**
     * Creates a deadline task.
     */
    DEADLINE("deadline", "Usage: deadline <description> /by <yyyy-MM-dd [HHmm]>"),
    /**
     * Creates an event task.
     */
    EVENT("event", "Usage: event <description> /from <yyyy-MM-dd [HHmm]> /to <yyyy-MM-dd [HHmm]>"),
    /**
     * Reverses the most recent side-effect command.
     */
    UNDO("undo", "Usage: undo");

    private final String word;
    private final String usageMessage;

    /**
     * Creates command metadata.
     *
     * @param word         Command word.
     * @param usageMessage Usage message, or null when no usage is needed.
     */
    CommandType(String word, String usageMessage) {
        this.word = word;
        this.usageMessage = usageMessage;
    }

    /**
     * Returns the command word.
     *
     * @return Command word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Returns the command usage message.
     *
     * @return Usage message.
     */
    public String getUsageMessage() {
        return usageMessage;
    }
}
