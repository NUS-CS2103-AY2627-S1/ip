package mouse.parser;

import mouse.task.Priority;

/**
 * A parsed {@code priority INDEX LEVEL} command.
 */
public class PriorityCommand {
    private final int index;
    private final Priority priority;

    /**
     * Creates a priority update for the given 0-based index.
     *
     * @param index Zero-based task index.
     * @param priority Priority to assign.
     */
    public PriorityCommand(int index, Priority priority) {
        this.index = index;
        this.priority = priority;
    }

    /**
     * Returns the 0-based task index.
     *
     * @return Task index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the priority to assign.
     *
     * @return Priority value.
     */
    public Priority getPriority() {
        return priority;
    }
}
