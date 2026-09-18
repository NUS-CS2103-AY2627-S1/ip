package herta.command;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import herta.storage.Storage;
import herta.task.Task;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that displays tasks in chronological order.
 */
public class SortCommand extends Command {
    private static final String SORT_CONFIRMATION_MESSAGE = "There. Your tasks are in date order.";
    /** Sort value that places tasks without a date after dated tasks. */
    private static final LocalDateTime UNSCHEDULED_TASK_SORT_TIME = LocalDateTime.MAX;

    /**
     * Displays all tasks sorted by their scheduled date without changing the
     * order stored in the task list.
     *
     * @param tasks the task list to sort for display.
     * @param ui the output interface used to display responses.
     * @param storage unused because sorting does not change stored data.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) {
        List<Integer> sortedIndices = tasks.getSortedIndices(Comparator.comparing((Task task) ->
                task.getScheduledDateTime().orElse(UNSCHEDULED_TASK_SORT_TIME)));
        if (sortedIndices.size() != tasks.size()) {
            throw new IllegalStateException("Sorting produced an incomplete task view.");
        }

        ui.showMessage(SORT_CONFIRMATION_MESSAGE);
        for (int index : sortedIndices) {
            ui.showTask(index + 1, tasks.get(index));
        }
    }
}
