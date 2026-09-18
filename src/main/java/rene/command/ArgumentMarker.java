package rene.command;

/**
 * Represents fixed markers that separate fields in task-creation commands.
 */
public enum ArgumentMarker {
    /** Separates a deadline description from its due date. */
    BY(" /by "),
    /** Separates an event description from its start value. */
    FROM(" /from "),
    /** Separates an event's start value from its end value. */
    TO(" /to ");

    private final String text;

    /**
     * Creates a command-field marker.
     *
     * @param text the exact text used to separate the command fields.
     */
    ArgumentMarker(String text) {
        this.text = text;
    }

    /**
     * Returns the text of this command-field marker.
     *
     * @return the marker text.
     */
    public String getText() {
        return text;
    }
}
