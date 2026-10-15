package elsa.command;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.Task;
import elsa.task.TaskList;
import elsa.ui.Ui;

/** Marks one task as not done after all. */
public class UnmarkCommand extends TaskNumberCommand {
    /**
     * Creates a command that will mark the numbered task as not done.
     *
     * @param number the task number the user typed, counting from 1
     */
    public UnmarkCommand(int number) {
        super(number, CommandType.UNMARK);
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ElsaException {
        Task unmarked = tasks.unmark(indexIn(tasks));
        storage.save(tasks);
        return ui.getUnmarkedMessage(unmarked);
    }
}
