package student.project.bob.parser;

/**
 * Represents a command recognized by Bob's parser.
 */
public class Command {
    /**
     * Identifies the action represented by a command.
     */
    public enum Type {
        BYE,
        LIST,
        UPCOMING,
        ON,
        OVERDUE,
        FIND,
        MARK,
        UNMARK,
        DELETE,
        UNDO,
        TASK
    }

    private final Type type;
    private final String input;

    /**
     * Creates a parsed command.
     *
     * @param type recognized command type
     * @param input original trimmed command input
     */
    public Command(Type type, String input) {
        assert type != null : "A command must have a recognized type";
        assert input != null : "A command must retain its input";
        this.type = type;
        this.input = input;
    }

    /**
     * Returns the recognized command type.
     *
     * @return command type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the original command input.
     *
     * @return original trimmed command input
     */
    public String getInput() {
        return input;
    }
}
