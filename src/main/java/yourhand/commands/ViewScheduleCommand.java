package yourhand.commands;

import java.time.LocalDate;

import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

/** Displays dated tasks occurring on a selected date. */
public class ViewScheduleCommand extends Command {
    private final LocalDate date;

    public ViewScheduleCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException {
        ui.showSchedule(taskList, date);
    }
}
