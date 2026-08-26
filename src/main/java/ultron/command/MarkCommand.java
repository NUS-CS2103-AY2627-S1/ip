package ultron.command;

import ultron.model.Task;
import ultron.model.TaskList;
import ultron.storage.Storage;
import ultron.ui.Ui;

/**
 * Marks a numbered task as done.
 */
public class MarkCommand extends Command {
    private final String taskNumberText;

    /**
     * Creates a mark command with its task-number argument.
     *
     * @param taskNumberText the task number supplied by the user
     */
    public MarkCommand(String taskNumberText) {
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
                task.markAsDone();
                storage.saveTasks(tasks.getTasks());
                ui.showTaskMarked(task);
            }
        } catch (NumberFormatException e) {
            ui.showInvalidTaskNumberFormat("mark");
        }
        ui.showSeparator();
    }
}
