package nori.command;

import java.time.LocalDate;

import nori.NoriException;
import nori.storage.Storage;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Displays one day's tasks laid out as a schedule.
 */
class ScheduleCommand extends InputCommand {
    /**
     * Creates a schedule command.
     *
     * @param details the optional date after the {@code schedule} keyword.
     */
    ScheduleCommand(String details) {
        super(details);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NoriException {
        LocalDate date = details.isEmpty() ? LocalDate.now() : Parser.parseDate(details);
        ui.showResponse(tasks.getScheduleDisplayLines(date));
    }
}
