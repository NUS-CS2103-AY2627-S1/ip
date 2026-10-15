package surveyprogram.processor;

/**
 * Identifies the visual category of a command reply.
 */
public enum ReplyType {
    DEFAULT(null),
    TASK_CREATED("task-created-label"),
    LIST("list-label"),
    FIND("find-label"),
    MARK_COMPLETED("mark-completed-label"),
    UNMARKED("unmarked-label"),
    DELETED("deleted-label"),
    DATE_QUERY("date-query-label"),
    ERROR("error-label"),
    EMPTY("empty-label");

    private final String styleClass;

    ReplyType(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * Returns the CSS class for this reply type.
     *
     * @return CSS class, or {@code null} for the unchanged default style
     */
    public String getStyleClass() {
        return styleClass;
    }
}
