package ari;

/**
 * Represents a command that Ari recognizes and its corresponding response.
 */
public enum CommandType {
    MARK("Good job! I've marked this task as done:"),
    UNMARK("Gotcha, I've unmarked this task:"),
    EXIT("Bye Bye. See you again!"),
    BYE("Bye Bye. See you again!"),
    LIST("Here are the tasks on your list:"),
    SORT("Here are your tasks sorted alphabetically (IDs updated):"),
    TODO("Gotcha. I've added this task for you:"),
    UNKNOWN("Error"),
    DEADLINE("Gotcha. I've added this task for you:"),
    EVENT("Gotcha. I've added this task for you:"),
    DELETE("Done! I've removed the task for you:"),
    FIND("Here are the matching tasks in your list:");

    private final String description;

    /**
     * Creates a command type with the response shown when it is handled.
     *
     * @param description Response associated with the command.
     */
    CommandType(String description) {
        this.description = description;
    }

    /**
     * Returns the response associated with this command.
     *
     * @return Response associated with this command.
     */
    public String getDescription() {
        return this.description;
    }
}
