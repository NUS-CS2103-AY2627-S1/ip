package grower.commands;

import java.time.LocalDateTime;

import grower.tasks.Deadline;
import grower.tasks.Task;
import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Represents a command that adds a deadline task.
 */
public class DeadlineCommand extends Command {
    private final String description;
    private final LocalDateTime deadline;

    /**
     * Creates a command that adds a deadline task.
     *
     * @param description Description of the task.
     * @param deadline Date and time by which the task must be completed.
     */
    public DeadlineCommand(String description, LocalDateTime deadline) {
        this.description = description;
        this.deadline = deadline;
    }

    @Override
    public boolean execute(TaskList tasks, Ui ui) {
        Task newTask = new Deadline(description, deadline);
        tasks.addTask(newTask);
        ui.showTaskAdded(newTask);
        return true;
    }
}
