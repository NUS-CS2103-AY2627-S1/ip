package chud.command;

import chud.exception.ChudException;
import chud.storage.Storage;
import chud.task.Task;
import chud.task.TaskList;
import chud.ui.Ui;

/**
 * A single user command, already parsed and ready to run against the task list. Each concrete
 * Command knows how to execute itself -- mutating the task list, saving to disk via Storage,
 * and/or printing to the user via Ui, as appropriate -- and whether running it should end the
 * program.
 */
public abstract class Command {
    /** Runs this command against the given task list, printing to ui and saving via storage. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException;

    /** Returns true if this command should end the program after it runs. */
    public boolean isExit() {
        return false;
    }

    /**
     * Adds the given task to the list, saves the list to disk, and prints the standard
     * "task added" confirmation -- the sequence shared by the three add-task commands
     * (AddTodoCommand, AddDeadlineCommand, AddEventCommand), which otherwise differ only in
     * which Task subtype they construct.
     */
    protected static void addTask(TaskList tasks, Ui ui, Storage storage, Task task) {
        tasks.add(task);
        storage.save(tasks);
        ui.showMessage("bet, locked in 🔒:");
        ui.showIndentedMessage(task);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }
}
