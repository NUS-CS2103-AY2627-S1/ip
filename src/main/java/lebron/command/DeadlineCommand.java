package lebron.command;

import java.time.LocalDateTime;

import lebron.exception.LeBronException;
import lebron.task.Deadline;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Represents the "deadline" command, which adds a Deadline to the TaskList.
 */
public class DeadlineCommand implements Command {
    private final String description;
    private final LocalDateTime by;

    /**
     * Constructs a DeadlineCommand with the given description and due date/time.
     *
     * @param description The Deadline's description.
     * @param by The date/time the Deadline is due.
     */
    public DeadlineCommand(String description, LocalDateTime by) {
        this.description = description;
        this.by = by;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LeBronException {
        Task task = new Deadline(description, by);
        taskList.addTask(task);
        return ui.showDeadlineAdded(task, taskList.size());
    }
}
