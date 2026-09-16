package brucli.command;

import brucli.storage.Storage;
import brucli.task.ListFilter;
import brucli.task.TaskList;
import brucli.ui.Messages;
import brucli.ui.ResponseType;
import brucli.ui.Ui;

/**
 * Displays all tasks or only dated tasks that satisfy a filter.
 */
public class ListCommand extends Command {
    private final ListFilter filter;

    /**
     * Creates a list command with an optional date filter.
     */
    public ListCommand(ListFilter filter) {
        this.filter = filter;
    }

    /**
     * Builds and displays the matching task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        StringBuilder output = new StringBuilder();

        for (TaskList.IndexedTask indexedTask : tasks.matching(filter)) {
            output.append(String.format(
                    "%d: %s%n",
                    indexedTask.number(),
                    indexedTask.task()
            ));
        }

        ui.showListHeader();
        ui.showTaskList(
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
