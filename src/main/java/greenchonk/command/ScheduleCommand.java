package greenchonk.command;

import java.time.LocalDate;
import java.util.stream.IntStream;

import greenchonk.storage.Storage;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Displays deadlines and events that occur on a requested date.
 */
public class ScheduleCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that displays tasks occurring on the specified date.
     *
     * @param date the date whose scheduled tasks should be displayed.
     */
    public ScheduleCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays deadlines and events occurring on the configured date.
     * Preserves each matching task's position in the complete task list.
     *
     * @param tasks the task list to search
     * @param ui the UI that displays matching tasks
     * @param storage the unused task storage
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int[] scheduledTaskIndexes = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).occursOn(date))
                .toArray();

        if (scheduledTaskIndexes.length == 0) {
            ui.showEmptySchedule(date);
            return;
        }

        ui.showScheduleHeader(date);
        for (int taskIndex : scheduledTaskIndexes) {
            ui.showNumberedTask(taskIndex + 1, tasks.get(taskIndex));
        }
    }
}
