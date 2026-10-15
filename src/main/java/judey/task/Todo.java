package judey.task;

/**
 * Represents a task with a description
 */
public class Todo extends Task{
    /**
     * Constructs a {@code Todo} task with a description
     * @param desc The description of the todo task
     */
    public Todo(String desc){
        super(desc);
    }

    @Override
    public String toString(){
        return "[" + TYPE_TODO + "]" + super.toString() + "\n";
    }

}
