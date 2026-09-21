package thaisbot.command.commands;

import java.time.LocalDate;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.command.Command;
import thaisbot.task.TaskList;

/**
 * Command that lists deadlines and events occurring on a specific date.
 */
public class FindOnDateCommand extends Command {
    private final LocalDate date;

    /**
     * Construct a FindOnDateCommand.
     * @param date date to search
     */
    public FindOnDateCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThaisBotException {
        ui.showTasksOnDate(tasks, date);
    }
}
