package panda.command;

import panda.exception.storage.TaskSavingException;
import panda.storage.Storage;
import panda.task.TaskList;
import panda.ui.ExceptionHandler;
import panda.ui.Ui;

/**
 * Saves Panda's data and requests exit confirmation from the frontend.
 */
public class SaveBeforeExitCommand implements Command {

    /**
     * Taunts the user, saves the current task list, and sends an exit-confirmation response.
     *
     * @param taskList Active task list.
     */
    @Override
    public void execute(TaskList taskList) {
        Ui.printMessage("Running away already? Don't call me your master!");
        try {
            Storage.saveState(taskList);
            Ui.printExitConfirmation(
                    "Miraculously, your training plan is safe.");
        } catch (TaskSavingException exception) {
            ExceptionHandler.handle(exception);
            Ui.printExitConfirmation(
                    "Your training plan refused to be saved. Even the file has standards. Quit anyway?");
        }
    }
}
