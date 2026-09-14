package stuart.task;

/**
 * The kind of a {@link Task}, and the single-letter tag used to display it,
 * e.g. {@code T} for a to-do.
 */
public enum TaskType {
    /** A task with no date/time attached. */
    TODO("T"),
    /** A task that needs to be done before a specific date. */
    DEADLINE("D"),
    /** A task that starts and ends on specific dates. */
    EVENT("E");

    private final String tag;

    /**
     * Creates a task type with the given display tag.
     *
     * @param tag the single-letter tag used to display this type
     */
    TaskType(String tag) {
        this.tag = tag;
    }

    /**
     * Returns the single-letter tag shown in a task's printed form,
     * e.g. {@code "T"} for {@link #TODO}.
     *
     * @return the tag
     */
    public String getTag() {
        return tag;
    }
}
