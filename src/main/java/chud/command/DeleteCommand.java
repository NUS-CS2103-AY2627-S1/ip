package chud.command;

import chud.exception.ChudException;
import chud.parser.Parser;
import chud.storage.Storage;
import chud.task.Task;
import chud.task.TaskList;
import chud.ui.Ui;

/** Removes the task at a given 1-based list number from the task list. */
public class DeleteCommand extends Command {
    private final String arguments;

    /** Creates a command that will delete the task at the raw (not yet validated) index text. */
    public DeleteCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size(), "delete");
        Task removedTask = tasks.remove(taskIndex);
        storage.save(tasks);
        ui.showMessage("yeeted into the shadow realm 🫡:");
        ui.showIndentedMessage(removedTask);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }
}
