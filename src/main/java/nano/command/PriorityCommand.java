package nano.command;

import nano.NanoException;
import nano.Storage;
import nano.task.Priority;
import nano.task.Task;
import nano.task.TaskList;

/**
 * Changes the priority of a task.
 */
public class PriorityCommand implements Command {
    private final int taskNumber;
    private final Priority priority;

    /**
     * Creates a command to change a task's priority.
     *
     * @param taskNumber user-visible task number.
     * @param priority new priority value.
     */
    public PriorityCommand(int taskNumber, Priority priority) {
        this.taskNumber = taskNumber;
        this.priority = priority;
    }

    /**
     * Updates and saves the task's priority.
     *
     * @param tasks current task list.
     * @param storage storage used to save tasks.
     * @return confirmation message.
     * @throws NanoException if the task number is invalid or saving fails.
     */
    @Override
    public String execute(TaskList tasks, Storage storage)
            throws NanoException {
        Task task = tasks.getTask(taskNumber);
        task.setPriority(priority);
        storage.save(tasks);

        return "Priority updated: " + task;
    }
}
