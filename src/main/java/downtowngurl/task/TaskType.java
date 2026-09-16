package downtowngurl.task;

/**
 * Represents the fixed categories of tasks supported by the chatbot.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String icon;

    /**
     * Creates a task type with the icon used when displaying tasks.
     *
     * @param icon Short symbol shown beside a task.
     */
    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the icon used to display this task type.
     *
     * @return Task type icon.
     */
    public String getIcon() {
        return this.icon;
    }
}
