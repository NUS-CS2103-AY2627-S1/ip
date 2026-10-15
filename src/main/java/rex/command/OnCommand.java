package rex.command;

import java.time.LocalDate;

import rex.TaskList;
import rex.Ui;
import rex.task.TaskDateTime;

/** Shows the tasks falling on one particular day. */
public class OnCommand extends Command {
    private final LocalDate day;

    /**
     * Creates a command that looks at one day.
     *
     * @param day the day the user asked about.
     */
    public OnCommand(LocalDate day) {
        this.day = day;
    }

    /**
     * Shows the tasks falling on the day, each keeping the number it has in
     * the full list so that it can still be marked or deleted by that number.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTasksOn(TaskDateTime.formatDate(day), tasks, tasks.findIndicesOn(day));
    }
}
