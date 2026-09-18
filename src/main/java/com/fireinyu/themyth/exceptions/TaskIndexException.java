package com.fireinyu.themyth.exceptions;

/**
 * Thrown when user references a task index outside the range of existing tasks.
 */
public class TaskIndexException extends TweakingException {

    /**
     * Initializes a TaskIndexException caused by the user referencing a task index outside the range of existing tasks.
     *
     * @param index Task index reference.
     * @param size Size of task list.
     * @see String
     */
    public TaskIndexException(int index, int size) {
        super(String.format("Task index %d? There are only %d tasks you naughty naughty", index, size));
    }
}
