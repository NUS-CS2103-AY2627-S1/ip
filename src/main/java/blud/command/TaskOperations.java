package blud.command;

import blud.model.Task;
import blud.model.TaskList;
import blud.storage.Storage;

/** Performs state-changing task commands and persists their results. */
public class TaskOperations {
    private final TaskList taskList;
    private final Storage storage;

    /** Creates task operations for the supplied task list and storage. */
    public TaskOperations(TaskList taskList, Storage storage) {
        this.taskList = taskList;
        this.storage = storage;
    }

    /** Marks a task and returns the corresponding response. */
    public String mark(String[] splitInput) {
        int index = CommandValidator.parseTaskIndex(splitInput[1]);
        taskList.markTask(index);
        storage.saveTasks(taskList);
        return "Nice! I've marked this task as done:\n" + taskList.getTask(index);
    }

    /** Unmarks a task and returns the corresponding response. */
    public String unmark(String[] splitInput) {
        int index = CommandValidator.parseTaskIndex(splitInput[1]);
        taskList.getTaskForCommandIndex(index).unmark();
        storage.saveTasks(taskList);
        return "OK, I've marked this task as not done yet:\n" + taskList.getTask(index);
    }

    /** Deletes a task and returns the corresponding response. */
    public String delete(String[] splitInput) {
        Task deletedTask = taskList.delete(splitInput);
        storage.saveTasks(taskList);
        return "Task removed successfully:\n" + deletedTask;
    }
}
