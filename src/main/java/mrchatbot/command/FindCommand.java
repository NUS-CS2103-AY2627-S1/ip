package mrchatbot.command;

import java.util.ArrayList;

import mrchatbot.storage.Storage;
import mrchatbot.task.Task;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Finds tasks that contain a keyword in their descriptions.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that finds tasks matching the given keyword.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Shows every task whose description contains the keyword.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> matchingTasks = tasks.find(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
