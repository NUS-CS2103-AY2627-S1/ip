package tardt.priority;

/** Priority levels available for tasks. */
public enum Priority {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high");

    private final String keyword;
    Priority(String keyword) {
        this.keyword = keyword;
    }

    /** Converts a user-facing keyword to a priority, or returns null if invalid. */
    public static Priority fromKeyword(String keyword) {
        for (Priority priority : values()) {
            if (priority.keyword.equalsIgnoreCase(keyword)) {
                return priority;
            }
        }
        return null;
    }

    /** Returns the keyword used in commands and storage. */
    public String getKeyword() {
        return keyword;
    }

}
