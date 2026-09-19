package carl.commands;

import java.time.LocalDateTime;

import carl.exceptions.CarlException;
import carl.task.Event;
import carl.task.Task;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.Ui;

/**
 * Represents a command to create and add an event task.
 */
public class EventCommand extends AddTaskCommand {

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Constructs an {@code EventCommand} with the specified task name, start date, and end date.
     *
     * @param name Name or description of the event task.
     * @param from Start date and time of the event.
     * @param to End date and time of the event.
     */
    public EventCommand(String name, LocalDateTime from, LocalDateTime to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    /**
     * {@inheritDoc}
     * Adds the event task to the task list, saves the list, and informs the user.
     *
     * @param ui      User interface for interacting with the user.
     * @param storage Task manager handling task data persistence.
     * @param tasks   List of current tasks.
     * @param raw     Raw input string entered by the user.
     * @return result describing the added event and updated task count.
     * @throws CarlException If an error occurs during execution.
     */
    @Override
    public CommandResult execute(Ui ui, TaskManager storage, TaskList tasks, String raw) throws CarlException {
        String response = addTask(name, from, to, tasks, ui);

        return CommandResult.createSuccess(response);

    }

    private String addTask(String name, LocalDateTime from, LocalDateTime to,
                           TaskList tasks, Ui ui) throws CarlException {
        Task task = new Event(name, from, to);
        tasks.addTaskToList(task);
        return ui.showAddTask(task, tasks.getTasksLeft());
    }

}
