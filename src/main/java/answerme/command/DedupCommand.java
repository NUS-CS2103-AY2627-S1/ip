package answerme.command;

import answerme.exception.AnswerMeException;
import answerme.storage.Storage;
import answerme.task.TaskList;
import answerme.ui.Ui;

/**
 * Represents a command that removes duplicate tasks from
 * the task list.
 */
public class DedupCommand extends Command {
    /**
     * {@inheritDoc}
     *
     * Removes duplicate tasks while retaining the first occurrence of each
     * task, saves the task list and displays the number of tasks removed.
     *
     * @param taskList The current list of tasks.
     * @param ui The user interface used to display results.
     * @param storage The storage manager used to save task changes.
     * @throws AnswerMeException If the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws AnswerMeException {
        TaskList originalTasks = new TaskList(taskList);
        TaskList duplicateTasks = taskList.removeDuplicateTasks();
        if (duplicateTasks.isEmpty()) {
            ui.showErrorMessage("No duplicate tasks found.");
            return;
        }

        try {
            storage.saveTasks(taskList);
        } catch (AnswerMeException exception) {
            taskList.clear();
            taskList.addAll(originalTasks);
            throw exception;
        }

        ui.printDedupItem(duplicateTasks, taskList.size());
    }
}
