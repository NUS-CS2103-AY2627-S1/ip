package victoria.model;

/** A task without date or time information. */
public class Todo extends Task {
    /** Creates an unfinished ToDo. */
    public Todo(String description) {
        super(description);
    }

    /** Returns the marker identifying this task as a ToDo. */
    @Override
    protected String getTypeMarker() {
        return "[T]";
    }

    /** Returns no timing text because a ToDo has no date. */
    @Override
    protected String getTimingText() {
        return "";
    }
}
