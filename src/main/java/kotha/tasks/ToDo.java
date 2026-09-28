package kotha.tasks;

/** Represents a task without a date or time. */
public class ToDo extends Task {
    /** Creates a to-do task with the supplied description. */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public void printAddText() {
        System.out.println("Master I have added the following task:");
        System.out.println(this.toString());
    }
}
