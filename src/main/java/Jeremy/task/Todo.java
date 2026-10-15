package jeremy.task;

/** A task without any date/time attached, e.g. "visit new theme park". */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }
}
