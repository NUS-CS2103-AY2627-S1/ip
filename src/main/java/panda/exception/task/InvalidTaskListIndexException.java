package panda.exception.task;

import panda.exception.ApplicationException;

/**
 * Signals that a task number does not identify a task in the current list.
 */
public class InvalidTaskListIndexException extends ApplicationException {

    /**
     * Creates an error that identifies the unavailable task number and list size.
     *
     * @param taskNumber Task number requested by the user.
     * @param taskCount Number of tasks currently in the list.
     */
    public InvalidTaskListIndexException(int taskNumber, int taskCount) {
        super(taskNumber == 0
                ? String.format("Task %d? Bold. Counting starts at 1 where the rest of us live.",
                        taskNumber)
                : String.format("Task %d exists only in your imagination. You have %d %s.",
                        taskNumber, taskCount, taskCount == 1 ? "task" : "tasks"));
    }
}
