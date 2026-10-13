package jimbo.task;

/**
 * A task to be done in the future with no time restrictions.
 */
public class Todo extends Task {
    public Todo(String data) {
        super(data);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString() + " " + formatTags();
    }

    @Override
    public String serialise() {
        return "T|" + super.serialise() + "|" + serialiseTags();
    }
}
