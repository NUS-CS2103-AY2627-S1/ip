package chud.task;

/**
 * The kind of a task, and the single-letter tag used to display it (e.g. "[T]").
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String tag;

    TaskType(String tag) {
        this.tag = tag;
    }

    /** Returns the single-letter tag used to display this task type (e.g. "T"). */
    public String getTag() {
        return tag;
    }
}
