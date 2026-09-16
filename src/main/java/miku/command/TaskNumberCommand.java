package miku.command;

import miku.MikuException;
import miku.task.Task;
import miku.task.TaskList;

/** Provides shared task-number lookup for commands that act on one task. */
public abstract class TaskNumberCommand extends Command {
    private final int taskNumber;

    /** Creates a command targeting a one-based task number. */
    protected TaskNumberCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Returns the selected task after ensuring its number exists in the list. */
    protected Task getTask(TaskList tasks) throws MikuException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new MikuException("That task number is not in Miku's list!! \u2728");
        }
        return tasks.get(taskNumber - 1);
    }

    /** Returns the selected task's zero-based list index after ensuring it exists. */
    protected int getTaskIndex(TaskList tasks) throws MikuException {
        getTask(tasks);
        return taskNumber - 1;
    }
}
