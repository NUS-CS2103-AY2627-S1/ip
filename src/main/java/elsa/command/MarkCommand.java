package elsa.command;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.Task;
import elsa.task.TaskList;
import elsa.ui.Ui;

/** Marks one task as done. */
public class MarkCommand extends TaskNumberCommand {
    /**
     * Creates a command that will mark the numbered task as done.
     *
     * @param number the task number the user typed, counting from 1
     */
    public MarkCommand(int number) {
        super(number, CommandType.MARK);
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ElsaException {
        Task marked = tasks.mark(indexIn(tasks));
        storage.save(tasks);
        return ui.getMarkedMessage(marked);
    }
}
