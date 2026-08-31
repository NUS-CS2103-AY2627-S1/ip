package bibi.command;

import bibi.Storage;
import bibi.Ui;
import bibi.task.Task;
import bibi.task.TaskList;

/**
 * Shows the tasks whose description contains a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that will search for the given keyword.
     *
     * @param keyword the text to look for in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        boolean hasMatch = false;

        // Matches keep the number they have in the full list, as the on command
        // does, so a task found this way can be marked or removed straight away.
        int taskNumber = 1;
        for (Task task : tasks.getTasks()) {
            if (task.hasKeyword(keyword)) {
                if (!hasMatch) {
                    ui.showMessage("Here are the matching tasks in your list:");
                    hasMatch = true;
                }
                ui.showNumberedTask(taskNumber, task);
            }
            taskNumber++;
        }

        if (!hasMatch) {
            ui.showMessage("No tasks match '" + keyword + "'.");
        }
    }
}
