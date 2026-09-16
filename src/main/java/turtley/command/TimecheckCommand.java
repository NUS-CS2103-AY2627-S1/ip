package turtley.command;

import java.time.temporal.Temporal;

import turtley.model.Deadline;
import turtley.model.Event;
import turtley.model.Task;
import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;
import turtley.util.DateTimeParser;

/**
 * Displays deadline and event tasks on or before a supplied date/time.
 */
public class TimecheckCommand extends Command {

    private final String input;

    /**
     * Creates a timecheck command.
     *
     * @param input the cutoff date/time.
     */
    public TimecheckCommand(String input) {
        this.input = input;
    }

    /**
     * Displays tasks whose deadline or event start is on or before the cutoff.
     *
     * @param tasks the application's task list.
     * @param ui the application's user interface.
     * @param storage the application's persistence service, which is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Temporal cutoff = DateTimeParser.parse(input);
        String cutoffText = DateTimeParser.format(cutoff);
        boolean hasMatchingTask = false;

        ui.showTimecheckHeader(cutoffText);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean isDeadline = task instanceof Deadline deadline
                    && DateTimeParser.isOnOrBefore(deadline.getBy(), cutoff);
            boolean isEvent = task instanceof Event event
                    && DateTimeParser.isOnOrBefore(event.getFrom(), cutoff);
            if (isDeadline || isEvent) {
                hasMatchingTask = true;
                ui.showTask(i, task);
            }
        }
        if (!hasMatchingTask) {
            ui.showNoMatchingTasks();
        }
        ui.showSeparator();
    }
}
