package lebron.parser;

import lebron.task.Task;

/**
 * The result of {@link Parser#parse(String)}: a command the user typed,
 * reduced to a type plus whatever typed data that command needs.
 *
 * <p>Only the fields relevant to {@link #getType()} are meaningful:
 * {@code TODO} carries a description, {@code FIND} carries a search keyword,
 * {@code DEADLINE}/{@code EVENT} carry a ready-built {@link Task}, and
 * {@code MARK}/{@code UNMARK}/{@code DELETE} carry a 1-based task number.
 * Instances are created through the static factory methods and are immutable.
 */
public class ParsedCommand {
    /** The kinds of command the chatbot understands. */
    public enum Type { LIST, TODO, DEADLINE, EVENT, MARK, UNMARK, DELETE, FIND, BYE }

    private final Type type;
    private final String description; // TODO, FIND (search keyword)
    private final Task task; // DEADLINE, EVENT
    private final int index; // MARK, UNMARK, DELETE

    private ParsedCommand(Type type, String description, Task task, int index) {
        this.type = type;
        this.description = description;
        this.task = task;
        this.index = index;
    }

    /** Returns a command that lists every task. */
    public static ParsedCommand list() {
        return new ParsedCommand(Type.LIST, null, null, 0);
    }

    /** Returns a command that exits the program. */
    public static ParsedCommand bye() {
        return new ParsedCommand(Type.BYE, null, null, 0);
    }

    /**
     * Returns a command that adds a todo.
     *
     * @param description the todo's description
     */
    public static ParsedCommand todo(String description) {
        return new ParsedCommand(Type.TODO, description, null, 0);
    }

    /** For {@code FIND}, carrying the keyword to search task descriptions for. */
    public static ParsedCommand find(String keyword) {
        return new ParsedCommand(Type.FIND, keyword, null, 0);
    }

    /** For {@code DEADLINE} or {@code EVENT}, carrying the built task. */
    public static ParsedCommand ofTask(Type type, Task task) {
        return new ParsedCommand(type, null, task, 0);
    }

    /** For {@code MARK}, {@code UNMARK} or {@code DELETE}, carrying the number. */
    public static ParsedCommand ofIndex(Type type, int oneBasedIndex) {
        return new ParsedCommand(type, null, null, oneBasedIndex);
    }

    /** Returns which kind of command this is. */
    public Type getType() {
        return type;
    }

    /** Returns the todo description; meaningful only for {@code TODO}. */
    public String getDescription() {
        return description;
    }

    /** Returns the pre-built task; meaningful only for {@code DEADLINE}/{@code EVENT}. */
    public Task getTask() {
        return task;
    }

    /** Returns the 1-based task number; meaningful only for {@code MARK}/{@code UNMARK}/{@code DELETE}. */
    public int getIndex() {
        return index;
    }
}
