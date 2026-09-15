package monkey.command;

import java.util.List;

import monkey.model.Task;
import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** A command object that can perform one application action. */
public abstract class CommandAction {
    /** Executes this command using the application's collaborators. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage);

    /** Returns whether running this command should end the application. */
    public boolean isExit() {
        return false;
    }

    /** Saves a proposed task state and reports any persistence failure. */
    protected boolean saveTasks(List<Task> proposedTasks, Ui ui, Storage storage) {
        if (storage.save(proposedTasks)) {
            return true;
        }
        ui.showMessage("OOPS! Monkey says: " + storage.getLastError());
        return false;
    }
}
