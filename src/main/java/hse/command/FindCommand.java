package hse.command;

import java.io.IOException;
import java.util.List;

import hse.storage.Storage;
import hse.task.Task;
import hse.task.TaskList;
import hse.ui.Ui;

/** Displays tasks whose descriptions contain a requested keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a find command for the supplied keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        if (keyword.isEmpty()) {
            throw new IllegalArgumentException("Please provide a keyword to find.");
        }
        List<Task> matchingTasks = tasks.find(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
