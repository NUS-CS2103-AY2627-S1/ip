package ayre.tasks;

/**
 * Parent class for all tasks. Abstract to prevent instantiation as it is not complete as is.
 * Holds the common methods and information that all tasks share.
 */
public abstract class Task {
    private final String name;
    private boolean isComplete;

    /**
     * Initializes a new Task to be incomplete by default.
     * @param name Name of the Task.
     */
    public Task(String name) {
        this.name = name;
        this.isComplete = false;
    }

    /**
     * Tests whether the name of the Task contains (leniently) the key.
     *
     * @param key Substring to be tested.
     * @return Whether the key is contained in the name of the Task without considering capitalization.
     */
    public boolean matchesName(String key) {
        return this.name.toLowerCase()
                .contains(key.toLowerCase());
    }

    /**
     * Getter for isComplete boolean.
     * Allows the TaskList to count how many incomplete tasks there are.
     *
     * @return isComplete
     */
    public boolean getComplete() {
        return this.isComplete;
    }

    /**
     * Marks the Task as complete.
     *
     * @return Context String
     */
    public String markComplete() {
        this.isComplete = true;
        return "~ Mission complete. Good work, Raven.\n" + this;
    }

    /**
     * Unmarks the Task as complete.
     *
     * @return Context String
     */
    public String unmarkComplete() {
        this.isComplete = false;
        return "~ The mission is still pending, Raven. Let's get to it.\n" + this;
    }

    /**
     * Converts the Task into a String that is easily parsable to store into the save file.
     *
     * @return String containing all necessary information about the Task to reconstruct it later
     */
    public String toLogString() {
        return (this.isComplete ? "1" : "0") + " " + this.name;
    }

    @Override
    public String toString() {
        return "[" + (this.isComplete ? "+" : " ") + "] " + this.name;
    }
}
