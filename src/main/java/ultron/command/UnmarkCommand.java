package ultron.command;

import ultron.model.Task;
import ultron.model.TaskList;
import ultron.storage.Storage;
import ultron.ui.Ui;

/**
 * Marks a numbered task as not done.
 */
public class UnmarkCommand extends Command {
    private final String taskNumberText;

    /**
     * Creates an unmark command with its task-number argument.
     *
     * @param taskNumberText the task number supplied by the user
     */
    public UnmarkCommand(String taskNumberText) {
        this.taskNumberText = taskNumberText;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                ui.showInvalidTaskNumber(tasks.size());
            } else {
                Task task = tasks.get(taskNumber - 1);
                task.markAsUndone();
                storage.saveTasks(tasks.getTasks());
                ui.showTaskUnmarked(task);
            }
        } catch (NumberFormatException e) {
            ui.showInvalidTaskNumberFormat("unmark");
        }
        ui.showSeparator();
    }
}
