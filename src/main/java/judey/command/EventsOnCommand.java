package judey.command;

import judey.storage.Storage;
import judey.tasklist.TaskList;
import judey.ui.Ui;

import java.time.LocalDate;

/**
 * Represents a command that filters and display tasks occurring on a specified date.
 */
public class EventsOnCommand extends Command {
    private final LocalDate date;

    /**
     * Constructs an {@code EventsOnCommand} with the target date to search
     * @param date The date to filter tasks by
     */
    public EventsOnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.printTasksOnDate(date);
    }
}