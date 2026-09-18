package alpha.task;

import java.time.LocalDateTime;

/** Represents one task in Moss's task list. */
public class Task {
    private static int counter = 0;
    private final int id;
    private final String description;
    private boolean done = false;

    /** Creates an unfinished task with the supplied description. */
    public Task(String description) {
        assert description != null : "A task must have a non-null description";
        this.id = Task.counter;
        Task.counter++;
        this.description = description;
    }

    /** Returns the unique identifier assigned to this task. */
    public int getId() {
        return this.id;
    }

    /** Returns the text describing this task. */
    public String getDescription() {
        return this.description;
    }

    /** Returns the task's display text, including its type, status, and details. */
    @Override
    public String toString() {
        String status = this.done ? "X" : " ";
        return String.format("[%s][%s] %s%s", this.getTypeIcon(), status,
                this.description, this.getTimeDetails());
    }

    /** Returns the letter used to identify this task type in the user interface. */
    protected String getTypeIcon() {
        return "T";
    }

    /** Returns the date/time details displayed after the task description. */
    protected String getTimeDetails() {
        return "";
    }

    /** Returns the date used for chronological sorting, or {@code null} for an undated task. */
    protected LocalDateTime getSortDateTime() {
        return null;
    }

    /** Marks this task as complete. */
    public void markDone() {
        this.done = true;
    }

    /** Changes this task back to an unfinished state. */
    public void markUndone() {
        this.done = false;
    }

    /** Returns whether this task is currently complete. */
    public boolean isDone() {
        return this.done;
    }
}
