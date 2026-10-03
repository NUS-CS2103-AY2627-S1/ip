package ramly.model;

/** Identifies the supported task categories and their display metadata. */
public enum TaskType {
    TODO("[•]", "T"),
    DEADLINE("[⏳]", "D"),
    EVENT("[◆]", "E");

    private final String icon;
    private final String code;

    TaskType(String icon, String code) {
        this.icon = icon;
        this.code = code;
    }

    /** Returns the icon used when displaying this task type. */
    public String getIcon() {
        return icon;
    }

    /** Returns the code used when serializing this task type. */
    public String getCode() {
        return code;
    }
}
