package bubu.command;

import java.time.LocalDateTime;

import bubu.exception.BubuException;
import bubu.storage.Storage;
import bubu.task.Deadline;
import bubu.task.TaskList;
import bubu.ui.Ui;

/**
 * Adds a deadline task.
 */
public class DeadlineCommand extends AddTaskCommand {
    private final String description;
    private final LocalDateTime deadline;

    /**
     * Creates a deadline command with its parsed deadline date and time.
     */
    public DeadlineCommand(String description, LocalDateTime deadline) {
        this.description = description;
        this.deadline = deadline;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BubuException {
        addTask(new Deadline(description, deadline), tasks, ui, storage);
    }
}
