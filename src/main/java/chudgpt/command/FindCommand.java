package chudgpt.command;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.TaskList;
import chudgpt.ui.Ui;

/** Finds all the tasks in the current task list that matches the provided keyword, and displays them */
public class FindCommand extends Command {
    private String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        if (keyword.isBlank()) {
            throw new ChudException("Please enter a keyword to search for");
        }

        TaskList matches = tasks.findTasks(keyword);
        return ui.getMatchesMessage(matches);
    }
}
