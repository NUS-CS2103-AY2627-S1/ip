package panda.command;

import java.time.temporal.Temporal;

import panda.task.Task;
import panda.task.TaskList;

/**
 * Adds a deadline task to Panda's task list.
 */
public class AddDeadlineCommand extends AddTaskCommand {
    private final String description;
    private final Temporal dueDate;

    /**
     * Creates a command for a deadline task.
     *
     * @param description Description of the task.
     * @param dueDate Date by which the task is due.
     */
    public AddDeadlineCommand(String description, Temporal dueDate) {
        this.description = description;
        this.dueDate = dueDate;
    }

    /**
     * Adds the deadline task to the supplied task list.
     *
     * @param taskList Active task list.
     * @return The newly added {@link panda.task.Deadline} task.
     */
    @Override
    protected Task addTask(TaskList taskList) {
        return taskList.addDeadline(description, dueDate);
    }
}
