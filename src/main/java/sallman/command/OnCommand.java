package sallman.command;

import java.time.LocalDate;

import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;


/**
 * Shows the tasks falling on one date.
 */
public class OnCommand extends Command {

    /** The date being asked about. */
    private final LocalDate date;

    /**
     * Creates a command that lists what falls on the given date.
     *
     * @param date the date being asked about
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Shows the tasks on this command's date. Nothing is changed, so nothing
     * is saved.
     *
     * @param tasks   the list to search
     * @param ui      used to show the matches
     * @param storage unused, since this command changes nothing
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(tasks, tasks.tasksOn(date), date);
    }
}
