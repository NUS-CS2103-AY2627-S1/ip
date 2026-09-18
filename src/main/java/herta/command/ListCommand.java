package herta.command;

import herta.storage.Storage;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that displays every task in the current order.
 */
public class ListCommand extends Command {
    private static final String LIST_HEADING = "Let's see what you've managed to pile up:";
    private static final String EMPTY_LIST_MESSAGE = "No tasks to show. Give me something to organize.";

    /**
     * Displays the tasks managed by Herta.
     *
     * @param tasks the task list to display.
     * @param ui the output interface used to display responses.
     * @param storage unused because listing does not change saved data.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) {
        if (tasks.size() == 0) {
            ui.showMessage(EMPTY_LIST_MESSAGE);
            return;
        }

        ui.showMessage(LIST_HEADING);
        for (int i = 0; i < tasks.size(); i++) {
            ui.showTask(i + 1, tasks.get(i));
        }
    }
}
