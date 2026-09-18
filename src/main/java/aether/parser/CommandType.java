package aether.parser;

/** Represents the commands that Aether understands. */
public enum CommandType {
    /** Shows every stored task. */
    LIST("list"),
    /** Shows tasks whose descriptions contain a keyword. */
    FIND("find"),
    /** Orders tasks by their deadline or event start date. */
    SORT("sort"),
    /** Marks a task as completed. */
    MARK("mark"),
    /** Marks a task as pending. */
    UNMARK("unmark"),
    /** Removes a task. */
    DELETE("delete"),
    /** Adds a todo. */
    TODO("todo"),
    /** Adds a deadline. */
    DEADLINE("deadline"),
    /** Adds an event. */
    EVENT("event"),
    /** Ends the chatbot session. */
    BYE("bye");

    private final String commandWord;

    CommandType(String commandWord) {
        this.commandWord = commandWord;
    }

    /** Returns the word the user types to invoke this command. */
    public String getCommandWord() {
        return commandWord;
    }
}
