package amadeus.task;

/**
 * Whether a task has been completed.
 * <p>
 * Modelled as an enum rather than a boolean so that each state owns the icon
 * used to display it, keeping the icon next to the state it stands for. It also
 * leaves room for further states later without changing every call site.
 */
public enum Status {
    DONE("X"),
    NOT_DONE(" ");

    /** The character shown between the brackets when a task is printed. */
    private final String icon;

    Status(String icon) {
        this.icon = icon;
    }

    /** Returns the character used to display this state, e.g. "X" for a done task. */
    public String getIcon() {
        return icon;
    }
}
