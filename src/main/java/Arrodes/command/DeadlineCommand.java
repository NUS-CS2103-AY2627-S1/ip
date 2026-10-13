package arrodes.command;

import java.time.LocalDateTime;

import arrodes.storage.Storage;
import arrodes.task.Deadline;
import arrodes.task.TaskList;
import arrodes.ui.Ui;


/** Command that adds a deadline task and saves the updated list. */
public class DeadlineCommand extends Command {
    /** Text describing the deadline. */
    private final String description;
    /** Date and optional time by which the task is due. */
    private final LocalDateTime dueBy;

    /**
     * Creates a command for adding a deadline.
     * @param description task description
     * @param dueBy deadline date and optional time
     */
    public DeadlineCommand(String description, LocalDateTime dueBy) {
        assert description != null : "Deadline description must not be null.";
        assert dueBy != null : "Deadline date must not be null.";
        this.description = description;
        this.dueBy = dueBy;
    }

    /**
     * Adds the deadline, persists it, and reports the new task count.
     * @param ui interface used for output
     * @param taskList list to update
     * @param storage persistence layer
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        addTaskAndSave(new Deadline(description, dueBy), ui, taskList, storage);
    }
}
