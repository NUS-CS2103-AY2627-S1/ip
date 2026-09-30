package bean.task;

/** Represents the state and common behavior of a task. */
class Task {
    protected String task;
    protected String tag = "";
    protected boolean isDone;
    protected Priority priority = Priority.LOW;
    protected int position;

    Task(String task, int position) {
        this.task = task;
        this.isDone = false;
        this.position = position;
    }

    Task(String task, Priority priority, int position) {
        this.task = task;
        this.isDone = false;
        this.priority = priority;
        this.position = position;
    }

    public Priority getPriority() {
        return priority;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTag() {
        return this.tag;
    }

    /** Marks the task as complete. */
    public void markDone() {
        this.isDone = true;
    }

    /** Marks the task as incomplete. */
    public void unmarkDone() {
        this.isDone = false;
    }

    public boolean isDone() {
        return this.isDone;
    }

    /** Returns the task fields in a format suitable for storage. */
    public String[] get() {
        return new String[] { isDone ? "1" : "0", task, String.valueOf(priority.getLevel())};
    }
}
