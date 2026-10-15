package yapatron.task;

/**
 * Represents a generic task in Yapatron
 */ 

public class Task {

    /** Description of the task */
    protected String desc;

    /** Status of task completion */
    protected boolean isDone;

    /**
     * Creates new Task instance with given description
     * Sets initial completion status to False
     *
     * @param desc Detals of task
     */

    public Task(String desc) {
        this.desc = desc;
        this.isDone = false;
    }

    /**
     * Returns status icon of the task
     *
     * @return "X" if the task if done, a blank space if not
     */
    public String getStatusIcon() {
        return (this.isDone ? "X" : " ");
        // mark compl task with X
    }

    /** 
     * Returns completion status of task
     *
     * @return True if completed, False if not
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Marks task as completed
     */
    public void markAsDone() {
        this.isDone = true;
        assert this.isDone : "maskAsDone must complete the task";
    }

    /**
     * Marks task as uncompleted
     */
    public void unmark() {
        this.isDone = false;
        assert !this.isDone : "unmark must leave task as incomplete";
    }

    /**
     * Returns task description
     *
     * @return task description
     */
    public String getDesc() {
        return this.desc;
    }


    /** 
     * Returns the string representation of the task
     *
     * @return Formatted string with status icon and task description
     */
    @Override
        public String toString() {
            return "[" + this.getStatusIcon() + "] " + this.desc;
        }

    /**
     * Formats the task data into a pipe-delimted string for file storage
     *
     * @return Formatted string for file storage
     */
    public String toFileFormat() {
        return (isDone ? "X" : "0" ) + " | " + desc;
    }
}
