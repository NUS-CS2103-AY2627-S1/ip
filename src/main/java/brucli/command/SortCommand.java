package brucli.command;

import java.util.List;

import brucli.storage.Storage;
import brucli.task.SortField;
import brucli.task.TaskList;
import brucli.ui.Messages;
import brucli.ui.ResponseType;
import brucli.ui.Ui;

/**
 * Displays tasks ordered by a selected field.
 */
public class SortCommand extends Command {
    private final SortField sortField;

    /**
     * Creates a command that displays tasks ordered by the specified field.
     *
     * @param sortField Field used to order the tasks.
     */
    public SortCommand(SortField sortField) {
        this.sortField = sortField;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<TaskList.IndexedTask> sortedTasks =
                tasks.sortedBy(sortField);
        StringBuilder output = new StringBuilder();

        for (TaskList.IndexedTask indexedTask : sortedTasks) {
            output.append(String.format(
                    "%d: %s%n",
                    indexedTask.number(),
                    indexedTask.task()
            ));
        }

        ui.showListHeader();
        ui.showTaskList(
                output.isEmpty()
                        ? Messages.noTasksToSort()
                        : output.toString().stripTrailing()
        );
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.VIEW;
    }
}
