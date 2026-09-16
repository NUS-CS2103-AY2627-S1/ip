package will.command;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.task.FlexibleDate;
import will.task.Task;

/** The "on &lt;date&gt;" command: lists every task occurring on that date. */
public class OnCommand extends Command {
    private final LocalDate queryDate;

    /**
     * Creates a command that lists tasks occurring on the given date.
     *
     * @param queryDate The date to list tasks for.
     */
    public OnCommand(LocalDate queryDate) {
        this.queryDate = queryDate;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Here are the tasks occurring on " + FlexibleDate.formatForDisplay(queryDate) + ":");
        List<Task> matches = tasks.getTasks().stream()
                .filter(task -> task.occursOn(queryDate))
                .collect(Collectors.toList());
        for (int i = 0; i < matches.size(); i++) {
            ui.showMessage((i + 1) + "." + matches.get(i).toString());
        }
    }
}
