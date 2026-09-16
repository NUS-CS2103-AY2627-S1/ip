package chattingheads.command;

import java.time.LocalDateTime;

import chattingheads.task.Deadline;
import chattingheads.task.Task;
import chattingheads.task.TaskList;
import chattingheads.ui.Ui;

/**
 * Represents a command that adds a deadline task.
 */
public class AddDeadlineCommand extends Command {

    private final String description;
    private final LocalDateTime deadline;

    /**
     * Creates a command for adding a deadline task.
     *
     * @param description Description of the deadline task.
     * @param deadline    Date and time by which the task should be completed.
     */
    public AddDeadlineCommand(String description, LocalDateTime deadline) {
        this.description = description;
        this.deadline = deadline;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) {
        assert taskList != null : "TaskList should not be null";
        assert ui != null : "Ui should not be null";

        Task newTask = new Deadline(description, deadline);
        taskList.add(newTask);
        return ui.getAddStatus(newTask, taskList);
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
