package serangooner.command;

// Enum-based command library refactored with the help of Codex.
/**
 * Represents a command that a user can enter in Serangooner.
 * Each constant pairs the keyword typed by the user with the syntax and
 * description shown in the help listing.
 */
public enum CommandType {
    TODO("todo", "todo <description>", "add a task without a date or time"),
    DEADLINE("deadline", "deadline <description> by <date>", "add a task with a deadline"),
    EVENT("event", "event <description> from <date> to <date>", "add an event"),
    LIST("list", "list", "view all saved tasks"),
    ON("on", "on <date> [to <date>]", "view deadlines and events on a date or within a range"),
    FIND("find", "find <keyword>", "find tasks whose description carries a keyword"),
    MARK("mark", "mark <number>", "mark a task as done"),
    UNMARK("unmark", "unmark <number>", "mark a task as incomplete"),
    DELETE("delete", "delete <number>", "delete a task"),
    UNDO("undo", "undo", "undo the last task change"),
    HELP("help", "help", "show commands"),
    BYE("bye", "bye", "exit serangooner");

    private final String keyword;
    private final String syntax;
    private final String description;

    CommandType(String keyword, String syntax, String description) {
        this.keyword = keyword;
        this.syntax = syntax;
        this.description = description;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getDescription() {
        return description;
    }
}
