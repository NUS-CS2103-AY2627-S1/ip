package elsa.command;

import java.time.LocalDate;

import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.ui.Ui;

/** Shows the tasks falling on one date. */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that will show what falls on the given date.
     *
     * @param date the date the user asked about
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        // Nothing changes, so the list is not saved.
        return ui.getTasksOnMessage(tasks, date);
    }
}
