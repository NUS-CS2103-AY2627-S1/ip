package lenzabot.task;

/**
 * Enumerates the supported task categories and their display icons.
 */
public enum TaskType {
    /** Task without an associated date or time. */
    TODO("T"),

    /** Task that must be completed by a specific date and time. */
    DEADLINE("D"),

    /** Task that takes place between two date-time values. */
    EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the display icon for this task category.
     *
     * @return Single-character task category icon.
     */
    public String getIcon() {
        return this.icon;
    }
}
