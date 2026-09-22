package nico;

import java.util.List;

/**
 * Shows all tasks currently stored in the task list.
 */
public class ListCommand extends Command {
    /**
     * Displays all tasks in their current order.
     *
     * @param tasks tasks currently managed by the chatbot.
     * @param ui user interface used to display the tasks.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) {
        ui.showTaskList(tasks);
    }
}
