package eli;

/**
 * Lists the supported kinds of tasks and their display icons.
 */
public enum TaskType {
    TODO("T", "Todos", 2),
    DEADLINE("D", "Deadlines", 0),
    EVENT("E", "Events", 1);

    private final String icon;
    private final String sectionName;
    private final int sortOrder;

    /**
     * Creates a task type with the icon shown in the task list.
     *
     * @param icon the one-letter task-type icon
     * @param sectionName the heading used for a group of this task type
     * @param sortOrder the position of this type in a sorted task list
     */
    TaskType(String icon, String sectionName, int sortOrder) {
        this.icon = icon;
        this.sectionName = sectionName;
        this.sortOrder = sortOrder;
    }

    /**
     * Returns the icon displayed before the task status.
     *
     * @return the one-letter task-type icon
     */
    public String getIcon() {
        return icon;
    }

    /** Returns the heading used for this task type in a sorted list. */
    String getSectionName() {
        return sectionName;
    }

    /** Returns this task type's position in a sorted list. */
    int getSortOrder() {
        return sortOrder;
    }
}
