package sumo.command;

import java.io.IOException;
import java.time.LocalDate;

import sumo.storage.Storage;
import sumo.task.TaskList;
import sumo.ui.Ui;

/** Displays tasks that occur on a particular date. */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a date-filtering command.
     *
     * @param date date whose tasks should be displayed.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        ui.showTasksOnDate(date.atStartOfDay(), tasks.findOn(date));
    }
}
