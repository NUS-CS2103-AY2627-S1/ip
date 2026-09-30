package bean.task;

/** Represents a task without a date or time constraint. */
public class Todo extends Task {

    Todo(String task, int position) {
        super(task, position);
        this.tag = "[T]";
    }

    Todo(String task, Priority priority, int position) {
        super(task, priority, position);
        this.tag = "[T]";
    }

    @Override
    public String toString() {
        return priority.toString() + " " + tag + (isDone ? "[X] " : "[ ] ") + task;
    }
}
