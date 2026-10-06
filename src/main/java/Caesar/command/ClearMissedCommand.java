package caesar.command;

import java.time.LocalDate;
import java.util.List;

import caesar.exception.CaesarException;
import caesar.storage.Storage;
import caesar.task.TaskList;
import caesar.ui.Ui;

/** Removes pending dated tasks whose deadlines or event end dates have passed. */
public class ClearMissedCommand extends Command {

    /** Clears missed tasks, saves the result, and reports the number removed. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CaesarException {
        List<TaskList.RemovedTask> removedTasks = tasks.clearMissedTasks(LocalDate.now());
        if (removedTasks.isEmpty()) {
            ui.showMissedTasksCleared(0);
            return;
        }

        try {
            storage.save(tasks);
        } catch (CaesarException exception) {
            tasks.restoreRemovedTasks(removedTasks);
            throw exception;
        }
        ui.showMissedTasksCleared(removedTasks.size());
    }
}
