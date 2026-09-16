package brucli.command;

import java.io.IOException;
import java.time.LocalDateTime;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.Ui;

/**
 * Adds an event task with start and end times.
 */
public class EventCommand extends AddCommand {
    private final LocalDateTime start;
    private final LocalDateTime end;

    /**
     * Creates an event command with its description and time range.
     */
    public EventCommand(
            String description,
            LocalDateTime start,
            LocalDateTime end
    ) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Adds the event, saves the list, and displays confirmation.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        tasks.addEvent(description, start, end);
        saveTasks(tasks, storage);
        ui.showEventAdded();
    }
}
