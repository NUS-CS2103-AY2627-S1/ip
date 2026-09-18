package blud.command;

import blud.model.Task;
import blud.model.TaskList;
import blud.storage.Storage;

/** Adds a parsed task to the task list and persists the change. */
public class AddTask {
    private final TaskList taskList;
    private final Storage storage;

    /** Creates an add operation for the supplied task list and storage. */
    public AddTask(TaskList taskList, Storage storage) {
        this.taskList = taskList;
        this.storage = storage;
    }

    /** Adds the task and returns the corresponding user-facing response. */
    public String execute(Task newTask) {
        assert newTask != null : "Command parsing must produce a valid task";
        taskList.addTask(newTask);
        storage.saveTasks(taskList);
        return "added: " + newTask + "\nNow you have " + taskList.getSize() + " tasks in the list";
    }
}
