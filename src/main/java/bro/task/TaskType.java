package bro.task;

/** Identifies the category of a task and the marker shown for that category. */
public enum TaskType {
    /** A task that has no date or time attached to it. */
    TODO("T"),

    /** A task that must be completed by a particular date or time. */
    DEADLINE("D"),

    /** A task that occurs between a start and end time. */
    EVENT("E");

    private final String displayCode;

    TaskType(String displayCode) {
        this.displayCode = displayCode;
    }

    /** Returns the short marker used when this category is displayed. */
    public String getDisplayCode() {
        return displayCode;
    }
}
