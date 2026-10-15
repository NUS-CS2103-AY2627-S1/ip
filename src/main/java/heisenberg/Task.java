package heisenberg;

/** Represents a task with a description and a completion status. */
public class Task {
    private String description;
    private boolean isDone;

    public Task(String description) {
        assert description != null && !description.isBlank()
                : "Task description must not be blank";
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    public void mark() {
        this.isDone = true;
    }

    public void unmark() {
        this.isDone = false;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.description);
    }
}
