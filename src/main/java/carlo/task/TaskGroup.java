package carlo.task;

/**
 * The urgency groups that tasks are sorted into in the main window, listed
 * in the order they are displayed (most urgent first).
 */
public enum TaskGroup {
    OVERDUE("Overdue"),
    SOON("Today and tomorrow"),
    THIS_WEEK("This week"),
    LATER("Later"),
    ANYTIME("Anytime"),
    DONE("Done");

    private final String label;

    TaskGroup(String label) {
        this.label = label;
    }

    /**
     * Returns the heading shown above this group.
     *
     * @return the group heading
     */
    public String getLabel() {
        return label;
    }
}
