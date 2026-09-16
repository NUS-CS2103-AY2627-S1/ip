package brucli.command;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.Messages;
import brucli.ui.ResponseType;
import brucli.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the keyword.
     *
     * @param keyword Keyword to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        StringBuilder output = new StringBuilder();

        for (TaskList.IndexedTask indexedTask : tasks.containing(keyword)) {
            output.append(String.format(
                    "%d: %s%n",
                    indexedTask.number(),
                    indexedTask.task()
            ));
        }

        ui.showFindResults(
                output.isEmpty()
                        ? Messages.noMatchingTasks()
                        : output.toString().stripTrailing()
        );
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.VIEW;
    }
}
