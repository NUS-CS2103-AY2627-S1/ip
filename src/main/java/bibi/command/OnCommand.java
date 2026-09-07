package bibi.command;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import bibi.Storage;
import bibi.Ui;
import bibi.task.Task;
import bibi.task.TaskDateTime;
import bibi.task.TaskList;

/**
 * Shows the deadlines due on one date and the events running on it.
 */
public class OnCommand extends Command {
    private final LocalDate queryDate;

    /**
     * Creates a command that will report on the given date.
     *
     * @param queryDate the date being asked about
     */
    public OnCommand(LocalDate queryDate) {
        this.queryDate = queryDate;
    }

    /**
     * Shows the tasks belonging to the queried date, or reports that there
     * are none.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String shownDate = TaskDateTime.formatDate(queryDate);
        List<Task> allTasks = tasks.getTasks();

        // Matches keep the number they have in the full list, so a task found
        // this way can be marked or removed without listing everything first.
        // Streaming the numbers rather than the tasks is what keeps that number
        // available after filtering.
        List<Integer> matchNumbers = IntStream.rangeClosed(1, allTasks.size())
                .filter(taskNumber -> allTasks.get(taskNumber - 1).occursOn(queryDate))
                .boxed()
                .toList();

        if (matchNumbers.isEmpty()) {
            ui.showMessage("You have nothing on " + shownDate + ".");
            return;
        }

        ui.showMessage("Here is what you have on " + shownDate + ":");
        matchNumbers.forEach(taskNumber ->
                ui.showNumberedTask(taskNumber, allTasks.get(taskNumber - 1)));
    }
}
