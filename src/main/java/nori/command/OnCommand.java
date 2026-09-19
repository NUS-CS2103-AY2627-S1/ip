package nori.command;

import nori.NoriException;
import nori.storage.Storage;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Displays tasks that occur on a specified date.
 */
class OnCommand extends InputCommand {
    /**
     * Creates a date-query command.
     *
     * @param details the date after the {@code on} keyword.
     */
    OnCommand(String details) {
        super(details);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NoriException {
        ui.showResponse(tasks.getTasksOnDateDisplayLines(Parser.parseDate(details)));
    }
}
