package ruby.task;

/**
 * Holds the tokens that make up the data-file task format.
 */
public final class TaskDataFormat {
    public static final String PART_SEPARATOR = " | ";
    public static final String DONE_MARKER = "1";
    public static final String NOT_DONE_MARKER = "0";
    public static final String TODO_TYPE = "T";
    public static final String DEADLINE_TYPE = "D";
    public static final String EVENT_TYPE = "E";

    /**
     * Prevents instantiation of this constants-only class.
     */
    private TaskDataFormat() {
    }
}
