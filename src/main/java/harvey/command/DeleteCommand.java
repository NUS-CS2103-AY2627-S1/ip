package harvey.command;

import harvey.HarveyException;
import harvey.storage.Storage;
import harvey.task.Task;
import harvey.task.TaskList;
import harvey.ui.Ui;
/** Removes one task from the list. */
public class DeleteCommand extends Command {
    /** The task number the user typed, still unchecked. */
    private final String argument;

    /**
     * Creates a command that will remove one task.
     *
     * @param argument the task number, as typed.
     */
    public DeleteCommand(String argument) {
        this.argument = argument;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws HarveyException {
        Task removed = tasks.delete(parseTaskNumber(argument, tasks, CommandType.DELETE));
        storage.save(tasks.asList());
        return ui.formatLines("Dropped. That one's off the table:",
                "  " + removed,
                ui.formatTaskCount(tasks.size()));
    }
}
