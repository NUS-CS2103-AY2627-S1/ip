package task;

/**
 * ToDo which extends Task
 */
public class ToDo extends Task {
    public ToDo(String name) {
        super(name);
    }

    public ToDo(String name, TaskStatus status) {
        super(name, status);
    }

    @Override
    public String renderTask() {
        return String.format("[T]%s", super.renderTask());
    }

    @Override
    public String serialize() {
        return String.format("%s,T", super.serialize());
    }
}
