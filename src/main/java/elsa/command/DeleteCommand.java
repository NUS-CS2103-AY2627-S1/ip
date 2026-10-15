package elsa.command;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.Task;
import elsa.task.TaskList;
import elsa.ui.Ui;

/** Removes one task from the list. */
public class DeleteCommand extends TaskNumberCommand {
    /**
     * Creates a command that will remove the numbered task.
     *
     * @param number the task number the user typed, counting from 1
     */
    public DeleteCommand(int number) {
        super(number, CommandType.DELETE);
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ElsaException {
        Task removed = tasks.delete(indexIn(tasks));
        storage.save(tasks);
        return ui.getRemovedMessage(removed, tasks.size());
    }
}
