package carl.commands;

import java.time.LocalDateTime;

import carl.exceptions.CarlException;
import carl.task.Deadline;
import carl.task.Task;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.Ui;

/**
 * Represents a command to create and add a deadline task.
 */
public class DeadlineCommand extends AddTaskCommand {

    /** Due date and time of the deadline task. */
    private final LocalDateTime by;

    /**
     * Constructs a {@code DeadlineCommand} with the specified task name and due date.
     *
     * @param name Name or description of the deadline task.
     * @param by Due date and time of the task.
     */
    public DeadlineCommand(String name, LocalDateTime by) {
        super(name);
        this.by = by;
    }


    /**
     * {@inheritDoc}
     * Adds the specified deadline task to the task list, saves the list, and informs the user.
     *
     * @param ui      User interface for interacting with the user.
     * @param storage Task manager handling task data persistence.
     * @param tasks   List of current tasks.
     * @param raw     Raw input string entered by the user.
     * @return result describing the added deadline and updated task count.
     * @throws CarlException If an error occurs during execution.
     */
    @Override
    public CommandResult execute(Ui ui, TaskManager storage, TaskList tasks, String raw)
            throws CarlException {

        Task task = new Deadline(name, by);
        tasks.addTaskToList(task);


        return CommandResult.createSuccess(ui.showAddTask(task, tasks.getTasksLeft()));

    }
}
