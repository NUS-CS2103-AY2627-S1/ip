package emma.command;

import emma.EmmaException;

/**
 * The shared part of every command the user aims at one task by its number,
 * as in "mark 2" or "delete 2": holding that number, and complaining in the
 * same words when the list has no such task.
 */
public abstract class TaskNumberCommand implements Command {

    private final int taskNumber;

    /**
     * Creates a command aimed at one task.
     *
     * @param taskNumber the 1-based task number the user gave, not yet checked
     *     against the list.
     */
    protected TaskNumberCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Returns the task number the user gave.
     *
     * @return the 1-based task number.
     */
    protected int getTaskNumber() {
        return taskNumber;
    }

    /**
     * Builds the complaint made when the number names no task in the list.
     *
     * @return the exception to throw, worded for the user.
     */
    protected EmmaException buildNoSuchTaskError() {
        return new EmmaException("You don't have a task numbered " + taskNumber + ".");
    }
}
