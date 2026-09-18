package hamtaro.task;

/** A task without a date or time. */
public class Todo extends Task{
    /** Creates an incomplete to-do task with the given description. */
    public Todo(String description){
        super(description);
    }

    /** Creates a to-do task with the given description and completion status. */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /** Returns a displayable representation of this to-do task. */
    @Override
    public String toString(){
        return "[T]" + super.toString();
    }
}
