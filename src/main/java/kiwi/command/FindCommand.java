package kiwi.command;

import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.TaskList;

/**
 * Lists tasks whose description contains a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that lists tasks whose description contains {@code keyword}.
     *
     * @param keyword case-sensitive substring to search for in descriptions.
     */
    public FindCommand(String keyword) {
        assert keyword != null && !keyword.isEmpty()
                : "Parser already rejects a missing find keyword";
        this.keyword = keyword;
    }

    /**
     * Shows tasks whose description contains this command's keyword.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
