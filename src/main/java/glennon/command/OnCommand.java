package glennon.command;

import java.time.LocalDate;

import glennon.Storage;
import glennon.Ui;
import glennon.task.TaskList;

/**
 * Displays missions scheduled on a selected calendar date.
 */
public final class OnCommand extends Command {
    /** Date whose scheduled missions should be displayed. */
    private final LocalDate date;

    /**
     * Creates a date-filter command.
     *
     * @param date date whose missions should be displayed.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays deadlines and events occurring on the selected date.
     *
     * @param missions missions in the current session.
     * @param ui interface used to display the result.
     * @param storage storage used by the application.
     */
    @Override
    public void execute(TaskList missions, Ui ui, Storage storage) {
        ui.showScheduledMissions(date, missions.occurringOn(date), missions.asList());
    }
}
