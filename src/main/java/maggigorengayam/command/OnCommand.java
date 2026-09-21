package maggigorengayam.command;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import maggigorengayam.storage.Storage;
import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;
import maggigorengayam.util.DateTimeUtil;

/** The {@code on <date>} command: shows deadlines/events occurring on a given date. */
public class OnCommand extends Command {
    private final LocalDate date;

    /** Shows deadlines/events occurring on the given date once {@link #execute} runs. */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /** Filters {@code tasks} down to those occurring on {@code date} and returns them as a message. */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.getAll().stream()
                .filter(task -> task.occursOn(date))
                .collect(Collectors.toList());
        return ui.showTasksOn(DateTimeUtil.formatDateOnlyForDisplay(date), matches);
    }
}
