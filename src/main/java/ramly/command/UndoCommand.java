package ramly.command;

import ramly.model.TaskList;
import ramly.model.UndoResult;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Command that reverses the latest successful task-list mutation. */
public class UndoCommand extends Command {
    /** Reverses the latest mutation, persists the result, and reports it. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        UndoResult result = tasks.undo();
        if (result == null) {
            ui.show("No steps to retrace yet.");
            return;
        }

        storage.save(tasks);
        switch (result.getAction()) {
        case ADD:
            ui.show("One step back—I've removed the task you added:",
                    " " + result.getTask(), taskCountMessage(result.getTaskCount()));
            break;
        case DELETE:
            ui.show("One step back—I've restored the task you removed:",
                    " " + result.getTask(), taskCountMessage(result.getTaskCount()));
            break;
        case STATUS_CHANGE:
            ui.show("One step back—I've restored this task's previous status:", " " + result.getTask());
            break;
        default:
            assert false : "Every undo action must have a display message";
        }
    }

    /** Returns a grammatically correct task-count message. */
    private String taskCountMessage(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return "Now you have " + taskCount + " " + taskWord + " in the list.";
    }
}
