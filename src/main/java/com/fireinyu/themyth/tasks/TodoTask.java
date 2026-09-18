package com.fireinyu.themyth.tasks;

/**
 * Todo Task created by the user. Tasks can be marked or unmarked as completed.
 */
public class TodoTask extends Task {

    /**
     * Initializes a TodoTask.
     *
     * @param description description of the task.
     * @see String
     */
    public TodoTask(String description) {
        super(description, "T");
    }

}
