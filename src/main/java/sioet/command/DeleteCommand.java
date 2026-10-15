package sioet.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import sioet.SioetException;
import sioet.storage.Storage;
import sioet.task.Task;
import sioet.task.TaskList;
import sioet.ui.Ui;

/**
 * Represents the command that deletes tasks.
 */
public class DeleteCommand extends Command {
    private final String taskNumbersText;

    /**
     * Creates a delete command.
     *
     * @param taskNumbersText comma-separated task numbers
     */
    public DeleteCommand(String taskNumbersText) {
        this.taskNumbersText = taskNumbersText;
    }

    /**
     * Executes the delete command.
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
                    "Provide at least one task number. Example: delete 1, 2");
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

        Set<Integer> uniqueIndexes = new HashSet<>();

        for (int index : taskIndexes) {
            if (!uniqueIndexes.add(index)) {
                throw new SioetException("You cannot specify the same task more than once.");
            }
        }

        StringBuilder deletedTasks = new StringBuilder();

        Arrays.sort(taskIndexes);

        for (int index = taskIndexes.length - 1; index >= 0; index--) {
            Task deletedTask = tasks.remove(taskIndexes[index]);
            deletedTasks.insert(0, deletedTask + "\n");
        }

        storage.save(tasks);

        ui.showTasksDeleted(
                deletedTasks.toString(),
                taskIndexes.length,
                tasks.size());
    }
}
