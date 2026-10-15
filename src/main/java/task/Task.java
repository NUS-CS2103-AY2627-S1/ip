package task;

/**
 * Represents a general task with a description and completion status.
 * This class serves as the parent class for other task types.
 */
public class Task {
    protected String desc;
    protected boolean isMarked = false;

    /**
     * Constructs the task with the specific description.
     *
     * @param desc the description of the task.
     */
    public Task (String desc){
        this.desc = desc;
    }

    /**
     * Returns the icon of the task.
     *
     * @return "X" if the task is marked or " " if unmarked.
     */
    public String getStatusIcon() {
        return (isMarked ? "X" : " ");
    }

    /**
     * Returns the description of the task.
     *
     * @return the task's description.
     */
    public String getDesc(){
        return this.desc;
    }

    /**
     * Sets the task as marked.
     */
    public void mark(){
        this.isMarked = true;
    }

    /**
     * Sets the task as unmarked.
     */
    public void unmark(){
        this.isMarked = false;
    }

    /**
     * Returns whether the task is marked or unmarked
     *
     * @return true if marked and false if unmarked.
     */
    public boolean isDone(){
        return isMarked;
    }

    /**
     * Returns a string representation of the task consisting of its status icon
     * followed by its description.
     *
     *  @return the task as a string in the format "[status] description"
     */
    @Override
    public String toString() {
        return "[" + this.getStatusIcon() + "] " + this.desc;
    }
}