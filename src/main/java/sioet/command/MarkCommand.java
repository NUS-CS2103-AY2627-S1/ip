package sioet.command;

import sioet.SioetException;
import sioet.storage.Storage;
import sioet.task.Task;
import sioet.task.TaskList;
import sioet.ui.Ui;

/**
 * Represents the command that marks or unmarks tasks.
 */
public class MarkCommand extends Command {
    private final String taskNumbersText;
    private final boolean shouldMarkDone;

    /**
     * Creates a mark or unmark command.
     *
     * @param taskNumbersText comma-separated task numbers
     * @param shouldMarkDone  whether the tasks should be marked as done
     */
    public MarkCommand(String taskNumbersText, boolean shouldMarkDone) {
        this.taskNumbersText = taskNumbersText;
        this.shouldMarkDone = shouldMarkDone;
    }

    /**
     * Executes the mark or unmark command.
     *
     * @param tasks   the task list
     * @param ui      the user interface
     * @param storage the task storage
     * @throws SioetException if the task numbers are invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws SioetException {
        if (taskNumbersText.isBlank()) {
            throw new SioetException(
                    "Provide at least one task number. Example: mark 1, 2");
        }

        String[] taskNumberTexts = taskNumbersText.split(",", -1);
        int[] taskIndexes = new int[taskNumberTexts.length];

        for (int index = 0; index < taskNumberTexts.length; index++) {
            try {
                taskIndexes[index] =
                        Integer.parseInt(taskNumberTexts[index].trim()) - 1;
            } catch (NumberFormatException exception) {
                throw new SioetException(
                        "sioet.task.Task numbers must be whole numbers separated by commas.");
            }

            if (taskIndexes[index] < 0
                    || taskIndexes[index] >= tasks.size()) {
                throw new SioetException(
                        "One or more task numbers are not in your list.");
            }
        }

        StringBuilder markedTasks = new StringBuilder();

        for (int taskIndex : taskIndexes) {
            Task task = tasks.get(taskIndex);

            if (shouldMarkDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }

            markedTasks.append(task).append('\n');
        }

        storage.save(tasks);

        ui.showTasksMarked(
                markedTasks.toString(),
                taskIndexes.length,
                shouldMarkDone);
    }
}
