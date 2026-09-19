package nori.command;

import nori.NoriException;
import nori.storage.Storage;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Displays the complete task list or tasks in a date range.
 */
class ListCommand extends InputCommand {
    /**
     * Creates a list command.
     *
     * @param details the optional date range after the {@code list} keyword.
     */
    ListCommand(String details) {
        super(details);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NoriException {
        if (details.isEmpty()) {
            ui.showResponse(tasks.getDisplayLines());
            return;
        }
        ui.showResponse(tasks.getTasksInDateRangeDisplayLines(Parser.parseListDateRange(details)));
    }
}
