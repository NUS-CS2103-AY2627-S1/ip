package blud.model;

/** Provides common state and behavior for all Blud tasks. */
public abstract class Task {
    private boolean done;
    private final String taskDescription;

    /** Creates a task with the supplied description and an incomplete status. */
    public Task(String taskDescription) {
        assert taskDescription != null : "A task must always have a description";
        this.taskDescription = taskDescription;
        this.done = false;
    }

    /** Returns the display marker for the task's completion status. */
    private char doneRep() {
        if (this.done) {
            return 'X';
        } else {
            return ' ';
        }
    }

    /** Marks this task as completed. */
    public void mark() {
        this.done = true;
    }

    /** Marks this task as incomplete. */
    public void unmark() {
        this.done = false;
    }

    /**
     * Returns this task's stable on-disk representation.
     *
     * @return a line that can be written to the task file
     */
    public abstract String toStorageString();

    /** Returns whether this task is completed. */
    protected boolean isDone() {
        return done;
    }

    /** Returns the description displayed for this task. */
    public String getTaskDescription() {
        return taskDescription;
    }

    /** Returns whether this task has the same user-supplied details as another task. */
    public boolean hasSameDetails(Task other) {
        if (other == null || getClass() != other.getClass()
                || !taskDescription.equals(other.taskDescription)) {
            return false;
        }
        if (this instanceof Deadline firstDeadline && other instanceof Deadline secondDeadline) {
            return firstDeadline.getDeadline().equals(secondDeadline.getDeadline());
        }
        if (this instanceof Event firstEvent && other instanceof Event secondEvent) {
            return firstEvent.getStartDate().equals(secondEvent.getStartDate())
                    && firstEvent.getEndDate().equals(secondEvent.getEndDate());
        }
        return true;
    }

    /** Returns this task's display representation. */
    @Override
    public String toString() {
        return "[" + doneRep() + "] " + this.taskDescription;
    }
}
